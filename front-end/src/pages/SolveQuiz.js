import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClock } from '@fortawesome/free-solid-svg-icons';
import {Modal} from 'react-bootstrap';
import "../styles/solveQuiz.css";
import {useLoading} from '../context/LoadingContext';

const SolveQuiz = ({email, token}) => {
    const { quizId } = useParams();
    const navigate = useNavigate();
    const [quiz, setQuiz] = useState(null);
    const [userAnswers, setUserAnswers] = useState({});
    const [startTime] = useState(new Date().getTime());
    const [elapsedTime, setElapsedTime] = useState(0);
    const {setLoading} = useLoading();
    const [showModal, setShowModal] = useState(false);
    const [modalImage, setModalImage] = useState('');

    useEffect(() => {
        const fetchQuiz = async () => {
            setLoading(true);
            try {
                const response = await fetch(`http://localhost:8090/api/v1/quiz/get-by-id?quizId=${quizId}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });
                if (!response.ok) {
                    throw new Error('Failed to fetch quiz details');
                }
                const data = await response.json();
                setQuiz(data);

                const initialAnswers = data.questions.reduce((acc, question) => {
                    acc[question.id] = [];
                    return acc;
                }, {});

                setUserAnswers(initialAnswers);
            } catch (error) {
                console.error('Error:', error);
                toast.error('Неуспешно зареждане на куиз.');
            }
            setTimeout(() => setLoading(false), 500);
        };

        fetchQuiz();
    }, [quizId, token, setLoading]);

    useEffect(() => {
        const timerId = setInterval(() => {
            const currentTime = new Date().getTime();
            const elapsedTimeSinceStart = (currentTime - startTime) / 1000;
            setElapsedTime(elapsedTimeSinceStart);
        }, 1000);

        return () => clearInterval(timerId);
    }, [startTime]);

    const handleAnswerChange = (questionIndex, answer, isMultiple) => {
        const questionId = quiz.questions[questionIndex].id;
        setUserAnswers(prevAnswers => {
            const updatedAnswers = JSON.parse(JSON.stringify(prevAnswers));

            if (isMultiple) {
                const currentAnswers = updatedAnswers[questionId] || [];
                const answerIndex = currentAnswers.indexOf(answer);
                if (answerIndex > -1) {
                    updatedAnswers[questionId] = currentAnswers.filter(a => a !== answer);
                } else {
                    updatedAnswers[questionId] = [...currentAnswers, answer];
                }
            } else {
                updatedAnswers[questionId] = [answer];
            }
            return updatedAnswers;
        });
    };

    const handleOpenAnswerChange = (questionIndex, answerText) => {
        const questionId = quiz.questions[questionIndex].id;
        setUserAnswers(prevAnswers => ({
            ...prevAnswers,
            [questionId]: [answerText],
        }));
    };

    const handleSubmitQuiz = async () => {
        let correctAnswersCount = 0;

        quiz.questions.forEach(question => {
            const userAnswer = userAnswers[question.id];
            if (question.questionType !== 'MULTIPLE_ANSWER') {
                if (question.answers.some(answer => answer.isCorrect && userAnswer.includes(answer.content))) {
                    correctAnswersCount += 1;
                }
            } else {
                const correctAnswers = question.answers.filter(answer => answer.isCorrect).map(answer => answer.content);
                const isCorrect = correctAnswers.length === userAnswer.length && correctAnswers.every(answer => userAnswer.includes(answer));
                if (isCorrect) correctAnswersCount += 1;
            }
        });

        const solveQuizRequest = {
            email: email,
            quizId: quizId,
            correctAnswers: correctAnswersCount,
            secondsToSolve: Math.floor(elapsedTime),
            isDaily: quiz.isDaily,
        };

        try {
            const response = await fetch(`http://localhost:8090/api/v1/quiz/solve`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(solveQuizRequest),
            });

            if (!response.ok) {
                throw new Error('Failed to submit quiz answers');
            }

            const result = await response.json();
            navigate(`/quiz-results/${quizId}`, {
                state: {
                    quiz,
                    userAnswers,
                    result: result,
                },
            });
        } catch (error) {
            console.error('Error:', error);
            toast.error('Failed to submit quiz answers.');
        }
    };

    const handleImageClick = (image) => {
        setModalImage(image);
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
    };

    console.log(quiz);

    if (!quiz) {
        return <div>Loading...</div>;
    }

    return (
        <div className="container solve-quiz-container">
            <div className="timer-sticky">
                <div className="quiz-header">
                    <h2>{quiz.title}</h2>
                </div>
                <div className='timer'>
                    <FontAwesomeIcon icon={faClock} className='mr-5'/>
                    <span>{`${Math.floor(elapsedTime / 60)}:${Math.floor(elapsedTime % 60) < 10 ? `0${Math.floor(elapsedTime % 60)}` : Math.floor(elapsedTime % 60)}`}</span>
                </div>
            </div>
            {quiz.questions.map((question, qIndex) => (
                <div key={question.id} className="question-container mb-4 p-3">
                    <h4 className="question-title mb-2">{question.question}</h4>
                    {question.image && (
                        <img
                            src={question.image}
                            alt={`Въпрос ${qIndex + 1}`}
                            className="img-fluid mb-2 question-image"
                            onClick={() => handleImageClick(question.image)}
                            style={{cursor: 'pointer'}}
                        />
                    )}
                    {question.questionType !== 'OPEN' ? question.answers.map((answer, aIndex) => (
                        <div key={aIndex} className="form-check">
                            <input
                                className="form-check-input"
                                type={question.questionType === 'SINGLE_ANSWER' ? 'radio' : 'checkbox'}
                                name={`question-${question.id}`}
                                id={`answer-${question.id}-${aIndex}`}
                                value={answer.content}
                                checked={userAnswers[question.id]?.includes(answer.content)}
                                onChange={() => handleAnswerChange(qIndex, answer.content, question.questionType === 'MULTIPLE_ANSWER')}
                            />
                            <label className="form-check-label" htmlFor={`answer-${question.id}-${aIndex}`}>
                                {answer.content}
                            </label>
                        </div>
                    )) : (
                        <textarea
                            className="form-control"
                            rows="3"
                            value={userAnswers[question.id] ? userAnswers[question.id][0] : ''}
                            onChange={e => handleOpenAnswerChange(qIndex, e.target.value)}
                            placeholder="Вашият отговор..."
                        ></textarea>
                    )}
                </div>
            ))}
            <div className="submit-quiz-container text-center mb-4">
                <button onClick={handleSubmitQuiz} className="btn btn-primary">Предай</button>
            </div>

            <Modal show={showModal} onHide={handleCloseModal} centered size="xl">
                <Modal.Body>
                    <img src={modalImage} alt="Full Size" className="img-fluid" style={{width: '100%'}}/>
                </Modal.Body>
            </Modal>
        </div>
    );
};

export default SolveQuiz;
