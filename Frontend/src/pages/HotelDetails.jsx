import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
    Box, Container, Grid, Typography, Button, Paper, Rating, Divider,
    Chip, Avatar, TextField, IconButton, CircularProgress, Alert, Snackbar
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import PoolIcon from '@mui/icons-material/Pool';
import WifiIcon from '@mui/icons-material/Wifi';
import AcUnitIcon from '@mui/icons-material/AcUnit';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import SpaIcon from '@mui/icons-material/Spa';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import ShareIcon from '@mui/icons-material/Share';

import { motion } from 'framer-motion';
import { hotelApi } from '../services/hotelApi';
import { bookingApi } from '../services/bookingApi';
import { ratingApi } from '../services/ratingApi';
import { useAuth } from '../context/AuthContext';
import dayjs from 'dayjs';


const HotelDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    // Data State
    const [villa, setVilla] = useState(null);
    const [ratings, setRatings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Booking State
    const [checkIn, setCheckIn] = useState(null);
    const [checkOut, setCheckOut] = useState(null);
    const [guests, setGuests] = useState(2);
    const [bookingLoading, setBookingLoading] = useState(false);
    const [bookedDates, setBookedDates] = useState([]); // Store booked ranges

    // Rating Form State
    const [reviewText, setReviewText] = useState("");
    const [reviewScore, setReviewScore] = useState(5);
    const [submittingReview, setSubmittingReview] = useState(false);

    // UI Feedback
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'info' });

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                // Parallel fetch
                const [villaRes, ratingRes, bookingRes] = await Promise.all([
                    hotelApi.getDetails(id),
                    ratingApi.getByVillaId(id),
                    bookingApi.getByVilla(id)
                ]);

                if (villaRes.success) {
                    setVilla(villaRes.data);
                } else {
                    setError("Failed to load villa details");
                }

                if (ratingRes.success) {
                    setRatings(ratingRes.data);
                }

                if (bookingRes.success) {
                    // Filter valid bookings
                    const activeBookings = bookingRes.data.filter(b =>
                        b.status !== 'CANCELLED' && b.status !== 'FAILED'
                    );

                    const ranges = activeBookings.map(b => ({
                        start: dayjs(b.checkInDate),
                        end: dayjs(b.checkOutDate)
                    }));
                    setBookedDates(ranges);
                }

            } catch (err) {
                console.error(err);
                setError("Could not load villa data.");
            } finally {
                setLoading(false);
            }
        };

        if (id) fetchData();
    }, [id]);

    const handleBooking = async () => {
        if (!user) {
            setSnackbar({ open: true, message: "Please login again.", severity: "error" });
            setTimeout(() => navigate('/login'), 1500);
            return;
        }

        if (!checkIn || !checkOut) {
            setSnackbar({ open: true, message: "Please select check-in and check-out dates.", severity: "error" });
            return;
        }

        setBookingLoading(true);
        try {
            // Correct Payload for Create Booking (returns PENDING status)
            const payload = {
                villaId: parseInt(id),
                userId: user.user_id,
                checkInDate: checkIn.format('YYYY-MM-DD'),
                checkOutDate: checkOut.format('YYYY-MM-DD'),
            };
            console.log(payload);

            const response = await bookingApi.create(payload);
            setBookingLoading(false); // Stop loading before navigate

            if (response && response.data && response.data.id) {
                setSnackbar({ open: true, message: "Booking initiated! Redirecting to payment...", severity: "success" });
                // Short delay to show snackbar
                setTimeout(() => {
                    navigate(`/payment/${response.data.id}`);
                }, 1000);
            }
        } catch (err) {
            console.error(err);
            setBookingLoading(false);
            setSnackbar({ open: true, message: "Booking failed: " + (err.response?.data?.message || err.message), severity: "error" });
        }
    };

    const handleReviewSubmit = async () => {
        if (!user) {
            setSnackbar({ open: true, message: "Please login to leave a review.", severity: "warning" });
            return;
        }

        if (!reviewText.trim()) return;

        setSubmittingReview(true);
        try {
            const payload = {
                userId: user.user_id,
                villaId: parseInt(id),
                score: reviewScore,
                feedback: reviewText
            };

            const response = await ratingApi.create(payload);
            if (response.success) {
                setSnackbar({ open: true, message: "Review posted!", severity: "success" });
                setReviewText("");
                // Refresh ratings
                const newRatings = await ratingApi.getByVillaId(id);
                if (newRatings.success) setRatings(newRatings.data);
            }
        } catch (err) {
            setSnackbar({ open: true, message: "Failed to post review: " + (err.response?.data?.message || err.message), severity: "error" });
        } finally {
            setSubmittingReview(false);
        }
    };

    const calculateTotal = () => {
        if (!villa || !checkIn || !checkOut) return 0;
        const nights = checkOut.diff(checkIn, 'day');
        if (nights <= 0) return 0;
        return nights * villa.pricePerNight;
    };

    const nightCount = (checkIn && checkOut) ? checkOut.diff(checkIn, 'day') : 0;
    const totalCost = calculateTotal();

    // Disable Date Function
    const shouldDisableDate = (date) => {
        // Disable past dates
        if (date.isBefore(dayjs(), 'day')) return true;

        // Check against booked ranges
        return bookedDates.some(range => {
            // Check if date is within range [start, end)
            // Using logic: date >= start && date < end 
            return (date.isAfter(range.start, 'day') || date.isSame(range.start, 'day')) &&
                (date.isBefore(range.end, 'day'));
        });
    };

    if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 10 }}><CircularProgress /></Box>;
    if (error || !villa) return <Container sx={{ py: 10 }}><Alert severity="error">{error || "Villa not found"}</Alert></Container>;

    return (
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.5 }}
            >
                <Container maxWidth="xl" sx={{ py: 4 }}>
                    <Snackbar
                        open={snackbar.open}
                        autoHideDuration={6000}
                        onClose={() => setSnackbar({ ...snackbar, open: false })}
                    >
                        <Alert severity={['error', 'warning', 'info', 'success'].includes(snackbar.severity) ? snackbar.severity : 'info'}>{snackbar.message}</Alert>
                    </Snackbar>

                    {/* TITLE HEADER */}
                    <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                        <Box>
                            <Typography variant="h3" fontWeight={700} gutterBottom>
                                {villa.name}
                            </Typography>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary' }}>
                                <LocationOnIcon fontSize="small" />
                                <Typography variant="h6" fontWeight={500}>
                                    {villa.address}
                                </Typography>
                            </Box>
                        </Box>
                        <Box sx={{ display: 'flex', gap: 1 }}>
                            <IconButton><ShareIcon /></IconButton>
                            <IconButton><FavoriteBorderIcon /></IconButton>
                        </Box>
                    </Box>

                    {/* IMAGE GALLERY - Dynamic */}
                    <Box sx={{
                        display: 'grid',
                        gridTemplateColumns: { xs: '1fr', md: '2fr 1fr', lg: '2fr 1fr 1fr' },
                        gridTemplateRows: { xs: 'auto', md: '200px 200px' },
                        gap: 2,
                        mb: 6,
                        height: { md: '416px' }
                    }}>
                        <Box
                            component="img"
                            src={villa.imageUrls?.[0] || "https://via.placeholder.com/800x600?text=No+Image"}
                            sx={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 4, gridRow: { xs: 'span 1', md: 'span 2' }, cursor: 'pointer' }}
                        />
                        {/* Sub Images (up to 4) */}
                        {villa.imageUrls?.slice(1, 5).map((img, idx) => (
                            <Box
                                key={idx}
                                component="img"
                                src={img}
                                sx={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 4, display: { xs: 'none', md: 'block' } }}
                            />
                        ))}
                    </Box>

                    <Grid container spacing={4}>
                        {/* LEFT CONTENT */}
                        <Grid size={{ xs: 12, md: 8 }}>
                            {/* DESCRIPTION */}
                            <Box sx={{ mb: 5 }}>
                                <Typography variant="h5" fontWeight={700} gutterBottom>About this place</Typography>
                                <Typography variant="body1" color="text.secondary" paragraph sx={{ lineHeight: 1.8 }}>
                                    {villa.description}
                                </Typography>
                            </Box>

                            <Divider sx={{ mb: 5 }} />

                            {/* REVIEWS SECTION */}
                            <Box sx={{ mb: 5 }}>
                                <Typography variant="h5" fontWeight={700} gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <Avatar sx={{ bgcolor: 'primary.main', width: 28, height: 28, fontSize: 14 }}>
                                        {villa.averageRating ? villa.averageRating.toFixed(1) : "New"}
                                    </Avatar>
                                    {villa.totalRatings || 0} Reviews
                                </Typography>

                                {/* Review List */}
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, mt: 3, maxHeight: '400px', overflowY: 'auto', pr: 1 }}>
                                    {ratings.length > 0 ? ratings.map(review => (
                                        <Paper key={review.id} elevation={0} sx={{ p: 3, bgcolor: 'grey.50', borderRadius: 3 }}>
                                            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
                                                <Avatar>{review.userName?.[0] || 'U'}</Avatar>
                                                <Box>
                                                    <Typography fontWeight={600}>{review.userName || 'Anonymous'}</Typography>
                                                    <Typography variant="caption" color="text.secondary">{review.createdAt.split('T')[0]}</Typography>
                                                </Box>
                                            </Box>
                                            <Rating value={review.score} readOnly size="small" sx={{ mb: 1 }} />
                                            <Typography variant="body2">{review.feedback}</Typography>
                                        </Paper>
                                    )) : (
                                        <Typography color="text.secondary">No reviews yet.</Typography>
                                    )}
                                </Box>

                                {/* Add Review */}
                                <Box sx={{ mt: 4, p: 3, border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
                                    <Typography variant="h6" gutterBottom>Add a Review</Typography>
                                    <Rating
                                        value={reviewScore}
                                        onChange={(event, newValue) => setReviewScore(newValue)}
                                        sx={{ mb: 2 }}
                                    />
                                    <TextField
                                        fullWidth
                                        multiline
                                        rows={3}
                                        placeholder="Share your experience..."
                                        value={reviewText}
                                        onChange={(e) => setReviewText(e.target.value)}
                                        sx={{ mb: 2 }}
                                    />
                                    <Button
                                        variant="contained"
                                        onClick={handleReviewSubmit}
                                        disabled={submittingReview}
                                    >
                                        {submittingReview ? "Posting..." : "Post Review"}
                                    </Button>
                                </Box>
                            </Box>
                        </Grid>

                        {/* RIGHT BOOKING SIDEBAR */}
                        <Grid size={{ xs: 12, md: 4 }}>
                            <Paper
                                elevation={3}
                                sx={{ p: 3, borderRadius: 4, position: 'sticky', top: 100, border: '1px solid rgba(0,0,0,0.08)', zIndex: 10 }}
                            >
                                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'end', mb: 3 }}>
                                    <Typography variant="h4" fontWeight={700}>
                                        ${villa.pricePerNight} <Typography component="span" variant="body1" color="text.secondary">/ night</Typography>
                                    </Typography>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                        <FavoriteBorderIcon fontSize="small" />
                                        <Typography fontWeight={600}>{villa.averageRating ? villa.averageRating.toFixed(1) : "New"}</Typography>
                                    </Box>
                                </Box>

                                <Grid container spacing={2} sx={{ mb: 3 }}>
                                    <Grid size={6}>
                                        <DatePicker
                                            label="Check-in"
                                            value={checkIn}
                                            onChange={(newValue) => setCheckIn(newValue)}
                                            disablePast
                                            shouldDisableDate={shouldDisableDate}
                                            slotProps={{ textField: { fullWidth: true } }}
                                        />
                                    </Grid>
                                    <Grid size={6}>
                                        <DatePicker
                                            label="Check-out"
                                            value={checkOut}
                                            onChange={(newValue) => setCheckOut(newValue)}
                                            minDate={checkIn}
                                            disabled={!checkIn}
                                            shouldDisableDate={shouldDisableDate}
                                            slotProps={{ textField: { fullWidth: true } }}
                                        />
                                    </Grid>
                                    <Grid size={12}>
                                        <TextField
                                            fullWidth
                                            label="Guests"
                                            type="number"
                                            value={guests}
                                            onChange={(e) => setGuests(Number(e.target.value))}
                                            InputProps={{ inputProps: { min: 1 } }}
                                        />
                                    </Grid>
                                </Grid>

                                <Button
                                    fullWidth
                                    variant="contained"
                                    size="large"
                                    onClick={handleBooking}
                                    disabled={bookingLoading}
                                    sx={{
                                        py: 1.5,
                                        fontSize: '1.1rem',
                                        background: 'linear-gradient(90deg, #FF385C 0%, #E61E43 100%)'
                                    }}
                                >
                                    {bookingLoading ? "Reserving..." : "Reserve"}
                                </Button>

                                <Box sx={{ mt: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
                                    {nightCount > 0 && (
                                        <>
                                            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                                <Typography sx={{ textDecoration: 'underline' }}>${villa.pricePerNight} x {nightCount} nights</Typography>
                                                <Typography>${(villa.pricePerNight * nightCount).toFixed(2)}</Typography>
                                            </Box>
                                            <Divider />
                                            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                                <Typography fontWeight={700}>Total</Typography>
                                                <Typography fontWeight={700}>${totalCost.toFixed(2)}</Typography>
                                            </Box>
                                        </>
                                    )}
                                </Box>
                            </Paper>
                        </Grid>
                    </Grid>
                </Container>
            </motion.div>
        </LocalizationProvider>
    );
};

export default HotelDetails;
