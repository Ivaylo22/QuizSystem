import React, {useState, useRef, useEffect} from 'react';
import {DragDropContext, Droppable, Draggable} from 'react-beautiful-dnd';
import '../styles/createTest.css';

const CreateTest = () => {
    const [test, setTest] = useState({
        sections: []
    });

    const containerRef = useRef(null);

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
                return {...section, usedQuestionsCount: Math.min(newCount, section.totalQuestionsCount)};
            }
            return section;
        });
        setTest({sections: updatedSections});
    };

    const addSection = () => {
        const newSection = {
            id: `section-${test.sections.length + 1}`,
            questions: [],
            totalQuestionsCount: 0,
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
                    id: `question-${sectionId}-${Math.random()}`,
                    question: '',
                    questionType: 'SINGLE_ANSWER',
                    answers: [{content: '', isCorrect: false}],
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
        setTest({sections: updatedSections});
    };

    const removeQuestion = (sectionId, questionId) => {
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                const filteredQuestions = section.questions.filter(question => question.id !== questionId);
                return {...section, questions: filteredQuestions, totalQuestionsCount: filteredQuestions.length};
            }
            return section;
        });
        setTest({sections: updatedSections});
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
                            if (type === 'question') {
                                question.question = value;
                            } else if (type === 'answer') {
                                question.answers[answerId].content = value;
                            } else if (type === 'toggleCorrect') {
                                if (question.questionType === 'SINGLE_ANSWER') {
                                    question.answers.forEach((answer, idx) => {
                                        answer.isCorrect = idx === answerId;
                                    });
                                } else {
                                    question.answers[answerId].isCorrect = !question.answers[answerId].isCorrect;
                                }
                            } else if (type === 'questionType') {
                                question.questionType = value;
                                if (value === 'OPEN') {
                                    question.answers = [{content: '', isCorrect: true}];
                                }
                            }
                            return {...question};
                        }
                        return question;
                    })
                };
            }
            return section;
        });
        setTest({sections: updatedSections});
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

        if (source.droppableId !== destination.droppableId) {
            const sourceSection = test.sections.find(section => section.id === source.droppableId);
            const destSection = test.sections.find(section => section.id === destination.droppableId);
            const sourceQuestions = [...sourceSection.questions];
            const [removed] = sourceQuestions.splice(source.index, 1);
            const destinationQuestions = [...destSection.questions];
            destinationQuestions.splice(destination.index, 0, removed);

            const newSections = test.sections.map(section => {
                if (section.id === source.droppableId) {
                    return {...section, questions: sourceQuestions};
                } else if (section.id === destination.droppableId) {
                    return {...section, questions: destinationQuestions};
                }
                return section;
            });
            setTest({sections: newSections});
        }
    };

    return (
        <DragDropContext onDragEnd={onDragEnd}>
            <div ref={containerRef} className="create-test-container">
                <button onClick={addSection} className="btn btn-primary add-section-btn">Добави секция</button>
                {test.sections.map((section, sIndex) => (
                    <Droppable key={section.id} droppableId={section.id}>
                        {(provided) => (
                            <div ref={provided.innerRef} {...provided.droppableProps} className="section-container">
                                <h2>Секция {sIndex + 1}</h2>
                                <div className="section-counts">
                                    <input
                                        type="number"
                                        min="0"
                                        value={section.usedQuestionsCount}
                                        onChange={(e) => handleCountChange(section.id, parseInt(e.target.value))}
                                        className="count-input"
                                    />
                                    <span> от {section.totalQuestionsCount}</span>
                                </div>
                                {section.questions.map((question, qIndex) => (
                                    <Draggable key={question.id} draggableId={question.id} index={qIndex}>
                                        {(provided, snapshot) => (
                                            <div
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
                                                    <div>
                                                        <img src={question.image} alt="Question"
                                                             style={{maxWidth: '100%', height: 'auto'}}/>
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
                                                          value={question.question}
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
                                                            value={answer.content}
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
                    </Droppable>
                ))}
            </div>
        </DragDropContext>
    );
};

export default CreateTest;
