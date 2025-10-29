import api from './axiosConfig';

// USER endpoints
export const getMenu = async () => {
    const response = await api.get('/user/menu');
    return response.data;
};

// ADMIN endpoints
export const getAllProducts = async () => {
    const response = await api.get('/admin/products');
    return response.data;
};

export const getProductById = async (id) => {
    const response = await api.get(`/admin/products/${id}`);
    return response.data;
};

export const createProduct = async (productData) => {
    const response = await api.post('/admin/products', productData);
    return response.data;
};

export const updateProduct = async (id, productData) => {
    const response = await api.put(`/admin/products/${id}`, productData);
    return response.data;
};

export const deleteProduct = async (id) => {
    const response = await api.delete(`/admin/products/${id}`);
    return response.data;
};

// Legacy upload - requires backend implementation
export const uploadProductImage = async (id, imageFile) => {
    const formData = new FormData();
    formData.append('image', imageFile);
    const response = await api.post(`/upload/products`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    return response.data;
};