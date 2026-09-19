import { useEffect, useMemo, useState } from "react";

import {
    Box,
    Typography,
    Paper,
    Chip,
    Button,
    Stack,
    Alert,
    CircularProgress,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField
} from "@mui/material";

import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";

import useAuth from "../../hooks/useAuth";

import TimetableGrid from "../../components/timetable/TimetableGrid";

import {
    getPendingTimetables,
    approveTimetable,
    rejectTimetable
} from "../../services/hodService";

const DAY_ORDER = [
    "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"
];

const HodTimetableApprovals = () => {

    const { user } = useAuth();

    const [entries, setEntries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [actionLoading, setActionLoading] = useState(false);

    const [decisionDialog, setDecisionDialog] = useState(null);
    // { type: "approve" | "reject", studentClassId, academicYearId, className }
    const [remarks, setRemarks] = useState("");

    useEffect(() => {

        loadPending();

    }, []);

    const loadPending = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getPendingTimetables();

            setEntries(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load pending timetables:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load pending timetables."
            );

        } finally {

            setLoading(false);

        }
    };

    // Group the flat list of timetable entries into one card per
    // student class + academic year, since that's the unit the HOD
    // actually approves or rejects.
    const groups = useMemo(() => {

        const map = new Map();

        entries.forEach((entry) => {

            const key = `${entry.studentClassId}-${entry.academicYearId}`;

            if (!map.has(key)) {

                map.set(key, {
                    studentClassId: entry.studentClassId,
                    academicYearId: entry.academicYearId,
                    className: entry.className,
                    academicYear: entry.academicYear,
                    rows: []
                });
            }

            map.get(key).rows.push(entry);

        });

        return Array.from(map.values()).map((group) => ({
            ...group,
            rows: [...group.rows].sort((a, b) => {

                const dayDiff =
                    DAY_ORDER.indexOf(a.day) - DAY_ORDER.indexOf(b.day);

                if (dayDiff !== 0) return dayDiff;

                return (a.periodNumber ?? 0) - (b.periodNumber ?? 0);
            })
        }));

    }, [entries]);

    const openDecision = (type, group) => {

        setRemarks("");

        setDecisionDialog({
            type,
            studentClassId: group.studentClassId,
            academicYearId: group.academicYearId,
            className: group.className
        });
    };

    const closeDecision = () => {

        setDecisionDialog(null);
        setRemarks("");

    };

    const confirmDecision = async () => {

        if (!decisionDialog) return;

        try {

            setActionLoading(true);
            setError("");
            setMessage("");

            const payload = {
                studentClassId: decisionDialog.studentClassId,
                academicYearId: decisionDialog.academicYearId,
                remarks
            };

            if (decisionDialog.type === "approve") {

                await approveTimetable(payload);

                setMessage(
                    `Timetable for ${decisionDialog.className} approved.`
                );

            } else {

                await rejectTimetable(payload);

                setMessage(
                    `Timetable for ${decisionDialog.className} rejected.`
                );
            }

            closeDecision();

            await loadPending();

        } catch (err) {

            console.error("Unable to record decision:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to record decision."
            );

        } finally {

            setActionLoading(false);

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

            <Typography variant="h4" fontWeight="bold" mb={1}>
                Timetable Approvals
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

            {message && (

                <Alert
                    severity="success"
                    sx={{ mb: 2 }}
                    onClose={() => setMessage("")}
                >
                    {message}
                </Alert>

            )}

            {groups.length === 0 && !error && (

                <Paper sx={{ p: 4, textAlign: "center" }}>

                    <Typography color="text.secondary">
                        No timetables are waiting for your approval right now.
                    </Typography>

                </Paper>

            )}

            <Stack spacing={3}>

                {groups.map((group) => (

                    <Paper
                        key={`${group.studentClassId}-${group.academicYearId}`}
                        sx={{ p: 3 }}
                    >

                        <Stack
                            direction={{ xs: "column", sm: "row" }}
                            justifyContent="space-between"
                            alignItems={{ xs: "flex-start", sm: "center" }}
                            spacing={1}
                            mb={2}
                        >

                            <Box>

                                <Typography variant="h6">
                                    {group.className}
                                </Typography>

                                <Chip
                                    size="small"
                                    label={group.academicYear}
                                    sx={{ mt: 0.5 }}
                                />

                            </Box>

                            <Stack direction="row" spacing={1}>

                                <Button
                                    variant="contained"
                                    color="success"
                                    size="small"
                                    startIcon={<CheckCircleIcon />}
                                    onClick={() => openDecision("approve", group)}
                                >
                                    Approve
                                </Button>

                                <Button
                                    variant="outlined"
                                    color="error"
                                    size="small"
                                    startIcon={<CancelIcon />}
                                    onClick={() => openDecision("reject", group)}
                                >
                                    Reject
                                </Button>

                            </Stack>

                        </Stack>

                        <TimetableGrid
                            timetable={group.rows}
                            title={null}
                        />

                    </Paper>

                ))}

            </Stack>

            <Dialog
                open={Boolean(decisionDialog)}
                onClose={closeDecision}
                fullWidth
                maxWidth="sm"
            >

                <DialogTitle>
                    {decisionDialog?.type === "approve"
                        ? "Approve Timetable"
                        : "Reject Timetable"}
                    {" - "}
                    {decisionDialog?.className}
                </DialogTitle>

                <DialogContent>

                    <TextField
                        autoFocus
                        fullWidth
                        multiline
                        minRows={3}
                        label="Remarks (optional)"
                        value={remarks}
                        onChange={(e) => setRemarks(e.target.value)}
                        sx={{ mt: 1 }}
                    />

                </DialogContent>

                <DialogActions>

                    <Button onClick={closeDecision} disabled={actionLoading}>
                        Cancel
                    </Button>

                    <Button
                        variant="contained"
                        color={decisionDialog?.type === "approve" ? "success" : "error"}
                        onClick={confirmDecision}
                        disabled={actionLoading}
                    >
                        {actionLoading
                            ? "Saving..."
                            : decisionDialog?.type === "approve"
                                ? "Confirm Approval"
                                : "Confirm Rejection"}
                    </Button>

                </DialogActions>

            </Dialog>

        </Box>

    );

};

export default HodTimetableApprovals;