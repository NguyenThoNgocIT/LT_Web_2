// src/pages/admin/Dashboard.jsx
import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import Card from '../../components/ui/Card';
import Layout from '../../components/layout/Layout';

const Dashboard = () => {
    const [stats, setStats] = useState({ totalUsers: 0, totalCompanies: 0 });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const response = await api.get('/api/admin/dashboard');
                setStats(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching dashboard stats:', error);
                setLoading(false);
            }
        };

        fetchStats();
    }, []);

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
                <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
                <p className="text-gray-600">Welcome back! Here's what's happening.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Card>
                    <Card.Content className="flex items-center justify-between">
                        <div>
                            <p className="text-sm font-medium text-gray-600">Total Users</p>
                            <p className="text-3xl font-bold text-gray-900">{stats.totalUsers}</p>
                        </div>
                        <div className="bg-indigo-100 p-3 rounded-full">
                            <svg className="h-8 w-8 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                            </svg>
                        </div>
                    </Card.Content>
                </Card>

                <Card>
                    <Card.Content className="flex items-center justify-between">
                        <div>
                            <p className="text-sm font-medium text-gray-600">Total Companies</p>
                            <p className="text-3xl font-bold text-gray-900">{stats.totalCompanies}</p>
                        </div>
                        <div className="bg-green-100 p-3 rounded-full">
                            <svg className="h-8 w-8 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                            </svg>
                        </div>
                    </Card.Content>
                </Card>
            </div>
        </Layout>
    );
};

export default Dashboard;