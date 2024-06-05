import React from 'react';
import {useNavigate} from 'react-router-dom';
import "../styles/createCreateChoice.css";

const GetMineChoice = () => {
    const navigate = useNavigate();

    return (
        <div className="create-create-choice-container text-center mt-5">
            <div className="create-button-row">
                <div className="create-btn-container">
                    <button className="btn btn-lg m-3 create-choice-button" onClick={() => navigate('/my-quizzes')}>
                        Моите куизове
                    </button>
                </div>
                <div className="create-btn-container">
                    <button className="btn btn-lg m-3 create-choice-button" onClick={() => navigate('/my-tests')}>
                        Моите тестове
                    </button>
                </div>
            </div>
        </div>
    );
};

export default GetMineChoice;
