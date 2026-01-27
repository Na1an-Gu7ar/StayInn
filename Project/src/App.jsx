import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from "react-router-dom"
import Login from "./pages/Login"
import SignUp from "./pages/SignUp"
import Navbar from "./components/Navbar"
import ListingsPage from "./pages/ListingsPage"
import { lazy, Suspense } from "react"
import { AnimatePresence } from "framer-motion"
import { Box } from "@mui/material"
import { AuthProvider } from "./context/AuthContext"
import PageLoader from "./components/PageLoader"
import ProtectedRoute from "./components/ProtectedRoute"

const Landing = lazy(() => import("./pages/Landing"))
const HotelDetails = lazy(() => import("./pages/HotelDetails"))

// Mock Admin Page
const AdminDashboard = () => (
  <Box sx={{ p: 5, textAlign: 'center' }}>
    <h1>Admin Dashboard</h1>
    <p>Only Admins can see this.</p>
  </Box>
);

const AnimatedRoutes = () => {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="*" element={<Navigate to="/login" />} />
        <Route path="/" element={<Navbar />}>
          <Route index element={<Landing />} />
          <Route path="/listings" element={<ListingsPage />} />
          <Route path="/listings/:id" element={<HotelDetails />} />

          {/* Admin Protected Route */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
        </Route>
      </Routes>
    </AnimatePresence>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
          <Suspense fallback={<PageLoader />}>
            <AnimatedRoutes />
          </Suspense>
        </Box>
      </Router>
    </AuthProvider>
  )
}

export default App
