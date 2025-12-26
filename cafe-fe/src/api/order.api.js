import api from './axiosConfig';

// USER endpoints
export const createOrder = async (orderData) => {
    const response = await api.post('/user/orders', orderData);
    return response.data;
};

export const getMyOrders = async () => {
    const response = await api.get('/user/orders');
    return response.data;
};

export const getOrderById = async (id) => {
    const response = await api.get(`/user/orders/${id}`);
    return response.data;
};

// ADMIN endpoints
export const getOrders = async () => {
    const response = await api.get('/admin/orders');
    return response.data;
};

export const getOrderDetail = async (id) => {
    const response = await api.get(`/admin/orders/${id}`);
    return response.data;
};

export const updateOrderStatus = async (id, status) => {
    const response = await api.put(`/admin/orders/${id}/status`, { status });
    return response.data;
};

// Note: /orders/recent endpoint doesn't exist in backend
export const getRecentOrders = async () => {
    console.warn('getRecentOrders: endpoint not implemented in backend');
    return [];
};