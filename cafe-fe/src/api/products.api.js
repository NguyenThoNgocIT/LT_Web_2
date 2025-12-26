import api from './axiosConfig';

export const getProducts = async () => {
    const res = await api.get('/products');
    return res.data;
};

export const getProduct = async (id) => {
    const res = await api.get(`/products/${id}`);
    return res.data;
};

export const createProduct = async (product) => {
    const res = await api.post('/products', product);
    return res.data;
};

export const updateProduct = async (id, product) => {
    const res = await api.put(`/products/${id}`, product);
    return res.data;
};

export const deleteProduct = async (id) => {
    const res = await api.delete(`/products/${id}`);
    return res.data;
};

export default { getProducts, getProduct, createProduct, updateProduct, deleteProduct };
