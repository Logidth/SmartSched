import { useEffect, useState } from "react";

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

import { DataGrid } from "@mui/x-data-grid";

import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";

import useAuth from "../../hooks/useAuth";

import {
    getPendingLeaves,
    approveLeave,
    rejectLeave
} from "../../services/hodService";

const LEAVE_TYPE_LABELS = {
    CASUAL: "Casual",
    SICK: "Sick",
    ON_DUTY: "On Duty",
    VACATION: "Vacation",
    EMERGENCY: "Emergency"
};

const HodLeaveApprovals = () => {

    const { user } = useAuth();

    const [leaves, setLeaves] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [actionLoading, setActionLoading] = useState(false);

    const [decisionDialog, setDecisionDialog] = useState(null);
    // { type: "approve" | "reject", leaveRequestId, faculty }
    const [remarks, setRemarks] = useState("");

    useEffect(() => {

        loadPending();

    }, []);

    const loadPending = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getPendingLeaves();

            setLeaves(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load pending leave requests:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load pending leave requests."
            );

        } finally {

            setLoading(false);

        }
    };

    const openDecision = (type, row) => {

        setRemarks("");

        setDecisionDialog({
            type,
            leaveRequestId: row.id,
            faculty: row.faculty
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
                leaveRequestId: decisionDialog.leaveRequestId,
                remarks
            };

            if (decisionDialog.type === "approve") {

                await approveLeave(payload);

                setMessage(
                    `Leave request for ${decisionDialog.faculty} approved.`
                );

            } else {

                await rejectLeave(payload);

                setMessage(
                    `Leave request for ${decisionDialog.faculty} rejected.`
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

    const columns = [

        {
            field: "faculty",
            headerName: "Faculty",
            flex: 1,
            minWidth: 160
        },

        {
            field: "leaveType",
            headerName: "Leave Type",
            width: 130,
            renderCell: (params) => (
                LEAVE_TYPE_LABELS[params.value] || params.value
            )
        },

        {
            field: "fromDate",
            headerName: "From",
            width: 120
        },

        {
            field: "toDate",
            headerName: "To",
            width: 120
        },

        {
            field: "reason",
            headerName: "Reason",
            flex: 1.4,
            minWidth: 200
        },

        {
            field: "status",
            headerName: "Status",
            width: 110,
            renderCell: (params) => (

                <Chip
                    label={params.value}
                    color="warning"
                    size="small"
                />

            )
        },

        {
            field: "actions",
            headerName: "Actions",
            width: 200,
            sortable: false,

            renderCell: (params) => (

                <Stack direction="row" spacing={1}>

                    <Button
                        variant="contained"
                        color="success"
                        size="small"
                        startIcon={<CheckCircleIcon />}
                        onClick={() => openDecision("approve", params.row)}
                    >
                        Approve
                    </Button>

                    <Button
                        variant="outlined"
                        color="error"
                        size="small"
                        startIcon={<CancelIcon />}
                        onClick={() => openDecision("reject", params.row)}
                    >
                        Reject
                    </Button>

                </Stack>

            )
        }

    ];

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
                Leave Requests
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

            {leaves.length === 0 && !error ? (

                <Paper sx={{ p: 4, textAlign: "center" }}>

                    <Typography color="text.secondary">
                        No leave requests are waiting for your approval right now.
                    </Typography>

                </Paper>

            ) : (

                <Paper sx={{ p: 2 }}>

                    <DataGrid
                        rows={leaves}
                        columns={columns}
                        pageSizeOptions={[5, 10, 20]}
                        initialState={{
                            pagination: {
                                paginationModel: {
                                    pageSize: 10
                                }
                            }
                        }}
                        disableRowSelectionOnClick
                        autoHeight
                        getRowHeight={() => "auto"}
                        sx={{
                            "& .MuiDataGrid-cell": {
                                py: 1.2
                            }
                        }}
                    />

                </Paper>

            )}

            <Dialog
                open={Boolean(decisionDialog)}
                onClose={closeDecision}
                fullWidth
                maxWidth="sm"
            >

                <DialogTitle>
                    {decisionDialog?.type === "approve"
                        ? "Approve Leave Request"
                        : "Reject Leave Request"}
                    {" - "}
                    {decisionDialog?.faculty}
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

export default HodLeaveApprovals;