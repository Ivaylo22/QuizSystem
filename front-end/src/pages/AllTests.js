import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import '../styles/allTests.css';
import {useLoading} from '../context/LoadingContext';

const AllTests = () => {
    const [tests, setTests] = useState([]);
    const [selectedTest, setSelectedTest] = useState(null);
    const [selectedSubject, setSelectedSubject] = useState('Всички');
    const [selectedGrade, setSelectedGrade] = useState('Всички');
    const [sortConfig, setSortConfig] = useState({key: '', direction: ''});
    const {setLoading} = useLoading();
    const navigate = useNavigate();

    const token = localStorage.getItem('token');

    useEffect(() => {
        const fetchTests = async () => {
            setLoading(true);
            try {
                const response = await fetch('http://localhost:8090/api/v1/test/get-public-tests', {
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
    }, [token, setLoading]);

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
        setSelectedTest(null);
    };

    const handleStartTest = (testId) => {
        navigate(`/solve-test/${testId}`);
    };

    const filterTests = () => {
        return tests.filter(test =>
            (selectedSubject === 'Всички' || test.subject === selectedSubject) &&
            (selectedGrade === 'Всички' || test.grade.toString() === selectedGrade)
        );
    };

    const sortTests = (key) => {
        let direction = 'ascending';
        if (sortConfig.key === key && sortConfig.direction === 'ascending') {
            direction = 'descending';
        }

        const sortedTests = [...tests].sort((a, b) => {
            if (a[key] < b[key]) {
                return direction === 'ascending' ? -1 : 1;
            }
            if (a[key] > b[key]) {
                return direction === 'ascending' ? 1 : -1;
            }
            return 0;
        });

        setTests(sortedTests);
        setSortConfig({key, direction});
    };

    const subjects = ['Всички', ...new Set(tests.map(test => test.subject))];
    const grades = ['Всички', ...Array.from({length: 12}, (_, i) => (i + 1).toString())];

    const filteredTests = filterTests();

    return (
        <div className="container all-tests-container align-items-center">
            <div className="filters-container mt-4 mx-auto">
                <div className="row mb-3">
                    <div className="col-md-6">
                        <select className="form-control" value={selectedSubject}
                                onChange={(e) => setSelectedSubject(e.target.value)}>
                            {subjects.map(subject => (
                                <option key={subject} value={subject}>{subject}</option>
                            ))}
                        </select>
                    </div>
                    <div className="col-md-6">
                        <select className="form-control" value={selectedGrade}
                                onChange={(e) => setSelectedGrade(e.target.value)}>
                            {grades.map(grade => (
                                <option key={grade} value={grade}>{grade}</option>
                            ))}
                        </select>
                    </div>
                </div>
            </div>
            <div className="test-list-header">
                <div className="header-item" onClick={() => sortTests('title')}>Заглавие</div>
                <div className="header-item" onClick={() => sortTests('grade')}>Клас</div>
                <div className="header-item" onClick={() => sortTests('subject')}>Предмет</div>
                <div className="header-item" onClick={() => sortTests('minutesToSolve')}>Минутите за решаване</div>
                <div className="header-item" onClick={() => sortTests('creatorEmail')}>Създател</div>
            </div>
            {filteredTests.length === 0 ? (
                <div className="no-tests">За съжаление няма такива тестове!</div>
            ) : (
                filteredTests.map(test => (
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
                            <div className="test-detail">{test.minutesToSolve}</div>
                            <div className="test-detail">{test.creatorEmail}</div>
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
                        <p>Минутите за решаване: {selectedTest.minutesToSolve}</p>
                        <p>Създател: {selectedTest.creatorEmail}</p>

                        <p className='mt-2 text-center'>При натискане на бутона "Започни" ще започне да тече вашето
                            време</p>
                        <div className="dialog-buttons-container">
                            <button className='btn-cancel' onClick={handleCloseModal}>Затвори</button>
                            <button className='btn-start' onClick={() => handleStartTest(selectedTest.id)}>Започни
                            </button>
                        </div>
                    </dialog>
                </div>
            )}
        </div>
    );
};

export default AllTests;
