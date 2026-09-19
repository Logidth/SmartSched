import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Button,
    CircularProgress,
    Alert
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import { getFaculties } from "../../services/facultyService";
import FacultySubjectAssignmentTable from "../../components/facultyassignment/FacultySubjectAssignmentTable";
import FacultySubjectAssignmentFormDialog from "../../components/facultyassignment/FacultySubjectAssignmentFormDialog";

import {
    getFacultySubjectAssignments,
    createFacultySubjectAssignment,
    deleteFacultySubjectAssignment
} from "../../services/facultyAssignmentService";

import { getAcademicYears } from "../../services/academicYearService";

import useAuth from "../../hooks/useAuth";
import api from "../../api/api";

const AdminFacultySubjectAssignmentManagement = () => {

    const { user } = useAuth();

    const [assignments, setAssignments] = useState([]);
    const [faculties, setFaculties] = useState([]);
    const [studentClasses, setStudentClasses] = useState([]);
    const [academicYears, setAcademicYears] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [openDialog, setOpenDialog] = useState(false);

    const [formData, setFormData] = useState({
        facultyId: "",
        studentClassId: "",
        curriculumSubjectId: ""
    });

    useEffect(() => {

        loadData();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.branchId]);

    /**
     * Faculty-subject assignments aren't stored with a branchId of
     * their own - they just carry a `branch` display name and a
     * `studentClassId`. To scope this reliably (not by string-
     * matching a name), we first fetch this department's own
     * faculty and student classes via their branch-scoped endpoints,
     * then keep only the assignments whose studentClassId belongs
     * to one of those classes.
     */
    const loadData = async () => {

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

            const [
    assignmentRes,
    allFaculties,
    classRes,
    academicYearRes
] = await Promise.all([

    getFacultySubjectAssignments(),

    getFaculties(),

    api.get(`/student-classes/branch/${user.branchId}`),

    getAcademicYears()

]);

const departmentFaculties = allFaculties ?? [];
            const departmentClasses = classRes.data?.data ?? [];

            const departmentClassIds = new Set(
                departmentClasses.map((cls) => cls.id)
            );

            const allAssignments = assignmentRes.data ?? [];

            const departmentAssignments = allAssignments.filter(
                (assignment) =>
                    departmentClassIds.has(assignment.studentClassId)
            );

            setAssignments(departmentAssignments);
            setFaculties(departmentFaculties);
            setStudentClasses(departmentClasses);
            setAcademicYears(academicYearRes);

        } catch (err) {

            console.error("Unable to load faculty assignments:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load faculty assignments."
            );

        } finally {

            setLoading(false);

        }

    };

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

    };

    const handleSave = async () => {

        // Guard against a stale/forged studentClassId that doesn't
        // belong to this department - the dropdown already only
        // shows this department's classes, but this keeps the
        // check server-side-equivalent on the client too.
        const belongsToDepartment = studentClasses.some(
            (cls) => String(cls.id) === String(formData.studentClassId)
        );

        if (!belongsToDepartment) {

            alert("Please select a class from your own department.");

            return;

        }

        try {

            await createFacultySubjectAssignment({

                facultyId: Number(formData.facultyId),

                studentClassId: Number(formData.studentClassId),

                curriculumSubjectId: Number(formData.curriculumSubjectId)

            });

            setOpenDialog(false);

            setFormData({
                facultyId: "",
                studentClassId: "",
                curriculumSubjectId: ""
            });

            loadData();

        } catch (err) {

            console.error(err);

            alert(
                "Unable to assign faculty: " +
                (err.response?.data?.message || err.message)
            );

        }

    };

    const handleDelete = async (row) => {

        const confirmed = window.confirm(
            `Remove ${row.facultyName} from ${row.subjectName}?`
        );

        if (!confirmed) return;

        try {

            await deleteFacultySubjectAssignment(row.id);

            loadData();

        } catch (err) {

            console.error(err);

            alert(
                "Unable to delete assignment: " +
                (err.response?.data?.message || err.message)
            );

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

            <Box
                display="flex"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Box>

                    <Typography variant="h4">
                        Faculty Subject Assignment
                    </Typography>

                    {user?.branchName && (

                        <Typography variant="body2" color="text.secondary">
                            {user.branchName} Department
                        </Typography>

                    )}

                </Box>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    disabled={
                        !user?.branchId ||
                        faculties.length === 0 ||
                        studentClasses.length === 0
                    }
                    onClick={() => {

                        setFormData({
                            facultyId: "",
                            studentClassId: "",
                            curriculumSubjectId: ""
                        });

                        setOpenDialog(true);

                    }}
                >
                    Assign Faculty
                </Button>

            </Box>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            {!error && faculties.length === 0 && (

                <Alert severity="info" sx={{ mb: 2 }}>
                    Add faculty to your department first, under
                    Faculty Management, before assigning them to subjects.
                </Alert>

            )}

            {!error && faculties.length > 0 && studentClasses.length === 0 && (

                <Alert severity="info" sx={{ mb: 2 }}>
                    Create a student class first, under Student Classes,
                    before assigning faculty to its subjects.
                </Alert>

            )}

            <FacultySubjectAssignmentTable
                assignments={assignments}
                onDelete={handleDelete}
            />

            <FacultySubjectAssignmentFormDialog
                open={openDialog}
                onClose={() => setOpenDialog(false)}
                onSave={handleSave}
                formData={formData}
                handleChange={handleChange}
                faculties={faculties}
                studentClasses={studentClasses}
                curriculumSubjects={[]}
                academicYears={academicYears}
            />

        </Box>

    );

};

export default AdminFacultySubjectAssignmentManagement;