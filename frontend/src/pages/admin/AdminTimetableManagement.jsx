import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Button,
    TextField,
    MenuItem,
    CircularProgress,
    Alert,
    Stack,
    Paper,
    Grid
} from "@mui/material";

import TimetableGrid from "../../components/timetable/TimetableGrid";

import {
    generateTimetable,
    getTimetable,
    deleteTimetable,
    deleteAllTimetablesForBranch,
    validateTimetable,
    submitTimetable
} from "../../services/schedulerService";

import { getAcademicYears } from "../../services/academicYearService";

import useAuth from "../../hooks/useAuth";
import api from "../../api/api";

const AdminTimetableManagement = () => {

    const { user } = useAuth();

    const [studentClasses, setStudentClasses] = useState([]);
    const [academicYears, setAcademicYears] = useState([]);
    const [timetable, setTimetable] = useState([]);

    const [studentClassId, setStudentClassId] = useState("");
    const [academicYearId, setAcademicYearId] = useState("");

    const [validation, setValidation] = useState(null);
    const [failures, setFailures] = useState([]);

    const [loading, setLoading] = useState(true);
    const [generating, setGenerating] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [deletingAll, setDeletingAll] = useState(false);
    const [validating, setValidating] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    useEffect(() => {

        loadInitialData();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.branchId]);

    const loadInitialData = async () => {

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

            const [classResponse, academicYearResponse] =
                await Promise.all([

                    // Only classes belonging to this admin's own
                    // department show up as generation targets.
                    api.get(`/student-classes/branch/${user.branchId}`),

                    getAcademicYears()

                ]);

            setStudentClasses(
                Array.isArray(classResponse.data?.data)
                    ? classResponse.data.data
                    : []
            );

            setAcademicYears(
                Array.isArray(academicYearResponse)
                    ? academicYearResponse
                    : []
            );

        } catch (err) {

            console.error("Unable to load timetable data:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load timetable data."
            );

        } finally {

            setLoading(false);

        }
    };

    const loadTimetable = async () => {

        if (!studentClassId || !academicYearId) {

            setError("Please select a Student Class first.");

            return;
        }

        try {

            setError("");
            setMessage("");
            setValidation(null);
            setFailures([]);

            const timetableResponse =
                await getTimetable(studentClassId, academicYearId);

            setTimetable(
                Array.isArray(timetableResponse)
                    ? timetableResponse
                    : []
            );

        } catch (err) {

            console.error("Unable to load timetable:", err);

            setTimetable([]);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load timetable."
            );
        }
    };

    const handleGenerate = async () => {

        if (!studentClassId || !academicYearId) {

            setError("Please select a Student Class first.");

            return;
        }

        try {

            setGenerating(true);

            setError("");
            setMessage("");
            setValidation(null);
            setFailures([]);

            const result =
                await generateTimetable(academicYearId, studentClassId);

            const entries = Array.isArray(result?.entries)
                ? result.entries
                : (Array.isArray(result) ? result : []);

            const generationFailures = Array.isArray(result?.failures)
                ? result.failures
                : [];

            setTimetable(entries);
            setFailures(generationFailures);

            if (generationFailures.length > 0) {

                setMessage(
                    `Timetable generated, but ${generationFailures.length} `
                        + `subject(s) could not be fully scheduled. `
                        + `See the details below.`
                );

            } else {

                setMessage("Timetable generated successfully.");
            }

        } catch (err) {

            console.error("Unable to generate timetable:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to generate timetable."
            );

        } finally {

            setGenerating(false);

        }
    };

    const handleValidate = async () => {

        if (!studentClassId || !academicYearId) {

            setError("Please select a Student Class first.");

            return;
        }

        try {

            setValidating(true);

            setError("");
            setMessage("");

            const validationResponse =
                await validateTimetable(studentClassId, academicYearId);

            setValidation(validationResponse || null);

            if (validationResponse?.valid) {

                setMessage("Timetable validation passed.");

            } else {

                setMessage("Timetable validation found issues.");
            }

        } catch (err) {

            console.error("Unable to validate timetable:", err);

            setValidation(null);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to validate timetable."
            );

        } finally {

            setValidating(false);

        }
    };

    const handleSubmitToHod = async () => {

        if (!studentClassId || !academicYearId) {

            setError("Please select a Student Class first.");

            return;
        }

        if (timetable.length === 0) {

            setError("Generate the timetable before submitting it.");

            return;
        }

        const confirmed = window.confirm(
            "Submit this timetable to the HOD for approval?"
        );

        if (!confirmed) return;

        try {

            setSubmitting(true);

            setError("");
            setMessage("");

            await submitTimetable({
                studentClassId,
                academicYearId,
                remarks: ""
            });

            setMessage("Timetable submitted to the HOD for approval.");

        } catch (err) {

            console.error("Unable to submit timetable:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to submit timetable."
            );

        } finally {

            setSubmitting(false);

        }
    };

    const handleDelete = async () => {

        if (!studentClassId || !academicYearId) {

            setError("Please select a Student Class first.");

            return;
        }

        const confirmed = window.confirm(
            "Are you sure you want to delete this timetable?"
        );

        if (!confirmed) return;

        try {

            setDeleting(true);

            setError("");
            setMessage("");
            setValidation(null);
            setFailures([]);

            await deleteTimetable(studentClassId, academicYearId);

            setTimetable([]);

            setMessage("Timetable deleted successfully.");

        } catch (err) {

            console.error("Unable to delete timetable:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to delete timetable."
            );

        } finally {

            setDeleting(false);

        }
    };

    const handleDeleteAll = async () => {

        if (!user?.branchId) {

            setError(
                "No department is linked to your account. " +
                "Please contact the Principal to assign a branch."
            );

            return;
        }

        const confirmed = window.confirm(
            "This will permanently delete ALL previous timetables " +
            `for ${user.branchName || "your department"}, across ` +
            "every class and academic year. This cannot be undone. " +
            "Continue?"
        );

        if (!confirmed) return;

        try {

            setDeletingAll(true);

            setError("");
            setMessage("");
            setValidation(null);
            setFailures([]);

            await deleteAllTimetablesForBranch(user.branchId);

            setTimetable([]);

            setMessage(
                "All previous timetables for the department " +
                "were deleted successfully."
            );

        } catch (err) {

            console.error(
                "Unable to delete all department timetables:",
                err
            );

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to delete all department timetables."
            );

        } finally {

            setDeletingAll(false);

        }
    };

    if (loading) {

        return (

            <Box
                display="flex"
                justifyContent="center"
                alignItems="center"
                minHeight="300px"
            >
                <CircularProgress />
            </Box>

        );
    }

    return (

        <Box>

            <Stack
                direction={{ xs: "column", sm: "row" }}
                justifyContent="space-between"
                alignItems={{ xs: "flex-start", sm: "center" }}
                mb={1}
                spacing={1}
            >

                <Typography variant="h4">
                    Timetable Generation
                </Typography>

                <Button
                    color="error"
                    variant="contained"
                    onClick={handleDeleteAll}
                    disabled={deletingAll || !user?.branchId}
                >
                    {deletingAll
                        ? "Deleting All..."
                        : "Delete All Previous Timetables"}
                </Button>

            </Stack>

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
                    severity={
                        (validation && !validation.valid) ||
                        failures.length > 0
                            ? "warning"
                            : "success"
                    }
                    sx={{ mb: 2 }}
                    onClose={() => setMessage("")}
                >
                    {message}
                </Alert>

            )}

            {failures.length > 0 && (

                <Alert
                    severity="warning"
                    sx={{ mb: 2 }}
                    onClose={() => setFailures([])}
                >

                    <Typography variant="subtitle2" mb={1}>
                        Subjects that could not be fully scheduled:
                    </Typography>

                    <Box component="ul" sx={{ m: 0, pl: 2.5 }}>

                        {failures.map((failure, index) => (

                            <li key={index}>
                                <Typography variant="body2">
                                    {failure}
                                </Typography>
                            </li>

                        ))}

                    </Box>

                </Alert>

            )}

            <Stack
                direction={{ xs: "column", md: "row" }}
                spacing={2}
                alignItems={{ xs: "stretch", md: "center" }}
                mb={3}
                flexWrap="wrap"
                useFlexGap
            >

                <TextField
                    select
                    label="Student Class"
                    value={studentClassId}
                    onChange={(e) => {

                        const selectedId = e.target.value;

                        setStudentClassId(selectedId);

                        const selectedClass = studentClasses.find(
                            (studentClass) =>
                                String(studentClass.id) === String(selectedId)
                        );

                        setAcademicYearId(
                            selectedClass?.academicYearId ?? ""
                        );

                        setTimetable([]);
                        setValidation(null);
                        setFailures([]);
                        setError("");
                        setMessage("");

                    }}
                    sx={{ minWidth: 280 }}
                >

                    <MenuItem value="">
                        Select Student Class
                    </MenuItem>

                    {studentClasses.map((studentClass) => (

                        <MenuItem key={studentClass.id} value={studentClass.id}>

                            Year {studentClass.year}
                            {" - "}
                            Sem {studentClass.semester}
                            {" - Section "}
                            {studentClass.section}

                        </MenuItem>

                    ))}

                </TextField>

                <TextField
                    select
                    label="Academic Year"
                    value={academicYearId}
                    disabled
                    helperText="Set automatically from the selected class"
                    onChange={() => {}}
                    sx={{ minWidth: 220 }}
                >

                    <MenuItem value="">
                        Select Student Class first
                    </MenuItem>

                    {academicYears.map((academicYear) => (

                        <MenuItem key={academicYear.id} value={academicYear.id}>
                            {academicYear.name}
                        </MenuItem>

                    ))}

                </TextField>

                <Button
                    variant="contained"
                    onClick={handleGenerate}
                    disabled={generating || !studentClassId || !academicYearId}
                >
                    {generating ? "Generating..." : "Generate Timetable"}
                </Button>

                <Button
                    variant="outlined"
                    onClick={loadTimetable}
                    disabled={!studentClassId || !academicYearId}
                >
                    Load
                </Button>

                <Button
                    variant="outlined"
                    color="success"
                    onClick={handleValidate}
                    disabled={
                        validating ||
                        !studentClassId ||
                        !academicYearId ||
                        timetable.length === 0
                    }
                >
                    {validating ? "Validating..." : "Validate"}
                </Button>

                <Button
                    variant="contained"
                    color="secondary"
                    onClick={handleSubmitToHod}
                    disabled={
                        submitting ||
                        !studentClassId ||
                        !academicYearId ||
                        timetable.length === 0
                    }
                >
                    {submitting ? "Submitting..." : "Submit to HOD"}
                </Button>

                <Button
                    color="error"
                    variant="outlined"
                    onClick={handleDelete}
                    disabled={
                        deleting ||
                        !studentClassId ||
                        !academicYearId ||
                        timetable.length === 0
                    }
                >
                    {deleting ? "Deleting..." : "Delete"}
                </Button>

            </Stack>

            {validation && (

                <Paper sx={{ p: 2, mb: 3 }}>

                    <Typography variant="h6" mb={2}>
                        Timetable Validation
                    </Typography>

                    <Grid container spacing={2}>

                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                            <Typography>
                                Faculty Conflicts: <strong>{validation.facultyConflicts ?? 0}</strong>
                            </Typography>
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                            <Typography>
                                Room Conflicts: <strong>{validation.roomConflicts ?? 0}</strong>
                            </Typography>
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                            <Typography>
                                Class Conflicts: <strong>{validation.classConflicts ?? 0}</strong>
                            </Typography>
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                            <Typography>
                                Missing Hours: <strong>{validation.missingHours ?? 0}</strong>
                            </Typography>
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                            <Typography>
                                Extra Hours: <strong>{validation.extraHours ?? 0}</strong>
                            </Typography>
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
                            <Typography>
                                Status: <strong>{validation.valid ? "VALID" : "INVALID"}</strong>
                            </Typography>
                        </Grid>

                    </Grid>

                </Paper>

            )}

            <TimetableGrid timetable={timetable} />

        </Box>

    );
};

export default AdminTimetableManagement;