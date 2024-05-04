import React from 'react';
import {useNavigate} from 'react-router-dom';
import "../styles/createChoice.css";

const CreateChoice = () => {
    const navigate = useNavigate();

    return (
        <div className="create-choice-container text-center mt-5">
            <div className="button-row">
                <div className="btn-container">
                    <button className="btn btn-lg m-3 choice-button" onClick={() => navigate('/create-quiz')}>
                        Създай куиз
                    </button>
                    <div className="dropdown-description">
                        Куизовете предлагат вълнуващо и динамично изживяване,
                        където можеш да спечелиш опит, да постигнеш различни постижения и да се изкачиш в класацията.
                        Идеални за забавление и образование със състезателен елемент
                    </div>
                </div>
                <div className="btn-container">
                    <button className="btn btn-primary btn-lg m-3 choice-button"
                            onClick={() => navigate('/create-test')}>
                        Създай тест
                    </button>
                    <div className="dropdown-description">
                        Тестовете са съсредоточени върху учебния процес и предоставят възможност за практикуване
                        и подготовка за училищни изпити и контролни.
                        Подходящи за ученици, които искат да подобрят знанията и уменията си в различни учебни
                        дисциплини.
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CreateChoice;
