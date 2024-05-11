import React, {useState, useRef, useEffect, useCallback} from 'react';
import {useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';
import {DragDropContext, Droppable, Draggable} from 'react-beautiful-dnd';
import '../styles/createTest.css';
import {useLoading} from '../context/LoadingContext';
import {v4 as uuidv4} from 'uuid';

const CreateTest = () => {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    const [test, setTest] = useState({
        title: '',
        grade: '',
        subject: '',
        sections: []
    });
    const [subjects, setSubjects] = useState([]);
    const {setLoading} = useLoading();
    const navigate = useNavigate();
    const hasNavigated = useRef(false);

    const containerRef = useRef(null);

    const fetchSubjects = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8090/api/v1/test/subjects`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            const data = await response.json();
            setSubjects(data.subjects);
        } catch (error) {
            console.error('Failed to fetch subjects:', error);
        }
        setLoading(false);

    }, [token, setLoading]);

    useEffect(() => {
        if (!token && !hasNavigated.current) {
            toast.error(`Създаването на куизове е само за вписани потребители`);
            navigate('/login');
            hasNavigated.current = true;
        }
        if (token) {
            fetchSubjects();
        }
    }, [token, navigate, fetchSubjects]);

    useEffect(() => {
        const handleDragOver = (event) => {
            if (!containerRef.current) return;
            const {clientY} = event;
            const {top, height} = containerRef.current.getBoundingClientRect();
            const threshold = 100;
            const maxScrollSpeed = 30;

            const distanceFromTop = clientY - top;
            const distanceFromBottom = top + height - clientY;

            if (distanceFromTop < threshold) {
                containerRef.current.scrollTop -= (1 - (distanceFromTop / threshold)) * maxScrollSpeed;
            } else if (distanceFromBottom < threshold) {
                containerRef.current.scrollTop += (1 - (distanceFromBottom / threshold)) * maxScrollSpeed;
            }
        };

        const currentContainer = containerRef.current;
        currentContainer.addEventListener('dragover', handleDragOver);

        return () => {
            currentContainer.removeEventListener('dragover', handleDragOver);
        };
    }, []);

    const handleCountChange = (sectionId, newCount) => {
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    usedQuestionsCount: newCount || 0
                };
            }
            return section;
        });
        setTest({sections: updatedSections});
    };

    const addSection = () => {
        const newSection = {
            id: `section-${uuidv4()}`,
            questions: [{
                id: uuidv4(),
                question: '',
                questionType: 'SINGLE_ANSWER',
                answers: [{content: '', isCorrect: true}, {content: '', isCorrect: false}],
                image: null
            }],
            totalQuestionsCount: 1,
            usedQuestionsCount: 0
        };
        setTest(prevTest => ({
            ...prevTest,
            sections: [...prevTest.sections, newSection]
        }));
    };

    const addQuestion = (sectionId) => {
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                const newQuestion = {
                    id: uuidv4(),
                    question: '',
                    questionType: 'SINGLE_ANSWER',
                    answers: [{content: '', isCorrect: true}, {content: '', isCorrect: false}],
                    image: null
                };
                return {
                    ...section,
                    questions: [...section.questions, newQuestion],
                    totalQuestionsCount: section.questions.length + 1
                };
            }
            return section;
        });
        setTest(prevTest => ({...prevTest, sections: updatedSections}));
    };

    const removeQuestion = (sectionId, questionId) => {
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                const filteredQuestions = section.questions.filter(question => question.id !== questionId);
                return {...section, questions: filteredQuestions, totalQuestionsCount: filteredQuestions.length};
            }
            return section;
        });
        setTest(prevTest => ({...prevTest, sections: updatedSections}));
    };

    const handleFileChange = (sectionId, questionId, event) => {
        const file = event.target.files[0];
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    questions: section.questions.map(question => {
                        if (question.id === questionId) {
                            return {...question, image: URL.createObjectURL(file)};
                        }
                        return question;
                    })
                };
            }
            return section;
        });
        setTest({sections: updatedSections});
    };

    const removeImage = (sectionId, questionId) => {
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    questions: section.questions.map(question => {
                        if (question.id === questionId) {
                            return {...question, image: null};
                        }
                        return question;
                    })
                };
            }
            return section;
        });
        setTest({sections: updatedSections});
    };

    const handleChange = (sectionId, questionId, answerId, type, value) => {
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    questions: section.questions.map(question => {
                        if (question.id === questionId) {
                            const newQuestion = {...question};
                            if (type === 'question') {
                                newQuestion.question = value || '';
                            } else if (type === 'answer' && answerId !== null) {
                                newQuestion.answers[answerId].content = value || '';
                            } else if (type === 'toggleCorrect' && answerId !== null) {
                                if (question.questionType === 'SINGLE_ANSWER') {
                                    newQuestion.answers.forEach((answer, idx) => {
                                        answer.isCorrect = idx === answerId;
                                    });
                                } else {
                                    newQuestion.answers[answerId].isCorrect = !newQuestion.answers[answerId].isCorrect;
                                }
                            } else if (type === 'questionType') {
                                newQuestion.questionType = value;
                                if (value === 'OPEN') {
                                    newQuestion.answers = [{content: '', isCorrect: true}];
                                }
                            }
                            return newQuestion;
                        }
                        return question;
                    })
                };
            }
            return section;
        });
        setTest({sections: updatedSections});
    };

    const handleTestChange = (e) => {
        const {name, value} = e.target;
        setTest(prevTest => ({
            ...prevTest,
            [name]: value
        }));
    };

    const addAnswer = (sectionId, questionId) => {
        const newAnswer = {
            id: `answer-${questionId}-${Math.random()}`,
            content: '',
            isCorrect: false
        };
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    questions: section.questions.map(question => {
                        if (question.id === questionId) {
                            return {...question, answers: [...question.answers, newAnswer]};
                        }
                        return question;
                    })
                };
            }
            return section;
        });
        setTest(prevTest => ({...prevTest, sections: updatedSections}));
    };

    const removeAnswer = (sectionId, questionId, answerId) => {
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    questions: section.questions.map(question => {
                        if (question.id === questionId) {
                            return {
                                ...question,
                                answers: question.answers.filter((_, idx) => idx !== answerId)
                            };
                        }
                        return question;
                    })
                };
            }
            return section;
        });
        setTest({sections: updatedSections});
    };

    const onDragEnd = (result) => {
        const {source, destination} = result;

        if (!destination) {
            return;
        }

        const sourceIndex = source.index;
        const destIndex = destination.index;
        const sourceId = source.droppableId;
        const destId = destination.droppableId;

        if (sourceId === destId) {
            const section = test.sections.find(s => s.id === sourceId);
            const newQuestions = Array.from(section.questions);
            const [removed] = newQuestions.splice(sourceIndex, 1);
            newQuestions.splice(destIndex, 0, removed);

            const newSections = test.sections.map(s => {
                if (s.id === section.id) {
                    return {...s, questions: newQuestions};
                }
                return s;
            });

            setTest(prevTest => ({...prevTest, sections: newSections}));
        } else {
            const sourceSection = test.sections.find(s => s.id === sourceId);
            const destSection = test.sections.find(s => s.id === destId);
            const sourceQuestions = Array.from(sourceSection.questions);
            const destQuestions = Array.from(destSection.questions);
            const [removed] = sourceQuestions.splice(sourceIndex, 1);
            destQuestions.splice(destIndex, 0, removed);

            const newSections = test.sections.map(s => {
                if (s.id === sourceSection.id) {
                    return {...s, questions: sourceQuestions, totalQuestionsCount: sourceQuestions.length};
                } else if (s.id === destSection.id) {
                    return {...s, questions: destQuestions, totalQuestionsCount: destQuestions.length};
                }
                return s;
            });

            setTest(prevTest => ({...prevTest, sections: newSections}));
        }
    };

    return (
        <DragDropContext onDragEnd={onDragEnd}>
            <div ref={containerRef} className="create-test-container">
                <input
                    type="text"
                    name="title"
                    value={test.title}
                    onChange={handleTestChange}
                    placeholder="Enter test title"
                    className="form-control title-input"
                />
                <select
                    name="grade"
                    value={test.grade}
                    onChange={handleTestChange}
                    className="form-control"
                >
                    {[...Array(12).keys()].map(grade => (
                        <option key={grade + 1} value={grade + 1}>{grade + 1}</option>
                    ))}
                </select>
                <select
                    name="subject"
                    value={test.subject}
                    onChange={handleTestChange}
                    className="form-control"
                >
                    {subjects.map(subject => (
                        <option key={subject} value={subject}>{subject}</option>
                    ))}
                </select>
                <div className="sticky-container">
                    <button onClick={addSection} className="btn btn-primary add-section-btn sticky-button">Добави
                        секция
                    </button>
                </div>
                {test.sections.map((section, sIndex) => (
                    <Droppable key={uuidv4()} droppableId={section.id}>
                        {(provided) => (
                            <div ref={provided.innerRef} {...provided.droppableProps} className="section-container">
                                <h2>Секция {sIndex + 1}</h2>
                                <div className="section-counts">
                                    <input
                                        type="number"
                                        min="0"
                                        value={section.usedQuestionsCount || 0}
                                        onChange={(e) => handleCountChange(section.id, parseInt(e.target.value))}
                                        className="count-input"
                                    />
                                    <span> от {section.totalQuestionsCount}</span>
                                </div>
                                {section.questions.map((question, qIndex) => (
                                    <Draggable key={uuidv4()} draggableId={question.id} index={qIndex}>
                                        {(provided, snapshot) => (
                                            <div
                                                key={question.id}
                                                ref={provided.innerRef}
                                                {...provided.draggableProps}
                                                {...provided.dragHandleProps}
                                                className="question-container"
                                                style={{
                                                    ...provided.draggableProps.style,
                                                    backgroundColor: snapshot.isDragging ? '#f4f4f4' : 'white',
                                                    boxShadow: snapshot.isDragging ? '0 0 10px rgba(0,0,0,0.2)' : 'none',
                                                    cursor: snapshot.isDragging ? 'grabbing' : 'grab'
                                                }}
                                            >
                                                <h3>Въпрос {qIndex + 1}</h3>
                                                <input
                                                    type="file"
                                                    id={`file-input-${section.id}-${question.id}`}
                                                    name="questionImage"
                                                    className="form-control-file"
                                                    onChange={(e) => handleFileChange(section.id, question.id, e)}
                                                    accept="image/*"
                                                />
                                                {question.image && (
                                                    <div className="image-container" style={{textAlign: 'center'}}>
                                                        <img
                                                            src={question.image}
                                                            alt="Question"
                                                            style={{
                                                                maxWidth: '200px',
                                                                height: 'auto',
                                                                display: 'block',
                                                                margin: '0 auto'
                                                            }}
                                                        />
                                                        <button
                                                            type="button"
                                                            className="btn btn-danger mt-2"
                                                            onClick={() => removeImage(section.id, question.id)}
                                                        >
                                                            Премахни снимка
                                                        </button>
                                                    </div>
                                                )}
                                                <select className="form-control"
                                                        value={question.questionType}
                                                        onChange={(e) => handleChange(section.id, question.id, null, 'questionType', e.target.value)}
                                                >
                                                    <option value="SINGLE_ANSWER">Един верен отговор</option>
                                                    <option value="MULTIPLE_ANSWER">Няколко верни отговори</option>
                                                    <option value="OPEN">Отворен отговор</option>
                                                </select>
                                                <textarea className="form-control"
                                                          value={question.question || ''}
                                                          onChange={(e) => handleChange(section.id, question.id, null, 'question', e.target.value)}
                                                          placeholder="Въведи въпрос"
                                                />
                                                {question.answers.map((answer, aIndex) => (
                                                    <div key={uuidv4()} className="answer-container">
                                                        <button
                                                            type="button"
                                                            className={`mark-correct-btn ${answer.isCorrect ? 'correct' : ''}`}
                                                            onClick={() => handleChange(section.id, question.id, aIndex, 'toggleCorrect')}
                                                        ></button>
                                                        <input
                                                            type="text"
                                                            className="answer-input"
                                                            placeholder="Въведи отговор"
                                                            value={answer.content || ''}
                                                            onChange={(e) => handleChange(section.id, question.id, aIndex, 'answer', e.target.value)}
                                                        />
                                                        <button
                                                            type="button"
                                                            className="remove-answer-btn"
                                                            onClick={() => removeAnswer(section.id, question.id, aIndex)}
                                                        ></button>
                                                    </div>
                                                ))}
                                                <button onClick={() => addAnswer(section.id, question.id)}
                                                        className="btn btn-info add-answer-btn">Добави отговор
                                                </button>
                                                <button
                                                    type="button"
                                                    className="btn btn-danger remove-question-btn"
                                                    onClick={() => removeQuestion(section.id, question.id)}
                                                >
                                                    Премахни въпрос
                                                </button>
                                            </div>
                                        )}
                                    </Draggable>
                                ))}
                                {React.cloneElement(provided.placeholder, {key: 'placeholder'})}
                                <button onClick={() => addQuestion(section.id)}
                                        className="btn btn-success add-question-btn">Добави въпрос
                                </button>
                                {provided.placeholder}
                            </div>
                        )}
                    </Droppable>
                ))}
            </div>
        </DragDropContext>
    );
};

export default CreateTest;
