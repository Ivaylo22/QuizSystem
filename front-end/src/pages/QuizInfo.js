import React, {useState, useEffect} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import "../styles/requestedQuizInfo.css";
import {useLoading} from '../context/LoadingContext';

const QuizInfo = () => {
    const {quizId} = useParams();
    const navigate = useNavigate();
    const [quiz, setQuiz] = useState(null);
    const token = localStorage.getItem('token');

    const {setLoading} = useLoading();
    useEffect(() => {
        const fetchQuizInfo = async () => {
            setLoading(true);
            try {
                const response = await fetch(`http://localhost:8090/api/v1/quiz/get-by-id?quizId=${quizId}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch quiz details');
                }

                const data = await response.json();
                setQuiz(data);
            } catch (error) {
                console.error('Error:', error);
            }
            setTimeout(() => setLoading(false), 500);
        };

        fetchQuizInfo();
    }, [quizId, token, setLoading]);

    const translateQuestionType = (questionType) => {
        switch (questionType) {
            case 'SINGLE_ANSWER':
                return 'Един верен отговор';
            case 'MULTIPLE_ANSWER':
                return 'Няколко верни отговора';
            case 'OPEN':
                return 'Отворен отговор';
            default:
                return 'Неопределен тип въпрос';
        }
    };

    if (!quiz) {
        return <div>Loading...</div>;
    }

    return (
        <div className="container requested-quiz-container">
            {quiz.questions.map((question, qIndex) => (
                <div key={qIndex} className="question-container mb-4 p-3">
                    <h4 className="question-title">Въпрос {qIndex + 1} ({translateQuestionType(question.questionType)})</h4>
                    {question.image && (
                        <img src={question.image} alt={`Въпрос ${qIndex + 1}`}
                             className="img-fluid mb-2 question-image"/>
                    )}
                    <p className='question-content mb-2'>{question.question}</p>
                    <ul className="list-unstyled answers-list">
                        {question.answers.map((answer, aIndex) => (
                            <li key={aIndex}
                                className={`answer p-2 ${answer.isCorrect ? 'correct-answer' : 'bg-light'}`}>
                                {answer.content}
                            </li>
                        ))}
                    </ul>
                </div>
            ))}
            <div className="quiz-actions text-center">
                <button onClick={() => navigate('/my-quizzes')} className="btn-cancel">Назад</button>
            </div>
        </div>
    );
};

export default QuizInfo;
