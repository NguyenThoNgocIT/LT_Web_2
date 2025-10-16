// src/components/ui/Button.jsx
import React from 'react';

const Button = ({
                    children,
                    onClick,
                    type = 'button',
                    variant = 'primary',
                    size = 'medium',
                    disabled = false,
                    className = '',
                    ...props
                }) => {
    const baseClasses = 'inline-flex items-center justify-center font-medium rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 transition-colors';

    const variants = {
        primary: 'bg-indigo-600 text-white hover:bg-indigo-700 focus:ring-indigo-500',
        secondary: 'bg-gray-600 text-white hover:bg-gray-700 focus:ring-gray-500',
        danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500',
        outline: 'border border-gray-300 text-gray-700 hover:bg-gray-50 focus:ring-gray-500',
        ghost: 'text-gray-700 hover:bg-gray-100 focus:ring-gray-500'
    };

    const sizes = {
        small: 'px-3 py-1.5 text-sm',
        medium: 'px-4 py-2 text-sm',
        large: 'px-6 py-3 text-base'
    };

    const disabledClasses = disabled ? 'opacity-50 cursor-not-allowed' : '';

    return (
        <button
            type={type}
            onClick={onClick}
            disabled={disabled}
            className={`${baseClasses} ${variants[variant]} ${sizes[size]} ${disabledClasses} ${className}`}
            {...props}
        >
            {children}
        </button>
    );
};

export default Button;