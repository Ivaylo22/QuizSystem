import React, {useState, useEffect} from 'react';
import {toast} from 'react-toastify';
import RequestedQuiz from '../components/RequestedQuiz';
import {useLoading} from '../context/LoadingContext';

const RequestedQuizzes = ({token}) => {
    const [quizzes, setQuizzes] = useState([]);
    const {setLoading} = useLoading();

    useEffect(() => {
        const fetchQuizzes = async () => {
            setLoading(true);
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
                setQuizzes(data.quizzes);
            } catch (error) {
                console.error('Failed to fetch quizzes:', error);
                toast.error('Неуспешно зареждане на куизове.');
            }
            setTimeout(() => setLoading(false), 500);
        };

        fetchQuizzes();
    }, [token, setLoading]);

    return (
        <div>
            <RequestedQuiz quizzes={quizzes}/>
        </div>
    );
};

export default RequestedQuizzes;