import React, { useState, useRef, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import "../styles/createQuiz.css";

const CreateQuiz = ({ email, token }) => {
    const [categories, setCategories] = useState([]);
    const [isOtherCategory, setIsOtherCategory] = useState(false);
    const [otherCategory, setOtherCategory] = useState('');
    const [problematicQuestions, setProblematicQuestions] = useState([]);
    const [activeQuestionIndex, setActiveQuestionIndex] = useState(null);
    const navigate = useNavigate();
    const hasNavigated = useRef(false);

    const fetchCategories = useCallback(async () => {
        try {
            const response = await fetch(`http://localhost:8090/api/v1/quiz/categories`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            const data = await response.json();
            setCategories(data.categories.concat('Друга категория'));
        } catch (error) {
            console.error('Failed to fetch categories:', error);
        }
    }, [token]);

    useEffect(() => {
        if (!token && !hasNavigated.current) {
            toast.error(`Създаването на куизове е само за вписани потребители`);
            navigate('/login');
            hasNavigated.current = true;
        }
        fetchCategories();
    }, [token, navigate, fetchCategories]);

    const [quiz, setQuiz] = useState({
        title: '',
        category: 'Обща култура',
        userEmail: email,
        questions: new Array(5).fill(null).map(() => ({
            question: '',
            questionType: 'SINGLE_ANSWER',
            answers: [{ content: '', isCorrect: false }],
            image: null
        })),
    });

    const handleFileChange = (questionIndex, event) => {
        const newQuestions = [...quiz.questions];
        newQuestions[questionIndex].image = event.target.files[0];
        setQuiz({ ...quiz, questions: newQuestions });
    };

    const uploadFile = async (file, questionId) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('questionId', questionId);
        const response = await fetch('http://localhost:8090/api/v1/upload-question-image', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            body: formData,
        });

        if (!response.ok) {
            throw new Error('Failed to upload image.');
        }

        const imageUrl = await response.text();

        return imageUrl;
    };

    const handleCategoryChange = (e) => {
        const value = e.target.value;
        setIsOtherCategory(value === 'Друга категория');
        if (value !== 'Друга категория') {
            setQuiz({ ...quiz, category: value });
        }
    };

    const handleOtherCategoryInput = (e) => {
        const value = e.target.value;
        setOtherCategory(value);
        setQuiz({ ...quiz, category: value });
    };

    const handleChange = (e, index, type, answerIndex = null) => {
        let updatedQuiz = { ...quiz };
        switch (type) {
            case 'title':
                updatedQuiz.title = e.target.value;
                break;
            case 'question':
                updatedQuiz.questions[index].question = e.target.value;
                break;
            case 'answer':
                updatedQuiz.questions[index].answers[answerIndex].content = e.target.value;
                break;
            case 'questionType':
                updatedQuiz.questions[index].questionType = e.target.value;
                if (e.target.value === 'OPEN') {
                    updatedQuiz.questions[index].answers = [{ content: '', isCorrect: true }];
                } else {
                    updatedQuiz.questions[index].answers = updatedQuiz.questions[index].answers.map(answer => ({ ...answer, isCorrect: false }));
                }
                break;
            case 'toggleCorrect':
                if (updatedQuiz.questions[index].questionType === 'SINGLE_ANSWER') {
                    updatedQuiz.questions[index].answers = updatedQuiz.questions[index].answers.map((answer, i) => ({ ...answer, isCorrect: i === answerIndex }));
                } else {
                    updatedQuiz.questions[index].answers[answerIndex].isCorrect = !updatedQuiz.questions[index].answers[answerIndex].isCorrect;
                }
                break;
            default:
                break;
        }
        setQuiz(updatedQuiz);
    };

    const addAnswer = (questionIndex) => {
        const updatedQuestions = quiz.questions.map((question, qIndex) =>
            qIndex === questionIndex ? {
                ...question,
                answers: [...question.answers, { content: '', isCorrect: false }]
            } : question
        );
        setQuiz({ ...quiz, questions: updatedQuestions });
    };

    const removeAnswer = (questionIndex, answerIndex) => {
        const updatedQuestions = quiz.questions.map((question, qIndex) =>
            qIndex === questionIndex ? {
                ...question,
                answers: question.answers.filter((_, aIndex) => aIndex !== answerIndex)
            } : question
        );
        setQuiz({ ...quiz, questions: updatedQuestions });
    };

    const handleRemoveFile = (qIndex) => {
        setQuiz((prevQuiz) => {
            const updatedQuestions = [...prevQuiz.questions];
            updatedQuestions[qIndex] = {
                ...updatedQuestions[qIndex],
                image: null
            };
            return { ...prevQuiz, questions: updatedQuestions };
        });

        document.getElementById(`questionImage${qIndex}`).value = '';
    };

    const addQuestion = () => {
        setQuiz((prevQuiz) => ({
            ...prevQuiz,
            questions: prevQuiz.questions.concat({
                question: '',
                questionType: 'SINGLE_ANSWER',
                answers: [{ content: '', isCorrect: false }],
                image: null
            })
        }));
    };

    const removeQuestion = (index) => {
        if (quiz.questions.length > 5) {
            setQuiz(prevQuiz => ({
                ...prevQuiz,
                questions: prevQuiz.questions.filter((_, i) => i !== index),
            }));
        } else {
            toast.error('Куизът не може да съдържа по-малко от 5 въпроса.');
        }
    };

    const validateQuiz = () => {
        const newProblematicQuestions = [];

        if (quiz.title.trim() === '') {
            toast.error('Моля, въведете заглавие на куиза.');
            return false;
        }

        quiz.questions.forEach((question, index) => {
            let hasError = false;
            if (!question.question.trim()) {
                hasError = true;
            }

            const correctAnswers = question.answers.filter(a => a.isCorrect);
            if (question.questionType !== 'OPEN' && correctAnswers.length === 0) {
                hasError = true;
            }

            if (question.questionType === 'OPEN' && correctAnswers[0].content.trim() === '') {
                hasError = true;
            }

            if (hasError) {
                newProblematicQuestions.push(index);
            }
        });

        setProblematicQuestions(newProblematicQuestions);
        return newProblematicQuestions.length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (isOtherCategory) {
            if (!otherCategory.trim()) {
                toast.error('Моля, въведете категория.');
                return;
            } else if (!/^[А-Яа-яЁё\s]+$/i.test(otherCategory.trim())) {
                toast.error('Моля, използвайте само кирилица за категорията.');
                return;
            }
        }

        if (!validateQuiz()) {
            return;
        }

        try {
            const questionsWithoutImages = quiz.questions.map(({ image, ...rest }) => rest);
            const quizToSubmit = { ...quiz, questions: questionsWithoutImages };

            const response = await fetch('http://localhost:8090/api/v1/quiz/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(quizToSubmit),
            });
            const { questionIds } = await response.json();

            const uploadPromises = quiz.questions.map((question, index) => {
                if (question.image) {
                    return uploadFile(question.image, questionIds[index]);
                }
                return Promise.resolve();
            });

            await Promise.all(uploadPromises);

            toast.success('Успешно създаден куиз. На изчакване за одобрение');
            navigate('/');
        } catch (error) {
            console.error('Error during quiz creation:', error);
        }
    };

    return (
        <div className="create-quiz-container">
            <form onSubmit={handleSubmit} className="quiz-form">
                <div className="form-group quiz-title-group">
                    <input type="text" className="form-control mb-3 quiz-title-input" placeholder="Заглавие на куиза" value={quiz.title} onChange={(e) => handleChange(e, null, 'title')} />

                    <label htmlFor="quiz-category-select">Категория:</label>
                    <select className="form-control mb-3 quiz-category-select mt-2" id="quiz-category-select" value={isOtherCategory ? 'Друга категория' : quiz.category} onChange={handleCategoryChange}>
                        {categories.map((category, index) => (
                            <option key={index} value={category}>{category}</option>
                        ))}
                    </select>

                    {isOtherCategory && (
                        <input type="text" className="form-control quiz-other-category-input" placeholder="Въведете категория" value={otherCategory} onChange={handleOtherCategoryInput} />
                    )}
                </div>
                {quiz.questions.map((question, qIndex) => (
                    <div className={`question-container ${problematicQuestions.includes(qIndex) ? 'border-danger' : ''}`} key={qIndex}>
                        <div className="mb-3">
                            <h4 className="fw-bold mb-3 question-number">Въпрос {qIndex + 1}</h4>
                            <label htmlFor={`questionImage${qIndex}`} className="form-label">Снимка към въпроса:</label>
                            <input
                                type="file"
                                id={`questionImage${qIndex}`}
                                name="questionImage"
                                className="form-control-file"
                                onChange={(e) => handleFileChange(qIndex, e)}
                                accept="image/*"
                            />
                            {quiz.questions[qIndex].image && (
                                <button
                                    type="button"
                                    className="remove-answer-btn mt-2 ml-5"
                                    onClick={() => handleRemoveFile(qIndex)}
                                >
                                    Премахване на снимка
                                </button>
                            )}
                        </div>
                        <select className="question-type-select" value={question.questionType} onChange={(e) => handleChange(e, qIndex, 'questionType')}>
                            <option value="SINGLE_ANSWER">Един верен отговор</option>
                            <option value="MULTIPLE_ANSWER">Няколко верни отговора</option>
                            <option value="OPEN">Отворен отговор</option>
                        </select>
                        <textarea className="question-textarea" placeholder="Въведи въроса тук" value={question.question} onChange={(e) => handleChange(e, qIndex, 'question')} />
                        {quiz.questions[qIndex].answers.map((answer, aIndex) => (
                            <div className="answer-container" key={aIndex}>
                                <button
                                    type="button"
                                    className={`mark-correct-btn ${answer.isCorrect ? 'correct' : ''}`}
                                    onClick={() => handleChange(null, qIndex, 'toggleCorrect', aIndex)}
                                ></button>
                                <input
                                    type="text"
                                    className="answer-input"
                                    placeholder="Въведи отговор"
                                    value={answer.content}
                                    onChange={(e) => handleChange(e, qIndex, 'answer', aIndex)}
                                />
                                <button
                                    type="button"
                                    className="remove-answer-btn"
                                    onClick={() => removeAnswer(qIndex, aIndex)}
                                ></button>
                            </div>
                        ))}
                        <div className="buttons-container">
                            {question.questionType !== 'OPEN' && (
                                <button type="button" className="add-answer-btn" onClick={() => addAnswer(qIndex)}>
                                    Добави отговор
                                </button>
                            )}
                            <button type="button" className="remove-question-btn" onClick={() => removeQuestion(qIndex)}>
                                Премахни въпрос
                            </button>
                        </div>
                    </div>
                ))}
                <div className="buttons-container">
                    <button type="button" onClick={addQuestion} className="add-question-btn">Добави въпрос</button>
                    <button type="submit" className="btn btn-success submit-quiz-btn">Създай куиз</button>
                </div>
            </form>
        </div>
    );
}

export default CreateQuiz;
