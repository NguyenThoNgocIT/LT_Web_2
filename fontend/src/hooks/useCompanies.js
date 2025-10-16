// src/hooks/useCompanies.js
import { useState, useEffect } from 'react';
import { companyService } from '../services/companyService';

export const useCompanies = () => {
    const [companies, setCompanies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchCompanies = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await companyService.getAllCompanies();
            setCompanies(response.data);
        } catch (err) {
            setError(err.message || 'Failed to fetch companies');
        } finally {
            setLoading(false);
        }
    };

    const createCompany = async (companyData) => {
        try {
            const response = await companyService.createCompany(companyData);
            setCompanies(prev => [...prev, response.data]);
            return response.data;
        } catch (err) {
            throw err;
        }
    };

    const deleteCompany = async (id) => {
        try {
            await companyService.deleteCompany(id);
            setCompanies(prev => prev.filter(company => company.id !== id));
        } catch (err) {
            throw err;
        }
    };

    useEffect(() => {
        fetchCompanies();
    }, []);

    return {
        companies,
        loading,
        error,
        fetchCompanies,
        createCompany,
        deleteCompany
    };
};