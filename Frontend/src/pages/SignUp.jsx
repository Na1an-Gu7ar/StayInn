import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Box, TextField, Button, Typography, Paper, MenuItem, useTheme } from '@mui/material';
import { motion } from "framer-motion"

const roles = ['normal_user', 'store_owner'];

export default function SignUp() {
    const [form, setForm] = useState({ name: '', email: '', address: '', password: '', role: 'normal_user' });
    const theme = useTheme();
    const navigate = useNavigate();

    const handleChange = e => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async e => {
        e.preventDefault();
        // Mock signup
        alert('Signup successful! Please log in.');
        navigate('/login');
    };

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh', width: '100vw', overflow: 'hidden' }}>
            {/* Left Side - Image */}
            <Box sx={{
                flex: 1,
                display: { xs: 'none', md: 'block' },
                position: 'relative'
            }}>
                <Box
                    component={motion.div}
                    initial={{ scale: 1.1 }}
                    animate={{ scale: 1 }}
                    transition={{ duration: 10, repeat: Infinity, repeatType: 'reverse' }}
                    sx={{
                        width: '100%',
                        height: '100%',
                        backgroundImage: 'url(https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=2070)',
                        backgroundSize: 'cover',
                        backgroundPosition: 'center',
                    }}
                />
                <Box sx={{
                    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
                    display: 'flex', flexDirection: 'column', justifyContent: 'center', p: 6, color: 'white'
                }}>
                    <Box sx={{ background: 'rgba(0,0,0,0.2)', p: 2, boxShadow: '0 4px 6px rgba(0,0,0,0.5)' }}>
                        <Typography variant="h2" fontWeight={700} gutterBottom>
                            Join Us
                        </Typography>
                        <Typography variant="h5">
                            Start your journey to finding the perfect stay today.
                        </Typography>
                    </Box>
                </Box>
            </Box>

            {/* Right Side - Form */}
            <Box sx={{
                flex: 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                bgcolor: 'background.paper'
            }}>
                <Paper
                    elevation={0}
                    sx={{
                        width: '100%',
                        maxWidth: 500,
                        p: 4,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center'
                    }}
                >
                    <Box
                        component={motion.div}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ duration: 0.5 }}
                        sx={{ width: '100%' }}
                    >
                        <Box sx={{ textAlign: 'center', mb: 3 }}>
                            <Typography variant="h4" fontWeight={700} color="primary">BookStay</Typography>
                            <Typography variant="h5" fontWeight={600} mt={2}>Create an account</Typography>
                        </Box>

                        <form onSubmit={handleSubmit} style={{ width: '100%' }}>
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                                <TextField
                                    fullWidth
                                    label="Full Name"
                                    name="name"
                                    value={form.name}
                                    onChange={handleChange}
                                    required
                                    InputProps={{ sx: { borderRadius: 2 } }}
                                />
                                <TextField
                                    fullWidth
                                    label="Email Address"
                                    name="email"
                                    type="email"
                                    value={form.email}
                                    onChange={handleChange}
                                    required
                                    InputProps={{ sx: { borderRadius: 2 } }}
                                />
                                <TextField
                                    fullWidth
                                    label="Address"
                                    name="address"
                                    value={form.address}
                                    onChange={handleChange}
                                    required
                                    InputProps={{ sx: { borderRadius: 2 } }}
                                />
                                <TextField
                                    fullWidth
                                    label="Password"
                                    name="password"
                                    type="password"
                                    value={form.password}
                                    onChange={handleChange}
                                    required
                                    InputProps={{ sx: { borderRadius: 2 } }}
                                />
                                <TextField
                                    select
                                    fullWidth
                                    label="I am a..."
                                    name="role"
                                    value={form.role}
                                    onChange={handleChange}
                                    required
                                    InputProps={{ sx: { borderRadius: 2 } }}
                                >
                                    {roles.map(role => (
                                        <MenuItem key={role} value={role}>{role.replace('_', ' ').toUpperCase()}</MenuItem>
                                    ))}
                                </TextField>
                            </Box>

                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                sx={{ mt: 3, mb: 2, py: 1.5, fontSize: '1rem' }}
                            >
                                Sign Up
                            </Button>

                            <Box sx={{ textAlign: 'center', mt: 2 }}>
                                <Typography variant="body2">
                                    Already have an account? {' '}
                                    <Link to="/login" style={{ color: theme.palette.primary.main, fontWeight: 600, textDecoration: 'none' }}>
                                        Log In
                                    </Link>
                                </Typography>
                            </Box>
                        </form>
                    </Box>
                </Paper>
            </Box>
        </Box>
    );
}
