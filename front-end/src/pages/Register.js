import React, { useState, useEffect} from 'react';
import '../styles/register.css'


const Register = () => {
    useEffect(() => {
        fetchDefaultAvatarImage();
    }, []);

    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        avatarData: null
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        const reader = new FileReader();

        reader.onloadend = () => {
            setFormData({ ...formData, avatarData: reader.result });
        };

        if (file) {
            reader.readAsDataURL(file);
        }
    };

    const fetchDefaultAvatarImage = () => {
        const defaultAvatarUrl = '../res/default-profile-picture.png';

        fetch(defaultAvatarUrl)
            .then(response => response.blob())
            .then(blob => {
                const reader = new FileReader();
                reader.onloadend = () => {
                    const arrayBuffer = reader.result;
                    const byteArray = new Uint8Array(arrayBuffer);
                    setFormData({ ...formData, avatarData: [...byteArray] });
                };
                reader.readAsArrayBuffer(blob);
                console.log(blob);
            })
            .catch(error => {
                console.error('Error fetching default avatar image:', error);
            });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            const response = await fetch('http://localhost:8090/api/v1/user/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            });
    
            if (response.ok) {
                console.log('Registration successful!');
            } else {
                console.error('Registration failed:', response.statusText);
            }
        } catch (error) {
            console.error('Error registering user:', error.message);
        }
    };

    return (
        <div className="container mt-5 pt-1 pb-2 register-container">
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <input type="text" name="username" className="form-control" placeholder="Username" onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <input type="email" name="email" className="form-control" placeholder="Email" onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <input type="password" name="password" className="form-control" placeholder="Password" onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <input type="password" name="confirmPassword" className="form-control" placeholder="Confirm Password" onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <input type="file" name="avatar" className="form-control" accept="image/*" onChange={handleFileChange} />
                </div>
                <div className="mb-2 text-center">
                    <button type="submit" className="btn btn-register">Register</button>
                </div>          
            </form>
        </div>
    );
};

export default Register;
