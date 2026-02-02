import { authInstance, springInstance } from './axiosInstance';

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

    // Get User Profile -> Maps to Spring Boot GET /api/users/{id}
    getUserProfile: async (userId) => {
        const response = await springInstance.get(`/users/${userId}`);
        return response.data;
    },

    // Update Profile -> Maps to Spring Boot PUT /api/users/{id}
    updateProfile: async (userId, data) => {
        // data: { name, email, mobile, role, ... }
        // Spring Boot expects the ID in path and Full DTO in body
        const response = await springInstance.put(`/users/${userId}`, data);
        return response.data;
    }
};
