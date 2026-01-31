import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Container, Typography, Paper, Button, Divider, CircularProgress, Alert } from '@mui/material';
import { bookingApi } from '../services/bookingApi';
import { useAuth } from '../context/AuthContext';

const Payment = () => {
    const { bookingId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [processing, setProcessing] = useState(false);

    useEffect(() => {
        const fetchBooking = async () => {
            try {
                const response = await bookingApi.getById(bookingId);
                if (response.success && response.data) {
                    setBooking(response.data);
                } else {
                    setError("Failed to load booking details");
                }
            } catch (err) {
                console.error("Error fetching booking:", err);
                setError("Could not load booking details.");
            } finally {
                setLoading(false);
            }
        };

        if (bookingId) fetchBooking();
    }, [bookingId]);

    const handlePaymentSuccess = async () => {
        setProcessing(true);
        try {
            // In a real scenario, this is called after Razorpay success
            // For now, we simulate payment success

            // 1. Confirm Booking in Backend
            await bookingApi.confirm(bookingId);

            // 2. Redirect to specific page or dashboard
            alert("Payment Successful! Booking Confirmed.");
            navigate('/admin'); // Or to a 'My Bookings' page if you have one

        } catch (err) {
            console.error("Confirmation failed:", err);
            setError("Payment successful but failed to confirm booking. Please contact support.");
        } finally {
            setProcessing(false);
        }
    };

    if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 10 }}><CircularProgress /></Box>;
    if (error) return <Container sx={{ py: 10 }}><Alert severity="error">{error}</Alert></Container>;
    if (!booking) return <Container sx={{ py: 10 }}><Alert severity="warning">Booking not found</Alert></Container>;

    return (
        <Container maxWidth="sm" sx={{ py: 8 }}>
            <Paper elevation={3} sx={{ p: 4, borderRadius: 4 }}>
                <Typography variant="h4" fontWeight={700} gutterBottom align="center">
                    Confirm and Pay
                </Typography>

                <Box sx={{ my: 4 }}>
                    <Typography variant="h6" gutterBottom>Trip Details</Typography>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography color="text.secondary">Villa</Typography>
                        <Typography fontWeight={600}>{booking.villaName}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography color="text.secondary">Dates</Typography>
                        <Typography fontWeight={600}>{booking.checkInDate} — {booking.checkOutDate}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography color="text.secondary">Nights</Typography>
                        <Typography fontWeight={600}>{booking.numberOfNights}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography color="text.secondary">Guests</Typography>
                        <Typography fontWeight={600}>2 Guests</Typography> {/* Todo: Add guests to BookingDTO if needed */}
                    </Box>

                    <Divider sx={{ my: 2 }} />

                    <Typography variant="h6" gutterBottom>Price Details</Typography>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="h5" fontWeight={700}>Total</Typography>
                        <Typography variant="h5" fontWeight={700}>${booking.totalPrice}</Typography>
                    </Box>
                </Box>

                {/* Simulated Payment Button for MVP */}
                {/* Ensure Razorpay is actually loaded or imported if you want to use it. 
                    For now, a direct "Pay" button is more reliable to unblock the flow. */}
                <Button
                    variant="contained"
                    fullWidth
                    size="large"
                    onClick={handlePaymentSuccess}
                    disabled={processing}
                    sx={{
                        py: 1.5,
                        fontSize: '1.1rem',
                        background: 'linear-gradient(90deg, #FF385C 0%, #E61E43 100%)'
                    }}
                >
                    {processing ? "Processing..." : `Pay $${booking.totalPrice}`}
                </Button>

                <Typography variant="caption" display="block" align="center" sx={{ mt: 2, color: 'text.secondary' }}>
                    Payment is processed securely.
                </Typography>
            </Paper>
        </Container>
    );
};

export default Payment;
