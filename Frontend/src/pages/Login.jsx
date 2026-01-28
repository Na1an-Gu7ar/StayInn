import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Box, TextField, Button, Typography, CircularProgress, Paper, useMediaQuery, useTheme } from '@mui/material';
import CustomizedSnackbars from '../components/Snackbar';
import { motion } from "framer-motion"

export default function Login() {
    const [form, setForm] = useState({ email: '', password: '' });
    const [loading, setLoading] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
    const navigate = useNavigate();
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down('md'));

    // Use login from context
    const { login } = useAuth(); // ADDED

    const handleChange = e => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async e => {
        e.preventDefault();
        setLoading(true);
        try {
            await login(form);
            setSnackbar({ open: true, message: 'Logged in successfully!', severity: 'success' });
            setTimeout(() => navigate('/'), 1500);
        } catch (error) {
            setSnackbar({ open: true, message: error.message || 'Login failed', severity: 'error' });
        } finally {
            setLoading(false);
        }
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
                        backgroundImage: 'url(https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070)',
                        backgroundSize: 'cover',
                        backgroundPosition: 'center',
                    }}
                />
                <Box sx={{
                    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
                    // background: 'rgba(0,0,0,0.4)',
                    display: 'flex', flexDirection: 'column', justifyContent: 'center', p: 6, color: 'white'
                }}>
                    <Box sx={{ background: 'rgba(0,0,0,0.2)', p: 2, boxShadow: '0 4px 6px rgba(0,0,0,0.5)' }}>
                        <Typography variant="h2" fontWeight={700} gutterBottom>
                            Welcome Back
                        </Typography>
                        <Typography variant="h5">
                            Discover your next dream destination with us.
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
                        maxWidth: 450,
                        p: 4,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center'
                    }}
                >
                    <Box
                        component={motion.div}
                        initial={{ opacity: 0, x: 20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ duration: 0.5 }}
                        sx={{ width: '100%' }}
                    >
                        <Box sx={{ textAlign: 'center', mb: 4 }}>
                            <Typography variant="h4" fontWeight={700} color="primary">BookStay</Typography>
                            <Typography variant="h5" fontWeight={600} mt={2}>Log in to your account</Typography>
                            <Typography variant="body2" color="text.secondary" mt={1}>Welcome back! Please enter your details.</Typography>
                        </Box>

                        <form onSubmit={handleSubmit} style={{ width: '100%' }}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                id="email"
                                label="Email Address"
                                name="email"
                                autoComplete="email"
                                autoFocus
                                value={form.email}
                                onChange={handleChange}
                                InputProps={{ sx: { borderRadius: 2 } }}
                            />
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                name="password"
                                label="Password"
                                type="password"
                                id="password"
                                autoComplete="current-password"
                                value={form.password}
                                onChange={handleChange}
                                InputProps={{ sx: { borderRadius: 2 } }}
                            />

                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                disabled={loading}
                                sx={{ mt: 3, mb: 2, py: 1.5, fontSize: '1rem' }}
                            >
                                {loading ? <CircularProgress size={24} color="inherit" /> : 'Sign In'}
                            </Button>

                            <Box sx={{ textAlign: 'center', mt: 2 }}>
                                <Typography variant="body2">
                                    Don't have an account? {' '}
                                    <Link to="/signup" style={{ color: theme.palette.primary.main, fontWeight: 600, textDecoration: 'none' }}>
                                        Sign Up
                                    </Link>
                                </Typography>
                            </Box>
                        </form>
                    </Box>
                </Paper>
            </Box>

            <CustomizedSnackbars
                openSnackbar={snackbar.open}
                message={snackbar.message}
                severity={snackbar.severity}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
            />
        </Box>
    );
}
