import { useEffect, useState } from "react";

import {
    Box,
    Grid,
    Paper,
    Typography,
    CircularProgress,
    Alert,
    Stack,
    Button
} from "@mui/material";

import PeopleIcon from "@mui/icons-material/People";
import SchoolIcon from "@mui/icons-material/School";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import PendingIcon from "@mui/icons-material/Pending";
import { useNavigate } from "react-router-dom";

import useAuth from "../../hooks/useAuth";
import { getHodDashboard } from "../../services/dashboardService";

const StatCard = ({ icon, label, value, color }) => (

    <Paper sx={{ p: 3, height: "100%" }}>

        <Stack direction="row" spacing={2} alignItems="center">

            <Box
                sx={{
                    width: 48,
                    height: 48,
                    borderRadius: "50%",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    backgroundColor: `${color}.light`,
                    color: `${color}.dark`
                }}
            >
                {icon}
            </Box>

            <Box>

                <Typography variant="h5" fontWeight="bold">
                    {value}
                </Typography>

                <Typography variant="body2" color="text.secondary">
                    {label}
                </Typography>

            </Box>

        </Stack>

    </Paper>

);

const AdminDashboard = () => {

    const { user } = useAuth();
    const navigate = useNavigate();

    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        loadDashboard();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.branchId]);

    const loadDashboard = async () => {

        if (!user?.branchId) {

            setError(
                "No department is linked to your account. " +
                "Please contact the Principal to assign a branch."
            );

            setLoading(false);

            return;

        }

        try {

            setLoading(true);
            setError("");

            const response = await getHodDashboard(user.branchId);

            // Controller wraps the payload in { success, message, data, timestamp }
            setSummary(response?.data ?? response ?? null);

        } catch (err) {

            console.error("Unable to load admin dashboard:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load department dashboard."
            );

        } finally {

            setLoading(false);

        }

    };

    if (loading) {

        return (

            <Box display="flex" justifyContent="center" mt={6}>
                <CircularProgress />
            </Box>

        );

    }

    return (

        <Box>

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Box>

                    <Typography variant="h4" fontWeight="bold">
                        {summary?.branch
                            ? `${summary.branch} Department`
                            : "Department Dashboard"}
                    </Typography>

                    <Typography variant="body2" color="text.secondary">
                        Welcome back, {user?.username}
                    </Typography>

                </Box>

            </Stack>

            {error && (

                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>

            )}

            {summary && (

                <>

                    <Grid container spacing={3} mb={3}>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <StatCard
                                icon={<PeopleIcon />}
                                label="Faculty"
                                value={summary.facultyCount}
                                color="primary"
                            />
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <StatCard
                                icon={<SchoolIcon />}
                                label="Student Classes"
                                value={summary.classCount}
                                color="secondary"
                            />
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <StatCard
                                icon={<MenuBookIcon />}
                                label="Subjects"
                                value={summary.subjectCount}
                                color="info"
                            />
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <StatCard
                                icon={<CheckCircleIcon />}
                                label="Approved Timetables"
                                value={summary.approvedTimetables}
                                color="success"
                            />
                        </Grid>

                    </Grid>

                    <Grid container spacing={3} mb={3}>

                        <Grid size={{ xs: 12 }}>

                            <StatCard
                                icon={<PendingIcon />}
                                label="Pending Timetable Approvals"
                                value={summary.pendingTimetables}
                                color="warning"
                            />

                        </Grid>

                    </Grid>

                    <Paper sx={{ p: 3 }}>

                        <Typography variant="h6" mb={2}>
                            Quick Actions
                        </Typography>

                        <Stack direction="row" spacing={2} flexWrap="wrap" useFlexGap>

                            <Button
                                variant="contained"
                                onClick={() => navigate("/admin/faculties")}
                            >
                                Add Faculty
                            </Button>

                            <Button
                                variant="outlined"
                                onClick={() => navigate("/admin/subjects")}
                            >
                                Manage Subjects
                            </Button>

                            <Button
                                variant="outlined"
                                onClick={() => navigate("/admin/student-classes")}
                            >
                                Manage Classes
                            </Button>

                            <Button
                                variant="outlined"
                                color="secondary"
                                onClick={() => navigate("/admin/timetable")}
                            >
                                Generate Timetable
                            </Button>

                        </Stack>

                    </Paper>

                </>

            )}

        </Box>

    );

};

export default AdminDashboard;