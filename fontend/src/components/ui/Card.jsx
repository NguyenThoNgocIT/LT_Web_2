// src/components/ui/Card.jsx
import React from 'react';

const Card = ({ children, className = '', ...props }) => {
    return (
        <div
            className={`bg-white shadow rounded-lg border border-gray-200 overflow-hidden ${className}`}
            {...props}
        >
            {children}
        </div>
    );
};

const CardHeader = ({ children, className = '', ...props }) => {
    return (
        <div className={`px-6 py-4 border-b border-gray-200 ${className}`} {...props}>
            {children}
        </div>
    );
};

const CardContent = ({ children, className = '', ...props }) => {
    return (
        <div className={`px-6 py-4 ${className}`} {...props}>
            {children}
        </div>
    );
};

const CardFooter = ({ children, className = '', ...props }) => {
    return (
        <div className={`px-6 py-4 bg-gray-50 border-t border-gray-200 ${className}`} {...props}>
            {children}
        </div>
    );
};

Card.Header = CardHeader;
Card.Content = CardContent;
Card.Footer = CardFooter;

export default Card;