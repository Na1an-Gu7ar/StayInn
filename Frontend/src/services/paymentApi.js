import { springInstance } from './axiosInstance';

export const paymentApi = {
    // Standard Payment Endpoints
    create: async (data) => {
        const response = await springInstance.post('/payments', data);
        return response.data;
    },

    getById: async (id) => {
        const response = await springInstance.get(`/payments/${id}`);
        return response.data;
    },

    getByBookingId: async (bookingId) => {
        const response = await springInstance.get(`/payments/booking/${bookingId}`);
        return response.data;
    },

    // Razorpay Endpoints
    createRazorpayOrder: async (bookingId) => {
        const response = await springInstance.post(`/payments/razorpay/create-order/${bookingId}`);
        return response.data;
    },

    verifyRazorpayPayment: async (data) => {
        const response = await springInstance.post('/payments/razorpay/verify', data);
        return response.data;
    }
};
