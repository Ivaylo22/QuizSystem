import React, {useState, useEffect, useCallback} from 'react';
import {useParams} from 'react-router-dom';
import {toast} from 'react-toastify';
import {useLoading} from '../context/LoadingContext';
import '../styles/solveTest.css';

const randomizeQuestions = (sections, mixedQuestions) => {
    return sections.map(section => {
        let questions = [...section.questions];
        if (section.usedQuestionsCount < section.totalQuestionsCount) {
            questions = getRandomQuestions(questions, section.usedQuestionsCount);
        }
        if (mixedQuestions) {
            questions = shuffleArray(questions);
        }
        return {...section, questions};
    });
};

const getRandomQuestions = (questions, count) => {
    const shuffled = shuffleArray(questions);
    return shuffled.slice(0, count);
};

const shuffleArray = (array) => {
    const shuffled = [...array];
    for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
};

const SolveTest = () => {
    const {testId} = useParams();
    const [test, setTest] = useState(null);
    const [sections, setSections] = useState([]);
    const token = localStorage.getItem('token');
    const {setLoading} = useLoading();

    const fetchTestData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/test/get-by-id?testId=${testId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Failed to fetch test data');
            }

            const data = await response.json();
            setTest(data);
            setSections(randomizeQuestions(data.sections, data.mixedQuestions));
        } catch (error) {
            console.error('Error fetching test data:', error);
        }
        setTimeout(() => setLoading(false), 500);
    }, [testId, token, setLoading]);

    useEffect(() => {
        fetchTestData();
    }, [fetchTestData]);

    const handleAnswerChange = (sectionId, questionId, answerIndex, value) => {
        setSections(sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    questions: section.questions.map(question => {
                        if (question.id === questionId) {
                            return {
                                ...question,
                                answers: question.answers.map((answer, index) => {
                                    if (index === answerIndex) {
                                        return {...answer, content: value};
                                    }
                                    return answer;
                                })
                            };
                        }
                        return question;
                    })
                };
            }
            return section;
        }));
    };

    const handleSubmit = async () => {
        // Implement submission logic here
        toast.success('Test submitted successfully!');
    };

    if (!test) {
        return <div>Loading...</div>;
    }

    return (
        <div className="solve-test-container">
            <h1>{test.title}</h1>
            {sections.map((section, index) => (
                <div key={section.id} className="section">
                    <h2>Section {index + 1}</h2>
                    {section.questions.map((question, qIndex) => (
                        <div key={question.id} className="question">
                            <h3>Question {qIndex + 1}</h3>
                            <p>{question.question}</p>
                            {question.image && <img src={question.image} alt="question"/>}
                            <div className="answers">
                                {question.answers.map((answer, aIndex) => (
                                    <div key={aIndex} className="answer">
                                        <input
                                            type={question.questionType === 'MULTIPLE_ANSWER' ? 'checkbox' : 'radio'}
                                            name={`question-${question.id}`}
                                            value={answer.content}
                                            onChange={(e) => handleAnswerChange(section.id, question.id, aIndex, e.target.value)}
                                        />
                                        <label>{answer.content}</label>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            ))}
            <button onClick={handleSubmit} className="submit-test">Submit Test</button>
        </div>
    );
};

export default SolveTest;
