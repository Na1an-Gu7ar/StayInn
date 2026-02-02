
import { springInstance } from './axiosInstance';

export const bookingApi = {
    create: async (data) => {
        const response = await springInstance.post('/bookings', data);
        return response.data;
    },

    checkAvailability: async (data) => {
        const response = await springInstance.post('/bookings/check-availability', data);
        return response.data;
    },

    getUserBookings: async (userId) => {
        const response = await springInstance.get(`/bookings/user/${userId}`);
        return response.data;
    },

    getById: async (id) => {
        const response = await springInstance.get(`/bookings/${id}`);
        return response.data;
    },

    confirm: async (id) => {
        const response = await springInstance.patch(`/bookings/${id}/confirm`);
        return response.data;
    },

    getAll: async () => {
        const response = await springInstance.get('/bookings');
        return response.data;
    },

    cancel: async (id, reason = '') => {
        const response = await springInstance.patch(`/bookings/${id}/cancel`, null, { params: { reason } });
        return response.data;
    },

    delete: async (id) => {
        const response = await springInstance.delete(`/bookings/${id}`);
        return response.data;
    }
};
