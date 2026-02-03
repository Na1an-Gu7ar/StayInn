ResetPassword.jsx
import { useParams, useNavigate } from "react-router-dom";
import { useState } from "react";
import { userApi } from "../services/userApi";
import {
    Box,
    Typography,
    TextField,
    Button,
    Paper,
    Container,
    InputAdornment,
    IconButton,
    CircularProgress
} from "@mui/material";
import { Visibility, VisibilityOff, LockReset as LockResetIcon } from "@mui/icons-material";
import { motion } from "framer-motion";

export default function ResetPassword() {
    const navigate = useNavigate();
    const { token } = useParams();
    const [resetPassword, setResetPassword] = useState({ email: '', newPassword: '' });
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await userApi.changePassword(resetPassword);
            alert("Password reset successful");
            navigate("/login");
        } catch (error) {
            console.error("Reset failed", error);
            alert("Failed to reset password. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleChange = e => setResetPassword({ ...resetPassword, [e.target.name]: e.target.value });

    const handleClickShowPassword = () => setShowPassword((show) => !show);

    return (
        <Box
            sx={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                bgcolor: 'grey.100',
                p: 2
            }}
        >
            <Container maxWidth="xs">
                <Paper
                    component={motion.div}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5 }}
                    elevation={3}
                    sx={{ p: 4, borderRadius: 4, textAlign: 'center' }}
                >
                    <Box sx={{ mb: 2, display: 'flex', justifyContent: 'center' }}>
                        <Box sx={{ p: 1.5, bgcolor: 'primary.light', borderRadius: '50%', color: 'primary.main' }}>
                            <LockResetIcon sx={{ fontSize: 40 }} />
                        </Box>
                    </Box>

                    <Typography variant="h5" fontWeight="bold" gutterBottom>
                        Set New Password
                    </Typography>

                    <Typography variant="body2" color="text.secondary" mb={4}>
                        Enter your email and new password to reset your account credentials.
                    </Typography>

                    <form onSubmit={handleSubmit}>
                        <TextField
                            fullWidth
                            margin="normal"
                            label="Email Address"
                            name="email"
                            type="email"
                            value={resetPassword.email}
                            onChange={handleChange}
                            required
                            InputProps={{ sx: { borderRadius: 2 } }}
                        />

                        <TextField
                            fullWidth
                            margin="normal"
                            label="New Password"
                            name="newPassword"
                            type={showPassword ? 'text' : 'password'}
                            value={resetPassword.newPassword}
                            onChange={handleChange}
                            required
                            InputProps={{
                                sx: { borderRadius: 2 },
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton
                                            aria-label="toggle password visibility"
                                            onClick={handleClickShowPassword}
                                            edge="end"
                                        >
                                            {showPassword ? <VisibilityOff /> : <Visibility />}
                                        </IconButton>
                                    </InputAdornment>
                                ),
                            }}
                        />

                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            size="large"
                            disabled={loading}
                            sx={{ mt: 3, mb: 2, borderRadius: 2, py: 1.5, fontWeight: 'bold' }}
                        >
                            {loading ? <CircularProgress size={24} color="inherit" /> : "Reset Password"}
                        </Button>
                    </form>
                </Paper>
            </Container>
        </Box>
    );
}
