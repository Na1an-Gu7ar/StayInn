import { useState, Suspense, lazy } from 'react';
import { useNavigate } from "react-router-dom";
import {
    Box, Typography, Button, Container, Grid, Paper, IconButton,
    TextField, InputAdornment, useMediaQuery, useTheme
} from "@mui/material";
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import LocationOnIcon from "@mui/icons-material/LocationOn";
import { motion } from "framer-motion";

const Swipper = lazy(() => import("../components/Swipper"));

const destinations = [
    { loc: "Mumbai", img: "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?q=80&w=2071&auto=format&fit=crop" },
    { loc: "Goa", img: "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?q=80&w=1974&auto=format&fit=crop" },
    { loc: "Delhi", img: "https://images.unsplash.com/photo-1587474262847-248102fa934a?q=80&w=2070&auto=format&fit=crop" },
    { loc: "Bengaluru", img: "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?q=80&w=2127&auto=format&fit=crop" },
    { loc: "Jaipur", img: "https://images.unsplash.com/photo-1477587458883-47145ed94245?q=80&w=2070&auto=format&fit=crop" },
    { loc: "Kerala", img: "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?q=80&w=2070&auto=format&fit=crop" }
];

const Landing = () => {
    const navigate = useNavigate();
    const [searchLocation, setSearchLocation] = useState('');
    const [checkIn, setCheckIn] = useState(null);
    const [checkOut, setCheckOut] = useState(null);

    // const handleSearch = () => {
    //     const params = new URLSearchParams();
    //     if (searchLocation) params.append('location', searchLocation);
    //     if (checkIn) params.append('checkIn', checkIn.toISOString());
    //     if (checkOut) params.append('checkOut', checkOut.toISOString());
    //     navigate(`/listings?${params.toString()}`);
    // };

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down('md'));
    const [location, setLocation] = useState('');

    const handleSearch = () => {
        if (location.trim()) {
            navigate(`/listings?location=${encodeURIComponent(location)}`);
        } else {
            navigate('/listings');
        }
    };

    return (
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>

                {/* HERO SECTION */}
                <Box sx={{ position: "relative", height: '85vh', width: '100%' }}>
                    <Suspense fallback={<Box sx={{ height: '100%', bgcolor: 'grey.300' }} />}>
                        <Swipper />
                    </Suspense>

                    <Box
                        sx={{
                            position: "absolute",
                            top: 0,
                            left: 0,
                            width: "100%",
                            height: "100%",
                            display: "flex",
                            flexDirection: "column",
                            alignItems: "center",
                            justifyContent: "center",
                            zIndex: 1,
                            pointerEvents: 'none' // Let clicks pass through transparent areas
                        }}
                    >
                        {/* Hero Text */}
                        <Box sx={{ textAlign: "center", color: "white", mb: 6, pointerEvents: 'auto', px: 2 }}>
                            <motion.div
                                initial={{ opacity: 0, y: 30 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ duration: 0.8 }}
                            >
                                <Typography
                                    variant="h1"
                                    sx={{
                                        fontWeight: 800,
                                        fontSize: { xs: '2.5rem', md: '4.5rem' },
                                        textShadow: '0px 4px 20px rgba(0,0,0,0.5)',
                                        mb: 2
                                    }}
                                >
                                    Find Your Next Stay
                                </Typography>
                                <Typography
                                    variant="h5"
                                    sx={{
                                        fontWeight: 500,
                                        opacity: 0.9,
                                        mb: 4,
                                        fontSize: { xs: '1.2rem', md: '1.5rem' }
                                    }}
                                >
                                    Search low prices on hotels, homes and much more...
                                </Typography>
                            </motion.div>
                        </Box>

                        {/* Search Bar */}
                        <Paper
                            elevation={4}
                            component={motion.div}
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            transition={{ delay: 0.3, duration: 0.5 }}
                            sx={{
                                p: 1,
                                display: 'flex',
                                alignItems: 'center',
                                gap: 2,
                                flexDirection: { xs: 'column', md: 'row' },
                                width: { xs: '90%', md: '80%', lg: '70%' },
                                maxWidth: '1000px',
                                borderRadius: '32px', // High border radius for pill shape
                                pointerEvents: 'auto',
                                border: '1px solid rgba(0,0,0,0.08)'
                            }}
                        >
                            <Box sx={{ display: 'flex', alignItems: 'center', flex: 1, width: '100%', px: 2 }}>
                                <LocationOnIcon color="action" sx={{ mr: 1 }} />
                                <TextField
                                    fullWidth
                                    placeholder="Where are you going?"
                                    variant="standard"
                                    value={location}
                                    onChange={(e) => setLocation(e.target.value)}
                                    InputProps={{ disableUnderline: true, style: { fontSize: '1.1rem' } }}
                                    onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                                />
                            </Box>

                            <Button
                                variant="contained"
                                size="large"
                                onClick={handleSearch}
                                sx={{
                                    borderRadius: '24px',
                                    px: 4,
                                    py: 1.5,
                                    width: { xs: '100%', md: 'auto' },
                                    fontSize: '1.1rem',
                                    flexShrink: 0
                                }}
                            >
                                Search
                            </Button>
                        </Paper>
                    </Box>
                </Box>

                {/* POPULAR DESTINATIONS */}
                <Container maxWidth="lg" sx={{ py: 8 }}>
                    <Typography variant="h3" fontWeight={700} sx={{ mb: 1 }}>
                        Explore India
                    </Typography>
                    <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
                        These popular destinations have a lot to offer
                    </Typography>

                    <Box sx={{
                        display: 'grid',
                        gridTemplateColumns: {
                            xs: '1fr',
                            sm: '1fr 1fr',
                            md: '1fr 1fr 1fr'
                        },
                        gap: 3
                    }}>
                        {destinations.map((city, index) => (
                            <motion.div
                                key={index}
                                whileHover={{ y: -8 }}
                                transition={{ type: 'spring', stiffness: 300 }}
                            >
                                <Paper
                                    elevation={2}
                                    sx={{
                                        height: 250,
                                        position: 'relative',
                                        borderRadius: 4,
                                        overflow: 'hidden',
                                        cursor: 'pointer',
                                        '&:hover .overlay': { opacity: 0.2 }
                                    }}
                                    onClick={() => navigate('/listings')}
                                >
                                    <Box
                                        component="img"
                                        src={city.img}
                                        alt={city.loc}
                                        sx={{
                                            width: '100%',
                                            height: '100%',
                                            objectFit: 'cover',
                                            transition: 'transform 0.5s',
                                            '&:hover': { transform: 'scale(1.05)' }
                                        }}
                                    />
                                    <Box
                                        className="overlay"
                                        sx={{
                                            position: 'absolute',
                                            top: 0, left: 0, width: '100%', height: '100%',
                                            background: 'linear-gradient(to top, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0) 50%)',
                                            transition: 'opacity 0.3s'
                                        }}
                                    />
                                    <Typography
                                        variant="h5"
                                        sx={{
                                            position: 'absolute',
                                            bottom: 20,
                                            left: 20,
                                            color: 'white',
                                            fontWeight: 700,
                                            textShadow: '0 2px 4px rgba(0,0,0,0.5)'
                                        }}
                                    >
                                        {city.loc}
                                    </Typography>
                                </Paper>
                            </motion.div>
                        ))}
                    </Box>
                </Container>
            </Box>
        </LocalizationProvider>
    );
};

export default Landing;
