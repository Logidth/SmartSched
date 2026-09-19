import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    MenuItem,
    CircularProgress,
    Alert
} from "@mui/material";
import { useEffect, useState } from "react";
import { getCurriculumSubjectsForClass } from "../../services/facultyAssignmentService";

const FacultySubjectAssignmentFormDialog = ({
    open,
    onClose,
    onSave,
    formData,
    handleChange,
    faculties = [],
    studentClasses = [],
    curriculumSubjects = []
}) => {

    const [filteredSubjects, setFilteredSubjects] = useState([]);
    const [loadingSubjects, setLoadingSubjects] = useState(false);
    const [subjectError, setSubjectError] = useState("");

    /**
     * When student class selection changes, fetch the filtered list
     * of curriculum subjects for that class.
     */
    useEffect(() => {
        if (formData.studentClassId) {
            fetchFilteredSubjects(formData.studentClassId);
        } else {
            setFilteredSubjects([]);
            setSubjectError("");
        }
    }, [formData.studentClassId]);

    const fetchFilteredSubjects = async (studentClassId) => {
        try {
            setLoadingSubjects(true);
            setSubjectError("");

            const response = await getCurriculumSubjectsForClass(studentClassId);

            // Extract the data from the ApiResponse wrapper
            setFilteredSubjects(response.data || []);

            // Clear any previously selected subject, since it might not be
            // valid for the new student class
            handleChange({
                target: {
                    name: "curriculumSubjectId",
                    value: ""
                }
            });

        } catch (err) {
            console.error("Failed to fetch curriculum subjects:", err);
            setSubjectError(
                err.response?.data?.message ||
                "Failed to load curriculum subjects"
            );
            setFilteredSubjects([]);
        } finally {
            setLoadingSubjects(false);
        }
    };

    return (
        <Dialog
            open={open}
            onClose={onClose}
            fullWidth
            maxWidth="sm"
        >

            <DialogTitle>
                Assign Faculty
            </DialogTitle>

            <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2 }}>

                {/* FACULTY */}

                <TextField
                    select
                    fullWidth
                    margin="normal"
                    label="Faculty"
                    name="facultyId"
                    value={formData.facultyId || ""}
                    onChange={handleChange}
                    disabled={false}
                >

                    {faculties.map((faculty) => (

                        <MenuItem
                            key={faculty.id}
                            value={faculty.id}
                        >
                            {faculty.employeeId} - {faculty.name}
                        </MenuItem>

                    ))}

                </TextField>


                {/* STUDENT CLASS */}

                <TextField
                    select
                    fullWidth
                    margin="normal"
                    label="Student Class"
                    name="studentClassId"
                    value={formData.studentClassId || ""}
                    onChange={handleChange}
                    disabled={false}
                >

                    {studentClasses.map((studentClass) => (

                        <MenuItem
                            key={studentClass.id}
                            value={studentClass.id}
                        >
                            {studentClass.branchName} - Year{" "}
                            {studentClass.year} - Sem{" "}
                            {studentClass.semester} - Section{" "}
                            {studentClass.section}
                        </MenuItem>

                    ))}

                </TextField>


                {/* CURRICULUM SUBJECT - with real-time filtering */}

                <TextField
                    select
                    fullWidth
                    margin="normal"
                    label="Curriculum Subject"
                    name="curriculumSubjectId"
                    value={formData.curriculumSubjectId || ""}
                    onChange={handleChange}
                    disabled={!formData.studentClassId || loadingSubjects}
                >

                    {filteredSubjects.map((subject) => (

                        <MenuItem
                            key={subject.id}
                            value={subject.id}
                        >
                            {subject.subjectCode} - {subject.subjectName}
                        </MenuItem>

                    ))}

                </TextField>

                {/* Loading indicator for subjects */}
                {loadingSubjects && (
                    <CircularProgress size={24} />
                )}

                {/* Error message if subjects failed to load */}
                {subjectError && (
                    <Alert severity="error">
                        {subjectError}
                    </Alert>
                )}

                {/* Info message if no student class selected */}
                {!formData.studentClassId && (
                    <Alert severity="info">
                        Select a student class to see available subjects
                    </Alert>
                )}

                {/* Info message if no subjects found for the class */}
                {formData.studentClassId &&
                    !loadingSubjects &&
                    filteredSubjects.length === 0 &&
                    !subjectError && (
                    <Alert severity="warning">
                        No subjects found for this class. Check the curriculum setup.
                    </Alert>
                )}

            </DialogContent>


            <DialogActions>

                <Button onClick={onClose}>
                    Cancel
                </Button>

                <Button
                    variant="contained"
                    onClick={onSave}
                    disabled={
                        !formData.facultyId ||
                        !formData.studentClassId ||
                        !formData.curriculumSubjectId ||
                        loadingSubjects
                    }
                >
                    Assign
                </Button>

            </DialogActions>

        </Dialog>
    );
};

export default FacultySubjectAssignmentFormDialog;
