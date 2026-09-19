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

import CurriculumTable from "../../components/curriculum/CurriculumTable";
import CurriculumFormDialog from "../../components/curriculum/CurriculumFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";

import {
    createCurriculum,
    updateCurriculum,
    activateCurriculum,
    deactivateCurriculum,
    deleteCurriculum
} from "../../services/curriculumService";

import { getRegulations } from "../../services/regulationService";
import { getAcademicYears } from "../../services/academicYearService";

import useAuth from "../../hooks/useAuth";
import api from "../../api/api";

const AdminCurriculumManagement = () => {

    const { user } = useAuth();

    const [curriculums, setCurriculums] = useState([]);
    const [regulations, setRegulations] = useState([]);
    const [academicYears, setAcademicYears] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [open, setOpen] = useState(false);
    const [selectedCurriculum, setSelectedCurriculum] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);
    const [curriculumToDelete, setCurriculumToDelete] = useState(null);

    // CurriculumFormDialog expects a `branches` array for its Branch
    // dropdown - same one-item-array trick used everywhere else in
    // the admin section, so a curriculum can only ever be created
    // for the admin's own department.
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

            const [curriculumResponse, regulationList, academicYearList] =
                await Promise.all([

                    // CurriculumController exposes GET
                    // /api/v1/curriculums/branch/{branchId} - it
                    // returns the list directly (no ApiResponse
                    // wrapper), same as curriculumService.getCurriculums().
                    api.get(`/v1/curriculums/branch/${user.branchId}`),

                    getRegulations(),

                    getAcademicYears()

                ]);

            setCurriculums(
                Array.isArray(curriculumResponse.data)
                    ? curriculumResponse.data
                    : []
            );

            setRegulations(regulationList);
            setAcademicYears(academicYearList);

        } catch (err) {

            console.error("Unable to load curriculums:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load curriculums."
            );

        } finally {

            setLoading(false);

        }

    };

    useEffect(() => {

        loadData();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.branchId]);

    const handleSave = async (curriculum) => {

        // Force the branch regardless of what the dialog submits -
        // same guard used in AdminFacultyManagement / AdminClassManagement.
        const payload = {
            ...curriculum,
            branchId: user.branchId
        };

        if (selectedCurriculum) {

            await updateCurriculum(selectedCurriculum.id, payload);

        } else {

            await createCurriculum(payload);

        }

        await loadData();

        setSelectedCurriculum(null);
        setOpen(false);

    };

    const handleEdit = (curriculum) => {

        setSelectedCurriculum(curriculum);
        setOpen(true);

    };

    const handleAdd = () => {

        setSelectedCurriculum(null);
        setOpen(true);

    };

    const handleActivate = async (curriculum) => {

        await activateCurriculum(curriculum.id);
        await loadData();

    };

    const handleDeactivate = async (curriculum) => {

        await deactivateCurriculum(curriculum.id);
        await loadData();

    };

    const handleDelete = (curriculum) => {

        setCurriculumToDelete(curriculum);
        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteCurriculum(curriculumToDelete.id);

        setDeleteOpen(false);
        setCurriculumToDelete(null);

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
                        Curriculum Management
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
                    Add Curriculum
                </Button>

            </Stack>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            {!error && curriculums.length === 0 && (

                <Alert severity="info" sx={{ mb: 2 }}>
                    No curriculum set up for your department yet. Add one
                    below, then use the book icon on its row to attach
                    subjects to it — that's what makes subjects available
                    for Faculty Subject Assignment and Student Classes.
                </Alert>

            )}

            <CurriculumTable
                curriculums={curriculums}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onActivate={handleActivate}
                onDeactivate={handleDeactivate}
            />

            <CurriculumFormDialog
                open={open}
                onClose={() => {

                    setOpen(false);
                    setSelectedCurriculum(null);

                }}
                onSubmit={handleSave}
                curriculum={selectedCurriculum}
                branches={ownBranch}
                regulations={regulations}
                academicYears={academicYears}
            />

            <DeleteConfirmDialog
                open={deleteOpen}
                title="Delete Curriculum"
                message={`Delete curriculum for ${curriculumToDelete?.branch}?`}
                onCancel={() => {

                    setDeleteOpen(false);
                    setCurriculumToDelete(null);

                }}
                onConfirm={confirmDelete}
            />

        </>

    );

};

export default AdminCurriculumManagement;