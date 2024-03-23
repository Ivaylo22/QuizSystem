import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import QuizCard from '../components/QuizCard';
import '../styles/allQuizzes.css';

const AllQuizzes = ({ email, token }) => {
    const [quizzes, setQuizzes] = useState([]);
    const [selectedQuiz, setSelectedQuiz] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchQuizzes = async () => {
            try {
                const response = await fetch(`http://localhost:8090/api/v1/quiz/get-all-for-user?email=${encodeURIComponent(email)}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });
                if (!response.ok) {
                    throw new Error('Failed to fetch quizzes.');
                }
                const data = await response.json();
                setQuizzes(data.quizModels);
            } catch (error) {
                console.error('Error:', error);
            }
        };

        fetchQuizzes();
    }, [email, token]);

    useEffect(() => {
        const handleKeyDown = (e) => {
            if (e.key === 'Escape') {
                handleCloseModal();
            }
        };

        document.addEventListener('keydown', handleKeyDown);

        return () => {
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, []);

    const handleCloseModal = () => {
        setSelectedQuiz(null);
    };

    const handleStartQuiz = (quizId) => {
        navigate(`/start-quiz/${quizId}`);
    };

    return (
        <div className="container all-quizzes-container align-items-center">
            <div className="row quiz-header pt-2">
                <div className="col-4 header">Име</div>
                <div className="col-2 header">Категория</div>
                <div className="col-1 header">Въпроси</div>
                <div className="col-1 header">Средно време</div>
                <div className="col-1 header">Среден резултат</div>
                <div className="col-1 header">Мой опит</div>
                <div className="col-2 header">Статус</div>
            </div>
            {quizzes.map(quiz => (
                <div className="all-quiz-card" key={quiz.quizId}>
                    <QuizCard quiz={quiz}  onClick={() => setSelectedQuiz(quiz)} />
                </div>
            ))}
            {selectedQuiz && (
                <div className="dialog-overlay" onClick={handleCloseModal}>
                    <dialog open className="quiz-dialog" onClick={e => e.stopPropagation()}>
                        <h3 className='text-center'>{selectedQuiz.name}</h3>
                        <p className='mt-2'>Категория: {selectedQuiz.category}</p>
                        <p>Въпроси: {selectedQuiz.questionsCount}</p>
                        <p className='mt-2'>Средно време: {selectedQuiz.averageSecondsNeeded ?? 0}</p>
                        <p>Среден резултат: {selectedQuiz.averageCorrectAnswers ?? 0}</p>
                        <p className='mt-2'>Мой най-добър резултат: {selectedQuiz.personalBestCorrectAnswers ?? 0}</p>
                        <p>Мое най-бързо време: {selectedQuiz.personalBestTime ?? 0}</p>
                        <p>Мой опит: {selectedQuiz.personalBestXpGained ?? 0}/100</p>

                        <p className='mt-2 text-center'>При натискане на бутона "Започни" ще започне да тече вашето време</p>
                        <div className="dialog-buttons-container">
                            <button className='btn-cancel' onClick={handleCloseModal}>Затвори</button>
                            <button className='btn-start' onClick={() => handleStartQuiz(selectedQuiz.quizId)}>Започни</button>
                        </div>
                    </dialog>
                </div>
            )}
        </div>
    );
};

export default AllQuizzes;
