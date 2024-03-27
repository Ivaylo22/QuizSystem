import React from 'react';
import { BounceLoader } from 'react-spinners';
import '../styles/loader.css'; // Create this file for additional styling if needed

const Loader = ({ loading }) => {
    return (
        <div className="loader-container" style={{ display: loading ? 'flex' : 'none' }}>
            <BounceLoader color="#a89932" loading={loading} size={150} />
        </div>
    );
};

export default Loader;