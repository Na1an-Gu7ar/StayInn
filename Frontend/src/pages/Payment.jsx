import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Container, Typography, Paper, Button, Divider, CircularProgress, Alert } from '@mui/material';
import { bookingApi } from '../services/bookingApi';
import { useAuth } from '../context/AuthContext';

import { paymentApi } from '../services/paymentApi';

const Payment = () => {
    const { bookingId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    // ... state ...
    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [processing, setProcessing] = useState(false);

    useEffect(() => {
        const fetchBooking = async () => {
            try {
                // We use bookingApi explicitly just to get details to show
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

    const loadRazorpayScript = () => {
        return new Promise((resolve) => {
            const script = document.createElement('script');
            script.src = 'https://checkout.razorpay.com/v1/checkout.js';
            script.onload = () => resolve(true);
            script.onerror = () => resolve(false);
            document.body.appendChild(script);
        });
    };

    const handlePayment = async () => {
        setProcessing(true);
        setError(null);

        try {
            // 1. Load Razorpay SDK
            const res = await loadRazorpayScript();
            if (!res) {
                setError("Razorpay SDK failed to load. Are you online?");
                setProcessing(false);
                return;
            }

            // 2. Create Order on Backend
            const orderRes = await paymentApi.createRazorpayOrder(bookingId);

            if (!orderRes.success) {
                throw new Error(orderRes.message || "Failed to create payment order");
            }

            const { orderId, amount, keyId, currency, companyName, villaName, userEmail, userPhone } = orderRes.data;

            // 3. Initialize Razorpay Options
            const options = {
                key: keyId,
                amount: amount.toString(),
                currency: currency,
                name: companyName || "StayInn",
                description: `Booking for ${villaName}`,
                order_id: orderId,
                handler: async function (response) {
                    try {
                        // 4. Verify Payment on Backend
                        const verifyPayload = {
                            razorpayOrderId: response.razorpay_order_id,
                            razorpayPaymentId: response.razorpay_payment_id,
                            razorpaySignature: response.razorpay_signature,
                        };

                        const verifyRes = await paymentApi.verifyRazorpayPayment(verifyPayload);

                        if (verifyRes.success) {
                            alert("Payment Successful!");
                            // Navigate to a success page or user bookings
                            navigate('/bookings'); // Or '/success'
                        } else {
                            alert("Payment verification failed. Please contact support.");
                            setError("Payment verification failed.");
                        }
                    } catch (verifyErr) {
                        console.error("Verification error:", verifyErr);
                        alert("Payment processed but verification failed on server.");
                        setError("Verification error.");
                    }
                },
                prefill: {
                    name: user?.name,
                    email: userEmail,
                    contact: userPhone,
                },
                notes: {
                    address: "StayInn Corporate Office",
                },
                theme: {
                    color: "#FF385C",
                },
                modal: {
                    ondismiss: () => {
                        setProcessing(false);
                        console.log("Payment modal closed");
                    }
                }
            };

            // @ts-ignore
            const paymentObject = new window.Razorpay(options);
            paymentObject.open();

        } catch (err) {
            console.error("Payment initiation failed:", err);
            setError(err.message || "Payment initiation failed");
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

                    <Divider sx={{ my: 2 }} />

                    <Typography variant="h6" gutterBottom>Price Details</Typography>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="h5" fontWeight={700}>Total</Typography>
                        <Typography variant="h5" fontWeight={700}>₹{booking.totalPrice}</Typography>
                    </Box>
                </Box>

                <Button
                    variant="contained"
                    fullWidth
                    size="large"
                    onClick={handlePayment}
                    disabled={processing}
                    sx={{
                        py: 1.5,
                        fontSize: '1.1rem',
                        background: 'linear-gradient(90deg, #FF385C 0%, #E61E43 100%)'
                    }}
                >
                    {processing ? "Processing..." : `Pay via Razorpay`}
                </Button>

                <Typography variant="caption" display="block" align="center" sx={{ mt: 2, color: 'text.secondary' }}>
                    Secured by Razorpay
                </Typography>
            </Paper>
        </Container>
    );
};

export default Payment;
