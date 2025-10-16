// src/services/authService.js
import { api } from './api';

export const authService = {
    login: async (email, password) => {
        return api.post('/api/auth/login', { email, password });
    },

    register: async (userData) => {
        return api.post('/api/auth/register', userData);
    },

    getCurrentUser: async () => {
        return api.get('/api/user/profile');
    },

    updateUserProfile: async (userData) => {
        return api.put('/api/user/profile', userData);
    }
};