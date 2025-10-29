import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/home/HomePage';
import AdminLayout from './components/AdminLayout';
import UserLayout from './layouts/UserLayout';
import MenuManagement from './pages/admin/MenuManagement';
import TableManagement from './pages/admin/TableManagement';
import UserManagement from './pages/admin/UserManagement';
import OrderManagement from './pages/admin/OrderManagement';
import Dashboard from './pages/admin/Dashboard';
import MenuPage from './pages/user/MenuPage';
import BookingPage from './pages/user/BookingPage';

function App() {
  // Check if user is authenticated
  const isAuthenticated = () => {
    return localStorage.getItem('token') !== null;
  };

  // Check if user is admin
  const isAdmin = () => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    return user.role === 'ADMIN';
  };

  // Protected Route component
  const ProtectedRoute = ({ children, requireAdmin }) => {
    // if (!isAuthenticated()) {
    //   return <Navigate to="/login" />;
    // }

    // if (requireAdmin && !isAdmin()) {
    //   return <Navigate to="/user" />;
    // }

    return children;
  };

  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/" element={
          <UserLayout>
            <HomePage />
          </UserLayout>
        } />
        
        {/* Admin Routes */}
        <Route path="/admin" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminLayout>
              <Dashboard />
            </AdminLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/users" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminLayout>
              <UserManagement />
            </AdminLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/menu" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminLayout>
              <MenuManagement />
            </AdminLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/tables" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminLayout>
              <TableManagement />
            </AdminLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/orders" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminLayout>
              <OrderManagement />
            </AdminLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/settings" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminLayout>
              <div>Settings</div>
            </AdminLayout>
          </ProtectedRoute>
        } />

        {/* User Routes */}
        <Route path="/user" element={
          <ProtectedRoute requireAdmin={false}>
            <UserLayout>
              <div>User Dashboard</div>
            </UserLayout>
          </ProtectedRoute>
        } />
        <Route path="/menu" element={
          <UserLayout>
            <MenuPage />
          </UserLayout>
        } />
        <Route path="/book" element={
          <UserLayout>
            <BookingPage />
          </UserLayout>
        } />
      </Routes>
    </Router>
  );
}

export default App;
