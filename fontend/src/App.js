// src/App.jsx
import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import Dashboard from './pages/admin/Dashboard';
import Users from './pages/admin/User';
import Companies from './pages/admin/Companies';
import Profile from './pages/user/Profile';
import Unauthorized from './pages/Unauthorized';

// Protected Route Components
// Trong App.jsx, cập nhật ProtectedRoute component
const ProtectedRoute = ({ children, roles }) => {
    const { isAuthenticated, user, loading } = useAuth();

    // Handle loading state
    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>;
    }

    // Handle unauthenticated state
    if (!isAuthenticated || !user) {
        return <Navigate to="/login" replace />;
    }

    // Handle role checking - ensure user and roles exist
    if (roles && user.roles) {
        const hasRequiredRole = roles.some(role =>
            user.roles.includes(role) || user.roles.includes(`ROLE_${role}`)
        );

        if (!hasRequiredRole) {
            return <Navigate to="/unauthorized" replace />;
        }
    }

    return children;
};

const AdminRoute = ({ children }) => (
    <ProtectedRoute roles={['ADMIN']}>{children}</ProtectedRoute>
);

const UserRoute = ({ children }) => (
    <ProtectedRoute roles={['USER', 'ADMIN']}>{children}</ProtectedRoute>
);

// Role-based Redirect
// Cập nhật RoleBasedRedirect component
const RoleBasedRedirect = () => {
    const { user, loading, isAuthenticated } = useAuth();

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>;
    }

    if (!isAuthenticated || !user) {
        return <Navigate to="/login" replace />;
    }

    // Check roles safely
    const userRoles = user.roles || [];
    const isAdmin = userRoles.includes('ADMIN') || userRoles.includes('ROLE_ADMIN');

    if (isAdmin) {
        return <Navigate to="/admin/Dashboard" replace />;
    }

    return <Navigate to="/user/Profile" replace />;
};
function AppContent() {
    return (
        <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* User Routes - USER and ADMIN can access */}
            <Route path="/user/profile" element={
                <UserRoute>
                    <Profile />
                </UserRoute>
            } />

            {/* Admin Routes - Only ADMIN can access */}
            <Route path="/admin/dashboard" element={
                <AdminRoute>
                    <Dashboard />
                </AdminRoute>
            } />
            <Route path="/admin/users" element={
                <AdminRoute>
                    <Users />
                </AdminRoute>
            } />
            <Route path="/admin/companies" element={
                <AdminRoute>
                    <Companies />
                </AdminRoute>
            } />

            {/* Root redirect based on role */}
            <Route path="/" element={<RoleBasedRedirect />} />

            {/* Fallback */}
            <Route path="/unauthorized" element={<Unauthorized />} />
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}

function App() {
    return (
        <AuthProvider>
            <AppContent />
        </AuthProvider>
    );
}

export default App;