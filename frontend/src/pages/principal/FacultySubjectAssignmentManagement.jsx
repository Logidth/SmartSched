import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Button,
    CircularProgress,
    Alert
} from "@mui/material";
import {
    getAcademicYears
} from "../../services/academicYearService";
import AddIcon from "@mui/icons-material/Add";
import DashboardLayout from "../../components/layout/DashboardLayout";
import FacultySubjectAssignmentTable from "../../components/facultyassignment/FacultySubjectAssignmentTable";
import FacultySubjectAssignmentFormDialog from "../../components/facultyassignment/FacultySubjectAssignmentFormDialog";

import {
    getFacultySubjectAssignments,
    createFacultySubjectAssignment,
    deleteFacultySubjectAssignment
} from "../../services/facultyAssignmentService";

import {
    getFaculties
} from "../../services/facultyService";

import {
    getStudentClasses
} from "../../services/studentClassService";

const FacultySubjectAssignmentManagement = () => {

    const [assignments, setAssignments] = useState([]);
    const [faculties, setFaculties] = useState([]);
    const [studentClasses, setStudentClasses] = useState([]);
    // NOTE: We no longer load ALL curriculum subjects here.
    // Instead, they're loaded on-demand in the dialog when a
    // student class is selected.
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
    }, []);

    /**
     * Load initial data (assignments, faculties, student classes, academic years).
     * 
     * Note: We no longer load ALL curriculum subjects here. Instead,
     * they're fetched on-demand in the dialog when the user selects
     * a student class. This is more efficient and ensures only valid
     * subjects for that class are shown.
     */
    const loadData = async () => {

        try {

            setLoading(true);

            const [
                assignmentRes,
                facultyRes,
                studentClassRes,
                academicYearRes
            ] = await Promise.all([
                getFacultySubjectAssignments(),
                getFaculties(),
                getStudentClasses(),
                getAcademicYears()
            ]);

            setAssignments(assignmentRes.data);

            setFaculties(facultyRes);

            setStudentClasses(studentClassRes);

            setAcademicYears(academicYearRes);

        } catch (err) {

            console.error(err);

            setError("Unable to load data.");

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

        }
        catch (err) {

            console.error(err);

            alert("Unable to assign faculty: " + (err.response?.data?.message || err.message));

        }

    };

    const handleDelete = async (row) => {

        try {

            await deleteFacultySubjectAssignment(row.id);

            loadData();

        } catch (err) {

            console.error(err);

            alert("Unable to delete assignment: " + (err.response?.data?.message || err.message));

        }

    };

    if (loading) return <CircularProgress />;

    if (error) return <Alert severity="error">{error}</Alert>;

    return (
<DashboardLayout>
        <Box p={3}>

            <Box
                display="flex"
                justifyContent="space-between"
                mb={3}
            >

                <Typography variant="h4">
                    Faculty Subject Assignment
                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
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
                // Pass empty array - dialog will fetch filtered subjects on demand
                curriculumSubjects={[]}
                academicYears={academicYears}
            />

        </Box>
        </DashboardLayout>

    );

};

export default FacultySubjectAssignmentManagement;
