import React, {useState, useEffect, useCallback} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';
import '../styles/testAttempts.css';
import {useLoading} from '../context/LoadingContext';

const TestAttempts = () => {
    const {testId} = useParams();
    const navigate = useNavigate();
    const [attempts, setAttempts] = useState([]);
    const [sortConfig, setSortConfig] = useState({key: 'solvedAt', direction: 'asc'});
    const [searchQuery, setSearchQuery] = useState('');
    const {setLoading} = useLoading();
    const token = localStorage.getItem('token');

    const fetchAttempts = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/test/get-test-attempts?testId=${testId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Failed to fetch test attempts');
            }

            const data = await response.json();
            setAttempts(data.attempts);
        } catch (error) {
            console.error('Error:', error);
            toast.error('Failed to fetch test attempts');
        }
        setLoading(false);
    }, [testId, token, setLoading]);

    useEffect(() => {
        fetchAttempts();
    }, [fetchAttempts]);

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({key, direction});
    };

    const sortedAttempts = [...attempts].sort((a, b) => {
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

    const filteredAttempts = sortedAttempts.filter(attempt =>
        attempt.userEmail.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
        <div className="container test-attempts-container">
            <h2 className="text-center mt-4">Опити за Тест</h2>
            <div className="search-container mb-3">
                <input
                    type="text"
                    className="form-control"
                    placeholder="Търсене по имейл"
                    value={searchQuery}
                    onChange={handleSearch}
                />
            </div>
            <div className="test-attempts-header mt-3">
                <div className="header-item" onClick={() => handleSort('testName')}>Име на Тест</div>
                <div className="header-item" onClick={() => handleSort('userEmail')}>Имейл</div>
                <div className="header-item" onClick={() => handleSort('solvedAt')}>Дата на Решаване</div>
                <div className="header-item" onClick={() => handleSort('grade')}>Оценка</div>
            </div>
            {filteredAttempts.length === 0 ? (
                <div className="no-attempts">Няма намерени опити!</div>
            ) : (
                filteredAttempts.map(attempt => (
                    <div className="attempt-card" key={`${attempt.userId}-${attempt.solvedAt}`}>
                        <div className="attempt-detail">{attempt.testName}</div>
                        <div className="attempt-detail">{attempt.userEmail}</div>
                        <div className="attempt-detail">
                            {new Date(attempt.solvedAt).toLocaleDateString('bg-BG', {
                                day: '2-digit',
                                month: '2-digit',
                                year: 'numeric'
                            })} {new Date(attempt.solvedAt).toLocaleTimeString('bg-BG', {
                            hour: '2-digit',
                            minute: '2-digit'
                        })}
                        </div>
                        <div className="attempt-detail">{attempt.grade}</div>
                    </div>
                ))
            )}
            <div className="text-center mt-4">
                <button onClick={() => navigate('/my-tests')} className="btn btn-primary btn-back">Назад</button>
            </div>
        </div>
    );
};

export default TestAttempts;
