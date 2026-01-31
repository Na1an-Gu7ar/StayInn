
import { springInstance } from './axiosInstance';

export const ratingApi = {
    create: async (data) => {
        const response = await springInstance.post('/ratings', data);
        return response.data;
    },

    getByVillaId: async (villaId) => {
        const response = await springInstance.get(`/ratings/villa/${villaId}`);
        return response.data;
    },

    checkUserRating: async (userId, villaId) => {
        const response = await springInstance.get(`/ratings/check`, { params: { userId, villaId } });
        return response.data;
    }
};
