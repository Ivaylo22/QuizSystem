import React, { useState, useEffect } from 'react';
import QuizCard from '../components/QuizCard';
import '../styles/allQuizzes.css';

const AllQuizzes = ({ email, token }) => {
    const [quizzes, setQuizzes] = useState([]);

    useEffect(() => {
        const fetchQuizzes = async () => {
            try {
                const response = await fetch(`http://localhost:8090/api/v1/quiz/get-all-for-user?email=${encodeURIComponent(email)}`, {
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
                setQuizzes(data.quizModels);
            } catch (error) {
                console.error('Error:', error);
            }
        };

        fetchQuizzes();
    }, [email, token]);

    console.log(quizzes);

    return (
        <div className="container all-quizzes-container align-items-center">
            <div className="row quiz-header pt-2">
                <div className="col-5 header">Име</div>
                <div className="col-2 header">Категория</div>
                <div className="col-1 header">Въпроси</div>
                <div className="col-1 header">Средно време</div>
                <div className="col-1 header">Среден резултат</div>
                <div className="col-2 header">Статус</div>
            </div>
            {quizzes.map(quiz => (
                <QuizCard key={quiz.quizId} quiz={quiz} />
            ))}
        </div>
    );
};

export default AllQuizzes;
