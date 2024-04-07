import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import QuizCard from '../components/QuizCard';
import '../styles/allQuizzes.css';
import {useLoading} from '../context/LoadingContext';

const AllQuizzes = ({ email, token }) => {
    const [quizzes, setQuizzes] = useState([]);
    const [selectedQuiz, setSelectedQuiz] = useState(null);
    const {setLoading} = useLoading();
    const [filterBy, setFilterBy] = useState('createdAt');
    const [order, setOrder] = useState('asc');
    const [selectedCategory, setSelectedCategory] = useState('Всички');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchQuizzes = async () => {
            setLoading(true);
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
            setTimeout(() => setLoading(false), 500);
        };

        fetchQuizzes();
    }, [email, token, setLoading]);

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
        navigate(`/solve-quiz/${quizId}`);
    };

    const sortQuizzes = () => {
        let sortedQuizzes = quizzes.filter(quiz => selectedCategory === 'Всички' || quiz.category === selectedCategory);
        sortedQuizzes.sort((a, b) => {
            let valA, valB;
            switch (filterBy) {
                case 'createdAt':
                    valA = new Date(a.createdAt);
                    valB = new Date(b.createdAt);
                    break;
                case 'averageCorrectAnswers':
                    valA = a.averageCorrectAnswers;
                    valB = b.averageCorrectAnswers;
                    break;
                case 'averageSecondsNeeded':
                    valA = a.averageSecondsNeeded;
                    valB = b.averageSecondsNeeded;
                    break;
                case 'personalBestXpGained':
                    valA = a.personalBestXpGained;
                    valB = b.personalBestXpGained;
                    break;
                case 'questionsCount':
                    valA = a.questionsCount;
                    valB = b.questionsCount;
                    break;
                default:
                    return 0;
            }

            if (order === 'asc') {
                return valA > valB ? 1 : -1;
            } else {
                return valA < valB ? 1 : -1;
            }
        });
        return sortedQuizzes;
    };

    const categories = ['Всички', ...new Set(quizzes.map(quiz => quiz.category))];

    console.log(quizzes);
    return (
        <div className="container all-quizzes-container align-items-center">
            <div className="container mt-4 mx-auto">
                <div className="row mb-3">
                    <div className="col-md-4">
                        <select className="form-control" value={selectedCategory}
                                onChange={(e) => setSelectedCategory(e.target.value)}>
                            {categories.map(category => (
                                <option key={category} value={category}>{category}</option>
                            ))}
                        </select>
                    </div>
                    <div className="col-md-4">
                        <select className="form-control" value={filterBy} onChange={(e) => setFilterBy(e.target.value)}>
                            <option value="personalBestXpGained">Мой опит</option>
                            <option value="createdAt">Дата на създаване</option>
                            <option value="averageCorrectAnswers">Среден резултат</option>
                            <option value="averageSecondsNeeded">Средено време</option>
                            <option value="questionsCount">Брой въпроси</option>
                        </select>
                    </div>
                    <div className="col-md-4 d-flex align-items-start pt-1">
                        <button className={`btn ${order === 'asc' ? 'btn-asc' : 'btn-desc'}`}
                                onClick={() => setOrder(order === 'asc' ? 'desc' : 'asc')}>
                            {order === 'asc' ? 'Възходящ' : 'Низходящ'}
                        </button>
                    </div>
                </div>
            </div>
            <div className="row quiz-header pt-2">
                <div className="col-4 header">Име</div>
                <div className="col-2 header">Категория</div>
                <div className="col-1 header">Въпроси</div>
                <div className="col-1 header">Средно време</div>
                <div className="col-1 header">Среден резултат</div>
                <div className="col-1 header">Мой опит</div>
                <div className="col-2 header">Статус</div>
            </div>
            {sortQuizzes().map(quiz => (
                <div className="all-quiz-card" key={quiz.quizId}>
                    <QuizCard quiz={quiz} onClick={() => setSelectedQuiz(quiz)}/>
                </div>
            ))}
            {selectedQuiz && (
                <div className="dialog-overlay" onClick={handleCloseModal}>
                    <dialog open className="quiz-dialog" onClick={e => e.stopPropagation()}>
                        <h3 className='text-center'>{selectedQuiz.name}</h3>
                        <p className='mt-2'>Категория: {selectedQuiz.category}</p>
                        <p>Въпроси: {selectedQuiz.questionsCount}</p>
                        <p className='mt-2'>Средно време: {selectedQuiz.averageSecondsNeeded ? `${Math.floor(selectedQuiz.averageSecondsNeeded / 60)}:${String(selectedQuiz.averageSecondsNeeded % 60).padStart(2, '0')} мин.` : 0}</p>
                        <p>Среден резултат: {selectedQuiz.averageCorrectAnswers ?? 0}/{selectedQuiz.questionsCount}</p>
                        <p className='mt-2'>Мой най-добър
                            резултат: {selectedQuiz.personalBestCorrectAnswers ?? 0}/{selectedQuiz.questionsCount}</p>
                        <p>Мое най-бързо
                            време: {selectedQuiz.personalBestTime ? `${Math.floor(selectedQuiz.personalBestTime / 60)}:${String(selectedQuiz.personalBestTime % 60).padStart(2, '0')} мин.` : 0}</p>
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
