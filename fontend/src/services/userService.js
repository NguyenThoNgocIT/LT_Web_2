// src/services/userService.js
import { api } from './api';

export const userService = {
    // Admin endpoints
    getAllUsers: async () => {
        return api.get('/api/admin/users');
    },

    createUser: async (userData, companyId) => {
        const params = companyId ? { companyId } : {};
        return api.post('/api/admin/users/save', userData, { params });
    },

    updateUser: async (id, userData) => {
        return api.put(`/api/admin/users/update/${id}`, userData);
    },

    deleteUser: async (id) => {
        return api.delete(`/api/admin/users/delete/${id}`);
    },

    // User endpoints
    getCurrentUserProfile: async () => {
        return api.get('/api/user/profile');
    },

    updateUserProfile: async (userData) => {
        return api.put('/api/user/profile', userData);
    }
};