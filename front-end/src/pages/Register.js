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
    const [passwordValidations, setPasswordValidations] = useState({
        length: false,
        upper: false,
        lower: false,
        number: false,
        special: false,
    });
    const [isPasswordFocused, setIsPasswordFocused] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });

        if (name === 'password') {
            validatePassword(value);
        }
    };

    const validatePassword = (password) => {
        const length = password.length >= 8;
        const upper = /[A-Z]/.test(password);
        const lower = /[a-z]/.test(password);
        const number = /[0-9]/.test(password);
        const special = /[!@#$%^&*(),.?":{}|<>]/.test(password);

        setPasswordValidations({
            length,
            upper,
            lower,
            number,
            special,
        });
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

        if (!Object.values(passwordValidations).every(Boolean)) {
            toast.error('Паролата не отговаря на всички изисквания');
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
                toast.error('Потребител с този имейл вече съществува');
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
                    <input
                        type="password"
                        name="password"
                        className="form-control"
                        placeholder="Парола"
                        onChange={handleChange}
                        onFocus={() => setIsPasswordFocused(true)}
                        onBlur={() => setIsPasswordFocused(false)}
                        required
                    />
                    <ul className={`password-requirements ${isPasswordFocused ? 'visible' : ''}`}>
                        <li className={passwordValidations.length ? 'valid' : 'invalid'}>
                            Поне 8 символа
                        </li>
                        <li className={passwordValidations.upper ? 'valid' : 'invalid'}>
                            Поне една главна буква
                        </li>
                        <li className={passwordValidations.lower ? 'valid' : 'invalid'}>
                            Поне една малка буква
                        </li>
                        <li className={passwordValidations.number ? 'valid' : 'invalid'}>
                            Поне едно число
                        </li>
                        <li className={passwordValidations.special ? 'valid' : 'invalid'}>
                            Поне един специален символ
                        </li>
                    </ul>
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
