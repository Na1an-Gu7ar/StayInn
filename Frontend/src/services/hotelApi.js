import { springInstance } from './axiosInstance';

export const hotelApi = {
    getAll: async () => {
        const response = await springInstance.get('/villas');
        return response.data;
    },

    getById: async (id) => {
        const response = await springInstance.get(`/villas/${id}`);
        return response.data;
    },

    getDetails: async (id) => {
        const response = await springInstance.get(`/villas/${id}/details`);
        return response.data;
    },

    search: async (keyword) => {
        const response = await springInstance.get(`/villas/search`, { params: { keyword } });
        return response.data;
    },

    filter: async (criteria) => {
        const response = await springInstance.post('/villas/filter', criteria);
        return response.data;
    },

    // ADMIN ONLY
    create: async (data) => {
        const response = await springInstance.post('/villas', data);
        return response.data;
    },

    update: async (id, data) => {
        const response = await springInstance.put(`/villas/${id}`, data);
        return response.data;
    },

    delete: async (id) => {
        const response = await springInstance.delete(`/villas/${id}`);
        return response.data;
    },

    addImage: async (id, imageUrl) => {
        const response = await springInstance.post(`/villas/${id}/images`, { imageUrl });
        return response.data;
    },

    removeImage: async (id, imageUrl) => {
        const response = await springInstance.delete(`/villas/${id}/images`, { data: { imageUrl } });
        return response.data;
    }
};
