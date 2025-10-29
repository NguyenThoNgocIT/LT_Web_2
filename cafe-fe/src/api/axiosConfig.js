import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8088/api',
    headers: {
        'Content-Type': 'application/json',
    }
});

// Add a request interceptor
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        console.log('📤 API Request:', {
            url: config.url,
            method: config.method,
            hasToken: !!token,
            tokenPreview: token ? token.substring(0, 20) + '...' : null
        });
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add a response interceptor
api.interceptors.response.use(
    (response) => {
        console.log('✅ API Success:', {
            url: response.config?.url,
            status: response.status
        });
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            // Log detailed error for debugging
            console.error('🔴 401 Unauthorized:', {
                url: error.config?.url,
                method: error.config?.method,
                data: error.config?.data,
                headers: error.config?.headers,
                response: error.response?.data,
                hasToken: !!localStorage.getItem('token')
            });
            
            // Only redirect to login if it's an auth-related endpoint or token is truly invalid
            // Don't auto-redirect for other 401s that might be permission issues
            const authEndpoints = ['/auth/login', '/auth/register', '/user/profile'];
            const isAuthEndpoint = authEndpoints.some(endpoint => error.config?.url?.includes(endpoint));
            
            if (isAuthEndpoint || error.response?.data?.message?.includes('Token')) {
                console.warn('⚠️ Clearing auth and redirecting to login');
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
            } else {
                console.warn('⚠️ 401 error but not auto-redirecting - might be permission issue');
            }
        }
        return Promise.reject(error);
    }
);

export default api;