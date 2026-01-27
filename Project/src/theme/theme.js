import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    palette: {
        primary: {
            main: '#FF385C', // Vibrant Coral/Red (Modern Travel Vibe)
            light: '#FF7990',
            dark: '#D90B3E',
            contrastText: '#ffffff',
        },
        secondary: {
            main: '#00A699', // Teal/Aqua
            light: '#33B8AD',
            dark: '#007A70',
            contrastText: '#ffffff',
        },
        background: {
            default: '#F7F7F9', // Very light grey, not stark white
            paper: '#ffffff',
        },
        text: {
            primary: '#222222',
            secondary: '#717171',
        },
    },
    typography: {
        fontFamily: [
            'Inter',
            '-apple-system',
            'BlinkMacSystemFont',
            '"Segoe UI"',
            'Roboto',
            '"Helvetica Neue"',
            'Arial',
            'sans-serif',
        ].join(','),
        h1: { fontWeight: 700, fontSize: '3rem' },
        h2: { fontWeight: 700, fontSize: '2.25rem' },
        h3: { fontWeight: 600, fontSize: '2rem' },
        h4: { fontWeight: 600, fontSize: '1.5rem' },
        h5: { fontWeight: 600, fontSize: '1.25rem' },
        h6: { fontWeight: 600, fontSize: '1rem' },
        button: { textTransform: 'none', fontWeight: 600 },
    },
    shape: {
        borderRadius: 12,
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: '50px', // Pill shape for buttons
                    padding: '10px 24px',
                    boxShadow: 'none',
                    '&:hover': {
                        boxShadow: '0px 4px 12px rgba(0, 0, 0, 0.15)',
                    },
                },
                containedPrimary: {
                    background: 'linear-gradient(90deg, #FF385C 0%, #E61E43 100%)',
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    borderRadius: '16px',
                    boxShadow: '0px 6px 16px rgba(0,0,0,0.08)',
                    transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                    '&:hover': {
                        transform: 'translateY(-4px)',
                        boxShadow: '0px 12px 24px rgba(0,0,0,0.12)',
                    },
                },
            },
        },
        MuiPaper: {
            elevation: 0,
        },
        MuiAppBar: {
            styleOverrides: {
                root: {
                    background: 'rgba(255, 255, 255, 0.8)',
                    backdropFilter: 'blur(10px)',
                    boxShadow: '0px 1px 0px rgba(0,0,0,0.08)',
                    color: '#222222',
                },
            },
        },
    },
});

export default theme;
