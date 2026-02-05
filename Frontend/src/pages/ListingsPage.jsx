import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useLocation, useSearchParams } from "react-router-dom"; // Add useLocation
import {
    Box, Container, Paper, Typography, Slider,
    FormControlLabel, Checkbox, FormGroup, Divider, Skeleton, Button, Alert
} from '@mui/material';
import Grid from '@mui/material/Grid';
import HotelCard from '../components/HotelCard';
import { hotelApi } from '../services/hotelApi';

const ListingsPage = () => {
    const [searchParams] = useSearchParams();
    const [loading, setLoading] = useState(true);
    const [priceRange, setPriceRange] = useState([50, 5000]);
    const [selectedRatings, setSelectedRatings] = useState({ 5: false, 4: false, 3: false }); // Rating state
    const [allVillas, setAllVillas] = useState([]);
    const [villas, setVillas] = useState([]);
    const [error, setError] = useState(null);
    const location = useLocation(); // Hook

    useEffect(() => {
        const fetchVillas = async () => {
            setLoading(true);
            const queryParams = new URLSearchParams(location.search);
            const searchLocation = queryParams.get('location');

            try {
                let response;
                if (searchLocation) {
                    // Use search API if location param exists
                    response = await hotelApi.search(searchLocation);
                } else {
                    // Otherwise get all
                    response = await hotelApi.getAll();
                }

                if (response.success && Array.isArray(response.data)) {
                    setAllVillas(response.data);
                    setVillas(response.data); // Initialize filtered set
                } else {
                    setAllVillas([]);
                    setVillas([]);
                }
            } catch (err) {
                console.error("Failed to fetch villas", err);
                setError("Perhaps no villas are added yet, or the server is down.");
            } finally {
                setLoading(false);
            }
        };

        fetchVillas();
    }, [location.search]);

    // FILTER LOGIC
    useEffect(() => {
        let filtered = [...allVillas];
        const locationQuery = searchParams.get('location');

        // 1. Location Filter (Redundant if handled by API, but good for client-side refinement)
        if (locationQuery) {
            const lowerLoc = locationQuery.toLowerCase();
            filtered = filtered.filter(villa =>
                (villa.address && villa.address.toLowerCase().includes(lowerLoc)) ||
                (villa.name && villa.name.toLowerCase().includes(lowerLoc))
            );
        }

        // 2. Price Filter
        filtered = filtered.filter(villa => {
            const price = Number(villa.pricePerNight);
            return price >= priceRange[0] && price <= priceRange[1];
        });

        // 3. Rating Filter
        // Check if any rating box is checked
        const activeRatings = Object.keys(selectedRatings).filter(r => selectedRatings[r]).map(Number);

        if (activeRatings.length > 0) {
            filtered = filtered.filter(villa => {
                const rating = Math.floor(villa.averageRating || 0);
                return activeRatings.includes(rating) || (activeRatings.includes(5) && rating >= 5);
            });
        }

        setVillas(filtered);
    }, [searchParams, allVillas, priceRange, selectedRatings]);

    const handlePriceChange = (event, newValue) => {
        setPriceRange(newValue);
    };

    const handleRatingChange = (event) => {
        setSelectedRatings({
            ...selectedRatings,
            [event.target.name]: event.target.checked,
        });
    };

    const handleReset = () => {
        setPriceRange([50, 5000]);
        setSelectedRatings({ 5: false, 4: false, 3: false });
    };

    return (
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default', pt: 4, pb: 8 }}>
            <Container maxWidth="xl">
                <Grid container spacing={4}>

                    {/* FILTER SIDEBAR */}
                    {/* FILTER SIDEBAR */}
                    <Grid size={{ xs: 12, md: 3 }}>
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
                                <Button size="small" onClick={handleReset} sx={{ textTransform: 'none' }}>Reset</Button>
                            </Box>

                            <Divider sx={{ mb: 3 }} />

                            <Typography variant="subtitle1" fontWeight={600} gutterBottom>Your Budget (per night)</Typography>
                            <Slider
                                getAriaLabel={() => 'Price range'}
                                value={priceRange}
                                onChange={handlePriceChange}
                                valueLabelDisplay="auto"
                                min={50}
                                max={5000}
                                sx={{ mb: 3 }}
                            />
                            <Typography variant="body2" color="text.secondary" mb={3}>
                                ${priceRange[0]} - ${priceRange[1]}
                            </Typography>

                            <Divider sx={{ mb: 3 }} />

                            {/* RATINGS FILTER */}
                            <Typography variant="subtitle1" fontWeight={600} gutterBottom>Property Rating</Typography>
                            <FormGroup>
                                <FormControlLabel
                                    control={<Checkbox checked={selectedRatings[5]} onChange={handleRatingChange} name="5" />}
                                    label="5 Stars & Up"
                                />
                                <FormControlLabel
                                    control={<Checkbox checked={selectedRatings[4]} onChange={handleRatingChange} name="4" />}
                                    label="4 Stars & Up"
                                />
                                <FormControlLabel
                                    control={<Checkbox checked={selectedRatings[3]} onChange={handleRatingChange} name="3" />}
                                    label="3 Stars & Up"
                                />
                            </FormGroup>

                            <Divider sx={{ my: 3 }} />

                            <Typography variant="subtitle1" fontWeight={600} gutterBottom>Popular Filters</Typography>
                            <FormGroup>
                                <FormControlLabel control={<Checkbox />} label="Breakfast Included" />
                                <FormControlLabel control={<Checkbox />} label="Pool" />
                                <FormControlLabel control={<Checkbox />} label="Free Cancellation" />
                                <FormControlLabel control={<Checkbox />} label="Pet Friendly" />
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
                    {/* LISTINGS GRID */}
                    <Grid size={{ xs: 12, md: 9 }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                            <Typography variant="h4" fontWeight={700}>
                                Recommended Places
                                <Typography component="span" variant="h6" color="text.secondary" sx={{ ml: 2 }}>
                                    {villas.length} places found
                                </Typography>
                            </Typography>
                        </Box>

                        {error && <Alert severity="warning" sx={{ mb: 3 }}>{error}</Alert>}

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
                                    Array.from(new Array(6)).map((_, index) => (
                                        <Box key={index}>
                                            <Paper sx={{ borderRadius: 4, overflow: 'hidden', border: '1px solid rgba(0,0,0,0.05)' }}>
                                                <Skeleton variant="rectangular" height={200} animation="wave" />
                                                <Box sx={{ p: 2 }}>
                                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                                        <Skeleton width="60%" height={28} />
                                                        <Skeleton width={40} height={24} sx={{ borderRadius: 1 }} />
                                                    </Box>
                                                    <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                                                        <Skeleton variant="circular" width={16} height={16} />
                                                        <Skeleton width="40%" />
                                                    </Box>
                                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
                                                        <Skeleton width={100} height={32} />
                                                        <Skeleton width={100} height={36} sx={{ borderRadius: 8 }} />
                                                    </Box>
                                                </Box>
                                            </Paper>
                                        </Box>
                                    ))
                                ) : (
                                    villas.length > 0 ? (
                                        villas.map((villa) => (
                                            <Box key={villa.id}>
                                                <HotelCard hotel={{
                                                    id: villa.id,
                                                    name: villa.name,
                                                    location: villa.address,
                                                    rating: villa.averageRating || "New",
                                                    reviews: villa.totalRatings || 0,
                                                    price: villa.pricePerNight,
                                                    image: villa.imageUrls?.[0] || "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070"
                                                }} />
                                            </Box>
                                        ))
                                    ) : (
                                        <Typography variant="h6" sx={{ gridColumn: '1 / -1', textAlign: 'center', mt: 5 }}>No villas found.</Typography>
                                    )
                                )}
                            </AnimatePresence>
                        </Box>
                    </Grid>
                </Grid>
            </Container>
        </Box >
    );
};

export default ListingsPage;
