import { springInstance } from './axiosInstance';

export const bookingApi = {
    create: async (bookingData) => {
        const response = await springInstance.post('/bookings', bookingData);
        return response.data;
    },

    checkAvailability: async (availabilityData) => {
        const response = await springInstance.post('/bookings/check-availability', availabilityData);
        return response.data;
    },

    getById: async (id) => {
        const response = await springInstance.get(`/bookings/${id}`);
        return response.data;
    },

    getUserBookings: async (userId) => {
        const response = await springInstance.get(`/bookings/user/${userId}`);
        return response.data;
    },

    getUpcomingBookings: async (userId) => {
        const response = await springInstance.get(`/bookings/user/${userId}/upcoming`);
        return response.data;
    },

    cancel: async (id, reason) => {
        const response = await springInstance.patch(`/bookings/${id}/cancel`, null, { params: { reason } });
        return response.data;
    },

    // ADMIN ONLY
    getAll: async () => {
        const response = await springInstance.get('/bookings');
        return response.data;
    },

    updateStatus: async (id, status) => {
        // status is BookingUpdateStatusDTO
        const response = await springInstance.patch(`/bookings/${id}/status`, status);
        return response.data;
    }
};
