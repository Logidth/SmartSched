import { useEffect, useState } from "react";

import {
    Button,
    Stack,
    Typography,
    Alert,
    Box,
    CircularProgress
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import StudentClassTable from "../../components/studentClass/StudentClassTable";
import StudentClassFormDialog from "../../components/studentClass/StudentClassFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";

import {
    createStudentClass,
    updateStudentClass,
    activateStudentClass,
    deactivateStudentClass,
    deleteStudentClass
} from "../../services/studentClassService";

import { getRegulations } from "../../services/regulationService";
import { getAcademicYears } from "../../services/academicYearService";

import useAuth from "../../hooks/useAuth";
import api from "../../api/api";

const AdminClassManagement = () => {

    const { user } = useAuth();

    const [studentClasses, setStudentClasses] = useState([]);
    const [regulations, setRegulations] = useState([]);
    const [academicYears, setAcademicYears] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [open, setOpen] = useState(false);
    const [selectedStudentClass, setSelectedStudentClass] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);
    const [studentClassToDelete, setStudentClassToDelete] = useState(null);

    // StudentClassFormDialog expects a `branches` array for its Branch
    // dropdown - handing it only the admin's own department locks
    // every class they create to that department, same trick used
    // in AdminFacultyManagement.
    const ownBranch = user?.branchId
        ? [{ id: user.branchId, name: user.branchName }]
        : [];

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

            const [classResponse, regulationList, academicYearList] =
                await Promise.all([

                    // StudentClassController exposes GET
                    // /api/student-classes/branch/{branchId}
                    api.get(`/student-classes/branch/${user.branchId}`),

                    getRegulations(),

                    getAcademicYears()

                ]);

            setStudentClasses(classResponse.data?.data ?? []);
            setRegulations(regulationList);
            setAcademicYears(academicYearList);

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

    useEffect(() => {

        loadData();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.branchId]);

    const handleSave = async (studentClass) => {

        // Force the branch regardless of what the dialog submits -
        // an admin should never be able to create a class in a
        // different department.
        const payload = {
            ...studentClass,
            branchId: user.branchId
        };

        if (selectedStudentClass) {

            await updateStudentClass(selectedStudentClass.id, payload);

        } else {

            await createStudentClass(payload);

        }

        await loadData();

        setSelectedStudentClass(null);
        setOpen(false);

    };

    const handleAdd = () => {

        setSelectedStudentClass(null);
        setOpen(true);

    };

    const handleEdit = (studentClass) => {

        setSelectedStudentClass(studentClass);
        setOpen(true);

    };

    const handleDelete = (studentClass) => {

        setStudentClassToDelete(studentClass);
        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteStudentClass(studentClassToDelete.id);

        setDeleteOpen(false);
        setStudentClassToDelete(null);

        await loadData();

    };

    const handleActivate = async (studentClass) => {

        await activateStudentClass(studentClass.id);
        await loadData();

    };

    const handleDeactivate = async (studentClass) => {

        await deactivateStudentClass(studentClass.id);
        await loadData();

    };

    if (loading) {

        return (

            <Box display="flex" justifyContent="center" mt={6}>
                <CircularProgress />
            </Box>

        );

    }

    return (

        <>

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Box>

                    <Typography variant="h4" fontWeight="bold">
                        Student Class Management
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
                    onClick={handleAdd}
                    disabled={!user?.branchId}
                >
                    Add Student Class
                </Button>

            </Stack>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <StudentClassTable
                studentClasses={studentClasses}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onActivate={handleActivate}
                onDeactivate={handleDeactivate}
            />

            <StudentClassFormDialog
                open={open}
                onClose={() => {

                    setOpen(false);
                    setSelectedStudentClass(null);

                }}
                onSubmit={handleSave}
                studentClass={selectedStudentClass}
                branches={ownBranch}
                regulations={regulations}
                academicYears={academicYears}
            />

            <DeleteConfirmDialog
                open={deleteOpen}
                title="Delete Student Class"
                message={`Delete ${studentClassToDelete?.branchName} ${studentClassToDelete?.section}?`}
                onCancel={() => {

                    setDeleteOpen(false);
                    setStudentClassToDelete(null);

                }}
                onConfirm={confirmDelete}
            />

        </>

    );

};

export default AdminClassManagement;