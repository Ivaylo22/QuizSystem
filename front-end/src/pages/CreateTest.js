import React, {useState, useEffect, useRef} from 'react';
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

        // Attach the event listener
        const currentContainer = containerRef.current;
        currentContainer.addEventListener('dragover', handleDragOver);

        // Cleanup function to remove the event listener
        return () => {
            if (currentContainer) {
                currentContainer.removeEventListener('dragover', handleDragOver);
            }
        };
    }, []);

    const addSection = () => {
        const newSection = {
            id: `section-${test.sections.length + 1}`,
            questions: []
        };
        setTest(prevTest => ({
            ...prevTest,
            sections: [...prevTest.sections, newSection]
        }));
    };

    const addQuestion = (sectionId) => {
        const newQuestion = {
            id: `question-${sectionId}-${Math.random()}`,
            content: '',
            answers: [],
            questionType: 'SINGLE_ANSWER'
        };
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {...section, questions: [...section.questions, newQuestion]};
            }
            return section;
        });
        setTest(prevTest => ({...prevTest, sections: updatedSections}));
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

    const handleAnswerChange = (sectionId, questionId, answerId, newValue) => {
        const updatedSections = test.sections.map(section => {
            if (section.id === sectionId) {
                return {
                    ...section,
                    questions: section.questions.map(question => {
                        if (question.id === questionId) {
                            return {
                                ...question,
                                answers: question.answers.map(answer => {
                                    if (answer.id === answerId) {
                                        return {...answer, content: newValue};
                                    }
                                    return answer;
                                })
                            };
                        }
                        return question;
                    })
                };
            }
            return section;
        });
        setTest({...test, sections: updatedSections});
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
            setTest({...test, sections: newSections});
        }
    };

    return (
        <DragDropContext onDragEnd={onDragEnd}>
            <div ref={containerRef} className="create-test-container">
                <h1>Create Test</h1>
                <button onClick={addSection} className="btn btn-primary add-section-btn">Add Section</button>
                {test.sections.map((section, sIndex) => (
                    <Droppable key={section.id} droppableId={section.id}>
                        {(provided) => (
                            <div ref={provided.innerRef} {...provided.droppableProps} className="section-container">
                                <h2>Section {sIndex + 1}</h2>
                                <button onClick={() => addQuestion(section.id)}
                                        className="btn btn-success add-question-btn">Add Question
                                </button>
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
                                                <h3>Question {qIndex + 1}</h3>
                                                <button onClick={() => addAnswer(section.id, question.id)}
                                                        className="btn btn-info add-answer-btn">Add Answer
                                                </button>
                                                {question.answers.map((answer, aIndex) => (
                                                    <div key={answer.id} className="answer-container">
                                                        Answer: <input type="text" value={answer.content}
                                                                       className="form-control"
                                                                       onChange={(e) => handleAnswerChange(section.id, question.id, answer.id, e.target.value)}/>
                                                    </div>
                                                ))}
                                            </div>
                                        )}
                                    </Draggable>
                                ))}
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
