import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import "../styles/createSolveChoice.css";
import {toast} from 'react-toastify';

const SolveChoice = () => {
    const navigate = useNavigate();
    const [showDialog, setShowDialog] = useState(false);
    const [accessKey, setAccessKey] = useState('');

    const token = localStorage.getItem("token");
    const email = localStorage.getItem("email");

    const handleStartTest = async () => {
        try {
            const response = await fetch(`http://localhost:8090/api/v1/test/get-by-access-key?accessKey=${accessKey}&userEmail=${email}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('Test not found');
            }
            const data = await response.json();
            if (data.alreadySolved) {
                toast.error('Вече сте решавали този тест');
            } else {
                navigate(`/solve-by-key/${accessKey}`);
            }
        } catch (error) {
            toast.error('Тест с този код не съществува.');
        }
    };

    return (
        <div className="create-choice-container text-center mt-5">
            <div className="button-row single-button-row">
                <div className="btn-container full-width">
                    <button className="btn btn-lg m-3 choice-button" onClick={() => setShowDialog(true)}>
                        Реши чрез код
                    </button>
                </div>
            </div>
            <div className="button-row double-button-row">
                <div className="btn-container half-width">
                    <button className="btn btn-lg m-3 choice-button" onClick={() => navigate('/quizzes')}>
                        Реши куиз
                    </button>
                    <div className="dropdown-description">
                        Куизовете предлагат вълнуващо и динамично изживяване,
                        където можеш да спечелиш опит, да постигнеш различни постижения и да се изкачиш в класацията.
                        Идеални за забавление и образование със състезателен елемент.
                    </div>
                </div>
                <div className="btn-container half-width">
                    <button className="btn btn-lg m-3 choice-button" onClick={() => navigate('/tests')}>
                        Реши тест
                    </button>
                    <div className="dropdown-description">
                        Тестовете са съсредоточени върху учебния процес и предоставят възможност за практикуване
                        и подготовка за училищни изпити и контролни.
                        Подходящи за ученици, които искат да подобрят знанията и уменията си в различни учебни
                        дисциплини.
                    </div>
                </div>
            </div>
            {showDialog && (
                <div className="dialog-overlay" onClick={() => setShowDialog(false)}>
                    <dialog open className="access-key-dialog" onClick={(e) => e.stopPropagation()}>
                        <h3>Въведи код за достъп</h3>
                        <input
                            type="text"
                            value={accessKey}
                            onChange={(e) => setAccessKey(e.target.value)}
                            placeholder="Код за достъп"
                            className="form-control"
                        />
                        <div className="dialog-buttons-container mt-3">
                            <button className="btn btn-cancel" onClick={() => setShowDialog(false)}>Затвори</button>
                            <button className="btn btn-start" onClick={handleStartTest}>Започни теста</button>
                        </div>
                    </dialog>
                </div>
            )}
        </div>
    );
};

export default SolveChoice;
