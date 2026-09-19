import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Paper,
    Table,
    TableHead,
    TableBody,
    TableRow,
    TableCell,
    Chip,
    Alert,
    CircularProgress,
    Collapse,
    IconButton,
    Stack
} from "@mui/material";

import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";

import useAuth from "../../hooks/useAuth";

import {
    getDepartmentStudentClasses,
    getSubjectsForClass
} from "../../services/hodService";

const ClassRow = ({ studentClass }) => {

    const [expanded, setExpanded] = useState(false);
    const [subjects, setSubjects] = useState(null);
    const [loadingSubjects, setLoadingSubjects] = useState(false);
    const [subjectsError, setSubjectsError] = useState("");

    const toggle = async () => {

        const next = !expanded;
        setExpanded(next);

        // Lazy-load subjects the first time this row is opened.
        if (next && subjects === null) {

            if (!studentClass.regulationId ||
                !studentClass.branchId ||
                !studentClass.academicYearId) {

                setSubjects([]);
                setSubjectsError(
                    "This class is missing branch, regulation or " +
                    "academic year information."
                );

                return;
            }

            try {

                setLoadingSubjects(true);
                setSubjectsError("");

                const data = await getSubjectsForClass(studentClass);

                setSubjects(Array.isArray(data) ? data : []);

            } catch (err) {

                console.error("Unable to load subjects:", err);

                setSubjects([]);

                setSubjectsError(
                    err.response?.data?.message ||
                    err.message ||
                    "No curriculum has been mapped for this class yet."
                );

            } finally {

                setLoadingSubjects(false);

            }
        }
    };

    return (

        <>

            <TableRow hover>

                <TableCell>
                    <IconButton size="small" onClick={toggle}>
                        {expanded
                            ? <KeyboardArrowUpIcon />
                            : <KeyboardArrowDownIcon />}
                    </IconButton>
                </TableCell>

                <TableCell>Year {studentClass.year}</TableCell>
                <TableCell>Sem {studentClass.semester}</TableCell>
                <TableCell>Section {studentClass.section}</TableCell>
                <TableCell>{studentClass.regulation || "-"}</TableCell>
                <TableCell>{studentClass.academicYear || "-"}</TableCell>
                <TableCell>{studentClass.strength ?? "-"}</TableCell>

                <TableCell>
                    <Chip
                        size="small"
                        label={studentClass.status}
                        color={
                            studentClass.status === "ACTIVE"
                                ? "success"
                                : "default"
                        }
                    />
                </TableCell>

            </TableRow>

            <TableRow>

                <TableCell
                    style={{ paddingBottom: 0, paddingTop: 0 }}
                    colSpan={8}
                >

                    <Collapse in={expanded} timeout="auto" unmountOnExit>

                        <Box sx={{ py: 2, pl: 2 }}>

                            <Typography variant="subtitle2" mb={1}>
                                Subjects
                            </Typography>

                            {loadingSubjects && (
                                <CircularProgress size={20} />
                            )}

                            {subjectsError && (
                                <Alert severity="warning" sx={{ mb: 1 }}>
                                    {subjectsError}
                                </Alert>
                            )}

                            {!loadingSubjects &&
                                !subjectsError &&
                                subjects?.length === 0 && (

                                <Typography variant="body2" color="text.secondary">
                                    No subjects have been mapped to this
                                    class&apos;s curriculum yet.
                                </Typography>

                            )}

                            {!loadingSubjects && subjects?.length > 0 && (

                                <Table size="small">

                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Code</TableCell>
                                            <TableCell>Subject</TableCell>
                                            <TableCell>Type</TableCell>
                                            <TableCell>Credits</TableCell>
                                        </TableRow>
                                    </TableHead>

                                    <TableBody>

                                        {subjects.map((subject) => (

                                            <TableRow key={subject.id}>
                                                <TableCell>
                                                    {subject.subjectCode}
                                                </TableCell>
                                                <TableCell>
                                                    {subject.subjectName}
                                                </TableCell>
                                                <TableCell>
                                                    {subject.subjectType}
                                                </TableCell>
                                                <TableCell>
                                                    {subject.credits}
                                                </TableCell>
                                            </TableRow>

                                        ))}

                                    </TableBody>

                                </Table>

                            )}

                        </Box>

                    </Collapse>

                </TableCell>

            </TableRow>

        </>

    );
};

const HodClassesSubjects = () => {

    const { user } = useAuth();

    const [studentClasses, setStudentClasses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        loadClasses();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.branchId]);

    const loadClasses = async () => {

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

            const data = await getDepartmentStudentClasses(user.branchId);

            setStudentClasses(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load student classes:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load student classes."
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

            <Typography variant="h4" fontWeight="bold" mb={1}>
                Student Classes &amp; Subjects
            </Typography>

            {user?.branchName && (

                <Typography variant="body2" color="text.secondary" mb={3}>
                    {user.branchName} Department
                </Typography>

            )}

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            {!error && studentClasses.length === 0 && (

                <Paper sx={{ p: 4, textAlign: "center" }}>
                    <Typography color="text.secondary">
                        No student classes found for your department yet.
                    </Typography>
                </Paper>

            )}

            {studentClasses.length > 0 && (

                <Paper sx={{ p: 2 }}>

                    <Table>

                        <TableHead>

                            <TableRow>
                                <TableCell />
                                <TableCell>Year</TableCell>
                                <TableCell>Semester</TableCell>
                                <TableCell>Section</TableCell>
                                <TableCell>Regulation</TableCell>
                                <TableCell>Academic Year</TableCell>
                                <TableCell>Strength</TableCell>
                                <TableCell>Status</TableCell>
                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {studentClasses.map((studentClass) => (

                                <ClassRow
                                    key={studentClass.id}
                                    studentClass={studentClass}
                                />

                            ))}

                        </TableBody>

                    </Table>

                </Paper>

            )}

            <Stack mt={2}>
                <Typography variant="caption" color="text.secondary">
                    Click a row to expand and view the subjects mapped to
                    that class's curriculum for its year and semester.
                </Typography>
            </Stack>

        </Box>

    );

};

export default HodClassesSubjects;