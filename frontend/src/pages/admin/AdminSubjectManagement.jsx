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

import SubjectTable from "../../components/subject/SubjectTable";
import SubjectFormDialog from "../../components/subject/SubjectFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";

import {
    getSubjects,
    createSubject,
    updateSubject,
    deleteSubject,
    activateSubject,
    deactivateSubject
} from "../../services/subjectService";

import { getRegulations } from "../../services/regulationService";

import useAuth from "../../hooks/useAuth";
import api from "../../api/api";

const AdminSubjectManagement = () => {

    const { user } = useAuth();

    const [subjects, setSubjects] = useState([]);
    const [regulations, setRegulations] = useState([]);
    const [usingAllRegulations, setUsingAllRegulations] = useState(false);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [open, setOpen] = useState(false);
    const [selectedSubject, setSelectedSubject] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);
    const [subjectToDelete, setSubjectToDelete] = useState(null);

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

            const [allSubjects, allRegulations, branchClasses] =
                await Promise.all([

                    getSubjects(),

                    getRegulations(),

                    // StudentClassController exposes this per-branch,
                    // which is how we work out which regulation(s)
                    // this department is actually running.
                    api.get(`/student-classes/branch/${user.branchId}`)

                ]);

            const departmentClasses =
                branchClasses.data?.data ?? [];

            const regulationIdsInUse = new Set(
                departmentClasses.map((cls) => cls.regulationId)
            );

            const relevantRegulations = allRegulations.filter(
                (reg) => regulationIdsInUse.has(reg.id)
            );

            // No classes created for this department yet - fall back
            // to every regulation so the admin can still create
            // subjects ahead of time, but flag it in the UI.
            const regulationsToUse = relevantRegulations.length > 0
                ? relevantRegulations
                : allRegulations;

            setUsingAllRegulations(relevantRegulations.length === 0);

            setRegulations(regulationsToUse);

            const relevantRegulationIds = new Set(
                regulationsToUse.map((reg) => reg.id)
            );

            setSubjects(
                allSubjects.filter((subject) =>
                    relevantRegulationIds.has(subject.regulationId)
                )
            );

        } catch (err) {

            console.error("Unable to load subjects:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load subjects."
            );

        } finally {

            setLoading(false);

        }

    };

    useEffect(() => {

        loadData();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.branchId]);

    const handleSave = async (subject) => {

        if (selectedSubject) {

            await updateSubject(selectedSubject.id, subject);

        } else {

            await createSubject(subject);

        }

        await loadData();

        setSelectedSubject(null);
        setOpen(false);

    };

    const handleAdd = () => {

        setSelectedSubject(null);
        setOpen(true);

    };

    const handleEdit = (subject) => {

        setSelectedSubject(subject);
        setOpen(true);

    };

    const handleDelete = (subject) => {

        setSubjectToDelete(subject);
        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteSubject(subjectToDelete.id);

        setDeleteOpen(false);
        setSubjectToDelete(null);

        await loadData();

    };

    const handleActivate = async (subject) => {

        await activateSubject(subject.id);
        await loadData();

    };

    const handleDeactivate = async (subject) => {

        await deactivateSubject(subject.id);
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
                        Subject Management
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
                    Add Subject
                </Button>

            </Stack>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            {usingAllRegulations && !error && (

                <Alert severity="info" sx={{ mb: 2 }}>
                    No student classes exist for your department yet, so
                    every regulation is shown. Once classes are created
                    under Student Classes, this list will narrow to the
                    regulation(s) your department actually uses.
                </Alert>

            )}

            <SubjectTable
                subjects={subjects}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onActivate={handleActivate}
                onDeactivate={handleDeactivate}
            />

            <SubjectFormDialog
                open={open}
                onClose={() => {

                    setOpen(false);
                    setSelectedSubject(null);

                }}
                onSubmit={handleSave}
                subject={selectedSubject}
                regulations={regulations}
            />

            <DeleteConfirmDialog
                open={deleteOpen}
                title="Delete Subject"
                message={`Delete ${subjectToDelete?.subjectName}?`}
                onCancel={() => {

                    setDeleteOpen(false);
                    setSubjectToDelete(null);

                }}
                onConfirm={confirmDelete}
            />

        </>

    );

};

export default AdminSubjectManagement;