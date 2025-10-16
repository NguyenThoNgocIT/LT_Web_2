// src/hooks/useUsers.js
import { useState, useEffect } from 'react';
import { userService } from '../services/userService';

export const useUsers = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await userService.getAllUsers();
            setUsers(response.data);
        } catch (err) {
            setError(err.message || 'Failed to fetch users');
        } finally {
            setLoading(false);
        }
    };

    const createUser = async (userData, companyId) => {
        try {
            const response = await userService.createUser(userData, companyId);
            setUsers(prev => [...prev, response.data]);
            return response.data;
        } catch (err) {
            throw err;
        }
    };

    const updateUser = async (id, userData) => {
        try {
            const response = await userService.updateUser(id, userData);
            setUsers(prev =>
                prev.map(user => user.id === id ? response.data : user)
            );
            return response.data;
        } catch (err) {
            throw err;
        }
    };

    const deleteUser = async (id) => {
        try {
            await userService.deleteUser(id);
            setUsers(prev => prev.filter(user => user.id !== id));
        } catch (err) {
            throw err;
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    return {
        users,
        loading,
        error,
        fetchUsers,
        createUser,
        updateUser,
        deleteUser
    };
};