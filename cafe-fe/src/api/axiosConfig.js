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
        console.log(' API Request:', {
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
        console.log(' API Success:', {
            url: response.config?.url,
            status: response.status
        });
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            // Log detailed error for debugging
            console.error(' 401 Unauthorized:', {
                url: error.config?.url,
                method: error.config?.method,
                data: error.config?.data,
                headers: error.config?.headers,
                response: error.response?.data,
                hasToken: !!localStorage.getItem('token'),
                currentPath: window.location.pathname
            });
            
            // Check if token exists - if not, likely already logged out
            const hasToken = !!localStorage.getItem('token');
            
            if (hasToken) {
                // Token exists but 401 - token might be invalid or expired
                console.warn(' Token exists but got 401 - token might be expired');
                console.warn(' Clearing auth...');
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                
                // Only redirect to login if:
                // 1. Not already on login page
                // 2. AND (on admin page OR explicit auth error)
                const isOnLoginPage = window.location.pathname.includes('/login');
                const isOnAdminPage = window.location.pathname.includes('/admin');
                const isAuthError = error.response?.data?.message?.toLowerCase().includes('token') || 
                                   error.response?.data?.error?.toLowerCase().includes('unauthorized');
                
                if (!isOnLoginPage && (isOnAdminPage || isAuthError)) {
                    console.warn('Will redirect to login in 1.5s...');
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 1500);
                } else {
                    console.warn(' Got 401 but not redirecting (user page + not auth error)');
                    // Show toast to user
                    console.warn(' User should re-login manually');
                }
            } else {
                // No token - already cleared, just log
                console.warn(' Got 401 but token already cleared');
            }
        }
        return Promise.reject(error);
    }
);

export default api;