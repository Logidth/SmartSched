import { useEffect, useState } from "react";
import DashboardLayout from "../../components/layout/DashboardLayout";
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
    validateTimetable
} from "../../services/schedulerService";

import {
    getStudentClasses
} from "../../services/studentClassService";

import {
    getAcademicYears
} from "../../services/academicYearService";


const TimetableManagement = () => {

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

    const [error, setError] = useState("");
    const [message, setMessage] = useState("");


    // =========================================================
    // INITIAL LOAD
    // =========================================================

    useEffect(() => {

        loadInitialData();

    }, []);


    const loadInitialData = async () => {

        try {

            setLoading(true);
            setError("");

            const [
                studentClassResponse,
                academicYearResponse
            ] = await Promise.all([

                getStudentClasses(),

                getAcademicYears()

            ]);


            setStudentClasses(
                Array.isArray(studentClassResponse)
                    ? studentClassResponse
                    : []
            );


            setAcademicYears(
                Array.isArray(academicYearResponse)
                    ? academicYearResponse
                    : []
            );

        } catch (err) {

            console.error(
                "Unable to load timetable data:",
                err
            );

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load timetable data."
            );

        } finally {

            setLoading(false);

        }
    };


    // =========================================================
    // LOAD TIMETABLE (manual only)
    // =========================================================
    // Deliberately NOT auto-triggered on class/year selection.
    // Auto-loading here was fetching whatever timetable already
    // existed in the DB for that class/year as soon as it was
    // selected, which looked like "generate" was producing a wrong
    // timetable when it was really just showing old/stale saved
    // data. The grid should stay empty until the user explicitly
    // clicks Generate Timetable.

    const loadTimetable = async () => {

        if (!studentClassId || !academicYearId) {

            setError(
                "Please select Student Class and Academic Year."
            );

            return;
        }


        try {

            setError("");
            setMessage("");
            setValidation(null);
            setFailures([]);

            const timetableResponse =
                await getTimetable(
                    studentClassId,
                    academicYearId
                );


            setTimetable(
                Array.isArray(timetableResponse)
                    ? timetableResponse
                    : []
            );

        } catch (err) {

            console.error(
                "Unable to load timetable:",
                err
            );

            setTimetable([]);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load timetable."
            );
        }
    };


    // =========================================================
    // GENERATE TIMETABLE
    // =========================================================

    const handleGenerate = async () => {

        if (!studentClassId || !academicYearId) {

            setError(
                "Please select Student Class and Academic Year."
            );

            return;
        }


        try {

            setGenerating(true);

            setError("");
            setMessage("");
            setValidation(null);
            setFailures([]);

            const result =
                await generateTimetable(
                    academicYearId,
                    studentClassId
                );

            /*
             * Backend returns { entries, failures }. A subject can
             * fail to get any periods (e.g. "Matrices" getting 0 of
             * its required hours because every eligible faculty was
             * already at their weekly cap, or the week's periods were
             * already used up by other subjects) without the overall
             * call failing - it just means part of the timetable is
             * incomplete. Show that clearly instead of only saying
             * "successfully".
             */
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

                setMessage(
                    "Timetable generated successfully."
                );
            }

        } catch (err) {

            console.error(
                "Unable to generate timetable:",
                err
            );

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to generate timetable."
            );

        } finally {

            setGenerating(false);

        }
    };


    // =========================================================
    // VALIDATE TIMETABLE
    // =========================================================

    const handleValidate = async () => {

        if (!studentClassId || !academicYearId) {

            setError(
                "Please select Student Class and Academic Year."
            );

            return;
        }


        try {

            setValidating(true);

            setError("");
            setMessage("");

            const validationResponse =
                await validateTimetable(
                    studentClassId,
                    academicYearId
                );


            setValidation(
                validationResponse || null
            );


            if (validationResponse?.valid) {

                setMessage(
                    "Timetable validation passed."
                );

            } else {

                setMessage(
                    "Timetable validation found issues."
                );
            }

        } catch (err) {

            console.error(
                "Unable to validate timetable:",
                err
            );

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


    // =========================================================
    // DELETE TIMETABLE
    // =========================================================

    const handleDelete = async () => {

        if (!studentClassId || !academicYearId) {

            setError(
                "Please select Student Class and Academic Year."
            );

            return;
        }


        const confirmed = window.confirm(
            "Are you sure you want to delete this timetable?"
        );


        if (!confirmed) {
            return;
        }


        try {

            setDeleting(true);

            setError("");
            setMessage("");
            setValidation(null);
            setFailures([]);

            await deleteTimetable(
                studentClassId,
                academicYearId
            );


            setTimetable([]);

            setMessage(
                "Timetable deleted successfully."
            );

        } catch (err) {

            console.error(
                "Unable to delete timetable:",
                err
            );

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to delete timetable."
            );

        } finally {

            setDeleting(false);

        }
    };


    // =========================================================
    // DELETE ALL PREVIOUS TIMETABLES FOR THE SELECTED DEPARTMENT
    // =========================================================

    const selectedClass = studentClasses.find(
        (studentClass) =>
            String(studentClass.id) === String(studentClassId)
    );

    const handleDeleteAll = async () => {

        if (!selectedClass?.branchId) {

            setError(
                "Select a Student Class first, so its department " +
                "can be determined."
            );

            return;
        }

        const branchLabel =
            selectedClass.branchName ||
            selectedClass.branch ||
            "this department";

        const confirmed = window.confirm(
            "This will permanently delete ALL previous timetables " +
            `for ${branchLabel}, across every class and academic ` +
            "year in that department. This cannot be undone. " +
            "Continue?"
        );

        if (!confirmed) return;

        try {

            setDeletingAll(true);

            setError("");
            setMessage("");
            setValidation(null);
            setFailures([]);

            await deleteAllTimetablesForBranch(
                selectedClass.branchId
            );

            setTimetable([]);

            setMessage(
                `All previous timetables for ${branchLabel} were ` +
                "deleted successfully."
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


    // =========================================================
    // LOADING
    // =========================================================

    if (loading) {

        return (
 <DashboardLayout>            
    <Box
                display="flex"
                justifyContent="center"
                alignItems="center"
                minHeight="300px"
            >

                <CircularProgress />

            </Box>
             </DashboardLayout>

        );
    }


    // =========================================================
    // UI
    // =========================================================

    return (
 <DashboardLayout>        <Box p={3}>

            <Typography
                variant="h4"
                mb={3}
            >
                Timetable Management
            </Typography>


            {/* ERROR */}

            {error && (

                <Alert
                    severity="error"
                    sx={{ mb: 2 }}
                    onClose={() => setError("")}
                >

                    {error}

                </Alert>

            )}


            {/* SUCCESS / INFORMATION */}

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


            {/* GENERATION FAILURES (e.g. a subject like Matrices */}
            {/* getting 0 of its required hours) */}

            {failures.length > 0 && (

                <Alert
                    severity="warning"
                    sx={{ mb: 2 }}
                    onClose={() => setFailures([])}
                >

                    <Typography
                        variant="subtitle2"
                        mb={1}
                    >
                        Subjects that could not be fully scheduled:
                    </Typography>

                    <Box
                        component="ul"
                        sx={{ m: 0, pl: 2.5 }}
                    >

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


            {/* CONTROLS */}

            <Stack
                direction={{
                    xs: "column",
                    md: "row"
                }}
                spacing={2}
                alignItems={{
                    xs: "stretch",
                    md: "center"
                }}
                mb={3}
            >


                {/* STUDENT CLASS */}

                <TextField
                    select
                    label="Student Class"
                    value={studentClassId}
                    onChange={(e) => {

                        const selectedId = e.target.value;

                        setStudentClassId(selectedId);

                        /*
                         * Every student class already belongs to
                         * exactly one academic year. Auto-select it
                         * here instead of leaving the user to guess
                         * from a separate dropdown - picking the
                         * wrong academic year is what causes an
                         * otherwise-correct faculty assignment (e.g.
                         * "Matrices" for VLSI Year 3 Sem 5) to appear
                         * missing from the generated timetable.
                         */
                        const selectedClass = studentClasses.find(
                            (studentClass) =>
                                String(studentClass.id) ===
                                String(selectedId)
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
                    sx={{
                        minWidth: 280
                    }}
                >

                    <MenuItem value="">
                        Select Student Class
                    </MenuItem>


                    {studentClasses.map(
                        (studentClass) => (

                            <MenuItem
                                key={studentClass.id}
                                value={studentClass.id}
                            >

                                {studentClass.branchName ||
                                    studentClass.branch ||
                                    "Unknown Branch"}

                                {" - "}

                                Year {studentClass.year}

                                {" - "}

                                Sem {studentClass.semester}

                                {" - Section "}

                                {studentClass.section}

                            </MenuItem>

                        )
                    )}

                </TextField>


                {/* ACADEMIC YEAR */}

                <TextField
                    select
                    label="Academic Year"
                    value={academicYearId}
                    disabled
                    helperText="Set automatically from the selected class"
                    onChange={() => {

                        /*
                         * Read-only: the academic year is fixed by
                         * the selected student class (see the
                         * Student Class handler above). Letting this
                         * be picked independently is what previously
                         * let a generated/loaded timetable silently
                         * use a different academic year than the one
                         * the class - and its faculty assignments -
                         * actually belong to.
                         */

                    }}
                    sx={{
                        minWidth: 220
                    }}
                >

                    <MenuItem value="">
                        Select Student Class first
                    </MenuItem>


                    {academicYears.map(
                        (academicYear) => (

                            <MenuItem
                                key={academicYear.id}
                                value={academicYear.id}
                            >

                                {academicYear.name}

                            </MenuItem>

                        )
                    )}

                </TextField>


                {/* GENERATE */}

                <Button
                    variant="contained"
                    onClick={handleGenerate}
                    disabled={
                        generating ||
                        !studentClassId ||
                        !academicYearId
                    }
                >

                    {generating
                        ? "Generating..."
                        : "Generate Timetable"}

                </Button>


                {/* VALIDATE */}

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

                    {validating
                        ? "Validating..."
                        : "Validate"}

                </Button>


                {/* DELETE */}

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

                    {deleting
                        ? "Deleting..."
                        : "Delete"}

                </Button>


                {/* DELETE ALL (DEPARTMENT) */}

                <Button
                    color="error"
                    variant="contained"
                    onClick={handleDeleteAll}
                    disabled={
                        deletingAll ||
                        !selectedClass?.branchId
                    }
                >

                    {deletingAll
                        ? "Deleting All..."
                        : "Delete All Previous Timetables"}

                </Button>

            </Stack>


            {/* VALIDATION RESULT */}

            {validation && (

                <Paper
                    sx={{
                        p: 2,
                        mb: 3
                    }}
                >

                    <Typography
                        variant="h6"
                        mb={2}
                    >
                        Timetable Validation
                    </Typography>


                    <Grid
                        container
                        spacing={2}
                    >

                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>

                            <Typography>
                                Faculty Conflicts:
                                {" "}
                                <strong>
                                    {validation.facultyConflicts ?? 0}
                                </strong>
                            </Typography>

                        </Grid>


                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>

                            <Typography>
                                Room Conflicts:
                                {" "}
                                <strong>
                                    {validation.roomConflicts ?? 0}
                                </strong>
                            </Typography>

                        </Grid>


                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>

                            <Typography>
                                Class Conflicts:
                                {" "}
                                <strong>
                                    {validation.classConflicts ?? 0}
                                </strong>
                            </Typography>

                        </Grid>


                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>

                            <Typography>
                                Missing Hours:
                                {" "}
                                <strong>
                                    {validation.missingHours ?? 0}
                                </strong>
                            </Typography>

                        </Grid>


                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>

                            <Typography>
                                Extra Hours:
                                {" "}
                                <strong>
                                    {validation.extraHours ?? 0}
                                </strong>
                            </Typography>

                        </Grid>


                        <Grid size={{ xs: 12, sm: 6, md: 4 }}>

                            <Typography>

                                Status:
                                {" "}

                                <strong>
                                    {validation.valid
                                        ? "VALID"
                                        : "INVALID"}
                                </strong>

                            </Typography>

                        </Grid>

                    </Grid>

                </Paper>

            )}


            {/* TIMETABLE */}

            <TimetableGrid
                timetable={timetable}
            />

        </Box>
 </DashboardLayout>
    );
};


export default TimetableManagement;