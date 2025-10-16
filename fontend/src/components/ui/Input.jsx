// src/components/ui/Input.jsx
import React from 'react';

const Input = ({
                   label,
                   id,
                   type = 'text',
                   value,
                   onChange,
                   placeholder,
                   required = false,
                   error = '',
                   className = '',
                   ...props
               }) => {
    return (
        <div className="space-y-1">
            {label && (
                <label htmlFor={id} className="block text-sm font-medium text-gray-700">
                    {label} {required && <span className="text-red-500">*</span>}
                </label>
            )}
            <input
                id={id}
                type={type}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                required={required}
                className={`block w-full px-3 py-2 border ${
                    error ? 'border-red-300' : 'border-gray-300'
                } rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm ${className}`}
                {...props}
            />
            {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
        </div>
    );
};

export default Input;