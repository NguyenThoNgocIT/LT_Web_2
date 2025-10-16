// src/pages/admin/Companies.jsx
import React, { useState, useEffect } from 'react';
import { companyService } from '../../services/companyService';
import Layout from '../../components/layout/Layout';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';

const Companies = () => {
    const [companies, setCompanies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showAddModal, setShowAddModal] = useState(false);
    const [formData, setFormData] = useState({ companyName: '' });
    const [error, setError] = useState('');

    useEffect(() => {
        fetchCompanies();
    }, []);

    const fetchCompanies = async () => {
        try {
            const response = await companyService.getAllCompanies();
            setCompanies(response.data);
            setLoading(false);
        } catch (error) {
            console.error('Error fetching companies:', error);
            setLoading(false);
        }
    };

    const handleAddCompany = async (e) => {
        e.preventDefault();
        setError('');

        try {
            await companyService.createCompany(formData);
            setShowAddModal(false);
            setFormData({ companyName: '' });
            fetchCompanies();
        } catch (error) {
            setError(error.response?.data?.message || 'Failed to add company');
        }
    };

    const handleDeleteCompany = async (id) => {
        if (window.confirm('Are you sure you want to delete this company?')) {
            try {
                await companyService.deleteCompany(id);
                fetchCompanies();
            } catch (error) {
                console.error('Error deleting company:', error);
            }
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
            <div className="mb-6 flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Companies Management</h1>
                    <p className="text-gray-600">Manage all companies</p>
                </div>
                <Button onClick={() => setShowAddModal(true)} variant="primary">
                    Add Company
                </Button>
            </div>

            <Card>
                <Card.Content>
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Company Name</th>
                                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                            </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                            {companies.map((company) => (
                                <tr key={company.id}>
                                    <td className="px-6 py-4 whitespace-nowrap">{company.companyName}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <Button
                                            variant="danger"
                                            size="small"
                                            onClick={() => handleDeleteCompany(company.id)}
                                        >
                                            Delete
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </Card.Content>
            </Card>

            {/* Add Company Modal */}
            {showAddModal && (
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
                                    value={formData.companyName}
                                    onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                                    required
                                />

                                <div className="flex justify-end space-x-3">
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={() => {
                                            setShowAddModal(false);
                                            setFormData({ companyName: '' });
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

export default Companies;