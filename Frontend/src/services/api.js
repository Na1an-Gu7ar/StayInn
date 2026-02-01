import axios from 'axios';

// Defines the base URL for your Spring Boot Backend
// Change this if your backend runs on a different port or URL
const API_BASE_URL = "http://localhost:8080/api";

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

// Add a request interceptor to attach the JWT token if it exists
api.interceptors.request.use(
    (config) => {
        const user = JSON.parse(localStorage.getItem('user'));
        if (user && user.token) {
            config.headers.Authorization = `Bearer ${user.token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export const authAPI = {
    login: (credentials) => api.post('/auth/login', credentials),
    register: (userData) => api.post('/auth/register', userData),
};

export const hotelAPI = {
    getAll: () => api.get('/hotels'),
    getById: (id) => api.get(`/hotels/${id}`),
    create: (data) => api.post('/hotels', data),
    update: (id, data) => api.put(`/hotels/${id}`, data),
    delete: (id) => api.delete(`/hotels/${id}`),
};

export default api;
