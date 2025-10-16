// src/services/companyService.js
import { api } from './api';

export const companyService = {
    // Admin endpoints
    getAllCompanies: async () => {
        return api.get('/api/admin/companies');
    },

    createCompany: async (companyData) => {
        return api.post('/api/admin/company/save', companyData);
    },

    deleteCompany: async (id) => {
        return api.delete(`/api/admin/company/delete/${id}`);
    },

    // User endpoints
    createCompanyAsUser: async (companyData) => {
        return api.post('/api/company/save', companyData);
    }
};