// src/components/ui/Modal.jsx
import React from 'react';

const Modal = ({
                   isOpen,
                   onClose,
                   children,
                   title,
                   size = 'medium',
                   className = ''
               }) => {
    if (!isOpen) return null;

    const sizeClasses = {
        small: 'max-w-md',
        medium: 'max-w-lg',
        large: 'max-w-2xl'
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className={`${sizeClasses[size]} w-full ${className}`}>
                <div className="bg-white rounded-lg shadow-xl">
                    {title && (
                        <div className="px-6 py-4 border-b border-gray-200">
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg font-medium text-gray-900">{title}</h3>
                                <button
                                    onClick={onClose}
                                    className="text-gray-400 hover:text-gray-500 focus:outline-none"
                                >
                                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>
                        </div>
                    )}
                    <div className="px-6 py-4">
                        {children}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Modal;