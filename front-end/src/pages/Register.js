import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import '../styles/register.css';

const Register = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        avatar: null
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleFileChange = (e) => {
        setFormData({ ...formData, avatar: e.target.files[0] });
    };

    const uploadAvatar = async (email, file) => {
        const uploadFormData = new FormData();
        uploadFormData.append('file', file);
        uploadFormData.append('userEmail', email);

        const response = await fetch('http://localhost:8090/api/v1/upload-image', {
            method: 'POST',
            body: uploadFormData,
        });

        if (!response.ok) {
            throw new Error('Failed to upload avatar.');
        }

        return await response.text();
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            toast.error('Паролите не съвпадат');
            return;
        }

        const { avatar, ...registrationData } = formData;

        try {
            const registrationResponse = await fetch('http://localhost:8090/api/v1/user/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(registrationData),
            });

            if (registrationResponse.ok) {
                if (formData.avatar) {
                    try {
                        await uploadAvatar(formData.email, formData.avatar);
                    } catch (error) {
                        console.error('Avatar upload failed:', error);
                        console.log('Avatar upload failed');
                    }
                }

                toast.success('Успешна регистрация');
                navigate('/login');
            } else {
                const errorText = await registrationResponse.text();
                console.log(`Registration error: ${errorText}`);
            }
        } catch (error) {
            console.error('Error during registration:', error);
            console.log(`Registration error: ${error.message}`);
        }
    };

    return (
        <div className="container pt-5 pb-2 register-container">
            <form onSubmit={handleSubmit} className="w-50 mx-auto">
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
                    <button type="submit" className="btn auth">Регистрирай се</button>
                </div>
            </form>
        </div>
    );
};

export default Register;
