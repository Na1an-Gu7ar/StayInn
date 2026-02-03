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
import ResetPassword from "./pages/ResetPassword"

const Landing = lazy(() => import("./pages/Landing"))
const HotelDetails = lazy(() => import("./pages/HotelDetails"))
const Payment = lazy(() => import('./pages/Payment'));
const AdminDashboard = lazy(() => import("./pages/AdminDashboard"));
const Profile = lazy(() => import("./pages/Profile"));

const AnimatedRoutes = () => {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/reset_password" element={<ResetPassword />} />

        <Route path="/payment/:bookingId" element={
          <ProtectedRoute allowedRoles={['USER', 'ADMIN']}>
            <Payment />
          </ProtectedRoute>
        } />

        {/* Redirect unknown routes to Login */}
        <Route path="*" element={<Navigate to="/login" replace />} />

        <Route path="/" element={<Navbar />}>
          <Route index element={<Landing />} />

          {/* Protected Routes */}
          <Route
            path="/listings"
            element={
              <ProtectedRoute>
                <ListingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/listings/:id"
            element={
              <ProtectedRoute>
                <HotelDetails />
              </ProtectedRoute>
            }
          />

          {/* Admin Protected Route */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />

          {/* User Profile */}
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
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
