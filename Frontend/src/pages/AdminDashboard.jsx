
import { useState, useEffect } from 'react';
import {
    Box, Container, Typography, Button, Paper, Table, TableBody,
    TableCell, TableContainer, TableHead, TableRow, IconButton,
    Dialog, DialogTitle, DialogContent, DialogActions, TextField,
    Alert, CircularProgress, Chip, MenuItem, Snackbar
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { hotelApi } from '../services/hotelApi';
import type { AlertColor } from '@mui/material/Alert';


const AdminDashboard = () => {
    const [villas, setVillas] = useState([]);
    const [loading, setLoading] = useState(true);

    // UI State
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [idToDelete, setIdToDelete] = useState(null);

    // Dialog State (Edit/Create)
    const [open, setOpen] = useState(false);
    const [isEdit, setIsEdit] = useState(false);
    const [currentId, setCurrentId] = useState(null);

    // Form State
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        address: '',
        pricePerNight: '',
        imageUrls: ''
    });

    const fetchVillas = async () => {
        setLoading(true);
        try {
            const response = await hotelApi.getAll();
            if (response.success && Array.isArray(response.data)) {
                setVillas(response.data);
            } else {
                setVillas([]);
            }
        } catch (err) {
            console.error("Failed to fetch villas", err);
            showSnackbar("Failed to load villas: " + (err.response?.data?.message || err.message), "error");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchVillas();
    }, []);

    const showSnackbar = (message, severity = 'success') => {
        setSnackbar({ open: true, message, severity });
    };

    const handeSnackbarClose = () => {
        setSnackbar({ ...snackbar, open: false });
    };

    const handleOpen = (villa = null) => {
        if (villa) {
            setIsEdit(true);
            setCurrentId(villa.id);
            setFormData({
                name: villa.name,
                description: villa.description,
                address: villa.address,
                pricePerNight: villa.pricePerNight,
                imageUrls: villa.imageUrls ? villa.imageUrls.join('\n') : ''
            });
        } else {
            setIsEdit(false);
            setCurrentId(null);
            setFormData({
                name: '',
                description: '',
                address: '',
                pricePerNight: '',
                imageUrls: ''
            });
        }
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async () => {
        const payload = {
            ...formData,
            pricePerNight: parseFloat(formData.pricePerNight),
            imageUrls: formData.imageUrls.split('\n').filter(url => url.trim() !== '')
        };

        // console.log(payload);

        try {
            if (isEdit) {
                await hotelApi.update(currentId, payload);
                showSnackbar('Villa updated successfully!');
            } else {
                await hotelApi.create(payload);
                showSnackbar('Villa created successfully!');
            }
            handleClose();
            fetchVillas();
        } catch (err) {
            console.error(err);
            showSnackbar('Operation failed: ' + (err.response?.data?.message || err.message), 'error');
        }
    };

    const confirmDelete = (id) => {
        setIdToDelete(id);
        setDeleteDialogOpen(true);
    };

    const handleDelete = async () => {
        if (!idToDelete) return;
        try {
            await hotelApi.delete(idToDelete);
            showSnackbar('Villa deleted successfully!');
            fetchVillas();
        } catch (err) {
            console.error(err);
            showSnackbar('Delete failed', 'error');
        } finally {
            setDeleteDialogOpen(false);
            setIdToDelete(null);
        }
    };

    return (
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default', pt: 4, pb: 8 }}>
            <Container maxWidth="xl">
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4, alignItems: 'center' }}>
                    <Typography variant="h4" fontWeight={700} color="text.primary">
                        Admin Dashboard
                    </Typography>
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={() => handleOpen()}
                        sx={{ borderRadius: 2, textTransform: 'none', fontWeight: 600 }}
                    >
                        Add New Villa
                    </Button>
                </Box>

                <Paper elevation={0} sx={{ border: '1px solid', borderColor: 'divider', borderRadius: 3, overflow: 'hidden' }}>
                    {loading ? (
                        <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}><CircularProgress /></Box>
                    ) : (
                        <TableContainer>
                            <Table>
                                <TableHead sx={{ bgcolor: 'grey.50' }}>
                                    <TableRow>
                                        <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>ID</TableCell>
                                        <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>Image</TableCell>
                                        <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>Name</TableCell>
                                        <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>Location</TableCell>
                                        <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>Price</TableCell>
                                        <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>Rating</TableCell>
                                        <TableCell sx={{ fontWeight: 600, color: 'text.secondary', textAlign: 'right' }}>Actions</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {villas.map((villa) => (
                                        <TableRow key={villa.id} hover>
                                            <TableCell sx={{ color: 'text.secondary' }}>#{villa.id}</TableCell>
                                            <TableCell>
                                                <Box
                                                    component="img"
                                                    src={villa.imageUrls?.[0] || 'https://via.placeholder.com/50'}
                                                    sx={{ width: 60, height: 60, borderRadius: 2, objectFit: 'cover' }}
                                                />
                                            </TableCell>
                                            <TableCell sx={{ fontWeight: 500 }}>{villa.name}</TableCell>
                                            <TableCell>{villa.address}</TableCell>
                                            <TableCell sx={{ fontWeight: 600 }}>${villa.pricePerNight}</TableCell>
                                            <TableCell>
                                                <Chip
                                                    label={`${villa.averageRating || 'New'} (${villa.totalRatings || 0})`}
                                                    size="small"
                                                    color={villa.averageRating > 4 ? "success" : "default"}
                                                    variant="outlined"
                                                />
                                            </TableCell>
                                            <TableCell align="right">
                                                <IconButton color="primary" onClick={() => handleOpen(villa)} sx={{ mr: 1 }}>
                                                    <EditIcon />
                                                </IconButton>
                                                <IconButton color="error" onClick={() => confirmDelete(villa.id)}>
                                                    <DeleteIcon />
                                                </IconButton>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                    {villas.length === 0 && (
                                        <TableRow>
                                            <TableCell colSpan={7} align="center" sx={{ py: 5 }}>
                                                <Typography variant="h6" color="text.secondary">No villas found.</Typography>
                                                <Button onClick={() => handleOpen()} sx={{ mt: 2 }}>Create your first villa</Button>
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    )}
                </Paper>

                {/* Add/Edit Dialog */}
                <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
                    <DialogTitle sx={{ fontWeight: 700 }}>{isEdit ? 'Edit Villa' : 'Add New Villa'}</DialogTitle>
                    <DialogContent dividers>
                        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, gap: 3 }}>
                            <Box sx={{ gridColumn: '1 / -1' }}>
                                <TextField
                                    label="Villa Name"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleChange}
                                    fullWidth
                                    required
                                    variant="outlined"
                                />
                            </Box>
                            <TextField
                                label="Address / Location"
                                name="address"
                                value={formData.address}
                                onChange={handleChange}
                                fullWidth
                                required
                            />
                            <TextField
                                label="Price Per Night ($)"
                                name="pricePerNight"
                                value={formData.pricePerNight}
                                onChange={handleChange}
                                type="number"
                                fullWidth
                                required
                            />
                            <Box sx={{ gridColumn: '1 / -1' }}>
                                <TextField
                                    label="Description"
                                    name="description"
                                    value={formData.description}
                                    onChange={handleChange}
                                    fullWidth
                                    multiline
                                    rows={4}
                                    required
                                />
                            </Box>
                            <Box sx={{ gridColumn: '1 / -1' }}>
                                <TextField
                                    label="Image URLs (Enter each URL on a new line)"
                                    name="imageUrls"
                                    value={formData.imageUrls}
                                    onChange={handleChange}
                                    fullWidth
                                    multiline
                                    rows={4}
                                    placeholder="https://example.com/image1.jpg&#10;https://example.com/image2.jpg"
                                    helperText="Provide direct links to images."
                                />
                            </Box>
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ px: 3, py: 2 }}>
                        <Button onClick={handleClose} color="inherit" size="large">Cancel</Button>
                        <Button onClick={handleSubmit} variant="contained" color="primary" size="large">
                            {isEdit ? 'Update Villa' : 'Create Villa'}
                        </Button>
                    </DialogActions>
                </Dialog>

                {/* Delete Confirmation Dialog */}
                <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
                    <DialogTitle>Confirm Delete</DialogTitle>
                    <DialogContent>
                        <Typography>Are you sure you want to delete this villa? This action cannot be undone.</Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
                        <Button onClick={handleDelete} color="error" variant="contained">Delete</Button>
                    </DialogActions>
                </Dialog>

                {/* Global Snackbar */}
                <Snackbar open={snackbar.open} autoHideDuration={6000} onClose={handeSnackbarClose}>
                    <Alert onClose={handeSnackbarClose} severity={['error', 'warning', 'info', 'success'].includes(snackbar.severity) ? snackbar.severity : 'success'} sx={{ width: '100%' }}>
                        {snackbar.message}
                    </Alert>
                </Snackbar>

            </Container>
        </Box>
    );
};

export default AdminDashboard;
