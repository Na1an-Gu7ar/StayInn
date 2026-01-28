import { authInstance } from './axiosInstance';

export const authApi = {
    // Login -> Maps to Node.js Backend POST /api/users/login
    login: async (credentials) => {
        // credentials: { email, password }
        const response = await authInstance.post('/users/login', credentials);
        return response.data;
    },

    // Register -> Maps to Node.js Backend POST /api/users/signup
    register: async (userData) => {
        // userData: { name, email, password, mobile, role }
        const response = await authInstance.post('/users/signup', userData);
        return response.data;
    },

    // Get User Profile -> Maps to Node.js Backend GET /api/profile
    getCurrentUser: async () => {
        const response = await authInstance.get('/profile');
        return response.data; // Expected: { message: "...", user: { ... } }
    }
};
