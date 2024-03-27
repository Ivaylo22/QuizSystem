import React, { useState, useEffect, useCallback } from 'react';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import {jwtDecode} from 'jwt-decode';
import { ToastContainer } from 'react-toastify';
import Home from './pages/Home';
import NotFound from './pages/NotFound';
import Register from './pages/Register';
import Login from './pages/Login';
import NavBar from './components/NavBar';
import Profile from './pages/Profile';
import Achievements from './pages/Achievements';
import Statistics from './pages/Statistics';
import CreateQuiz from './pages/CreateQuiz';
import RequestedQuizzes from './pages/RequestedQuizzes';
import RequestedQuizInfo from './pages/RequestedQuizInfo';
import AdminRoute from './AdminRoute';
import AllQuizzes from './pages/AllQuizzes';
import SolveQuiz from './pages/SolveQuiz';
import QuizResults from './pages/QuizResults';

function App() {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);
    const [userInformation, setUserInformation] = useState({});

    const isTokenExpired = (token) => {
        try {
            const decoded = jwtDecode(token);
            const currentTime = Date.now() / 1000;
            return decoded.exp < currentTime;
        } catch (error) {
            console.error('Token decoding error:', error);
            return true;
        }
    };

    const logoutUser = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('email');
        localStorage.removeItem('isAdmin');
        setIsLoggedIn(false);
        setIsAdmin(false);
    };

    const checkUserStatus = useCallback(async (email, token) => {

        if (token && isTokenExpired(token)) {
            logoutUser();
            alert('Моля презаредете страницата и се впишете отново');
            return;
        }

        if (!token) {
            setIsLoggedIn(false);
            setIsAdmin(false);
            return;
        }

        try {
            const response = await fetch(`http://localhost:8090/api/v1/user/info?email=${encodeURIComponent(email)}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (response.ok) {
                const { userInformation } = await response.json();
                const isAdmin = userInformation.role === 'ADMIN';
                setIsLoggedIn(true);
                setIsAdmin(isAdmin);
                setUserInformation(userInformation);
                localStorage.setItem('isAdmin', isAdmin);
            } else {
                setIsLoggedIn(false);
                setIsAdmin(false);
                localStorage.removeItem('token');
                localStorage.removeItem('email');
                localStorage.removeItem('isAdmin');
            }
        } catch (error) {
            console.error('Error fetching user info:', error);
            setIsLoggedIn(false);
            setIsAdmin(false);
            localStorage.removeItem('token');
            localStorage.removeItem('email');
            localStorage.removeItem('isAdmin');
        }
    }, []);

    useEffect(() => {
        const storedIsAdmin = localStorage.getItem('isAdmin') === 'true';
        setIsAdmin(storedIsAdmin);

        if (token && email) {
            checkUserStatus(email, token);
        }
    }, [checkUserStatus, token, email]);

    return (
        <Router>
            <div className='app-container'>
                <NavBar isLoggedIn={isLoggedIn} isAdmin={isAdmin} setIsLoggedIn={setIsLoggedIn} setIsAdmin={setIsAdmin}
                        userInformation={userInformation}/>
                <div className='content'>
                    <Routes>
                        <Route path="/" element={<Home/>}/>

                        <Route path="/register" element={<Register/>}/>
                        <Route path="/login" element={<Login setIsLoggedIn={setIsLoggedIn} setIsAdmin={setIsAdmin}
                                                             setUserInformation={setUserInformation}/>}/>

                        <Route path="/profile" element={<Profile userInformation={userInformation}/>}/>
                        <Route path="/achievements" element={<Achievements token={token} email={email}/>}/>
                        <Route path="/stats" element={<Statistics userInformation={userInformation}/>}/>

                        <Route path="/create-quiz" element={<CreateQuiz email={email} token={token}/>}/>
                        <Route path="/quizzes" element={<AllQuizzes email={email} token={token}/>}/>
                        <Route path="/solve-quiz/:quizId" element={<SolveQuiz email={email} token={token}/>}/>
                        <Route path="/quiz-results/:quizId" element={<QuizResults token={token}/>}/>

                        <Route path="/requested" element={<AdminRoute><RequestedQuizzes token={token}/></AdminRoute>}/>
                        <Route path="/requested/:quizId"
                               element={<AdminRoute><RequestedQuizInfo token={token}/></AdminRoute>}/>
                        <Route path="/not-found" element={<NotFound/>}/>
                    </Routes>
                </div>
            </div>
            <ToastContainer/>
        </Router>
    );
}

export default App;