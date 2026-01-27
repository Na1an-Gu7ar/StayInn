import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { PrimeReactProvider } from 'primereact/api'

import { ThemeProvider } from '@mui/material/styles'
import CssBaseline from '@mui/material/CssBaseline'
import theme from './theme/theme'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <PrimeReactProvider value={{ unstyled: true }}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <App />
      </ThemeProvider>
    </PrimeReactProvider>
  </StrictMode>,
)
