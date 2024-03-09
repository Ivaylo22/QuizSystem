import React, {useState, useEffect} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';
import "../styles/requestedQuizInfo.css";

const RequestedQuizInfo = ({token}) => {
    const {quizId} = useParams();
    const navigate = useNavigate();
    const [quiz, setQuiz] = useState(null);
    console.log(quiz)

    useEffect(() => {
        const fetchQuizInfo = async () => {
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
        };

        fetchQuizInfo();
    }, [quizId, token]);

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

    const approveQuiz = async () => {
        try {
            const response = await fetch(`http://localhost:8090/api/v1/quiz/approve`, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({id: quizId}),
            });

            if (!response.ok) {
                throw new Error('Failed to approve quiz');
            }

            await response.json();
            toast.success('Куизът е одобрен!');
            navigate('/');
        } catch (error) {
            console.error('Error:', error);
        }
    };


    if (!quiz) {
        return <div>Loading...</div>;
    }

    return (
        <div className="container requested-quiz-container">
            <div className="quiz-details mb-3">
                <h3>Заглавие: {quiz.title}</h3>
                <p>Категория: {quiz.category}</p>
                <p>Създаден от: {quiz.userEmail}</p>
            </div>
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
                <button className="btn-decline">Отхвърли</button>
                <button onClick={approveQuiz} className="btn-approve me-2"> Одобри</button>
            </div>
        </div>
    );
};

export default RequestedQuizInfo;