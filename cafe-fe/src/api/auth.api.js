import api from './axiosConfig';

// Backend expects { email, password } for login
const toLoginPayload = (input, password) => {
    if (typeof input === 'object') {
        const obj = { ...input };
        // Support legacy 'username' field by mapping to 'email'
        if (obj.username && !obj.email) obj.email = obj.username;
        return { email: obj.email, password: obj.password ?? password };
    }
    return { email: input, password };
};

export const login = async (usernameOrCredentials, password) => {
    const payload = toLoginPayload(usernameOrCredentials, password);
    const response = await api.post('/auth/login', payload);
    return response.data;
};

export const register = async (userData) => {
    // Backend expects { email, name, password }
    const payload = {
        email: userData.email,
        name: userData.name || userData.fullName || userData.username || '',
        password: userData.password,
    };
    const response = await api.post('/auth/register', payload);
    return response.data;
};

export const getCurrentUser = async () => {
    // Backend profile endpoint is /user/profile
    const response = await api.get('/user/profile');
    return response.data;
};