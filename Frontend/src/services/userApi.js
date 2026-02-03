import { springInstance } from './axiosInstance';

export const userApi = {
    getProfile: async (id) => {
        const response = await springInstance.get(`/users/${id}/profile`);
        return response.data;
    },

    update: async (id, data) => {
        const response = await springInstance.put(`/users/${id}`, data);
        return response.data;
    },

    changePassword: async (data) => {
        const response = await springInstance.post(`/users/change_password`, data);
        return response.data;
    },

    // ADMIN ONLY
    getAll: async () => {
        const response = await springInstance.get('/users');
        return response.data;
    },

    delete: async (id) => {
        const response = await springInstance.delete(`/users/${id}`);
        return response.data;
    }
};
