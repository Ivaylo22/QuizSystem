import React, {useState, useEffect, useCallback} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';
import {useLoading} from '../context/LoadingContext';
import "../styles/solveTest.css";
import 'bootstrap/dist/css/bootstrap.min.css';

const SolveTest = () => {
    const {testId} = useParams();
    const [test, setTest] = useState(null);
    const [questions, setQuestions] = useState([]);
    const [answers, setAnswers] = useState({});
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
            const response = await fetch(`http://localhost:8090/api/v1/test/get-by-id?testId=${testId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            const data = await response.json();
            setTest(data);

            const savedTest = sessionStorage.getItem(`test-${testId}`);
            if (!savedTest) {
                const allQuestions = buildQuestions(data.sections, data.mixedQuestions);
                setQuestions(allQuestions);

                const initialAnswers = allQuestions.reduce((acc, question) => {
                    acc[question.id] = question.questionType === 'OPEN' ? '' : [];
                    return acc;
                }, {});

                sessionStorage.setItem(`test-${testId}`, JSON.stringify({
                    questions: allQuestions,
                    answers: initialAnswers
                }));
                setAnswers(initialAnswers);
            } else {
                const parsedSavedTest = JSON.parse(savedTest);
                setQuestions(parsedSavedTest.questions);
                setAnswers(parsedSavedTest.answers);
            }
        } catch (error) {
            console.error('Failed to fetch test:', error);
            toast.error('Failed to fetch test.');
        }
        setLoading(false);
    }, [testId, token, setLoading, buildQuestions]);

    useEffect(() => {
        fetchTest();
    }, [fetchTest]);

    useEffect(() => {
        if (Object.keys(answers).length > 0) {
            const savedTest = JSON.parse(sessionStorage.getItem(`test-${testId}`));
            if (savedTest) {
                savedTest.answers = answers;
                sessionStorage.setItem(`test-${testId}`, JSON.stringify(savedTest));
            }
            console.log("Saved answers to session storage:", answers); // Debugging line
        }
    }, [answers, testId]);

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

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        const questionAttempts = Object.entries(answers).map(([questionId, answerArray]) => ({
            question: {id: questionId},
            answers: Array.isArray(answerArray) ? answerArray : [answerArray],
            pointsAwarded: null
        }));

        const submission = {
            email,
            testId,
            questionAttempts,
            attemptTime: new Date().toISOString(),
            totalPoints: null,
            finalScore: null,
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

            toast.success('Test submitted successfully.');
            sessionStorage.removeItem(`test-${testId}`);
            navigate('/');
        } catch (error) {
            console.error('Error during test submission:', error);
            toast.error('Failed to submit test.');
        }
        setLoading(false);
    };

    if (!test) {
        return <div>Loading...</div>;
    }

    return (
        <div className="solve-test-container container">
            <h2 className="my-4">{test.title}</h2>
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
                                placeholder="Your answer..."
                            ></textarea>
                        )}
                    </div>
                ))}
                <div className="submit-test-container text-center mb-4">
                    <button type="submit" className="btn btn-primary">Submit Test</button>
                </div>
            </form>
        </div>
    );
};

export default SolveTest;
