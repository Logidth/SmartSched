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
    TextField,
    MenuItem
} from "@mui/material";

import { DataGrid } from "@mui/x-data-grid";
import AddIcon from "@mui/icons-material/Add";

import {
    getMyLeaves,
    applyLeave
} from "../../services/Facultyselfservice";

const LEAVE_TYPES = [
    { value: "CASUAL", label: "Casual" },
    { value: "SICK", label: "Sick" },
    { value: "ON_DUTY", label: "On Duty" },
    { value: "VACATION", label: "Vacation" },
    { value: "EMERGENCY", label: "Emergency" }
];

const LEAVE_TYPE_LABELS = LEAVE_TYPES.reduce((acc, t) => {
    acc[t.value] = t.label;
    return acc;
}, {});

const STATUS_COLORS = {
    PENDING: "warning",
    APPROVED: "success",
    REJECTED: "error"
};

const emptyForm = {
    leaveType: "CASUAL",
    fromDate: "",
    toDate: "",
    reason: ""
};

const FacultyLeave = () => {

    const [leaves, setLeaves] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [dialogOpen, setDialogOpen] = useState(false);
    const [form, setForm] = useState(emptyForm);
    const [submitting, setSubmitting] = useState(false);
    const [formError, setFormError] = useState("");

    useEffect(() => {

        loadLeaves();

    }, []);

    const loadLeaves = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getMyLeaves();

            setLeaves(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load leave requests:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load your leave requests."
            );

        } finally {

            setLoading(false);

        }
    };

    const openDialog = () => {

        setForm(emptyForm);
        setFormError("");
        setDialogOpen(true);

    };

    const closeDialog = () => {

        setDialogOpen(false);

    };

    const handleChange = (field) => (e) => {

        setForm((prev) => ({
            ...prev,
            [field]: e.target.value
        }));

    };

    const handleSubmit = async () => {

        if (!form.fromDate || !form.toDate) {

            setFormError("Please select both a from date and a to date.");
            return;

        }

        if (form.toDate < form.fromDate) {

            setFormError("To date cannot be before the from date.");
            return;

        }

        try {

            setSubmitting(true);
            setFormError("");

            await applyLeave(form);

            setMessage("Leave request submitted successfully.");
            closeDialog();

            await loadLeaves();

        } catch (err) {

            console.error("Unable to submit leave request:", err);

            setFormError(
                err.response?.data?.message ||
                err.message ||
                "Unable to submit leave request."
            );

        } finally {

            setSubmitting(false);

        }
    };

    const columns = [

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
            width: 120,
            renderCell: (params) => (

                <Chip
                    label={params.value}
                    color={STATUS_COLORS[params.value] || "default"}
                    size="small"
                />

            )
        },

        {
            field: "approvalRemarks",
            headerName: "Remarks",
            flex: 1,
            minWidth: 160
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

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="flex-start"
                mb={3}
            >

                <Box>

                    <Typography variant="h4" fontWeight="bold" mb={1}>
                        My Leave Requests
                    </Typography>

                    <Typography variant="body2" color="text.secondary">
                        Apply for leave and track approval status
                    </Typography>

                </Box>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={openDialog}
                >
                    Apply for Leave
                </Button>

            </Stack>

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
                        You haven't applied for any leave yet.
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
                open={dialogOpen}
                onClose={closeDialog}
                fullWidth
                maxWidth="sm"
            >

                <DialogTitle>
                    Apply for Leave
                </DialogTitle>

                <DialogContent>

                    {formError && (

                        <Alert severity="error" sx={{ mb: 2, mt: 1 }}>
                            {formError}
                        </Alert>

                    )}

                    <TextField
                        select
                        fullWidth
                        label="Leave Type"
                        value={form.leaveType}
                        onChange={handleChange("leaveType")}
                        margin="normal"
                    >

                        {LEAVE_TYPES.map((type) => (
                            <MenuItem key={type.value} value={type.value}>
                                {type.label}
                            </MenuItem>
                        ))}

                    </TextField>

                    <TextField
                        fullWidth
                        type="date"
                        label="From Date"
                        value={form.fromDate}
                        onChange={handleChange("fromDate")}
                        margin="normal"
                        InputLabelProps={{ shrink: true }}
                    />

                    <TextField
                        fullWidth
                        type="date"
                        label="To Date"
                        value={form.toDate}
                        onChange={handleChange("toDate")}
                        margin="normal"
                        InputLabelProps={{ shrink: true }}
                    />

                    <TextField
                        fullWidth
                        multiline
                        minRows={3}
                        label="Reason"
                        value={form.reason}
                        onChange={handleChange("reason")}
                        margin="normal"
                    />

                </DialogContent>

                <DialogActions>

                    <Button onClick={closeDialog} disabled={submitting}>
                        Cancel
                    </Button>

                    <Button
                        variant="contained"
                        onClick={handleSubmit}
                        disabled={submitting}
                    >
                        {submitting ? "Submitting..." : "Submit Request"}
                    </Button>

                </DialogActions>

            </Dialog>

        </Box>

    );

};

export default FacultyLeave;
