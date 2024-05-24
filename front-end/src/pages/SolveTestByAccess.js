import React, {useState, useEffect, useCallback} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';
import {useLoading} from '../context/LoadingContext';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faClock} from '@fortawesome/free-solid-svg-icons';
import "../styles/solveTest.css";
import 'bootstrap/dist/css/bootstrap.min.css';

const SolveTestByAccess = () => {
    const {accessKey} = useParams();
    const [test, setTest] = useState(null);
    const [questions, setQuestions] = useState([]);
    const [answers, setAnswers] = useState({});
    const [remainingTime, setRemainingTime] = useState(0);
    const navigate = useNavigate();
    const {setLoading} = useLoading();

    const token = localStorage.getItem("token");
    const email = localStorage.getItem("email");

    const shuffleArray = (array) => {
        let shuffledArray = array.slice();
        for (let i = shuffledArray.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [shuffledArray[i], shuffledArray[j]] = [shuffledArray[j], shuffledArray[i]];
        }
        return shuffledArray;
    };

    const buildQuestions = useCallback((sections, mixed) => {
        let allQuestions = [];
        sections.forEach(section => {
            let mixedQuestions = section.questions;
            if (section.usedQuestionsCount < section.totalQuestionsCount) {
                mixedQuestions = shuffleArray(mixedQuestions).slice(0, section.usedQuestionsCount);
            }
            allQuestions = allQuestions.concat(mixedQuestions);
        });

        if (mixed) {
            allQuestions = shuffleArray(allQuestions);
        }

        return allQuestions;
    }, []);

    const fetchTest = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/test/get-by-access-key?accessKey=${accessKey}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            const data = await response.json();
            setTest(data);

            let savedTest = sessionStorage.getItem(`test-${accessKey}`);
            if (!savedTest) {
                const allQuestions = buildQuestions(data.sections, data.mixedQuestions);
                setQuestions(allQuestions);

                const initialAnswers = allQuestions.reduce((acc, question) => {
                    acc[question.id] = question.questionType === 'OPEN' ? '' : [];
                    return acc;
                }, {});

                sessionStorage.setItem(`test-${accessKey}`, JSON.stringify({
                    questions: allQuestions,
                    answers: initialAnswers
                }));
                setAnswers(initialAnswers);

                const startTime = new Date().getTime();
                sessionStorage.setItem(`startTime-${accessKey}`, startTime);
                setRemainingTime(data.minutesToSolve * 60);
            } else {
                const parsedSavedTest = JSON.parse(savedTest);
                setQuestions(parsedSavedTest.questions);
                setAnswers(parsedSavedTest.answers);

                const startTime = parseInt(sessionStorage.getItem(`startTime-${accessKey}`), 10);
                const currentTime = new Date().getTime();
                const elapsedTime = Math.floor((currentTime - startTime) / 1000);
                const remaining = data.minutesToSolve * 60 - elapsedTime;
                setRemainingTime(remaining > 0 ? remaining : 0);
            }
        } catch (error) {
            console.error('Failed to fetch test:', error);
            toast.error('Грешка при зареждане на тест.');
        }
        setLoading(false);
    }, [accessKey, token, setLoading, buildQuestions]);

    useEffect(() => {
        fetchTest();
    }, [fetchTest]);

    useEffect(() => {
        if (Object.keys(answers).length > 0) {
            const savedTest = JSON.parse(sessionStorage.getItem(`test-${accessKey}`));
            if (savedTest) {
                savedTest.answers = answers;
                sessionStorage.setItem(`test-${accessKey}`, JSON.stringify(savedTest));
            }
        }
    }, [answers, accessKey]);

    const handleSubmit = useCallback(async (e) => {
        if (e) e.preventDefault();
        setLoading(true);

        let totalPoints = 0;

        const questionAttempts = Object.entries(answers).map(([questionId, answerArray]) => {
            const question = questions.find(q => q.id === questionId);
            let pointsAwarded = 0;

            if (question.questionType === 'OPEN') {
                const correctAnswer = question.answers.find(a => a.isCorrect).content;
                if (answerArray === correctAnswer) { // Ensure correct comparison
                    pointsAwarded = question.maximumPoints;
                }
            } else {
                const correctAnswers = question.answers.filter(a => a.isCorrect).map(a => a.content);
                if (correctAnswers.length === answerArray.length && correctAnswers.every(ans => answerArray.includes(ans))) {
                    pointsAwarded = question.maximumPoints;
                }
            }

            totalPoints += pointsAwarded;

            return {
                questionId: questionId,
                answers: Array.isArray(answerArray) ? answerArray : [answerArray],
                pointsAwarded: pointsAwarded
            };
        });

        const finalScore = totalPoints / questions.length;

        const submission = {
            email,
            testId: test.id,
            questionAttempts,
            attemptTime: new Date().toISOString(),
            totalPoints: totalPoints,
            finalScore: finalScore,
        };

        try {
            const response = await fetch('http://localhost:8090/api/v1/test/solve', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(submission),
            });

            if (!response.ok) {
                throw new Error('Failed to submit test.');
            }

            const result = await response.json();
            const grade = result.grade;

            toast.success('Тестът е предаден успешно.');
            sessionStorage.removeItem(`test-${accessKey}`);
            sessionStorage.removeItem(`startTime-${accessKey}`);

            const shownQuestions = questions.filter(q => questionAttempts.some(attempt => attempt.questionId === q.id));

            if (test.status === 'PUBLIC') {
                navigate('/test-result', {
                    state: {
                        userAnswers: answers,
                        result: {totalPoints, finalScore, grade},
                        shownQuestions
                    }
                });
            } else {
                navigate('/');
            }
        } catch (error) {
            console.error('Error during test submission:', error);
            toast.error('Грешка при предаване на тест.');
        }
        setLoading(false);
    }, [answers, email, navigate, setLoading, test, accessKey, token, questions]);

    useEffect(() => {
        if (remainingTime > 0) {
            const timer = setInterval(() => {
                setRemainingTime(prevTime => {
                    const newTime = prevTime - 1;
                    if (newTime <= 0) {
                        clearInterval(timer);
                        handleSubmit();
                    }
                    return newTime;
                });
            }, 1000);

            return () => clearInterval(timer);
        }
    }, [remainingTime, handleSubmit]);

    const handleAnswerChange = (questionId, answer, isMultiple) => {
        setAnswers(prevAnswers => {
            const updatedAnswers = {...prevAnswers};

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

    const handleOpenAnswerChange = (questionId, answerText) => {
        setAnswers(prevAnswers => ({
            ...prevAnswers,
            [questionId]: answerText,
        }));
    };

    if (!test) {
        return <div>Loading...</div>;
    }

    const formatTime = (time) => {
        const minutes = Math.floor(time / 60);
        const seconds = time % 60;
        return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    };

    return (
        <div className="solve-test-container container">
            <div className="timer-sticky">
                <div className="quiz-header">
                    <h2>{test.title}</h2>
                </div>
                <div className='timer'>
                    <FontAwesomeIcon icon={faClock} className='mr-5'/>
                    <span>{formatTime(remainingTime)}</span>
                </div>
            </div>
            <form onSubmit={handleSubmit}>
                {questions.map((question, qIndex) => (
                    <div key={question.id} className="question-container mb-4 p-3">
                        <h4 className="question-title mb-2">{question.question}</h4>
                        {question.image && (
                            <img src={question.image} alt={`Question ${qIndex + 1}`} className="img-fluid mb-2"/>
                        )}
                        {question.questionType !== 'OPEN' ? question.answers.map((answer, aIndex) => (
                            <div key={aIndex} className="form-check">
                                <input
                                    className="form-check-input"
                                    type={question.questionType === 'SINGLE_ANSWER' ? 'radio' : 'checkbox'}
                                    name={`question-${question.id}`}
                                    id={`answer-${question.id}-${aIndex}`}
                                    value={answer.content}
                                    checked={answers[question.id]?.includes(answer.content) || false}
                                    onChange={(e) => {
                                        handleAnswerChange(question.id, answer.content, question.questionType === 'MULTIPLE_ANSWER');
                                    }}
                                />
                                <label className="form-check-label" htmlFor={`answer-${question.id}-${aIndex}`}>
                                    {answer.content}
                                </label>
                            </div>
                        )) : (
                            <textarea
                                className="form-control"
                                rows="3"
                                value={answers[question.id] || ''}
                                onChange={e => handleOpenAnswerChange(question.id, e.target.value)}
                                placeholder="Въведи отговор"
                            ></textarea>
                        )}
                    </div>
                ))}
                <div className="submit-test-container text-center mb-4">
                    <button type="submit" className="btn btn-primary">Предай</button>
                </div>
            </form>
        </div>
    );
};

export default SolveTestByAccess;
