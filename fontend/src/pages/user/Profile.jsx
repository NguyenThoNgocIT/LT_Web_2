// src/pages/user/Profile.jsx
import React, { useState, useEffect } from 'react';
import { companyService } from '../../services/companyService';
import {userService} from "../../services/userService";
import Layout from '../../components/layout/Layout';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';

const Profile = () => {
    const [user, setUser] = useState(null);
    const [companies, setCompanies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showAddCompanyModal, setShowAddCompanyModal] = useState(false);
    const [formData, setFormData] = useState({ name: '', email: '', phone: '', companyId: '' });
    const [newCompanyData, setNewCompanyData] = useState({ companyName: '' });
    const [error, setError] = useState('');

    useEffect(() => {
        fetchUserProfile();
        fetchCompanies();
    }, []);

    const fetchUserProfile = async () => {
        try {
            const response = await userService.getCurrentUserProfile();
            setUser(response.data.user);
            setFormData({
                name: response.data.user.name,
                email: response.data.user.email,
                phone: response.data.user.phone || '',
                companyId: response.data.user.company?.id?.toString() || ''
            });
            setLoading(false);
        } catch (error) {
            console.error('Error fetching user profile:', error);
            setLoading(false);
        }
    };

    const fetchCompanies = async () => {
        try {
            const response = await companyService.getAllCompanies();
            setCompanies(response.data);
        } catch (error) {
            console.error('Error fetching companies:', error);
        }
    };

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const updateData = {
                name: formData.name,
                phone: formData.phone
            };

            if (formData.companyId) {
                updateData.companyId = parseInt(formData.companyId);
            }

            await userService.updateUserProfile(updateData);
            setShowEditModal(false);
            fetchUserProfile();
        } catch (error) {
            setError(error.response?.data?.error || 'Failed to update profile');
        }
    };

    const handleAddCompany = async (e) => {
        e.preventDefault();
        setError('');

        try {
            await companyService.createCompanyAsUser(newCompanyData);
            setShowAddCompanyModal(false);
            setNewCompanyData({ companyName: '' });
            fetchUserProfile();
            fetchCompanies();
        } catch (error) {
            setError(error.response?.data?.error || 'Failed to add company');
        }
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
            <div className="mb-6">
                <h1 className="text-2xl font-bold text-gray-900">User Profile</h1>
                <p className="text-gray-600">Manage your profile information</p>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2">
                    <Card>
                        <Card.Header>
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg font-medium text-gray-900">Profile Information</h3>
                                <Button onClick={() => setShowEditModal(true)} variant="outline">
                                    Edit Profile
                                </Button>
                            </div>
                        </Card.Header>
                        <Card.Content className="space-y-4">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Name</p>
                                <p className="text-lg text-gray-900">{user.name}</p>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-500">Email</p>
                                <p className="text-lg text-gray-900">{user.email}</p>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-500">Phone</p>
                                <p className="text-lg text-gray-900">{user.phone || '-'}</p>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-500">Company</p>
                                <p className="text-lg text-gray-900">{user.company?.name || '-'}</p>
                            </div>
                        </Card.Content>
                    </Card>
                </div>

                <div>
                    <Card>
                        <Card.Header>
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg font-medium text-gray-900">Companies</h3>
                                <Button onClick={() => setShowAddCompanyModal(true)} variant="outline" size="small">
                                    Add Company
                                </Button>
                            </div>
                        </Card.Header>
                        <Card.Content>
                            <div className="space-y-2">
                                {companies.length > 0 ? (
                                    companies.map((company) => (
                                        <div key={company.id} className="p-3 bg-gray-50 rounded-md">
                                            <p className="font-medium">{company.companyName}</p>
                                        </div>
                                    ))
                                ) : (
                                    <p className="text-gray-500 text-center">No companies available</p>
                                )}
                            </div>
                        </Card.Content>
                    </Card>
                </div>
            </div>

            {/* Edit Profile Modal */}
            {showEditModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <Card className="w-full max-w-md">
                        <Card.Header>
                            <h3 className="text-lg font-medium text-gray-900">Edit Profile</h3>
                        </Card.Header>
                        <Card.Content>
                            <form onSubmit={handleUpdateProfile} className="space-y-4">
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
                                    disabled
                                />
                                <Input
                                    label="Phone"
                                    value={formData.phone}
                                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                                />
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Company</label>
                                    <select
                                        value={formData.companyId}
                                        onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                                        className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                    >
                                        <option value="">Select a company</option>
                                        {companies.map((company) => (
                                            <option key={company.id} value={company.id}>
                                                {company.companyName}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="flex justify-end space-x-3">
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={() => {
                                            setShowEditModal(false);
                                            setError('');
                                        }}
                                    >
                                        Cancel
                                    </Button>
                                    <Button type="submit" variant="primary">
                                        Update Profile
                                    </Button>
                                </div>
                            </form>
                        </Card.Content>
                    </Card>
                </div>
            )}

            {/* Add Company Modal */}
            {showAddCompanyModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <Card className="w-full max-w-md">
                        <Card.Header>
                            <h3 className="text-lg font-medium text-gray-900">Add New Company</h3>
                        </Card.Header>
                        <Card.Content>
                            <form onSubmit={handleAddCompany} className="space-y-4">
                                {error && <div className="bg-red-50 text-red-700 p-3 rounded-md text-sm">{error}</div>}

                                <Input
                                    label="Company Name"
                                    value={newCompanyData.companyName}
                                    onChange={(e) => setNewCompanyData({ ...newCompanyData, companyName: e.target.value })}
                                    required
                                />

                                <div className="flex justify-end space-x-3">
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={() => {
                                            setShowAddCompanyModal(false);
                                            setNewCompanyData({ companyName: '' });
                                            setError('');
                                        }}
                                    >
                                        Cancel
                                    </Button>
                                    <Button type="submit" variant="primary">
                                        Add Company
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

export default Profile;