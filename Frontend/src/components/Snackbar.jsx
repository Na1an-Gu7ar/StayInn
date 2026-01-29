import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';

export default function CustomizedSnackbars({openSnackbar, message, severity, onClose}) {
    return (
        <div>
            <Snackbar open={openSnackbar} autoHideDuration={1500} onClose={onClose}>
                <Alert
                    severity={severity || 'success'}
                    onClose={onClose}
                    variant="filled"
                    sx={{ width: '100%' }}
                >
                    {message || 'This is a success message!'}
                </Alert>
            </Snackbar>
        </div>
    );
}
