import React, { useState } from 'react';
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

        return await response.text(); // Assuming the response text is the URL of the uploaded image
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            toast.error('Passwords do not match');
            return;
        }

        // Exclude the avatar from the registration data
        const { avatar, ...registrationData } = formData;

        try {
            // Step 1: Register the user
            const registrationResponse = await fetch('http://localhost:8090/api/v1/user/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(registrationData),
            });

            if (registrationResponse.ok) {
                // Step 2: If the user is successfully registered and an avatar is selected, upload the avatar.
                if (formData.avatar) {
                    try {
                        await uploadAvatar(formData.email, formData.avatar);
                    } catch (error) {
                        console.error('Avatar upload failed:', error);
                        toast.error('Avatar upload failed');
                        // Consider how to handle avatar upload failure - rollback user registration or proceed?
                    }
                }

                toast.success('Successful registration');
                navigate('/');
            } else {
                const errorText = await registrationResponse.text();
                toast.error(`Registration error: ${errorText}`);
            }
        } catch (error) {
            console.error('Error during registration:', error);
            toast.error(`Registration error: ${error.message}`);
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
