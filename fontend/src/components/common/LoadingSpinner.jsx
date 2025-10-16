// src/components/common/LoadingSpinner.jsx
import React from 'react';

const LoadingSpinner = ({ size = 'medium', className = '' }) => {
    const sizeClasses = {
        small: 'h-4 w-4',
        medium: 'h-8 w-8',
        large: 'h-12 w-12'
    };

    return (
        <div className="flex items-center justify-center">
            <div
                className={`animate-spin rounded-full border-2 border-indigo-600 border-t-transparent ${sizeClasses[size]} ${className}`}
            ></div>
        </div>
    );
};

export default LoadingSpinner;