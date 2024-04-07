import React, { useState, useEffect } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { toast } from 'react-toastify';

const ProtectedRoute = ({ children, isLoggedIn }) => {
    const [toastShown, setToastShown] = useState(false);
    const location = useLocation();

    useEffect(() => {
        if (!isLoggedIn && !toastShown) {
            toast.warn('Трябва да сте влезли в профила си.');
            setToastShown(true);
        }
        return () => setToastShown(false);
    }, [isLoggedIn, toastShown, location]);

    if (!isLoggedIn) {
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default ProtectedRoute;