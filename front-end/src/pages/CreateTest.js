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
        grade: 1,
        subject: "Английски език",
        hasMixedQuestions: false,
        minutesToSolve: 40,
        status: 'PUBLIC',
        scoringFormula: 'formula1',
        sections: [],
        creatorEmail: email
    });
    const [subjects, setSubjects] = useState([]);
    const {setLoading} = useLoading();
    const navigate = useNavigate();
    const hasNavigated = useRef(false);
    const containerRef = useRef(null);
    const [collapsedSections, setCollapsedSections] = useState({});
    const [showSettings, setShowSettings] = useState(false);
    const [problematicIssues, setProblematicIssues] = useState({
        sections: [],
        questions: new Map()
    });

    const toggleSettingsDialog = () => {
        setShowSettings(!showSettings);
    };

    const toggleCollapse = (sectionId) => {
        setCollapsedSections(prev => ({
            ...prev,
            [sectionId]: !prev[sectionId]
        }));
    };

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

            const uniqueSubjects = Array.from(new Set(data.subjects));
            setSubjects(uniqueSubjects);
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
                const validatedCount = Math.min(newCount, section.totalQuestionsCount);

                return {
                    ...section,
                    usedQuestionsCount: validatedCount >= 0 ? validatedCount : 0
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
                answers: [{id: uuidv4(), content: '', isCorrect: true}, {id: uuidv4(), content: '', isCorrect: false}],
                maximumPoints: 2,
                image: null
            }],
            totalQuestionsCount: 1,
            usedQuestionsCount: 1
        };
        setTest(prevTest => ({
            ...prevTest,
            sections: [...prevTest.sections, newSection]
        }));
    };

    const removeSection = (sectionId) => {
        const updatedSections = test.sections.filter(section => section.id !== sectionId);
        setTest(prevTest => ({
            ...prevTest,
            sections: updatedSections
        }));
    };

    const addQuestion = (sectionId) => {
        const newQuestion = {
            id: uuidv4(),
            question: '',
            questionType: 'SINGLE_ANSWER',
            answers: [{id: uuidv4(), content: '', isCorrect: true}, {id: uuidv4(), content: '', isCorrect: false}],
            image: null,
            maximumPoints: 2
        };

        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    questions: [...section.questions, newQuestion],
                    totalQuestionsCount: section.questions.length + 1,
                    usedQuestionsCount: section.usedQuestionsCount + 1
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
                return {
                    ...section,
                    questions: filteredQuestions,
                    totalQuestionsCount: filteredQuestions.length,
                    usedQuestionsCount: section.usedQuestionsCount - 1
                };
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
        setTest({...test, sections: updatedSections});
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
        setTest({...test, sections: updatedSections});
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
                                    newQuestion.answers = [{id: uuidv4(), content: '', isCorrect: true}];
                                }
                            } else if (type === 'maximumPoints') {
                                newQuestion.maximumPoints = value;
                            }
                            return newQuestion;
                        }
                        return question;
                    })
                };
            }
            return section;
        });
        setTest({...test, sections: updatedSections});
    };

    const addAnswer = (sectionId, questionId) => {
        const newAnswer = {
            id: uuidv4(),
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
                    return {
                        ...s,
                        questions: sourceQuestions,
                        totalQuestionsCount: sourceQuestions.length,
                        usedQuestionsCount: s.usedQuestionsCount - 1
                    };
                } else if (s.id === destSection.id) {
                    return {
                        ...s,
                        questions: destQuestions,
                        totalQuestionsCount: destQuestions.length,
                        usedQuestionsCount: s.usedQuestionsCount + 1
                    };
                }
                return s;
            });

            setTest(prevTest => ({...prevTest, sections: newSections}));
        }
    };

    const validateTest = () => {
        let valid = true;
        const newProblematicSections = [];
        const newProblematicQuestions = new Map();

        // Basic test validations
        if (!test.title.trim()) {
            toast.error('Моля, въведете заглавие на теста.');
            valid = false;
        }
        if (!test.grade) {
            toast.error('Моля, изберете клас.');
            valid = false;
        }
        if (!test.subject) {
            toast.error('Моля, изберете предмет.');
            valid = false;
        }
        if (test.sections.length === 0) {
            toast.error('Моля, добавете поне една секция.');
            valid = false;
        }

        // Section and question level validations
        test.sections.forEach((section, sIndex) => {
            let sectionHasError = false;
            let problematicQuestions = [];

            if (section.questions.length === 0) {
                sectionHasError = true;
                toast.error(`Секция ${sIndex + 1} не съдържа въпроси.`);
            }

            section.questions.forEach((question, qIndex) => {
                let questionHasError = false;

                if (!question.question.trim() || question.maximumPoints <= 0) {
                    questionHasError = true;
                }

                if (question.questionType !== 'OPEN') {
                    const hasCorrectAnswer = question.answers.some(answer => answer.isCorrect);
                    if (!hasCorrectAnswer) {
                        questionHasError = true;
                        toast.error(`Въпрос ${qIndex + 1} в секция ${sIndex + 1} няма правилен отговор.`);
                    }
                }

                if (questionHasError) {
                    problematicQuestions.push(qIndex);
                    sectionHasError = true;
                }
            });

            if (problematicQuestions.length > 0) {
                newProblematicQuestions.set(sIndex, problematicQuestions);
            }

            if (sectionHasError) {
                newProblematicSections.push(sIndex);
            }
        });

        setProblematicIssues({
            sections: newProblematicSections,
            questions: newProblematicQuestions
        });

        return valid && newProblematicSections.length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateTest()) {
            return;
        }

        const testToSubmit = {
            ...test,
            sections: test.sections.map(section => ({
                ...section,
                questions: section.questions.map(({image, ...rest}) => rest)
            }))
        };

        try {
            const response = await fetch('http://localhost:8090/api/v1/test/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(testToSubmit),
            });

            if (!response.ok) {
                throw new Error('Failed to create test.');
            }

            const data = await response.json();

            const uploadPromises = test.sections.flatMap(section =>
                section.questions.map(async (question) => {
                    if (question.image) {
                        return uploadFile(question.image, data.questionIds[question.id]);
                    }
                })
            );

            await Promise.all(uploadPromises);

            toast.success('Успешно създаден тест.');
            navigate('/');
        } catch (error) {
            console.error('Error during test creation:', error);
            toast.error('Проблем при създаването на теста.');
        }
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

    return (
        <DragDropContext onDragEnd={onDragEnd}>
            <div ref={containerRef} className="create-test-container">
                <div className="sticky-container">
                    <button onClick={addSection} className="btn btn-primary add-section-btn">Добави секция</button>
                    <button onClick={toggleSettingsDialog} className="btn settings-button">
                        <i className="fas fa-cog settings-icon"></i>
                    </button>
                </div>
                {showSettings && (
                    <div className="dialog-overlay" onClick={toggleSettingsDialog}>
                        <dialog open className="settings-dialog" onClick={e => e.stopPropagation()}>
                            <h3>Настройки на теста</h3>
                            <div className="form-group">
                                <label htmlFor="title">Заглавие:</label>
                                <input type="text" id="title" name="title" value={test.title}
                                       onChange={(e) => setTest({...test, title: e.target.value})}
                                       className="form-control"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="grade">Клас:</label>
                                <select id="grade" name="grade" value={test.grade}
                                        onChange={(e) => setTest({...test, grade: e.target.value})}
                                        className="form-control">
                                    {[...Array(12).keys()].map(grade => (
                                        <option key={grade + 1} value={grade + 1}>{grade + 1}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-group">
                                <label htmlFor="subject">Предмет:</label>
                                <select id="subject" name="subject" value={test.subject}
                                        onChange={(e) => setTest({...test, subject: e.target.value})}
                                        className="form-control">
                                    {subjects.map((subject, index) => (
                                        <option key={`${subject}-${index}`} value={subject}>{subject}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-group">
                                <label htmlFor="secondsToSolve">Минути за решаване:</label>
                                <input type="number" id="minutesToSolve" min="0" value={test.minutesToSolve}
                                       onChange={(e) => setTest({
                                           ...test,
                                           minutesToSolve: parseInt(e.target.value) || 0
                                       })}
                                       className="form-control"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="status">Вид тест:</label>
                                <select id="status" value={test.status}
                                        onChange={(e) => setTest({...test, status: e.target.value})}
                                        className="form-control">
                                    <option value="PRIVATE">Частен</option>
                                    <option value="PUBLIC">Публичен</option>
                                </select>
                            </div>
                            <label htmlFor="scoringFormula">Формула за оценяване:</label>
                            <select
                                id="scoringFormula"
                                value={test.scoringFormula}
                                onChange={(e) => setTest({...test, scoringFormula: e.target.value})}
                                className="form-control"
                            >
                                <option value="formula1">2 + (получени точки / всички точки) * 4</option>
                                <option value="formula2">(получени точки / всички точки) * 6</option>
                                <option value="formula3">1 + (получени точки / всички точки) * 5</option>
                            </select>
                            <div className="form-group form-check">
                                <label className="form-check-label" htmlFor="mixedQuestions">Разбъркване на
                                    въпросите</label>
                                <input type="checkbox" id="mixedQuestions" checked={test.hasMixedQuestions}
                                       onChange={(e) => setTest({...test, hasMixedQuestions: e.target.checked})}
                                       className="form-check-input mixed-questions-check"/>
                            </div>
                            <div className="dialog-buttons-container mx-auto">
                                <button className="btn btn-primary mx-auto" onClick={toggleSettingsDialog}>Затвори
                                </button>
                            </div>
                        </dialog>
                    </div>
                )}
                {test.sections.map((section, sIndex) => (
                    <Droppable key={section.id} droppableId={section.id}>
                        {(provided) => (
                            <div ref={provided.innerRef} {...provided.droppableProps}
                                 className={`section-container ${problematicIssues.sections.includes(sIndex) ? 'border-danger' : ''}`}>
                                <div className="section-header">
                                    <h3 onClick={() => toggleCollapse(section.id)}>
                                        Секция {sIndex + 1} <span
                                        className={`toggle-icon ${collapsedSections[section.id] ? 'collapsed' : ''}`}>&#9660;</span>
                                    </h3>
                                    <div className="section-counts">
                                        <span>Използвани въпроси в секция:</span>
                                        <input
                                            type="number"
                                            min="0"
                                            max={section.totalQuestionsCount}
                                            onChange={(e) => handleCountChange(section.id, parseInt(e.target.value))}
                                            value={section.usedQuestionsCount || ""}
                                            className="count-input"
                                        />
                                        <span> от {section.totalQuestionsCount}</span>
                                    </div>
                                    <button
                                        onClick={() => removeSection(section.id)}
                                        className="btn remove-section-btn"
                                        aria-label="Remove section"
                                    >
                                        <i className="fas fa-times"></i>
                                    </button>
                                </div>
                                {!collapsedSections[section.id] && (
                                    <div>
                                        {section.questions.map((question, qIndex) => (
                                            <Draggable key={question.id} draggableId={question.id} index={qIndex}>
                                                {(provided, snapshot) => (
                                                    <div
                                                        ref={provided.innerRef}
                                                        {...provided.draggableProps}
                                                        {...provided.dragHandleProps}
                                                        className={`question-container ${problematicIssues.questions.get(sIndex)?.includes(qIndex) ? 'border-danger' : ''}`}
                                                        style={{
                                                            ...provided.draggableProps.style,
                                                            backgroundColor: snapshot.isDragging ? '#f4f4f4' : 'white',
                                                            boxShadow: snapshot.isDragging ? '0 0 10px rgba(0,0,0,0.2)' : 'none',
                                                            cursor: snapshot.isDragging ? 'grabbing' : 'grab'
                                                        }}
                                                    >
                                                        <div className="question-header">
                                                            <h4>Въпрос {qIndex + 1}</h4>
                                                            <div className="points-input">
                                                                <label>Точки:</label>
                                                                <input
                                                                    type="number"
                                                                    min="0"
                                                                    value={question.maximumPoints || ''}
                                                                    onChange={(e) => handleChange(section.id, question.id, null, 'maximumPoints', parseInt(e.target.value))}
                                                                    className="form-control points-control"
                                                                />
                                                            </div>
                                                        </div>
                                                        <input
                                                            type="file"
                                                            id={`file-input-${section.id}-${question.id}`}
                                                            name="questionImage"
                                                            className="form-control-file"
                                                            onChange={(e) => handleFileChange(section.id, question.id, e)}
                                                            accept="image/*"
                                                        />
                                                        {question.image && (
                                                            <div className="image-container"
                                                                 style={{textAlign: 'center'}}>
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
                                                                    className="btn btn-danger mt-2 remove-img"
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
                                                            <option value="MULTIPLE_ANSWER">Няколко верни отговори
                                                            </option>
                                                            <option value="OPEN">Отворен отговор</option>
                                                        </select>
                                                        <textarea className="form-control"
                                                                  value={question.question || ''}
                                                                  onChange={(e) => handleChange(section.id, question.id, null, 'question', e.target.value)}
                                                                  placeholder="Въведи въпрос"
                                                        />
                                                        {question.answers.map((answer, aIndex) => (
                                                            <div key={answer.id} className="answer-container">
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
                                        <button onClick={() => addQuestion(section.id)}
                                                className="btn btn-success add-question-btn">Добави въпрос
                                        </button>
                                        {provided.placeholder}
                                    </div>
                                )}
                            </div>
                        )}
                    </Droppable>
                ))}
                <button onClick={handleSubmit} className="btn btn-success submit-test">
                    Създай тест
                </button>
            </div>
        </DragDropContext>
    );
};

export default CreateTest;
