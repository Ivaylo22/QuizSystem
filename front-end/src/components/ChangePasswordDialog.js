import React, {useState} from 'react';
import '../styles/changePasswordDialog.css';
import {toast} from 'react-toastify';

const ChangePasswordDialog = ({isOpen, onClose, onSubmit}) => {
    const [formData, setFormData] = useState({
        password: '',
        confirmPassword: ''
    });

    const [passwordValidations, setPasswordValidations] = useState({
        length: false,
        upper: false,
        lower: false,
        number: false,
        special: false
    });

    const [isPasswordFocused, setIsPasswordFocused] = useState(false);

    const validatePassword = (password) => {
        const validations = {
            length: password.length >= 8,
            upper: /[A-Z]/.test(password),
            lower: /[a-z]/.test(password),
            number: /[0-9]/.test(password),
            special: /[!@#$%^&*,]/.test(password)
        };
        setPasswordValidations(validations);
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData({...formData, [name]: value});
        if (name === 'password') {
            validatePassword(value);
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (formData.password !== formData.confirmPassword) {
            toast.error("Паролите не съвпадат!")
            return;
        }
        onSubmit(formData);
    };

    return (
        isOpen && (
            <div className="change-password-dialog">
                <div className="dialog-content">
                    <h2>Смяна на парола</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label htmlFor="password" className="form-label">Нова парола:</label>
                            <input
                                type="password"
                                name="password"
                                className="form-control"
                                placeholder="Нова парола"
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
                            <input
                                type="password"
                                name="confirmPassword"
                                className="form-control"
                                placeholder="Потвърди парола"
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="dialog-buttons-container">
                            <button type="button" className="btn-cancel-change" onClick={onClose}>Отказ</button>
                            <button type="submit" className="btn-submit-change">Смяна</button>
                        </div>
                    </form>
                </div>
            </div>
        )
    );
};

export default ChangePasswordDialog;
