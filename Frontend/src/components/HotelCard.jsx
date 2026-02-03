import { Box, Card, CardContent, CardMedia, Typography, IconButton, Button, Rating, Chip } from '@mui/material';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';

import { useAuth } from '../context/AuthContext';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
// ... top imports

const HotelCard = ({ hotel }) => {
    const [isLiked, setIsLiked] = useState(false);
    const navigate = useNavigate();
    const { isAdmin } = useAuth(); // Hooks must be at top level

    // Default mock data if no props
    const {
        id = 1,
        image = "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070",
        name = "Grand Luxury Resort",
        location = "Maldives, Private Island",
        rating = 4.8,
        reviews = 124,
        price = 450,
        oldPrice = 600
    } = hotel || {};

    return (
        <motion.div
            whileHover={{ y: -5 }}
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.3 }}
        >
            <Card sx={{
                maxWidth: '100%',
                borderRadius: 4,
                position: 'relative',
                overflow: 'hidden',
                border: '1px solid rgba(0,0,0,0.05)'
            }}>
                <Box sx={{ position: 'relative' }}>
                    <CardMedia
                        component="img"
                        height="200"
                        image={image}
                        alt={name}
                        sx={{ transition: 'transform 0.5s', '&:hover': { transform: 'scale(1.05)' }, cursor: 'pointer' }}
                        onClick={() => navigate(`/listings/${id}`)}
                    />
                    <IconButton
                        onClick={() => setIsLiked(!isLiked)}
                        sx={{
                            position: 'absolute',
                            top: 10,
                            right: 10,
                            bgcolor: 'rgba(255,255,255,0.7)',
                            '&:hover': { bgcolor: 'white' }
                        }}
                    >
                        {isLiked ? <FavoriteIcon color="error" /> : <FavoriteBorderIcon />}
                    </IconButton>

                    {hotel.rating >= 4 && <Chip
                        label="Top Rated"
                        color="secondary"
                        size="small"
                        sx={{ position: 'absolute', top: 10, left: 10, fontWeight: 600 }}
                    />}
                </Box>

                <CardContent sx={{ pb: 1 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 1 }}>
                        <Typography gutterBottom variant="h6" component="div" fontWeight={700} sx={{ lineHeight: 1.2 }}>
                            {name}
                        </Typography>
                        <Box sx={{ display: 'flex', alignItems: 'center', bgcolor: 'primary.main', borderRadius: 1, px: 0.8, py: 0.3 }}>
                            <Typography variant="body2" color="white" fontWeight="bold">
                                {rating}
                            </Typography>
                        </Box>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <LocationOnIcon fontSize="small" color="action" />
                        <Typography variant="body2" color="text.secondary" noWrap>
                            {location}
                        </Typography>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <Rating value={rating} precision={0.5} size="small" readOnly />
                        <Typography variant="caption" color="text.secondary" ml={1}>
                            ({reviews} reviews)
                        </Typography>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mt: 2 }}>
                        <Box>
                            <Typography variant="h5" color="primary.main" fontWeight={700}>
                                ₹{price} <Typography component="span" variant="body2" color="text.secondary">/ night</Typography>
                            </Typography>
                        </Box>

                        {/* {isAdmin ? (
                            <Box sx={{ display: 'flex', gap: 1 }}>
                                <Button variant="outlined" color="info" size="small" startIcon={<EditIcon />}>
                                    Edit
                                </Button>
                                <Button variant="outlined" color="error" size="small" startIcon={<DeleteIcon />}>
                                    Delete
                                </Button>
                            </Box>
                        ) : (
                        )} */}
                        <Button
                            variant="outlined"
                            sx={{ borderRadius: 8 }}
                            onClick={() => navigate(`/listings/${id}`)}
                        >
                            View Deal
                        </Button>
                    </Box>
                </CardContent>
            </Card>
        </motion.div>
    );
};

export default HotelCard;
