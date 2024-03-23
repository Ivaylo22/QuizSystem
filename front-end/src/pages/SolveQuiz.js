import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClock } from '@fortawesome/free-solid-svg-icons';
import "../styles/solveQuiz.css";

const SolveQuiz = ({ token }) => {
    const { quizId } = useParams();
    const navigate = useNavigate();
    const [quiz, setQuiz] = useState(null);
    const [userAnswers, setUserAnswers] = useState({});
    const [timer, setTimer] = useState(0);

    useEffect(() => {
        const fetchQuiz = async () => {
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
                // Initialize user answers state
                const initialAnswers = {};
                data.questions.forEach((question, index) => {
                    if (question.questionType === 'OPEN') {
                        initialAnswers[index] = '';
                    } else {
                        initialAnswers[index] = [];
                    }
                });
                setUserAnswers(initialAnswers);
            } catch (error) {
                console.error('Error:', error);
                toast.error('Неуспешно зареждане на куиз.');
            }
        };

        fetchQuiz();
    }, [quizId, token]);

    useEffect(() => {
        const timerId = setInterval(() => {
            setTimer(prevTimer => prevTimer + 1);
        }, 1000);

        return () => clearInterval(timerId);
    }, []);

    const handleAnswerChange = (questionIndex, answer, isMultiple) => {
        setUserAnswers(prevAnswers => {
            const updatedAnswers = { ...prevAnswers };
            if (isMultiple) {
                if (updatedAnswers[questionIndex].includes(answer)) {
                    updatedAnswers[questionIndex] = updatedAnswers[questionIndex].filter(a => a !== answer);
                } else {
                    updatedAnswers[questionIndex].push(answer);
                }
            } else {
                updatedAnswers[questionIndex] = [answer];
            }
            return updatedAnswers;
        });
    };

    const handleOpenAnswerChange = (questionIndex, answerText) => {
        setUserAnswers(prevAnswers => ({
            ...prevAnswers,
            [questionIndex]: answerText
        }));
    };

    const handleSubmitQuiz = () => {
        // Here you would typically send userAnswers to your backend for evaluation
        console.log(userAnswers);
        navigate(`/quiz-results/${quizId}`); // Navigate to results page
    };

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
                    <span>{`${Math.floor(timer / 60)}:${timer % 60 < 10 ? `0${timer % 60}` : timer % 60}`}</span>
                </div>
            </div>
            {quiz.questions.map((question, qIndex) => (
                <div key={qIndex} className="question-container mb-4 p-3">
                    <h4 className="question-title mb-2">Въпрос {qIndex + 1}</h4>
                    {question.image && (
                        <img src={question.image} alt={`Въпрос ${qIndex + 1}`} className="img-fluid mb-2" />
                    )}
                    <p>{question.question}</p>
                    <div className="answers-container">
                        {question.questionType !== 'OPEN' ? question.answers.map((answer, aIndex) => (
                            <div key={aIndex} className="form-check">
                                <input className="form-check-input" type={question.questionType === 'SINGLE_ANSWER' ? 'radio' : 'checkbox'}
                                    name={`question-${qIndex}`} id={`answer-${qIndex}-${aIndex}`}
                                       value={answer.content}
                                    onChange={() => handleAnswerChange(qIndex, answer.content, question.questionType !== 'SINGLE_ANSWER')} />
                                <label className="form-check-label" htmlFor={`answer-${qIndex}-${aIndex}`}>
                                    {answer.content}
                                </label>
                            </div>
                        )) : (
                            <textarea className="form-control" rows="3"
                                      value={userAnswers[qIndex]}
                                onChange={(e) => handleOpenAnswerChange(qIndex, e.target.value)}
                                placeholder="Вашият отговор..."></textarea>
                        )}
                    </div>
                </div>
            ))}
            <div className="submit-quiz-container text-center mb-4">
                <button onClick={handleSubmitQuiz} className="btn btn-primary">Предай</button>
            </div>
        </div>
    );
};

export default SolveQuiz;
