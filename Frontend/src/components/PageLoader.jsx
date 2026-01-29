import { Box, CircularProgress, Typography, useTheme } from '@mui/material';
import { motion } from 'framer-motion';

const PageLoader = ({ message = "Loading..." }) => {
    const theme = useTheme();

    return (
        <Box
            sx={{
                position: 'fixed',
                top: 0,
                left: 0,
                width: '100vw',
                height: '100vh',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                bgcolor: 'background.default',
                zIndex: 9999,
            }}
            component={motion.div}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
        >
            <Box position="relative" display="inline-flex">
                <CircularProgress
                    size={80}
                    thickness={4}
                    sx={{ color: theme.palette.primary.main }}
                />
                <Box
                    sx={{
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 0,
                        position: 'absolute',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                    }}
                >
                    <Typography
                        variant="caption"
                        component="div"
                        color="text.secondary"
                        fontWeight={700}
                    >
                        BSTAY
                    </Typography>
                </Box>
            </Box>
            <Typography
                variant="h6"
                sx={{ mt: 3, fontWeight: 500, color: 'text.secondary', letterSpacing: 1 }}
                component={motion.div}
                animate={{ opacity: [0.5, 1, 0.5] }}
                transition={{ duration: 1.5, repeat: Infinity }}
            >
                {message}
            </Typography>
        </Box>
    );
};

export default PageLoader;
