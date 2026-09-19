import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Paper,
    Chip,
    Button,
    Alert,
    CircularProgress,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Table,
    TableHead,
    TableBody,
    TableRow,
    TableCell
} from "@mui/material";

import { DataGrid } from "@mui/x-data-grid";

import EventNoteIcon from "@mui/icons-material/EventNote";

import useAuth from "../../hooks/useAuth";

import {
    getDepartmentFaculty,
    getFacultyLeaves
} from "../../services/hodService";

const LEAVE_TYPE_LABELS = {
    CASUAL: "Casual",
    SICK: "Sick",
    ON_DUTY: "On Duty",
    VACATION: "Vacation",
    EMERGENCY: "Emergency"
};

const LEAVE_STATUS_COLORS = {
    PENDING: "warning",
    APPROVED: "success",
    REJECTED: "error"
};

const HodFaculty = () => {

    const { user } = useAuth();

    const [faculties, setFaculties] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [leaveDialog, setLeaveDialog] = useState(null);
    // { facultyName }
    const [leaveHistory, setLeaveHistory] = useState([]);
    const [leaveLoading, setLeaveLoading] = useState(false);
    const [leaveError, setLeaveError] = useState("");

    useEffect(() => {

        loadFaculty();

    }, []);

    const loadFaculty = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getDepartmentFaculty();

            setFaculties(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load department faculty:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load department faculty."
            );

        } finally {

            setLoading(false);

        }
    };

    const openLeaveHistory = async (row) => {

        setLeaveDialog({
            facultyId: row.id,
            facultyName: row.name
        });

        setLeaveHistory([]);
        setLeaveError("");
        setLeaveLoading(true);

        try {

            const data = await getFacultyLeaves(row.id);

            setLeaveHistory(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load leave history:", err);

            setLeaveError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load leave history."
            );

        } finally {

            setLeaveLoading(false);

        }
    };

    const closeLeaveHistory = () => {

        setLeaveDialog(null);
        setLeaveHistory([]);
        setLeaveError("");

    };

    const columns = [

        {
            field: "employeeId",
            headerName: "Employee ID",
            width: 130
        },

        {
            field: "name",
            headerName: "Name",
            flex: 1,
            minWidth: 160
        },

        {
            field: "email",
            headerName: "Email",
            flex: 1,
            minWidth: 200
        },

        {
            field: "phone",
            headerName: "Phone",
            width: 140
        },

        {
            field: "designation",
            headerName: "Designation",
            width: 170
        },

        {
            field: "maxWeeklyHours",
            headerName: "Hours",
            width: 90
        },

        {
            field: "status",
            headerName: "Status",
            width: 110,

            renderCell: (params) => (

                <Chip
                    label={params.value}
                    color={
                        params.value === "ACTIVE"
                            ? "success"
                            : "error"
                    }
                    size="small"
                />

            )

        },

        {
            field: "actions",
            headerName: "Actions",
            width: 170,
            sortable: false,

            renderCell: (params) => (

                <Button
                    variant="outlined"
                    size="small"
                    startIcon={<EventNoteIcon />}
                    onClick={() => openLeaveHistory(params.row)}
                >
                    Leave History
                </Button>

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
                Department Faculty
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

            {faculties.length === 0 && !error ? (

                <Paper sx={{ p: 4, textAlign: "center" }}>

                    <Typography color="text.secondary">
                        No faculty are assigned to your department yet.
                    </Typography>

                </Paper>

            ) : (

                <Paper sx={{ p: 2 }}>

                    <DataGrid
                        rows={faculties}
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
                    />

                </Paper>

            )}

            <Dialog
                open={Boolean(leaveDialog)}
                onClose={closeLeaveHistory}
                fullWidth
                maxWidth="md"
            >

                <DialogTitle>
                    Leave History - {leaveDialog?.facultyName}
                </DialogTitle>

                <DialogContent>

                    {leaveLoading ? (

                        <Box display="flex" justifyContent="center" my={3}>
                            <CircularProgress size={28} />
                        </Box>

                    ) : leaveError ? (

                        <Alert severity="error">
                            {leaveError}
                        </Alert>

                    ) : leaveHistory.length === 0 ? (

                        <Typography
                            color="text.secondary"
                            sx={{ py: 3, textAlign: "center" }}
                        >
                            No leave requests found for this faculty member.
                        </Typography>

                    ) : (

                        <Table size="small">

                            <TableHead>

                                <TableRow>
                                    <TableCell>Type</TableCell>
                                    <TableCell>From</TableCell>
                                    <TableCell>To</TableCell>
                                    <TableCell>Reason</TableCell>
                                    <TableCell>Status</TableCell>
                                    <TableCell>Remarks</TableCell>
                                </TableRow>

                            </TableHead>

                            <TableBody>

                                {leaveHistory.map((leave) => (

                                    <TableRow key={leave.id}>

                                        <TableCell>
                                            {LEAVE_TYPE_LABELS[leave.leaveType] || leave.leaveType}
                                        </TableCell>

                                        <TableCell>{leave.fromDate}</TableCell>

                                        <TableCell>{leave.toDate}</TableCell>

                                        <TableCell>{leave.reason || "-"}</TableCell>

                                        <TableCell>

                                            <Chip
                                                label={leave.status}
                                                color={LEAVE_STATUS_COLORS[leave.status] || "default"}
                                                size="small"
                                            />

                                        </TableCell>

                                        <TableCell>{leave.approvalRemarks || "-"}</TableCell>

                                    </TableRow>

                                ))}

                            </TableBody>

                        </Table>

                    )}

                </DialogContent>

                <DialogActions>

                    <Button onClick={closeLeaveHistory}>
                        Close
                    </Button>

                </DialogActions>

            </Dialog>

        </Box>

    );

};

export default HodFaculty;