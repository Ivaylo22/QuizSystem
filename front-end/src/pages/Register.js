import React, { useState, useEffect, useCallback } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import '../styles/register.css';

const Register = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        avatarData: []
    });

    const fetchDefaultAvatarImage = useCallback(() => {
        const defaultAvatarUrl = '../res/default-profile-picture.png';
        fetch(defaultAvatarUrl)
            .then(response => response.blob())
            .then(blob => {
                const reader = new FileReader();
                reader.onload = function () {
                    const arrayBuffer = reader.result;
                    const byteArray = new Uint8Array(arrayBuffer);
                    setFormData(formData => ({ ...formData, avatarData: Array.from(byteArray) }));
                };
                reader.readAsArrayBuffer(blob);
            })
            .catch(error => {
                console.error('Error fetching default avatar image:', error);
            });
    }, []);

    useEffect(() => {
        fetchDefaultAvatarImage();
    }, [fetchDefaultAvatarImage]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(formData => ({ ...formData, [name]: value }));
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (event) {
                const arrayBuffer = event.target.result;
                const byteArray = new Uint8Array(arrayBuffer);
                setFormData(formData => ({ ...formData, avatarData: Array.from(byteArray) }));
            };
            reader.readAsArrayBuffer(file);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            toast.error('Паролите не съвпадат');
            return;
        }

        try {
            const response = await fetch('http://localhost:8090/api/v1/user/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            });

            if (response.ok) {
                toast.success('Успешна регистрация');
                navigate('/');
            } else {
                toast.error('Грешка при регистриране');
                console.error('User is already existing', response.statusText);
            }
        } catch (error) {
            console.error('Error registering user:', error.message);
            toast.error('Грешка при регистрация');
        }
    };

    return (
        <div className="container pt-5 pb-2 register-container">
            <form onSubmit={handleSubmit} className="w-50 mx-auto">
                <div className="mb-3">
                    <label htmlFor="username" className="form-label">Потребителско име:</label>
                    <input type="text" name="username" className="form-control" placeholder="Потребителско име" onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label htmlFor="email" className="form-label">Имейл:</label>
                    <input type="email" name="email" className="form-control" placeholder="Имейл" onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label htmlFor="password" className="form-label">Парола:</label>
                    <input type="password" name="password" className="form-control" placeholder="Парола" onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label htmlFor="confirmPassword" className="form-label">Потвърди парола:</label>
                    <input type="password" name="confirmPassword" className="form-control" placeholder="Потвърди парола" onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label htmlFor="avatar" className="form-label">Профилна снимка:</label>
                    <input type="file" name="avatar" className="form-control" accept="image/*" onChange={handleFileChange} />
                </div>
                <div className="mb-2 text-center">
                    <button type="submit" className="btn btn-register">Регистрирай се</button>
                </div>
            </form>
        </div>
    );
};

export default Register;
