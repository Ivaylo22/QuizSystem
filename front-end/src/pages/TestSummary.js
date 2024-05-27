import React, {useState, useEffect} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';
import "../styles/testSummary.css";
import {useLoading} from '../context/LoadingContext';

const TestSummary = () => {
    const {testId} = useParams();
    const navigate = useNavigate();
    const [test, setTest] = useState(null);
    const {setLoading} = useLoading();
    const token = localStorage.getItem('token');

    useEffect(() => {
        const fetchTestSummary = async () => {
            setLoading(true);
            try {
                const response = await fetch(`http://localhost:8090/api/v1/test/get-test-summary?testId=${testId}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch test summary');
                }

                const data = await response.json();
                setTest(data.model);
            } catch (error) {
                console.error('Error:', error);
                toast.error('Failed to fetch test summary');
            }
            setLoading(false);
        };

        fetchTestSummary();
    }, [testId, token, setLoading]);

    if (!test) {
        return <div>Loading...</div>;
    }

    const translateQuestionType = (questionType) => {
        switch (questionType) {
            case 'SINGLE_ANSWER':
                return 'Един верен отговор';
            case 'MULTIPLE_ANSWER':
                return 'Няколко верни отговора';
            case 'OPEN':
                return 'Отворен отговор';
            default:
                return 'Неопределен тип въпрос';
        }
    };

    const sortAnswers = (answers) => {
        return answers.sort((a, b) => {
            if (a.isCorrect && !b.isCorrect) return -1;
            if (!a.isCorrect && b.isCorrect) return 1;
            return b.percentage - a.percentage;
        });
    };

    const sortedQuestions = test.questions.sort((a, b) => a.order - b.order);

    return (
        <div className="container test-summary-container">
            <h2 className="text-center mt-4">{test.title}</h2>
            {sortedQuestions.map((question, qIndex) => (
                <div key={qIndex} className="question-container mb-4 p-3">
                    <h4 className="question-title">Въпрос {qIndex + 1} ({translateQuestionType(question.questionType)})</h4>
                    {question.image && (
                        <img src={question.image} alt={`Въпрос ${qIndex + 1}`}
                             className="img-fluid mb-2 question-image"/>
                    )}
                    <p className='question-content mb-2'>{question.question}</p>
                    <ul className="list-unstyled answers-list">
                        {sortAnswers(question.answers).map((answer, aIndex) => (
                            <li key={aIndex}
                                className={`answer p-2 ${answer.isCorrect ? 'correct-answer' : 'bg-light'}`}>
                                <span>{answer.content}</span>
                                <span className="answer-statistics">{answer.percentage.toFixed(2)}%</span>
                            </li>
                        ))}
                    </ul>
                </div>
            ))}
        </div>
    );
};

export default TestSummary;
