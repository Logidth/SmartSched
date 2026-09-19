import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Paper,
    Alert,
    CircularProgress
} from "@mui/material";

import { DataGrid } from "@mui/x-data-grid";

import { getMyAssignments } from "../../services/facultySelfService";

const FacultyMySubjects = () => {

    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        loadAssignments();

    }, []);

    const loadAssignments = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getMyAssignments();

            setAssignments(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load assigned subjects:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load your assigned subjects."
            );

        } finally {

            setLoading(false);

        }
    };

    const columns = [

        {
            field: "subjectCode",
            headerName: "Code",
            width: 110
        },

        {
            field: "subjectName",
            headerName: "Subject",
            flex: 1,
            minWidth: 200
        },

        {
            field: "branch",
            headerName: "Branch",
            width: 130
        },

        {
            field: "year",
            headerName: "Year",
            width: 90
        },

        {
            field: "semester",
            headerName: "Semester",
            width: 110
        },

        {
            field: "section",
            headerName: "Section",
            width: 100
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
                My Subjects
            </Typography>

            <Typography variant="body2" color="text.secondary" mb={3}>
                Subjects and classes currently allocated to you
            </Typography>

            {error && (

                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>

            )}

            {assignments.length === 0 && !error ? (

                <Paper sx={{ p: 4, textAlign: "center" }}>

                    <Typography color="text.secondary">
                        No subjects have been allocated to you yet.
                    </Typography>

                </Paper>

            ) : (

                <Paper sx={{ p: 2 }}>

                    <DataGrid
                        rows={assignments}
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

        </Box>

    );

};

export default FacultyMySubjects;
