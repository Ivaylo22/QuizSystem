import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import '../styles/myTests.css';
import {useLoading} from '../context/LoadingContext';

const MyTests = () => {
    const [tests, setTests] = useState([]);
    const [selectedTest, setSelectedTest] = useState(null);
    const {setLoading} = useLoading();
    const navigate = useNavigate();

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

    const handleCloseModal = () => {
        setSelectedTest(null);
    };

    const handleViewAttempts = (testId) => {
        navigate(`/test-attempts/${testId}`);
    };

    const handleGenerateAccessKey = async (testId) => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8090/api/v1/test/generate-access-key', {
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
            alert(`Access Key: ${data.accessKey}`);
        } catch (error) {
            console.error('Error generating access key:', error);
            alert('Failed to generate access key.');
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
                <div className="header-item">Заглавие</div>
                <div className="header-item">Клас</div>
                <div className="header-item">Предмет</div>
                <div className="header-item">Статус</div>
                <div className="header-item">Брой Опити</div>
                <div className="header-item">Дата на Създаване</div>
            </div>
            {tests.length === 0 ? (
                <div className="no-tests">Нямате създадени тестове!</div>
            ) : (
                tests.map(test => (
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
                            <div className="test-detail">{new Date(test.createdAt).toLocaleDateString()}</div>
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
                        <div className="dialog-buttons-container">
                            <button className='btn-cancel' onClick={handleCloseModal}>Затвори</button>
                            {selectedTest.status === 'PUBLIC' ? (
                                <button className='btn-view'
                                        onClick={() => navigate(`/test-summary/${selectedTest.id}`)}>Виж
                                    Резултати</button>
                            ) : (
                                <>
                                    <button className='btn-start'
                                            onClick={() => handleGenerateAccessKey(selectedTest.id)}>Генерирай Достъп
                                    </button>
                                    <button className='btn-view' onClick={() => handleViewAttempts(selectedTest.id)}>Виж
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
