import React, {useEffect, useRef} from 'react';
import {Navigate, useLocation} from 'react-router-dom';
import {toast} from 'react-toastify';

const ProtectedRoute = ({children}) => {
    const token = localStorage.getItem('token');
    const toastId = useRef(null);
    const location = useLocation();

    useEffect(() => {
        if (!token && !toast.isActive(toastId.current)) {
            toastId.current = toast.warn("Трябва да бъдете вписани за да достъпите тази страница");
        }
    }, [token]);

    return token ? children : <Navigate to="/login" replace state={{from: location}}/>;
};

export default ProtectedRoute;
