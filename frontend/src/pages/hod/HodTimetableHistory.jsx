import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Paper,
    Chip,
    Stack,
    Alert,
    CircularProgress,
    Tabs,
    Tab,
    Divider
} from "@mui/material";

import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";
import EventNoteIcon from "@mui/icons-material/EventNote";

import useAuth from "../../hooks/useAuth";

import {
    getApprovedTimetables,
    getRejectedTimetables
} from "../../services/hodService";

const formatDateTime = (value) => {

    if (!value) return "-";

    try {

        return new Date(value).toLocaleString(undefined, {
            dateStyle: "medium",
            timeStyle: "short"
        });

    } catch {

        return value;
    }
};

const HodTimetableHistory = () => {

    const { user } = useAuth();

    const [tab, setTab] = useState(0); // 0 = Approved, 1 = Rejected

    const [approved, setApproved] = useState([]);
    const [rejected, setRejected] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        loadHistory();

    }, []);

    const loadHistory = async () => {

        try {

            setLoading(true);
            setError("");

            const [approvedData, rejectedData] = await Promise.all([
                getApprovedTimetables(),
                getRejectedTimetables()
            ]);

            setApproved(Array.isArray(approvedData) ? approvedData : []);
            setRejected(Array.isArray(rejectedData) ? rejectedData : []);

        } catch (err) {

            console.error("Unable to load timetable history:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load timetable history."
            );

        } finally {

            setLoading(false);

        }
    };

    const list = tab === 0 ? approved : rejected;

    if (loading) {

        return (

            <Box display="flex" justifyContent="center" mt={6}>
                <CircularProgress />
            </Box>

        );
    }

    return (

        <Box>

            <Typography variant="h4" fontWeight="bold" mb={1}>
                Timetable Approval History
            </Typography>

            {user?.branchName && (

                <Typography variant="body2" color="text.secondary" mb={3}>
                    {user.branchName} Department
                </Typography>

            )}

            {error && (

                <Alert
                    severity="error"
                    sx={{ mb: 2 }}
                    onClose={() => setError("")}
                >
                    {error}
                </Alert>

            )}

            <Paper sx={{ mb: 3 }}>

                <Tabs
                    value={tab}
                    onChange={(e, value) => setTab(value)}
                    indicatorColor="primary"
                    textColor="primary"
                >

                    <Tab
                        icon={<CheckCircleIcon fontSize="small" />}
                        iconPosition="start"
                        label={`Approved (${approved.length})`}
                    />

                    <Tab
                        icon={<CancelIcon fontSize="small" />}
                        iconPosition="start"
                        label={`Rejected (${rejected.length})`}
                    />

                </Tabs>

            </Paper>

            {list.length === 0 && !error && (

                <Paper sx={{ p: 4, textAlign: "center" }}>

                    <EventNoteIcon
                        sx={{ fontSize: 40, color: "text.disabled", mb: 1 }}
                    />

                    <Typography color="text.secondary">
                        {tab === 0
                            ? "You haven't approved any timetables yet."
                            : "You haven't rejected any timetables yet."}
                    </Typography>

                </Paper>

            )}

            <Stack spacing={2}>

                {list.map((item) => (

                    <Paper
                        key={`${item.studentClassId}-${item.academicYearId}`}
                        sx={{ p: 3 }}
                    >

                        <Stack
                            direction={{ xs: "column", sm: "row" }}
                            justifyContent="space-between"
                            alignItems={{ xs: "flex-start", sm: "center" }}
                            spacing={1}
                        >

                            <Box>

                                <Stack direction="row" spacing={1} alignItems="center">

                                    <Typography variant="h6">
                                        {item.className}
                                    </Typography>

                                    <Chip
                                        size="small"
                                        label={item.academicYear}
                                    />

                                    <Chip
                                        size="small"
                                        color={tab === 0 ? "success" : "error"}
                                        label={item.status}
                                    />

                                </Stack>

                                <Typography
                                    variant="body2"
                                    color="text.secondary"
                                    sx={{ mt: 0.5 }}
                                >
                                    Decided on {formatDateTime(item.decidedAt)}
                                    {" · "}
                                    {item.periodCount} period
                                    {item.periodCount === 1 ? "" : "s"}
                                </Typography>

                            </Box>

                        </Stack>

                        {item.remarks && (

                            <>
                                <Divider sx={{ my: 1.5 }} />

                                <Typography variant="body2">
                                    <strong>Remarks:</strong> {item.remarks}
                                </Typography>
                            </>

                        )}

                    </Paper>

                ))}

            </Stack>

        </Box>

    );

};

export default HodTimetableHistory;
