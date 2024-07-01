import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import '../styles/myTests.css';
import {useLoading} from '../context/LoadingContext';
import {toast} from 'react-toastify';

const MyTests = () => {
    const [tests, setTests] = useState([]);
    const [selectedTest, setSelectedTest] = useState(null);
    const {setLoading} = useLoading();
    const navigate = useNavigate();

    const [sortConfig, setSortConfig] = useState({key: 'createdAt', direction: 'asc'});

    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');

    useEffect(() => {
        const fetchTests = async () => {
            setLoading(true);
            try {
                const response = await fetch(`http://localhost:8090/api/v1/test/get-mine?userEmail=${encodeURIComponent(email)}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });
                if (!response.ok) {
                    throw new Error('Failed to fetch tests.');
                }
                const data = await response.json();
                setTests(data.tests);
            } catch (error) {
                console.error('Error:', error);
            }
            setLoading(false);
        };

        fetchTests();
    }, [token, email, setLoading]);

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({key, direction});
    };

    const sortedTests = [...tests].sort((a, b) => {
        if (a[sortConfig.key] < b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
    });

    const handleCloseModal = () => {
        setSelectedTest(null);
    };

    const handleViewAttempts = (testId) => {
        navigate(`/test-attempts/${testId}`);
    };

    const handleGenerateAccessKey = async (testId) => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8090/api/v1/test/generate-key', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({testId}),
            });
            if (!response.ok) {
                throw new Error('Failed to generate access key.');
            }
            const data = await response.json();
            setTests(prevTests => prevTests.map(test => test.id === testId ? {
                ...test,
                accessKey: data.accessKey
            } : test));
            toast.success(`Код за достъп ${data.accessKey}`);
            handleCloseModal();
        } catch (error) {
            console.error('Error generating access key:', error);
            toast.error('Грешка при създаване на ключ. Моля опитайте по-късно');
        }
        setLoading(false);
    };

    const handleDeleteAccessKey = async (testId) => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8090/api/v1/test/delete-key', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({testId}),
            });
            if (!response.ok) {
                throw new Error('Failed to delete access key.');
            }
            setTests(prevTests => prevTests.map(test => test.id === testId ? {...test, accessKey: null} : test));
            toast.success('Успешно премахнат код за достъп');
            handleCloseModal();
        } catch (error) {
            console.error('Error deleting access key:', error);
            toast.error('Грешка при изтриване на ключ Моля опитайте по-късно.');
        }
        setLoading(false);
    };

    const transformStatus = (status) => {
        return status === 'PUBLIC' ? 'публичен' : 'частен';
    };

    return (
        <div className="container my-tests-container">
            <h2 className="text-center mt-4">Моите Тестове</h2>
            <div className="test-list-header mt-3">
                <div className="header-item" onClick={() => handleSort('title')}>Заглавие</div>
                <div className="header-item" onClick={() => handleSort('grade')}>Клас</div>
                <div className="header-item" onClick={() => handleSort('subject')}>Предмет</div>
                <div className="header-item" onClick={() => handleSort('status')}>Статус</div>
                <div className="header-item" onClick={() => handleSort('attemptsCount')}>Брой Опити</div>
                <div className="header-item" onClick={() => handleSort('createdAt')}>Дата на Създаване</div>
            </div>
            {sortedTests.length === 0 ? (
                <div className="no-tests">Нямате създадени тестове!</div>
            ) : (
                sortedTests.map(test => (
                    <div
                        className="test-card"
                        key={test.id}
                        onClick={() => setSelectedTest(test)}
                        role="button"
                        tabIndex={0}
                        onKeyDown={(e) => e.key === 'Enter' && setSelectedTest(test)}
                    >
                        <div className="test-card-content">
                            <div className="test-detail">{test.title}</div>
                            <div className="test-detail">{test.grade}</div>
                            <div className="test-detail">{test.subject}</div>
                            <div className="test-detail">{transformStatus(test.status)}</div>
                            <div className="test-detail">{test.attemptsCount}</div>
                            <div className="test-detail">{new Date(test.createdAt).toLocaleDateString('bg-BG', {
                                day: '2-digit',
                                month: '2-digit',
                                year: 'numeric'
                            })}</div>
                        </div>
                    </div>
                ))
            )}
            {selectedTest && (
                <div className="dialog-overlay" onClick={handleCloseModal}>
                    <dialog open className="test-dialog" onClick={e => e.stopPropagation()}>
                        <h3 className='text-center'>{selectedTest.title}</h3>
                        <p className='mt-2'>Клас: {selectedTest.grade}</p>
                        <p>Предмет: {selectedTest.subject}</p>
                        <p>Статус: {transformStatus(selectedTest.status)}</p>
                        <p>Брой Опити: {selectedTest.attemptsCount}</p>
                        <p>Дата на Създаване: {new Date(selectedTest.createdAt).toLocaleDateString()}</p>
                        {selectedTest.accessKey && (
                            <p>Код за достъп: {selectedTest.accessKey}</p>
                        )}
                        <div className="dialog-buttons-container">
                            <button className='btn-cancel' onClick={handleCloseModal}>Затвори</button>
                            {selectedTest.status === 'PUBLIC' ? (
                                <button className='btn-view btn-start'
                                        onClick={() => navigate(`/test-summary/${selectedTest.id}`)}>Виж
                                    Резултати</button>
                            ) : (
                                <>
                                    {selectedTest.accessKey ? (
                                        <button className='btn-start'
                                                onClick={() => handleDeleteAccessKey(selectedTest.id)}>Изтрий
                                            Достъп</button>
                                    ) : (
                                        <button className='btn-start'
                                                onClick={() => handleGenerateAccessKey(selectedTest.id)}>Генерирай
                                            Достъп</button>
                                    )}
                                    <button className='btn-view btn-start'
                                            onClick={() => handleViewAttempts(selectedTest.id)}>Виж
                                        Опити
                                    </button>
                                </>
                            )}
                        </div>
                    </dialog>
                </div>
            )}
        </div>
    );
};

export default MyTests;

