import { useEffect, useState } from "react";

import {
    Box,
    Grid,
    Paper,
    Typography,
    CircularProgress,
    Alert,
    LinearProgress,
    Stack,
    Button
} from "@mui/material";

import MenuBookIcon from "@mui/icons-material/MenuBook";
import EventNoteIcon from "@mui/icons-material/EventNote";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import PendingIcon from "@mui/icons-material/Pending";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import { useNavigate } from "react-router-dom";

import useAuth from "../../hooks/useAuth";
import { getMyDashboard } from "../../services/Facultyselfservice";

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
                    {value ?? 0}
                </Typography>

                <Typography variant="body2" color="text.secondary">
                    {label}
                </Typography>

            </Box>

        </Stack>

    </Paper>

);

const FacultyDashboard = () => {

    const { user } = useAuth();
    const navigate = useNavigate();

    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        loadDashboard();

    }, []);

    const loadDashboard = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getMyDashboard();

            setSummary(data);

        } catch (err) {

            console.error("Unable to load faculty dashboard:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load your dashboard."
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
                        {summary?.facultyName
                            ? `Welcome, ${summary.facultyName}`
                            : "Faculty Dashboard"}
                    </Typography>

                    <Typography variant="body2" color="text.secondary">
                        {user?.username}
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
                                icon={<MenuBookIcon />}
                                label="Assigned Subjects"
                                value={summary.assignedSubjects}
                                color="primary"
                            />
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <StatCard
                                icon={<CalendarMonthIcon />}
                                label="Weekly Periods"
                                value={summary.weeklyPeriods}
                                color="secondary"
                            />
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <StatCard
                                icon={<CheckCircleIcon />}
                                label="Completed Lectures"
                                value={summary.completedLectures}
                                color="success"
                            />
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <StatCard
                                icon={<PendingIcon />}
                                label="Pending Lectures"
                                value={summary.pendingLectures}
                                color="warning"
                            />
                        </Grid>

                    </Grid>

                    <Paper sx={{ p: 3, mb: 3 }}>

                        <Typography variant="body2" color="text.secondary" mb={1}>
                            Syllabus Completion
                        </Typography>

                        <Stack direction="row" spacing={2} alignItems="center">

                            <Box flexGrow={1}>
                                <LinearProgress
                                    variant="determinate"
                                    value={Math.min(
                                        summary.completionPercentage || 0,
                                        100
                                    )}
                                    sx={{ height: 10, borderRadius: 5 }}
                                />
                            </Box>

                            <Typography variant="body2" fontWeight="bold">
                                {(summary.completionPercentage || 0).toFixed(1)}%
                            </Typography>

                        </Stack>

                    </Paper>

                    <Paper sx={{ p: 3 }}>

                        <Typography variant="h6" mb={2}>
                            Quick Actions
                        </Typography>

                        <Stack direction="row" spacing={2} flexWrap="wrap" useFlexGap>

                            <Button
                                variant="contained"
                                color="secondary"
                                startIcon={<MenuBookIcon />}
                                onClick={() => navigate("/faculty/subjects")}
                            >
                                My Subjects
                            </Button>

                            <Button
                                variant="outlined"
                                startIcon={<CalendarMonthIcon />}
                                onClick={() => navigate("/faculty/timetable")}
                            >
                                My Timetable
                            </Button>

                            <Button
                                variant="outlined"
                                startIcon={<EventNoteIcon />}
                                onClick={() => navigate("/faculty/leave")}
                            >
                                Apply / View Leave
                            </Button>

                        </Stack>

                    </Paper>

                </>

            )}

        </Box>

    );

};

export default FacultyDashboard;
