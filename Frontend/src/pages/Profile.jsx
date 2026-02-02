import React, { useState, useEffect } from 'react';
import {
    Box, Container, Typography, Paper, Grid, TextField, Button,
    Avatar, Tab, Tabs, Chip, Divider, List, ListItem, ListItemText,
    CircularProgress, Alert, Card, CardContent, CardActions
} from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../services/authApi';
import { bookingApi } from '../services/bookingApi';
import { useNavigate } from 'react-router-dom';

const Profile = () => {
    const { user, login } = useAuth(); // login function can be used to update context
    const navigate = useNavigate();
    const [tabValue, setTabValue] = useState(0);

    // Profile State
    const [profileData, setProfileData] = useState({
        name: '',
        email: '',
        mobile: '',
        role: ''
    });
    const [isEditing, setIsEditing] = useState(false);
    const [profileLoading, setProfileLoading] = useState(false);
    const [profileError, setProfileError] = useState(null);
    const [profileSuccess, setProfileSuccess] = useState(null);

    // Bookings State
    const [bookings, setBookings] = useState([]);
    const [bookingsLoading, setBookingsLoading] = useState(false);
    const [bookingsError, setBookingsError] = useState(null);

    useEffect(() => {
        if (user) {
            setProfileData({
                name: user.name || '',
                email: user.email || '',
                mobile: user.mobile || '',
                role: user.role || 'USER'
            });
            fetchBookings();
        }
    }, [user]);

    const fetchBookings = async () => {
        if (!user || !user.id && !user.user_id) return;
        setBookingsLoading(true);
        try {
            // Support both id variants depending on what backend returns
            const userId = user.id || user.user_id;
            const response = await bookingApi.getUserBookings(userId);
            if (response.success) {
                // Sort by ID desc (newest first)
                const sorted = response.data.sort((a, b) => b.id - a.id);
                setBookings(sorted);
            }
        } catch (err) {
            console.error("Error fetching bookings:", err);
            setBookingsError("Failed to load bookings");
        } finally {
            setBookingsLoading(false);
        }
    };

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    const handleProfileUpdate = async () => {
        setProfileLoading(true);
        setProfileError(null);
        setProfileSuccess(null);
        try {
            const userId = user.id || user.user_id;
            const updatePayload = {
                name: profileData.name,
                email: profileData.email,
                mobile: profileData.mobile,
                role: profileData.role
            };

            const response = await authApi.updateProfile(userId, updatePayload);

            // Response format from Spring Boot: { success: true, data: { ... } }
            const updatedUser = { ...user, ...response.data };
            // Ensure we keep the token if it's not returned (it won't be from Spring Boot)
            // Local storage update is handled primarily by auth flow, but we update context here
            localStorage.setItem('user', JSON.stringify(updatedUser));

            setProfileSuccess("Profile updated successfully!");
            // Refresh local state to match
            setProfileData({
                name: updatedUser.name,
                email: updatedUser.email,
                mobile: updatedUser.mobile,
                role: updatedUser.role
            })
            setIsEditing(false);
        } catch (err) {
            console.error("Update failed:", err);
            setProfileError(err.response?.data?.message || "Failed to update profile");
        } finally {
            setProfileLoading(false);
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'CONFIRMED': return 'success';
            case 'PENDING': return 'warning';
            case 'CANCELLED': return 'error';
            case 'COMPLETED': return 'info';
            default: return 'default';
        }
    };

    const handleCancelBooking = async (bookingId) => {
        if (!window.confirm("Are you sure you want to cancel this booking?")) return;
        try {
            await bookingApi.cancel(bookingId); // Need to check if this API exists/is exposed
            fetchBookings(); // Refresh
        } catch (err) {
            alert("Failed to cancel booking: " + err.message);
        }
    };

    return (
        <Container maxWidth="md" sx={{ py: 6 }}>
            <Paper elevation={3} sx={{ borderRadius: 4, overflow: 'hidden' }}>
                <Box sx={{ bgcolor: '#eee', p: 4, display: 'flex', alignItems: 'center', gap: 3 }}>
                    <Avatar sx={{ width: 80, height: 80, fontSize: '2rem', bgcolor: '#FF385C' }}>
                        {profileData.name.charAt(0).toUpperCase()}
                    </Avatar>
                    <Box>
                        <Typography variant="h4" fontWeight={700}>{profileData.name}</Typography>
                        <Typography color="text.secondary">{profileData.email}</Typography>
                        <Chip label={profileData.role} size="small" sx={{ mt: 1 }} />
                    </Box>
                </Box>

                <Tabs value={tabValue} onChange={handleTabChange} centered sx={{ borderBottom: 1, borderColor: 'divider' }}>
                    <Tab label="Profile Details" />
                    <Tab label="My Bookings" />
                </Tabs>

                <Box sx={{ p: 4 }}>
                    {/* PROFILE TAB */}
                    {tabValue === 0 && (
                        <Box maxWidth="sm" mx="auto">
                            {profileError && <Alert severity="error" sx={{ mb: 2 }}>{profileError}</Alert>}
                            {profileSuccess && <Alert severity="success" sx={{ mb: 2 }}>{profileSuccess}</Alert>}

                            <Grid container spacing={3}>
                                <Grid item xs={12}>
                                    <TextField
                                        label="Full Name"
                                        fullWidth
                                        value={profileData.name}
                                        onChange={(e) => setProfileData({ ...profileData, name: e.target.value })}
                                        disabled={!isEditing}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        label="Email Address"
                                        fullWidth
                                        value={profileData.email}
                                        disabled={true} // Email cannot be changed
                                        helperText="Email cannot be changed"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        label="Mobile Number"
                                        fullWidth
                                        value={profileData.mobile}
                                        onChange={(e) => setProfileData({ ...profileData, mobile: e.target.value })}
                                        disabled={!isEditing}
                                    />
                                </Grid>
                                <Grid item xs={12} sx={{ display: 'flex', gap: 2, mt: 2 }}>
                                    {isEditing ? (
                                        <>
                                            <Button
                                                variant="contained"
                                                onClick={handleProfileUpdate}
                                                disabled={profileLoading}
                                            >
                                                {profileLoading ? "Saving..." : "Save Changes"}
                                            </Button>
                                            <Button
                                                variant="outlined"
                                                onClick={() => setIsEditing(false)}
                                                disabled={profileLoading}
                                            >
                                                Cancel
                                            </Button>
                                        </>
                                    ) : (
                                        <Button
                                            variant="contained"
                                            onClick={() => setIsEditing(true)}
                                            sx={{ bgcolor: '#FF385C', '&:hover': { bgcolor: '#E61E43' } }}
                                        >
                                            Edit Profile
                                        </Button>
                                    )}
                                </Grid>
                            </Grid>
                        </Box>
                    )}

                    {/* BOOKINGS TAB */}
                    {tabValue === 1 && (
                        <Box>
                            {bookingsLoading ? (
                                <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
                                    <CircularProgress />
                                </Box>
                            ) : bookingsError ? (
                                <Alert severity="error">{bookingsError}</Alert>
                            ) : bookings.length === 0 ? (
                                <Typography align="center" color="text.secondary">
                                    No bookings found.
                                </Typography>
                            ) : (
                                <Grid container spacing={3}>
                                    {bookings.map((booking) => (
                                        <Grid item xs={12} key={booking.id}>
                                            <Card variant="outlined" sx={{ display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, p: 2 }}>
                                                <Box sx={{ flexGrow: 1 }}>
                                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                                                        <Typography variant="h6" fontWeight={600}>
                                                            {booking.villaName}
                                                        </Typography>
                                                        <Chip label={booking.status} color={getStatusColor(booking.status)} size="small" />
                                                    </Box>
                                                    <Typography variant="body2" color="text.secondary" gutterBottom>
                                                        {booking.villaLocation}
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {booking.checkInDate} to {booking.checkOutDate} • {booking.numberOfNights} Nights
                                                    </Typography>
                                                    <Typography variant="h6" fontWeight={700} color="#FF385C" sx={{ mt: 1 }}>
                                                        ₹{booking.totalPrice}
                                                    </Typography>
                                                </Box>
                                                <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', gap: 1, ml: { sm: 2 }, mt: { xs: 2, sm: 0 }, minWidth: 150 }}>
                                                    {booking.status === 'PENDING' && (
                                                        <Button
                                                            variant="contained"
                                                            size="small"
                                                            color="primary"
                                                            onClick={() => navigate(`/payment/${booking.id}`)}
                                                        >
                                                            Pay Now
                                                        </Button>
                                                    )}
                                                    {['PENDING', 'CONFIRMED'].includes(booking.status) && (
                                                        <Button
                                                            variant="outlined"
                                                            size="small"
                                                            color="error"
                                                        >
                                                            Cancel
                                                        </Button>
                                                    )}
                                                </Box>
                                            </Card>
                                        </Grid>
                                    ))}
                                </Grid>
                            )}
                        </Box>
                    )}
                </Box>
            </Paper>
        </Container>
    );
};

export default Profile;
