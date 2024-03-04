import React, {useState, useEffect} from 'react';
import {toast} from 'react-toastify';
import RequestedQuiz from '../components/RequestedQuiz';

const RequestedQuizzes = ({token}) => {
    const [quizzes, setQuizzes] = useState([]);

    useEffect(() => {
        const fetchQuizzes = async () => {
            try {
                const response = await fetch(`http://localhost:8090/api/v1/quiz/get-requested`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });

                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }

                const data = await response.json();
                console.log(data);
                setQuizzes(data.quizzes);
            } catch (error) {
                console.error('Failed to fetch quizzes:', error);
                toast.error('Неуспешно зареждане на куизове.');
            }
        };

        fetchQuizzes();
    }, [token]);

    return (
        <div>
            <RequestedQuiz quizzes={quizzes}/>
        </div>
    );
};

export default RequestedQuizzes;