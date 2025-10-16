// src/utils/constants.js
export const ROLES = {
    ADMIN: 'ADMIN',
    USER: 'USER'
};

export const API_ENDPOINTS = {
    AUTH: {
        LOGIN: '/api/auth/login',
        REGISTER: '/api/auth/register'
    },
    ADMIN: {
        USERS: '/api/admin/users',
        COMPANIES: '/api/admin/companies',
        DASHBOARD: '/api/admin/dashboard'
    },
    USER: {
        PROFILE: '/api/user/profile'
    }
};

export const ROUTES = {
    PUBLIC: {
        LOGIN: '/login',
        REGISTER: '/register'
    },
    ADMIN: {
        DASHBOARD: '/dashboard',
        USERS: '/admin/users',
        COMPANIES: '/admin/companies'
    },
    USER: {
        PROFILE: '/user/profile'
    },
    UNAUTHORIZED: '/unauthorized'
};

export const STORAGE_KEYS = {
    TOKEN: 'token',
    USER: 'user'
};