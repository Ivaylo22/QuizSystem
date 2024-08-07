import React, {useState, useEffect, useCallback} from 'react';
import '../styles/myQuizzes.css';
import {useLoading} from '../context/LoadingContext';
import {useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';

const MyQuizzes = () => {
    const [quizzes, setQuizzes] = useState([]);
    const [selectedQuiz, setSelectedQuiz] = useState(null);
    const [sortConfig, setSortConfig] = useState({key: 'createdAt', direction: 'asc'});
    const [searchQuery, setSearchQuery] = useState('');
    const {setLoading} = useLoading();

    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');

    const fetchQuizzes = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/quiz/get-mine?userEmail=${encodeURIComponent(email)}`, {
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
            setQuizzes(data.quizModelList);
        } catch (error) {
            console.error('Error:', error);
        }
        setLoading(false);
    }, [token, email, setLoading]);

    useEffect(() => {
        fetchQuizzes();
    }, [fetchQuizzes]);

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({key, direction});
    };

    const sortedQuizzes = [...quizzes].sort((a, b) => {
        if (a[sortConfig.key] < b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
    });

    const handleSearch = (e) => {
        setSearchQuery(e.target.value);
    };

    const filteredQuizzes = sortedQuizzes.filter(quiz =>
        quiz.name.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const handleCloseModal = () => {
        setSelectedQuiz(null);
    };

    const handleArchiveQuiz = async (quizId) => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/quiz/archive`, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({id: quizId}),
            });
            if (!response.ok) {
                throw new Error('Failed to archive quiz.');
            }
            setQuizzes(prevQuizzes => prevQuizzes.map(quiz => quiz.quizId === quizId ? {
                ...quiz,
                status: 'ARCHIVED'
            } : quiz));
            toast.success('Куизът е архивиран успешно');
            handleCloseModal();
        } catch (error) {
            console.error('Error archiving quiz:', error);
            toast.error('Грешка при архивиране на куиз. Моля опитайте по-късно.');
        }
        setLoading(false);
    };

    const handleActivateQuiz = async (quizId) => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/quiz/active`, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({id: quizId}),
            });
            if (!response.ok) {
                throw new Error('Failed to activate quiz.');
            }
            setQuizzes(prevQuizzes => prevQuizzes.map(quiz => quiz.quizId === quizId ? {
                ...quiz,
                status: 'ACTIVE'
            } : quiz));
            toast.success('Куизът е активиран успешно');
            handleCloseModal();
        } catch (error) {
            console.error('Error activating quiz:', error);
            toast.error('Грешка при активиране на куиз. Моля опитайте по-късно.');
        }
        setLoading(false);
    };

    const translateStatus = (status) => {
        switch (status) {
            case 'DECLINED':
                return 'Отказан';
            case 'ACTIVE':
                return 'Активен';
            case 'REQUESTED':
                return 'Заявен';
            case 'ARCHIVED':
                return 'Архивиран';
            default:
                return status;
        }
    };

    const navigate = useNavigate();

    const handleViewQuiz = (quizId) => {
        navigate(`/quiz-info/${quizId}`);
    };

    return (
        <div className="container my-quizzes-container">
            <h2 className="text-center mt-4">Моите Куизове</h2>
            <div className="search-container mb-3">
                <input
                    type="text"
                    className="form-control"
                    placeholder="Търсене по заглавие"
                    value={searchQuery}
                    onChange={handleSearch}
                />
            </div>
            <div className="quiz-list-header mt-3">
                <div className="header-item" onClick={() => handleSort('name')}>Заглавие</div>
                <div className="header-item" onClick={() => handleSort('category')}>Категория</div>
                <div className="header-item" onClick={() => handleSort('attemptsCount')}>Брой Опити</div>
                <div className="header-item" onClick={() => handleSort('createdAt')}>Дата на Създаване</div>
                <div className="header-item" onClick={() => handleSort('status')}>Статус</div>
            </div>
            {filteredQuizzes.length === 0 ? (
                <div className="no-quizzes">Нямате създадени куизове!</div>
            ) : (
                filteredQuizzes.map(quiz => (
                    <div
                        className="quiz-card"
                        key={quiz.quizId}
                        onClick={() => setSelectedQuiz(quiz)}
                        role="button"
                        tabIndex={0}
                        onKeyDown={(e) => e.key === 'Enter' && setSelectedQuiz(quiz)}
                    >
                        <div className="quiz-card-content">
                            <div className="quiz-detail">{quiz.name}</div>
                            <div className="quiz-detail">{quiz.category}</div>
                            <div className="quiz-detail">{quiz.attemptsCount}</div>
                            <div className="quiz-detail">{new Date(quiz.createdAt).toLocaleDateString('bg-BG', {
                                day: '2-digit',
                                month: '2-digit',
                                year: 'numeric'
                            })}</div>
                            <div className="quiz-detail">{translateStatus(quiz.status)}</div>
                        </div>
                    </div>
                ))
            )}
            {selectedQuiz && (
                <div className="dialog-overlay" onClick={handleCloseModal}>
                    <dialog open className="quiz-dialog" onClick={e => e.stopPropagation()}>
                        <h3 className='text-center'>{selectedQuiz.name}</h3>
                        <p className='mt-2'>Категория: {selectedQuiz.category}</p>
                        <p>Брой Опити: {selectedQuiz.attemptsCount}</p>
                        <p>Дата на Създаване: {new Date(selectedQuiz.createdAt).toLocaleDateString()}</p>
                        <p>Статус: {translateStatus(selectedQuiz.status)}</p>
                        <div className="dialog-buttons-container">
                            <button className='btn-cancel' onClick={handleCloseModal}>Затвори</button>
                            <button className='btn-start' onClick={() => handleViewQuiz(selectedQuiz.quizId)}>Виж куиз
                            </button>
                            {selectedQuiz.status === 'ACTIVE' && (
                                <button className='btn-start  btn-view'
                                        onClick={() => handleArchiveQuiz(selectedQuiz.quizId)}>Архивирай</button>
                            )}
                            {selectedQuiz.status === 'ARCHIVED' && (
                                <button className='btn-start btn-view'
                                        onClick={() => handleActivateQuiz(selectedQuiz.quizId)}>Активирай</button>
                            )}
                        </div>
                    </dialog>
                </div>
            )}
        </div>
    );
};

export default MyQuizzes;
