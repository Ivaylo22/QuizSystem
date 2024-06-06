import React, {useState, useEffect, useCallback} from 'react';
import '../styles/leaderboard.css';
import {useLoading} from '../context/LoadingContext';
import {toast} from 'react-toastify';

const Leaderboard = () => {
    const [leaderboard, setLeaderboard] = useState([]);
    const [sortConfig, setSortConfig] = useState({key: 'level', direction: 'asc'});
    const [searchQuery, setSearchQuery] = useState('');
    const [initialLoad, setInitialLoad] = useState(true); // Flag to track the initial load
    const {setLoading} = useLoading();
    const token = localStorage.getItem('token');
    const currentEmail = localStorage.getItem('email');

    const fetchLeaderboard = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/quiz/get-leaderboard`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('Failed to fetch leaderboard');
            }
            const data = await response.json();
            setLeaderboard(data.leaderboardModelList);
        } catch (error) {
            console.error('Error:', error);
            toast.error('Failed to fetch leaderboard');
        }
        setLoading(false);
    }, [setLoading, token]);

    useEffect(() => {
        fetchLeaderboard();
    }, [fetchLeaderboard]);

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({key, direction});
        setInitialLoad(false); // Disable initial load flag after the first sort
    };

    const sortedLeaderboard = [...leaderboard].sort((a, b) => {
        if (initialLoad) {
            if (a.userEmail === currentEmail) return -1;
            if (b.userEmail === currentEmail) return 1;
        }
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

    const filteredLeaderboard = sortedLeaderboard.filter(user =>
        user.userEmail.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
        <div className="container leaderboard-container">
            <h2 className="text-center mt-4">Лидерборд</h2>
            <div className="search-container mb-3">
                <input
                    type="text"
                    className="form-control"
                    placeholder="Търсене по имейл"
                    value={searchQuery}
                    onChange={handleSearch}
                />
            </div>
            <div className="leaderboard-header mt-3">
                <div className="header-item" onClick={() => handleSort('userEmail')}>Имейл</div>
                <div className="header-item" onClick={() => handleSort('level')}>Ниво</div>
                <div className="header-item" onClick={() => handleSort('achievementPoints')}>Точки за постижения</div>
                <div className="header-item" onClick={() => handleSort('solvedQuizzesCount')}>Брой решени куизове</div>
                <div className="header-item" onClick={() => handleSort('registeredAt')}>Дата на регистрация</div>
            </div>
            {filteredLeaderboard.length === 0 ? (
                <div className="no-users">Няма намерени потребители!</div>
            ) : (
                filteredLeaderboard.map(user => (
                    <div
                        className={`leaderboard-card ${user.userEmail === currentEmail ? 'current-user' : ''}`}
                        key={user.userEmail}
                    >
                        <div className="leaderboard-card-content">
                            <div className="leaderboard-detail">{user.userEmail}</div>
                            <div className="leaderboard-detail">{user.level}</div>
                            <div className="leaderboard-detail">{user.achievementPoints}</div>
                            <div className="leaderboard-detail">{user.solvedQuizzesCount}</div>
                            <div
                                className="leaderboard-detail">{new Date(user.registeredAt).toLocaleDateString('bg-BG', {
                                day: '2-digit',
                                month: '2-digit',
                                year: 'numeric'
                            })}</div>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
};

export default Leaderboard;
