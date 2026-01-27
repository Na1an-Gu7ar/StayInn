import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Box, Typography, Button, Container } from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';

const Unauthorized = () => {
    return (
        <Container maxWidth="sm" sx={{ height: '80vh', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
            <Box sx={{ bgcolor: 'error.soft', p: 3, borderRadius: '50%', mb: 3 }}>
                <LockOutlinedIcon sx={{ fontSize: 60, color: 'error.main' }} />
            </Box>
            <Typography variant="h3" fontWeight={700} gutterBottom align="center">
                Access Denied
            </Typography>
            <Typography variant="h6" color="text.secondary" align="center" paragraph>
                You do not have permission to view this page.
            </Typography>
            <Button variant="contained" href="/" sx={{ mt: 2 }}>
                Back to Home
            </Button>
        </Container>
    );
};

const ProtectedRoute = ({ children, allowedRoles }) => {
    const { user, isAuthenticated } = useAuth();
    const location = useLocation();

    if (!isAuthenticated) {
        // Redirect to login but save the attempted location
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return <Unauthorized />;
    }

    return children;
};

export default ProtectedRoute;
