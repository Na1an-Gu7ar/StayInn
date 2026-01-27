import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import {
    Box, Container, Grid, Paper, Typography, Slider,
    FormControlLabel, Checkbox, FormGroup, Divider, Skeleton, Button
} from '@mui/material';
import HotelCard from '../components/HotelCard';

const mockHotels = [
    { id: 1, name: "Grand Luxury Resort", location: "Maldives", rating: 4.9, reviews: 120, price: 450, oldPrice: 600, image: "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070" },
    { id: 2, name: "City Center Inn", location: "Mumbai, India", rating: 4.2, reviews: 85, price: 120, oldPrice: 150, image: "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?q=80&w=2070" },
    { id: 3, name: "Mountain View Cabin", location: "Manali, India", rating: 4.7, reviews: 200, price: 80, oldPrice: 100, image: "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?q=80&w=2070" },
    { id: 4, name: "Seaside Villa", location: "Goa, India", rating: 4.5, reviews: 45, price: 300, oldPrice: 400, image: "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=2070" },
    { id: 5, name: "Urban Loft", location: "Bengaluru, India", rating: 3.9, reviews: 30, price: 60, oldPrice: 80, image: "https://images.unsplash.com/photo-1590490360182-c8729fcdfc26?q=80&w=2070" },
    { id: 6, name: "Desert Camp", location: "Jaisalmer, India", rating: 4.8, reviews: 150, price: 200, oldPrice: 250, image: "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=2070" },
];

const ListingsPage = () => {
    const [loading, setLoading] = useState(true);
    const [priceRange, setPriceRange] = useState([50, 500]);

    useEffect(() => {
        const timer = setTimeout(() => setLoading(false), 1500);
        return () => clearTimeout(timer);
    }, []);

    const handlePriceChange = (event, newValue) => {
        setPriceRange(newValue);
    };

    return (
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default', pt: 4, pb: 8 }}>
            <Container maxWidth="xl">
                <Grid container spacing={4}>

                    {/* FILTER SIDEBAR */}
                    <Grid item xs={12} md={3}>
                        <Paper
                            elevation={0}
                            sx={{
                                p: 3,
                                borderRadius: 4,
                                position: 'sticky',
                                top: 100,
                                border: '1px solid',
                                borderColor: 'divider',
                                display: { xs: 'none', md: 'block' } // Hide on mobile for now (can add drawer later)
                            }}
                        >
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                                <Typography variant="h6" fontWeight={700}>Filters</Typography>
                                <Button size="small" sx={{ textTransform: 'none' }}>Reset</Button>
                            </Box>

                            <Divider sx={{ mb: 3 }} />

                            <Typography variant="subtitle1" fontWeight={600} gutterBottom>Your Budget (per night)</Typography>
                            <Slider
                                getAriaLabel={() => 'Price range'}
                                value={priceRange}
                                onChange={handlePriceChange}
                                valueLabelDisplay="auto"
                                min={50}
                                max={1000}
                                sx={{ mb: 3 }}
                            />
                            <Typography variant="body2" color="text.secondary" mb={3}>
                                ${priceRange[0]} - ${priceRange[1]}
                            </Typography>

                            <Divider sx={{ mb: 3 }} />

                            <Typography variant="subtitle1" fontWeight={600} gutterBottom>Popular Filters</Typography>
                            <FormGroup>
                                <FormControlLabel control={<Checkbox defaultChecked />} label="Breakfast Included" />
                                <FormControlLabel control={<Checkbox />} label="Pool" />
                                <FormControlLabel control={<Checkbox />} label="Free Cancellation" />
                                <FormControlLabel control={<Checkbox />} label="Pet Friendly" />
                            </FormGroup>

                            <Divider sx={{ my: 3 }} />

                            <Typography variant="subtitle1" fontWeight={600} gutterBottom>Property Rating</Typography>
                            <FormGroup>
                                <FormControlLabel control={<Checkbox defaultChecked />} label="5 Stars" />
                                <FormControlLabel control={<Checkbox />} label="4 Stars" />
                                <FormControlLabel control={<Checkbox />} label="3 Stars" />
                            </FormGroup>
                        </Paper>

                        {/* Mobile Filter Button Placeholder */}
                        <Button
                            variant="outlined"
                            fullWidth
                            sx={{ display: { xs: 'block', md: 'none' }, mb: 2 }}
                        >
                            Filters
                        </Button>
                    </Grid>

                    {/* LISTINGS GRID */}
                    <Grid item xs={12} md={9}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                            <Typography variant="h4" fontWeight={700}>
                                Recommended Places
                                <Typography component="span" variant="h6" color="text.secondary" sx={{ ml: 2 }}>
                                    {mockHotels.length} places found
                                </Typography>
                            </Typography>
                            <Box>
                                {/* Sort Dropdown could go here */}
                            </Box>
                        </Box>

                        <Box sx={{
                            display: 'grid',
                            gridTemplateColumns: {
                                xs: '1fr',
                                sm: '1fr 1fr',
                                lg: '1fr 1fr 1fr'
                            },
                            gap: 3
                        }}>
                            <AnimatePresence>
                                {loading ? (
                                    // SKELETON LOADING
                                    // SKELETON LOADING
                                    Array.from(new Array(6)).map((_, index) => (
                                        <Box key={index}>
                                            <Paper sx={{ borderRadius: 4, overflow: 'hidden', border: '1px solid rgba(0,0,0,0.05)' }}>
                                                {/* Image Area */}
                                                <Skeleton variant="rectangular" height={200} animation="wave" />

                                                <Box sx={{ p: 2 }}>
                                                    {/* Title & Rating */}
                                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                                        <Skeleton width="60%" height={28} />
                                                        <Skeleton width={40} height={24} sx={{ borderRadius: 1 }} />
                                                    </Box>

                                                    {/* Location */}
                                                    <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                                                        <Skeleton variant="circular" width={16} height={16} />
                                                        <Skeleton width="40%" />
                                                    </Box>

                                                    {/* Reviews */}
                                                    <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                                                        <Skeleton width={80} height={20} />
                                                        <Skeleton width={60} height={20} />
                                                    </Box>

                                                    {/* Price & Button */}
                                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
                                                        <Box>
                                                            <Skeleton width={50} height={20} />
                                                            <Skeleton width={100} height={32} />
                                                        </Box>
                                                        <Skeleton width={100} height={36} sx={{ borderRadius: 8 }} />
                                                    </Box>
                                                </Box>
                                            </Paper>
                                        </Box>
                                    ))
                                ) : (
                                    // HOTEL CARDS
                                    mockHotels.map((hotel) => (
                                        <Box key={hotel.id}>
                                            <HotelCard hotel={hotel} />
                                        </Box>
                                    ))
                                )}
                            </AnimatePresence>
                        </Box>
                    </Grid>
                </Grid>
            </Container>
        </Box>
    );
};

export default ListingsPage;
