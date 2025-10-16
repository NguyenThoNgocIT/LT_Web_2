// src/pages/admin/Users.jsx
import React, { useState, useEffect } from 'react';
import { userService } from '../../services/userService';
import Layout from '../../components/layout/Layout';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';

const Users = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showAddModal, setShowAddModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [formData, setFormData] = useState({ name: '', email: '', password: '', phone: '', companyId: '' });
    const [companies, setCompanies] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchUsers();
        fetchCompanies();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await userService.getAllUsers();
            setUsers(response.data);
            setLoading(false);
        } catch (error) {
            console.error('Error fetching users:', error);
            setLoading(false);
        }
    };

    const fetchCompanies = async () => {
        try {
            const response = await userService.getAllUsers(); // This should be companyService, but using userService for now
            // In a real app, you'd call companyService.getAllCompanies()
            setCompanies([]);
        } catch (error) {
            console.error('Error fetching companies:', error);
        }
    };

    const handleAddUser = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const userData = {
                name: formData.name,
                email: formData.email,
                password: formData.password,
                phone: formData.phone
            };

            const companyId = formData.companyId ? parseInt(formData.companyId) : null;
            await userService.createUser(userData, companyId);
            setShowAddModal(false);
            setFormData({ name: '', email: '', password: '', phone: '', companyId: '' });
            fetchUsers();
        } catch (error) {
            setError(error.response?.data?.message || 'Failed to add user');
        }
    };

    const handleUpdateUser = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const userData = {
                name: formData.name,
                email: formData.email,
                phone: formData.phone,
                company: formData.companyId ? { id: parseInt(formData.companyId) } : null
            };

            await userService.updateUser(selectedUser.id, userData);
            setShowEditModal(false);
            setFormData({ name: '', email: '', password: '', phone: '', companyId: '' });
            setSelectedUser(null);
            fetchUsers();
        } catch (error) {
            setError(error.response?.data?.message || 'Failed to update user');
        }
    };

    const handleDeleteUser = async (id, email) => {
        if (email === 'admin@example.com') {
            alert('Cannot delete default admin account');
            return;
        }

        if (window.confirm('Are you sure you want to delete this user?')) {
            try {
                await userService.deleteUser(id);
                fetchUsers();
            } catch (error) {
                console.error('Error deleting user:', error);
            }
        }
    };

    const openEditModal = (user) => {
        setSelectedUser(user);
        setFormData({
            name: user.name,
            email: user.email,
            phone: user.phone || '',
            companyId: user.company?.id?.toString() || ''
        });
        setShowEditModal(true);
    };

    if (loading) {
        return (
            <Layout>
                <div className="flex items-center justify-center min-h-64">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
                </div>
            </Layout>
        );
    }

    return (
        <Layout>
            <div className="mb-6 flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Users Management</h1>
                    <p className="text-gray-600">Manage all user accounts</p>
                </div>
                <Button onClick={() => setShowAddModal(true)} variant="primary">
                    Add User
                </Button>
            </div>

            <Card>
                <Card.Content>
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Phone</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Company</th>
                                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                            </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                            {users.map((user) => (
                                <tr key={user.id}>
                                    <td className="px-6 py-4 whitespace-nowrap">{user.name}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{user.email}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{user.phone || '-'}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{user.company?.companyName || '-'}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        {user.email !== 'admin@example.com' && (
                                        <Button
                                            variant="outline"
                                            size="small"
                                            onClick={() => openEditModal(user)}
                                            className="mr-2"
                                        >
                                            Edit
                                        </Button>
                                        )}
                                        {/* 👇 ẨN NÚT DELETE CHO TÀI KHOẢN ADMIN */}
                                        {user.email !== 'admin@example.com' && (
                                            <Button
                                                variant="danger"
                                                size="small"
                                                onClick={() => handleDeleteUser(user.id, user.email)}
                                            >
                                                Delete
                                            </Button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </Card.Content>
            </Card>

            {/* Add User Modal */}
            {showAddModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <Card className="w-full max-w-md">
                        <Card.Header>
                            <h3 className="text-lg font-medium text-gray-900">Add New User</h3>
                        </Card.Header>
                        <Card.Content>
                            <form onSubmit={handleAddUser} className="space-y-4">
                                {error && <div className="bg-red-50 text-red-700 p-3 rounded-md text-sm">{error}</div>}

                                <Input
                                    label="Name"
                                    value={formData.name}
                                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                    required
                                />
                                <Input
                                    label="Email"
                                    type="email"
                                    value={formData.email}
                                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                    required
                                />
                                <Input
                                    label="Password"
                                    type="password"
                                    value={formData.password}
                                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                    required
                                />
                                <Input
                                    label="Phone"
                                    value={formData.phone}
                                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                                />
                                <Input
                                    label="Company ID (optional)"
                                    value={formData.companyId}
                                    onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                                    type="number"
                                />

                                <div className="flex justify-end space-x-3">
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={() => {
                                            setShowAddModal(false);
                                            setFormData({ name: '', email: '', password: '', phone: '', companyId: '' });
                                            setError('');
                                        }}
                                    >
                                        Cancel
                                    </Button>
                                    <Button type="submit" variant="primary">
                                        Add User
                                    </Button>
                                </div>
                            </form>
                        </Card.Content>
                    </Card>
                </div>
            )}

            {/* Edit User Modal */}
            {showEditModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <Card className="w-full max-w-md">
                        <Card.Header>
                            <h3 className="text-lg font-medium text-gray-900">Edit User</h3>
                        </Card.Header>
                        <Card.Content>
                            <form onSubmit={handleUpdateUser} className="space-y-4">
                                {error && <div className="bg-red-50 text-red-700 p-3 rounded-md text-sm">{error}</div>}

                                <Input
                                    label="Name"
                                    value={formData.name}
                                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                    required
                                />
                                <Input
                                    label="Email"
                                    type="email"
                                    value={formData.email}
                                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                    required
                                />
                                <Input
                                    label="Phone"
                                    value={formData.phone}
                                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                                />
                                <Input
                                    label="Company ID (optional)"
                                    value={formData.companyId}
                                    onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                                    type="number"
                                />

                                <div className="flex justify-end space-x-3">
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={() => {
                                            setShowEditModal(false);
                                            setFormData({ name: '', email: '', password: '', phone: '', companyId: '' });
                                            setSelectedUser(null);
                                            setError('');
                                        }}
                                    >
                                        Cancel
                                    </Button>
                                    <Button type="submit" variant="primary">
                                        Update User
                                    </Button>
                                </div>
                            </form>
                        </Card.Content>
                    </Card>
                </div>
            )}
        </Layout>
    );
};

export default Users;