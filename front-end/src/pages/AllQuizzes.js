import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/allQuizzes.css';
import {useLoading} from '../context/LoadingContext';

const AllQuizzes = ({ email, token }) => {
    const [quizzes, setQuizzes] = useState([]);
    const [selectedQuiz, setSelectedQuiz] = useState(null);
    const {setLoading} = useLoading();
    const [selectedCategory, setSelectedCategory] = useState('Всички');
    const [searchTerm, setSearchTerm] = useState('');
    const [sortConfig, setSortConfig] = useState({key: 'createdAt', direction: 'asc'});
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
            setLoading(false);
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
        let sortedQuizzes = quizzes.filter(quiz => {
            const matchesCategory = selectedCategory === 'Всички' || quiz.category === selectedCategory;
            const matchesSearchTerm = quiz.name.toLowerCase().includes(searchTerm.toLowerCase());
            return matchesCategory && matchesSearchTerm;
        });

        sortedQuizzes.sort((a, b) => {
            let valA, valB;
            switch (sortConfig.key) {
                case 'createdAt':
                    valA = new Date(a.createdAt);
                    valB = new Date(b.createdAt);
                    break;
                case 'personalBestXpGained':
                    valA = a.personalBestXpGained;
                    valB = b.personalBestXpGained;
                    break;
                case 'questionsCount':
                    valA = a.questionsCount;
                    valB = b.questionsCount;
                    break;
                case 'name':
                    valA = a.name.toLowerCase();
                    valB = b.name.toLowerCase();
                    break;
                case 'category':
                    valA = a.category.toLowerCase();
                    valB = b.category.toLowerCase();
                    break;
                default:
                    return 0;
            }

            if (sortConfig.direction === 'asc') {
                return valA > valB ? 1 : -1;
            } else {
                return valA < valB ? 1 : -1;
            }
        });
        return sortedQuizzes;
    };

    const requestSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({key, direction});
    };

    const categories = ['Всички', ...new Set(quizzes.map(quiz => quiz.category))];

    return (
        <div className="container all-quizzes-container align-items-center">
            <div className="filters-container mt-4 mx-auto">
                <div className="row mb-3">
                    <div className="col-md-6">
                        <label htmlFor="category" className="form-label">Категория:</label>
                        <select id="category" className="form-control" value={selectedCategory}
                                onChange={(e) => setSelectedCategory(e.target.value)}>
                            {categories.map(category => (
                                <option key={category} value={category}>{category}</option>
                            ))}
                        </select>
                    </div>
                    <div className="col-md-6">
                        <label htmlFor="search" className="form-label">Търсене по име:</label>
                        <input
                            type="text"
                            id="search"
                            className="form-control"
                            placeholder="Търсене..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>
            </div>
            <div className="quiz-list-header">
                <div className="header-item" onClick={() => requestSort('name')}>Име</div>
                <div className="header-item" onClick={() => requestSort('category')}>Категория</div>
                <div className="header-item" onClick={() => requestSort('questionsCount')}>Въпроси</div>
                <div className="header-item" onClick={() => requestSort('personalBestXpGained')}>Мой опит</div>
                <div className="header-item" onClick={() => requestSort('createdAt')}>Дата на създаване</div>
            </div>
            {sortQuizzes().map(quiz => {
                const statusClass = quiz.haveBeenPassed ? 'status-passed' :
                    quiz.haveBeenCompleted ? 'status-completed' : 'status-not-taken';

                return (
                    <div
                        className={`quiz-card ${statusClass}`}
                        key={quiz.quizId}
                        onClick={() => setSelectedQuiz(quiz)}
                        role="button"
                        tabIndex={0}
                        onKeyDown={(e) => e.key === 'Enter' && setSelectedQuiz(quiz)}
                    >
                        <div className="quiz-card-content">
                            <div className="quiz-detail">{quiz.name}</div>
                            <div className="quiz-detail">{quiz.category}</div>
                            <div className="quiz-detail">{quiz.questionsCount}</div>
                            <div className="quiz-detail">{quiz.personalBestXpGained ?? 0}/100</div>
                            <div className="quiz-detail">{new Date(quiz.createdAt).toLocaleDateString('bg-BG', {
                                day: '2-digit',
                                month: '2-digit',
                                year: 'numeric'
                            })}</div>
                        </div>
                    </div>
                );
            })}
            {selectedQuiz && (
                <div className="dialog-overlay" onClick={handleCloseModal}>
                    <dialog open className="quiz-dialog" onClick={e => e.stopPropagation()}>
                        <h3 className='text-center'>{selectedQuiz.name}</h3>
                        <p className='mt-2'>Категория: {selectedQuiz.category}</p>
                        <p>Въпроси: {selectedQuiz.questionsCount}</p>
                        <p>Мой опит: {selectedQuiz.personalBestXpGained ?? 0}/100</p>
                        <p>Дата на създаване: {new Date(selectedQuiz.createdAt).toLocaleDateString('bg-BG', {
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric'
                        })}</p>

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
