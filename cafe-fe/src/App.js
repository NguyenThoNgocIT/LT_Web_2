import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/home/HomePage';
import AdminLayout from './components/AdminLayout';
import UserLayout from './layouts/UserLayout';
import MenuManagement from './pages/admin/MenuManagement';
import TableManagement from './pages/admin/TableManagement';
// import UserManagement from './pages/admin/UserManagement'; // Disabled: backend doesn't have /api/users endpoints
import OrderManagement from './pages/admin/OrderManagement';
import Dashboard from './pages/admin/Dashboard';
import MenuPage from './pages/user/MenuPage';
import BookingPage from './pages/user/BookingPage';
import DebugAuth from './pages/admin/DebugAuth';

function App() {
  // Protected Route component
  const ProtectedRoute = ({ children, requireAdmin }) => {
    const hasToken = localStorage.getItem('token') !== null;
    const userStr = localStorage.getItem('user');
    
    console.log('🔒 ProtectedRoute check:', {
      requireAdmin,
      hasToken,
      userStr,
      timestamp: new Date().toISOString()
    });

    if (!hasToken) {
      console.warn('⚠️ No token found, redirecting to /login');
      return <Navigate to="/login" replace />;
    }

    if (requireAdmin) {
      try {
        const user = JSON.parse(userStr || '{}');
        const roles = Array.isArray(user.roles) ? user.roles : [];
        const isUserAdmin = roles.includes('ADMIN') || roles.includes('ROOT');
        
        console.log('🔒 Admin check:', {
          user,
          roles,
          isUserAdmin
        });

        if (!isUserAdmin) {
          console.warn('⚠️ User is not admin, redirecting to /');
          return <Navigate to="/" replace />;
        }
      } catch (e) {
        console.error('❌ Error parsing user from localStorage:', e);
        return <Navigate to="/login" replace />;
      }
    }

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
        {/* Temporarily disabled: backend doesn't have /api/users endpoints yet */}
        {/* <Route path="/admin/users" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminLayout>
              <UserManagement />
            </AdminLayout>
          </ProtectedRoute>
        } /> */}
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
        <Route path="/admin/debug" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminLayout>
              <DebugAuth />
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
         <Route path="/user/reservations" element={
           <ProtectedRoute requireAdmin={false}>
             <UserLayout>
               {require('./pages/user/ReservationHistory.jsx').default()}
             </UserLayout>
           </ProtectedRoute>
         } />
         <Route path="/user/orders" element={
           <ProtectedRoute requireAdmin={false}>
             <UserLayout>
               {require('./pages/user/OrderHistory.jsx').default()}
             </UserLayout>
           </ProtectedRoute>
         } />
      </Routes>
    </Router>
  );
}

export default App;
