import React from 'react';
import {useNavigate} from 'react-router-dom';
import '../styles/requestedQuiz.css';

const RequestedQuiz = ({quizzes}) => {
    const navigate = useNavigate();

    const handleShowDetails = (quizId) => {
        navigate(`/requested/${quizId}`);
    };

    return (
        <div className="requested-quizzes-container">
            <h2 className="quizzes-title">Заявени Куизове</h2>
            {quizzes.length > 0 ? (
                <div className="list-group">
                    {quizzes.map((quiz, index) => (
                        <div key={index}
                             className="list-group-item d-flex justify-content-between align-items-center mb-3">
                            <div>
                                <h5 className="mb-1 quiz-title">{quiz.name}</h5>
                                <p className="mb-1 quiz-detail">Категория: <span
                                    className="quiz-info">{quiz.category}</span></p>
                                <p>Заявен от: <span className="quiz-info">{quiz.userEmail}</span></p>
                            </div>
                            <button
                                onClick={() => handleShowDetails(quiz.quizId)}
                                className="btn btn-details">
                                Виж Детайли
                            </button>
                        </div>
                    ))}
                </div>
            ) : (
                <p className="text-center no-quizzes">Няма заявени куизове.</p>
            )}
        </div>
    );
};

export default RequestedQuiz;
