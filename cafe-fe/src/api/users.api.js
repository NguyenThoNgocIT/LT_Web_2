import api from './axiosConfig';

export const getUsers = async () => {
    const res = await api.get('/users');
    return res.data;
};

export const createUser = async (user) => {
    const res = await api.post('/users', user);
    return res.data;
};

export const deleteUser = async (id) => {
    const res = await api.delete(`/users/${id}`);
    return res.data;
};

export default { getUsers, createUser, deleteUser };
