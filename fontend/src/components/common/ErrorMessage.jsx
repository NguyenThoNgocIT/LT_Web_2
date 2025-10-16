// src/components/common/ErrorMessage.jsx
import React from 'react';

const ErrorMessage = ({ message, className = '' }) => {
    if (!message) return null;

    return (
        <div className={`bg-red-50 text-red-700 p-3 rounded-md text-sm ${className}`}>
            {message}
        </div>
    );
};

export default ErrorMessage;