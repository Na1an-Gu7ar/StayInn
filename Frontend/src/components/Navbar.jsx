import { useState, useEffect } from 'react';
import { AppBar, Box, Toolbar, IconButton, Typography, Menu, Container, Button, MenuItem, Drawer, List, ListItem, ListItemText, useScrollTrigger, Avatar, Tooltip } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import HotelIcon from '@mui/icons-material/Hotel'; // Brand Icon
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useTheme } from '@mui/material/styles';
import { useAuth } from '../context/AuthContext';

const pages = [
    { name: 'Home', path: '/' },
    { name: 'Stays', path: '/listings' },
    { name: 'Deals', path: '/listings' }, // Placeholder
];

function ElevationScroll(props) {
    const { children, window } = props;
    const trigger = useScrollTrigger({
        disableHysteresis: true,
        threshold: 0,
        target: window ? window() : undefined,
    });

    return children;
}

const Navbar = (props) => {
    const [anchorElNav, setAnchorElNav] = useState(null);
    const [anchorElUser, setAnchorElUser] = useState(null);
    const [mobileOpen, setMobileOpen] = useState(false);

    const navigate = useNavigate();
    const location = useLocation();
    const theme = useTheme();
    const { user, login, logout, isAuthenticated, isAdmin } = useAuth();

    const handleOpenNavMenu = (event) => {
        setAnchorElNav(event.currentTarget);
    };
    const handleOpenUserMenu = (event) => {
        setAnchorElUser(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

    const handleCloseUserMenu = () => {
        setAnchorElUser(null);
    };

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };

    const handleNavigate = (path) => {
        navigate(path);
        setMobileOpen(false);
    };

    const drawer = (
        <Box onClick={handleDrawerToggle} sx={{ textAlign: 'center' }}>
            <Typography variant="h6" sx={{ my: 2, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, color: theme.palette.primary.main }}>
                <HotelIcon /> BookStay
            </Typography>
            <List>
                {pages.map((item) => (
                    <ListItem key={item.name} disablePadding>
                        <Button
                            fullWidth
                            sx={{
                                textAlign: 'center',
                                color: location.pathname === item.path ? 'primary.main' : 'text.primary'
                            }}
                            onClick={() => handleNavigate(item.path)}
                        >
                            {item.name}
                        </Button>
                    </ListItem>
                ))}

                {isAuthenticated ? (
                    <>
                        <ListItem disablePadding>
                            <Button fullWidth onClick={() => handleNavigate('/profile')}>My Profile</Button>
                        </ListItem>
                        {isAdmin && (
                            <ListItem disablePadding>
                                <Button fullWidth onClick={() => handleNavigate('/admin')} color="warning">Admin Dashboard</Button>
                            </ListItem>
                        )}
                        <ListItem disablePadding>
                            <Button fullWidth onClick={logout}>Logout</Button>
                        </ListItem>
                    </>
                ) : (
                    <>
                        <ListItem disablePadding>
                            <Button fullWidth onClick={() => { navigate('/login'); setMobileOpen(false); }}>Login</Button>
                        </ListItem>
                        <ListItem disablePadding>
                            <Button fullWidth onClick={() => { navigate('/signup'); setMobileOpen(false); }} color="primary">Sign Up</Button>
                        </ListItem>
                    </>
                )}
            </List>
        </Box >
    );

    return (
        <>
            <ElevationScroll {...props}>
                <AppBar
                    position="sticky"
                    sx={{
                        borderBottom: '1px solid rgba(0,0,0,0.05)'
                    }}
                >
                    <Container maxWidth="xl">
                        <Toolbar disableGutters>
                            {/* DESKTOP LOGO */}
                            <HotelIcon sx={{ display: { xs: 'none', md: 'flex' }, mr: 1, color: 'primary.main' }} />
                            <Typography
                                variant="h6"
                                noWrap
                                component="a"
                                onClick={() => navigate('/')}
                                sx={{
                                    mr: 2,
                                    display: { xs: 'none', md: 'flex' },
                                    fontFamily: 'Inter',
                                    fontWeight: 700,
                                    letterSpacing: '-0.5px',
                                    color: 'text.primary',
                                    textDecoration: 'none',
                                    cursor: 'pointer'
                                }}
                            >
                                BookStay
                            </Typography>

                            {/* MOBILE MENU */}
                            <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
                                <IconButton
                                    size="large"
                                    aria-label="open drawer"
                                    aria-controls="menu-appbar"
                                    aria-haspopup="true"
                                    onClick={handleDrawerToggle}
                                    color="inherit"
                                >
                                    <MenuIcon sx={{ color: 'text.primary' }} />
                                </IconButton>
                            </Box>

                            {/* MOBILE LOGO */}
                            <HotelIcon sx={{ display: { xs: 'flex', md: 'none' }, mr: 1, color: 'primary.main' }} />
                            {/* Responsive spacer */}
                            <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }} />

                            {/* DESKTOP LINKS */}
                            <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' }, justifyContent: 'center', gap: 2 }}>
                                {pages.map((page) => (
                                    <Button
                                        key={page.name}
                                        onClick={() => handleNavigate(page.path)}
                                        sx={{
                                            my: 2,
                                            display: 'block',
                                            color: location.pathname === page.path ? 'primary.main' : 'text.primary',
                                            fontWeight: location.pathname === page.path ? 600 : 400,
                                        }}
                                    >
                                        {page.name}
                                    </Button>
                                ))}
                            </Box>

                            {/* USER / AUTH */}
                            <Box sx={{ flexGrow: 0, display: { xs: 'none', md: 'flex' }, gap: 1, alignItems: 'center' }}>
                                {isAuthenticated ? (
                                    <>
                                        {isAdmin && (
                                            <Button
                                                onClick={() => navigate('/admin')}
                                                variant="outlined"
                                                size="small"
                                                color="warning"
                                                sx={{ mr: 1, borderRadius: '20px' }}
                                            >
                                                Admin
                                            </Button>
                                        )}
                                        <Tooltip title={`Logged in as ${user.name}`}>
                                            <Avatar
                                                src={user.avatar}
                                                sx={{ width: 32, height: 32, mr: 1, cursor: 'pointer' }}
                                                onClick={() => navigate('/profile')}
                                            />
                                        </Tooltip>
                                        <Button
                                            onClick={logout}
                                            sx={{ color: 'text.primary', fontWeight: 600 }}
                                        >
                                            Logout
                                        </Button>
                                    </>
                                ) : (
                                    <>
                                        <Button
                                            onClick={() => navigate('/login')}
                                            sx={{ color: 'text.primary', fontWeight: 600 }}
                                        >
                                            Login
                                        </Button>
                                        <Button
                                            variant="contained"
                                            color="primary"
                                            onClick={() => navigate('/signup')}
                                            sx={{ borderRadius: '20px', px: 3 }}
                                        >
                                            Sign up
                                        </Button>
                                    </>
                                )}
                            </Box>
                        </Toolbar>
                    </Container>
                </AppBar>
            </ElevationScroll>

            <nav>
                <Drawer
                    variant="temporary"
                    open={mobileOpen}
                    onClose={handleDrawerToggle}
                    ModalProps={{
                        keepMounted: true, // Better open performance on mobile.
                    }}
                    sx={{
                        display: { xs: 'block', md: 'none' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: 240 },
                    }}
                >
                    {drawer}
                </Drawer>
            </nav>

            <Box component="main">
                <Outlet />
            </Box>
        </>
    );
};
export default Navbar;
