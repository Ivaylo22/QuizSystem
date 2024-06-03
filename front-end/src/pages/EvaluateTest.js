import React, {useState, useEffect, useCallback} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';
import '../styles/testResults.css';
import {useLoading} from '../context/LoadingContext';

const EvaluateTest = () => {
    const {userEmail, testId} = useParams();
    const navigate = useNavigate();
    const [userAttempt, setUserAttempt] = useState(null);
    const [shownQuestions, setShownQuestions] = useState([]);
    const [points, setPoints] = useState({});
    const {setLoading} = useLoading();
    const token = localStorage.getItem('token');

    const fetchAttemptData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/test/get-attempt-data?userEmail=${userEmail}&testId=${testId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Failed to fetch attempt data');
            }

            const data = await response.json();
            setUserAttempt(data.testAttempt);
            setShownQuestions(data.questions);
            const initialPoints = data.questions.reduce((acc, question) => {
                acc[question.id] = question.earnedPoints;
                return acc;
            }, {});
            setPoints(initialPoints);
        } catch (error) {
            console.error('Error:', error);
            toast.error('Failed to fetch attempt data');
        }
        setLoading(false);
    }, [userEmail, testId, token, setLoading]);

    useEffect(() => {
        fetchAttemptData();
    }, [fetchAttemptData]);

    const handlePointsChange = (questionId, newPoints) => {
        if (newPoints <= shownQuestions.find(q => q.id === questionId).maximumPoints) {
            setPoints(prevPoints => ({
                ...prevPoints,
                [questionId]: newPoints
            }));
        }
    };

    const handleSaveChanges = async () => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8090/api/v1/test/update-attempt-points', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({userEmail, testId, points})
            });

            if (!response.ok) {
                throw new Error('Failed to update attempt points');
            }

            toast.success('Points updated successfully');
            navigate(`/test-attempts/${testId}`);
        } catch (error) {
            console.error('Error:', error);
            toast.error('Failed to update attempt points');
        }
        setLoading(false);
    };

    const isOpenAnswerCorrect = (questionId, userAnswer) => {
        const correctAnswers = shownQuestions
            .find(q => q.id === questionId)
            .answers.filter(a => a.isCorrect)
            .map(a => a.content);
        return correctAnswers.includes(userAnswer); // Use userAnswer directly for open questions
    };

    if (!userAttempt) {
        return <div>Loading...</div>;
    }

    return (
        <div className="container mt-5">
            <h3>Оценка: {userAttempt.grade}</h3>
            <h3>Верни отговори: {shownQuestions.length}</h3>
            {shownQuestions.map((question, qIndex) => (
                <div key={qIndex} className="mb-4 p-3 question-container">
                    <div className="d-flex justify-content-between align-items-center">
                        <div>Максимални точки: {question.maximumPoints}</div>
                        <div className="input-label">
                            Точки:
                            <input
                                type="number"
                                className="points-input"
                                value={points[question.id]}
                                onChange={(e) => handlePointsChange(question.id, parseInt(e.target.value))}
                                max={question.maximumPoints}
                            />
                        </div>
                    </div>
                    <h4>{question.question}</h4>
                    {question.image && (
                        <img src={question.image ?? 'default-placeholder.png'} alt={`Question ${qIndex + 1}`}
                             className="img-fluid mb-3"/>
                    )}
                    {question.questionType === 'OPEN' ? (
                        <>
                            <div
                                className={`user-answer ${isOpenAnswerCorrect(question.id, question.chosenAnswers.join(', ')) ? "user-correct" : "user-incorrect"}`}>
                                Твой отговор: {question.chosenAnswers.join(', ')}
                            </div>
                            {!isOpenAnswerCorrect(question.id, question.chosenAnswers.join(', ')) && (
                                <div className="correct-answer-display">
                                    Верен
                                    отговор: {question.answers.filter(a => a.isCorrect).map(a => a.content).join(', ')}
                                </div>
                            )}
                        </>
                    ) : (
                        question.answers.map((answer, aIndex) => {
                            const isCorrectAnswer = answer.isCorrect;
                            const isUserSelected = question.chosenAnswers.includes(answer.content);
                            let className = isCorrectAnswer ? "question-correct" : "answer-option";
                            if (isUserSelected) {
                                className += isCorrectAnswer ? " user-correct" : " user-incorrect";
                            }

                            return (
                                <div key={aIndex} className={className}>
                                    {answer.content}
                                </div>
                            );
                        })
                    )}
                </div>
            ))}
            <div className="text-center mt-4 mb-3">
                <button className="btn btn-secondary" onClick={() => navigate(`/test-attempts/${testId}`)}>Назад
                </button>
                <button className="btn btn-primary" onClick={handleSaveChanges}>Запази промените</button>
            </div>
        </div>
    );
};

export default EvaluateTest;
