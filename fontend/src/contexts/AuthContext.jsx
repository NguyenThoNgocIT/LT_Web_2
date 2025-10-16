// src/contexts/AuthContext.jsx
import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';
import { api } from '../services/api';

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [authState, setAuthState] = useState({
        token: null,
        user: null,
        loading: true,
        isAuthenticated: false
    });

    useEffect(() => {
        const initializeAuth = async () => {
            try {
                const token = localStorage.getItem('token');
                const userStr = localStorage.getItem('user');

                if (token && userStr) {
                    try {
                        const user = JSON.parse(userStr);
                        // Verify token is still valid
                        await api.get('/api/user/profile');
                        setAuthState({
                            token,
                            user,
                            loading: false,
                            isAuthenticated: true
                        });
                        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
                    } catch (error) {
                        // Token invalid, clear storage
                        localStorage.removeItem('token');
                        localStorage.removeItem('user');
                        setAuthState({
                            token: null,
                            user: null,
                            loading: false,
                            isAuthenticated: false
                        });
                    }
                } else {
                    setAuthState({
                        token: null,
                        user: null,
                        loading: false,
                        isAuthenticated: false
                    });
                }
            } catch (error) {
                setAuthState({
                    token: null,
                    user: null,
                    loading: false,
                    isAuthenticated: false
                });
            }
        };

        initializeAuth();
    }, []);

    const login = async (email, password) => {
        try {
            const response = await authService.login(email, password);
            const { token, user } = response.data;

            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify(user));

            api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

            setAuthState({
                token,
                user,
                loading: false,
                isAuthenticated: true
            });

            return { success: true, user };
        } catch (error) {
            throw error;
        }
    };

    const register = async (userData) => {
        try {
            const response = await authService.register(userData);
            return { success: true, user: response.data.user };
        } catch (error) {
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        delete api.defaults.headers.common['Authorization'];

        setAuthState({
            token: null,
            user: null,
            loading: false,
            isAuthenticated: false
        });
    };

    const value = {
        ...authState,
        login,
        register,
        logout
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}