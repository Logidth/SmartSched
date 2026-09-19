import { Navigate } from "react-router-dom";
import { Box, CircularProgress } from "@mui/material";
import useAuth from "../hooks/useAuth";
import DashboardLayout from "../components/layout/DashboardLayout";

/**
 * Wraps a route element and only renders it when:
 *   1. the user is authenticated, and
 *   2. their role is in `allowedRoles` (when provided)
 *
 * Once those checks pass, the element is rendered inside
 * DashboardLayout (sidebar + topbar) so every protected page gets
 * consistent navigation without having to wrap itself individually.
 * Pass `withLayout={false}` for the rare protected page that wants
 * to manage its own full-bleed layout instead.
 *
 * Usage:
 *   <Route path="/admin/dashboard" element={
 *       <ProtectedRoute allowedRoles={["ADMIN"]}>
 *           <AdminDashboard />
 *       </ProtectedRoute>
 *   } />
 */
const ProtectedRoute = ({ children, allowedRoles, withLayout = true }) => {

    const { user, loading, isAuthenticated } = useAuth();

    if (loading) {

        return (

            <Box
                display="flex"
                justifyContent="center"
                alignItems="center"
                minHeight="100vh"
            >

                <CircularProgress />

            </Box>

        );

    }

    if (!isAuthenticated) {

        return <Navigate to="/login" replace />;

    }

    if (user?.firstLogin) {

        return <Navigate to="/change-password" replace />;

    }

    if (allowedRoles && !allowedRoles.includes(user?.role)) {

        // Logged in, but wrong role for this route -
        // send them to their own dashboard instead of a dead end.
        const fallback = {
            PRINCIPAL: "/principal/dashboard",
            ADMIN: "/admin/dashboard",
            HOD: "/hod/dashboard",
            FACULTY: "/faculty/dashboard"
        }[user?.role] || "/login";

        return <Navigate to={fallback} replace />;

    }

    if (!withLayout) {

        return children;

    }

    return (
        <DashboardLayout>
            {children}
        </DashboardLayout>
    );

};

export default ProtectedRoute;