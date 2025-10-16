// src/components/layout/Sidebar.jsx
import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const Sidebar = () => {
    const { user } = useAuth();

    const isAdmin = user?.roles?.includes('ADMIN');
    const isUser = user?.roles?.includes('USER');

    const adminLinks = [
        { name: 'Dashboard', path: '/dashboard' },
        { name: 'Users', path: '/admin/users' },
        { name: 'Companies', path: '/admin/companies' }
    ];

    const userLinks = [
        { name: 'Profile', path: '/user/profile' }
    ];

    return (
        <div className="hidden md:flex md:flex-shrink-0">
            <div className="flex flex-col w-64">
                <div className="flex flex-col flex-grow pt-5 pb-4 overflow-y-auto bg-gray-800">
                    <div className="flex items-center flex-shrink-0 px-4">
                        <span className="text-white text-lg font-bold">LT Web2</span>
                    </div>
                    <nav className="flex-1 px-2 mt-5 space-y-1">
                        {isAdmin && adminLinks.map((link) => (
                            <NavLink
                                key={link.path}
                                to={link.path}
                                className={({ isActive }) =>
                                    `text-gray-300 hover:bg-gray-700 hover:text-white group flex items-center px-2 py-2 text-sm font-medium rounded-md ${
                                        isActive ? 'bg-gray-900 text-white' : ''
                                    }`
                                }
                            >
                                {link.name}
                            </NavLink>
                        ))}
                        {isUser && userLinks.map((link) => (
                            <NavLink
                                key={link.path}
                                to={link.path}
                                className={({ isActive }) =>
                                    `text-gray-300 hover:bg-gray-700 hover:text-white group flex items-center px-2 py-2 text-sm font-medium rounded-md ${
                                        isActive ? 'bg-gray-900 text-white' : ''
                                    }`
                                }
                            >
                                {link.name}
                            </NavLink>
                        ))}
                    </nav>
                </div>
            </div>
        </div>
    );
};

export default Sidebar;