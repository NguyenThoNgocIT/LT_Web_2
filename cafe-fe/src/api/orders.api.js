import api from './axiosConfig';

export const createOrder = async (order) => {
    const res = await api.post('/orders', order);
    return res.data;
};

export const getOrders = async () => {
    const res = await api.get('/orders');
    return res.data;
};

export const getRecentOrders = async () => {
    const res = await api.get('/orders/recent');
    return res.data;
};

export const updateOrderStatus = async (id, status) => {
    const res = await api.put(`/orders/${id}/status`, { status });
    return res.data;
};

export default { createOrder, getOrders, getRecentOrders, updateOrderStatus };
