import React, { useState, useEffect, useCallback } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
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
import CreateChoice from './pages/CreateChoice';
import CreateTest from './pages/CreateTest';
import SolveChoice from './pages/SolveChoice';
import QuizResults from './pages/QuizResults';
import SolveTest from './pages/SolveTest.js';
import { useLoading } from './context/LoadingContext';
import Loader from './components/Loader';
import AllTests from './pages/AllTests.js';
import TestResults from './pages/TestResults.js';
import ProtectedRoute from './ProtectedRoute.js';
import MyTests from './pages/MyTests.js';
import SolveTestByAccess from './pages/SolveTestByAccess.js';
import TestSummary from './pages/TestSummary.js';
import TestAttempts from './pages/TestAttempts.js';
import EvaluateTest from './pages/EvaluateTest.js';
import GetMineChoice from './pages/GetMineChoice.js';

function App() {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);
    const [userInformation, setUserInformation] = useState({});
    const { loading, setLoading } = useLoading();

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
        setLoading(true);

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
                const {userInformation} = await response.json();
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
        setTimeout(() => setLoading(false), 1000);
    }, [setLoading]);

    useEffect(() => {
        const storedIsAdmin = localStorage.getItem('isAdmin') === 'true';
        setIsAdmin(storedIsAdmin);

        if (token && email) {
            checkUserStatus(email, token);
        } else {
            setIsLoggedIn(false);
            setIsAdmin(false);
        }
    }, [checkUserStatus, token, email]);

    return (
        <Router>
            <Loader loading={loading}/>
            <div className='app-container'>
                <NavBar isLoggedIn={isLoggedIn} isAdmin={isAdmin} setIsLoggedIn={setIsLoggedIn} setIsAdmin={setIsAdmin}
                        userInformation={userInformation}/>
                <div className='content'>
                    <Routes>
                        <Route path="/" element={<Home/>}/>
                        <Route path="/register" element={<Register/>}/>
                        <Route path="/login" element={<Login setIsLoggedIn={setIsLoggedIn} setIsAdmin={setIsAdmin}
                                                             setUserInformation={setUserInformation}/>}/>
                        <Route path="*" element={
                            <ProtectedRoute>
                                <Routes>
                                    <Route path="/profile" element={<Profile userInformation={userInformation}/>}/>
                                    <Route path="/achievements" element={<Achievements token={token} email={email}/>}/>
                                    <Route path="/stats" element={<Statistics userInformation={userInformation}/>}/>
                                    <Route path="/create" element={<CreateChoice/>}/>
                                    <Route path="/get-mine" element={<GetMineChoice/>}/>
                                    <Route path="/my-tests" element={<MyTests/>}/>
                                    <Route path="/create-quiz" element={<CreateQuiz email={email} token={token}/>}/>
                                    <Route path="/create-test" element={<CreateTest email={email} token={token}/>}/>
                                    <Route path="/solve" element={<SolveChoice/>}/>
                                    <Route path="/quizzes" element={<AllQuizzes email={email} token={token}/>}/>
                                    <Route path="/tests" element={<AllTests/>}/>
                                    <Route path="/solve-test/:testId" element={<SolveTest/>}/>
                                    <Route path="/solve-by-key/:accessKey" element={<SolveTestByAccess/>}/>
                                    <Route path="/solve-quiz/:quizId"
                                           element={<SolveQuiz email={email} token={token}/>}/>
                                    <Route path="/quiz-results/:quizId" element={<QuizResults token={token}/>}/>
                                    <Route path="/test-result" element={<TestResults/>}/>
                                    <Route path="/requested"
                                           element={<AdminRoute><RequestedQuizzes token={token}/></AdminRoute>}/>
                                    <Route path="/requested/:quizId"
                                           element={<AdminRoute><RequestedQuizInfo token={token}/></AdminRoute>}/>
                                    <Route path="/test-summary/:testId" element={<TestSummary/>}/>
                                    <Route path="/test-attempts/:testId" element={<TestAttempts/>}/>
                                    <Route path="/evaluate-test/:userEmail/:testId" element={<EvaluateTest/>}/>
                                    <Route path="/not-found" element={<NotFound/>}/>
                                </Routes>
                            </ProtectedRoute>
                        }/>
                    </Routes>
                </div>
            </div>
            <ToastContainer
                position="bottom-center"
                autoClose={2000}
                hideProgressBar={true}
                pauseOnHover={false}/>
        </Router>
    );
}

export default App;