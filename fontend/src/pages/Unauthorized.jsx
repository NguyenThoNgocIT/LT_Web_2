// src/pages/Unauthorized.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../components/ui/Button';

const Unauthorized = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8 text-center">
                <div>
                    <h1 className="text-4xl font-bold text-gray-900">403</h1>
                    <h2 className="mt-6 text-2xl font-bold text-gray-900">Access Denied</h2>
                    <p className="mt-2 text-gray-600">
                        You don't have permission to access this page.
                    </p>
                </div>
                <Button onClick={() => navigate(-1)} variant="primary">
                    Go Back
                </Button>
            </div>
        </div>
    );
};

export default Unauthorized;