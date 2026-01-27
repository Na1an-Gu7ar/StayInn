import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import {
    Box, Container, Grid, Typography, Button, Paper, Rating, Divider,
    Chip, Avatar, TextField, IconButton
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

const mockHotel = {
    id: 1,
    name: "Grand Luxury Resort",
    location: "Maldives, Private Island",
    description: "Experience world-class service at Grand Luxury Resort. Nestled in the heart of the Maldives, this resort offers a pristine beachfront, private villas with infinity pools, and an award-winning spa. Enjoy gourmet dining under the stars and explore the vibrant coral reefs just steps from your room.",
    price: 450,
    rating: 4.8,
    reviews: 124,
    images: [
        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070",
        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=2025",
        "https://images.unsplash.com/photo-1540541338287-41700207dee6?q=80&w=2074",
        "https://images.unsplash.com/photo-1563911302283-d2bc129e7c1f?q=80&w=2070",
        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=2070"
    ],
    amenities: [
        { icon: <WifiIcon />, name: "Free Wifi" },
        { icon: <PoolIcon />, name: "Infinity Pool" },
        { icon: <SpaIcon />, name: "Spa & Wellness" },
        { icon: <RestaurantIcon />, name: "Gourmet Dining" },
        { icon: <AcUnitIcon />, name: "Air Conditioning" },
        { icon: <DirectionsCarIcon />, name: "Free Parking" },
    ],
    reviewsList: [
        { id: 1, user: "Alice Johnson", rating: 5, date: "Oct 12, 2025", comment: "Absolutely breathtaking! The service was impeccable and the views are unreal." },
        { id: 2, user: "Mark Smith", rating: 4, date: "Sep 28, 2025", comment: "Great stay, but the food was a bit pricey. Overall wonderful experience." },
    ]
};

const HotelDetails = () => {
    const { id } = useParams();
    const [reviewText, setReviewText] = useState("");

    // In a real app, fetch data based on ID. Using mockHotel for now.

    const handleBooking = () => {
        alert("Booking confirmed! (Sidebar Mock)");
    };

    return (
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.5 }}
            >
                <Container maxWidth="xl" sx={{ py: 4 }}>

                    {/* TITLE HEADER */}
                    <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                        <Box>
                            <Typography variant="h3" fontWeight={700} gutterBottom>
                                {mockHotel.name}
                            </Typography>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary' }}>
                                <LocationOnIcon fontSize="small" />
                                <Typography variant="h6" fontWeight={500}>
                                    {mockHotel.location}
                                </Typography>
                            </Box>
                        </Box>
                        <Box sx={{ display: 'flex', gap: 1 }}>
                            <IconButton><ShareIcon /></IconButton>
                            <IconButton><FavoriteBorderIcon /></IconButton>
                        </Box>
                    </Box>

                    {/* IMAGE GALLERY - Bento Grid Style */}
                    <Box sx={{
                        display: 'grid',
                        gridTemplateColumns: { xs: '1fr', md: '2fr 1fr', lg: '2fr 1fr 1fr' },
                        gridTemplateRows: { xs: 'auto', md: '200px 200px' },
                        gap: 2,
                        mb: 6,
                        height: { md: '416px' } // 200*2 + 16 gap
                    }}>
                        {/* Main Image */}
                        <Box
                            component="img"
                            src={mockHotel.images[0]}
                            sx={{
                                width: '100%',
                                height: '100%',
                                objectFit: 'cover',
                                borderRadius: 4,
                                gridRow: { xs: 'span 1', md: 'span 2' },
                                cursor: 'pointer'
                            }}
                        />
                        {/* Sub Images */}
                        {mockHotel.images.slice(1, 5).map((img, idx) => (
                            <Box
                                key={idx}
                                component="img"
                                src={img}
                                sx={{
                                    width: '100%',
                                    height: '100%',
                                    objectFit: 'cover',
                                    borderRadius: 4,
                                    display: { xs: 'none', md: 'block' }
                                }}
                            />
                        ))}
                    </Box>

                    <Grid container spacing={4}>
                        {/* LEFT CONTENT */}
                        <Grid item xs={12} md={8}>

                            {/* DESCRIPTION */}
                            <Box sx={{ mb: 5 }}>
                                <Typography variant="h5" fontWeight={700} gutterBottom>
                                    About this place
                                </Typography>
                                <Typography variant="body1" color="text.secondary" paragraph sx={{ lineHeight: 1.8 }}>
                                    {mockHotel.description}
                                </Typography>
                            </Box>

                            <Divider sx={{ mb: 5 }} />

                            {/* AMENITIES */}
                            <Box sx={{ mb: 5 }}>
                                <Typography variant="h5" fontWeight={700} gutterBottom>
                                    What this place offers
                                </Typography>
                                <Grid container spacing={2} sx={{ mt: 1 }}>
                                    {mockHotel.amenities.map((item, index) => (
                                        <Grid item xs={6} sm={4} key={index}>
                                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                                                {item.icon}
                                                <Typography>{item.name}</Typography>
                                            </Box>
                                        </Grid>
                                    ))}
                                </Grid>
                            </Box>

                            <Divider sx={{ mb: 5 }} />

                            {/* REVIEWS SECTION */}
                            <Box sx={{ mb: 5 }}>
                                <Typography variant="h5" fontWeight={700} gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <Avatar sx={{ bgcolor: 'primary.main', width: 28, height: 28, fontSize: 14 }}>{mockHotel.rating}</Avatar>
                                    {mockHotel.reviews} Reviews
                                </Typography>

                                {/* Review List */}
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, mt: 3 }}>
                                    {mockHotel.reviewsList.map(review => (
                                        <Paper key={review.id} elevation={0} sx={{ p: 3, bgcolor: 'grey.50', borderRadius: 3 }}>
                                            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
                                                <Avatar>{review.user[0]}</Avatar>
                                                <Box>
                                                    <Typography fontWeight={600}>{review.user}</Typography>
                                                    <Typography variant="caption" color="text.secondary">{review.date}</Typography>
                                                </Box>
                                            </Box>
                                            <Rating value={review.rating} readOnly size="small" sx={{ mb: 1 }} />
                                            <Typography variant="body2">{review.comment}</Typography>
                                        </Paper>
                                    ))}
                                </Box>

                                {/* Add Review */}
                                <Box sx={{ mt: 4 }}>
                                    <Typography variant="h6" gutterBottom>Add a Review</Typography>
                                    <TextField
                                        fullWidth
                                        multiline
                                        rows={3}
                                        placeholder="Share your experience..."
                                        value={reviewText}
                                        onChange={(e) => setReviewText(e.target.value)}
                                        sx={{ mb: 2 }}
                                    />
                                    <Button variant="contained">Post Review</Button>
                                </Box>
                            </Box>
                        </Grid>

                        {/* RIGHT BOOKING SIDEBAR */}
                        <Grid item xs={12} md={4}>
                            <Paper
                                elevation={3}
                                sx={{
                                    p: 3,
                                    borderRadius: 4,
                                    position: 'sticky',
                                    top: 100,
                                    border: '1px solid rgba(0,0,0,0.08)'
                                }}
                            >
                                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'end', mb: 3 }}>
                                    <Typography variant="h4" fontWeight={700}>
                                        ${mockHotel.price} <Typography component="span" variant="body1" color="text.secondary">/ night</Typography>
                                    </Typography>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                        <FavoriteBorderIcon fontSize="small" />
                                        <Typography fontWeight={600}>{mockHotel.rating}</Typography>
                                        <Typography variant="body2" color="text.secondary" sx={{ textDecoration: 'underline' }}>
                                            ({mockHotel.reviews} reviews)
                                        </Typography>
                                    </Box>
                                </Box>

                                <Grid container spacing={2} sx={{ mb: 3 }}>
                                    <Grid item xs={6}>
                                        <DatePicker label="Check-in" slotProps={{ textField: { fullWidth: true } }} />
                                    </Grid>
                                    <Grid item xs={6}>
                                        <DatePicker label="Check-out" slotProps={{ textField: { fullWidth: true } }} />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField fullWidth label="Guests" type="number" defaultValue={2} />
                                    </Grid>
                                </Grid>

                                <Button
                                    fullWidth
                                    variant="contained"
                                    size="large"
                                    onClick={handleBooking}
                                    sx={{
                                        py: 1.5,
                                        fontSize: '1.1rem',
                                        background: 'linear-gradient(90deg, #FF385C 0%, #E61E43 100%)'
                                    }}
                                >
                                    Reserve
                                </Button>

                                <Typography variant="caption" display="block" textAlign="center" sx={{ mt: 2, color: 'text.secondary' }}>
                                    You won't be charged yet
                                </Typography>

                                <Box sx={{ mt: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <Typography sx={{ textDecoration: 'underline' }}>${mockHotel.price} x 5 nights</Typography>
                                        <Typography>${mockHotel.price * 5}</Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <Typography sx={{ textDecoration: 'underline' }}>Cleaning fee</Typography>
                                        <Typography>$50</Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <Typography sx={{ textDecoration: 'underline' }}>Service fee</Typography>
                                        <Typography>$80</Typography>
                                    </Box>
                                    <Divider />
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <Typography fontWeight={700}>Total before taxes</Typography>
                                        <Typography fontWeight={700}>${mockHotel.price * 5 + 130}</Typography>
                                    </Box>
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
