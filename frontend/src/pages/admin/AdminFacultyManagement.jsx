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

import FacultyTable from "../../components/faculty/FacultyTable";
import FacultyFormDialog from "../../components/faculty/FacultyFormDialog";

import toast from "react-hot-toast";

import {
    createFaculty,
    updateFaculty,
    activateFaculty,
    deactivateFaculty,
    setFacultyCredentials
} from "../../services/facultyService";

import useAuth from "../../hooks/useAuth";
import api from "../../api/api";

const AdminFacultyManagement = () => {

    const { user } = useAuth();

    const [faculties, setFaculties] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [open, setOpen] = useState(false);
    const [selectedFaculty, setSelectedFaculty] = useState(null);

    // FacultyFormDialog expects a `branches` array for its Branch
    // dropdown. Handing it only the admin's own department means the
    // dropdown effectively has one, fixed option - the admin can add
    // faculty to their department only, without touching the shared
    // dialog component.
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

            // FacultyController exposes GET /api/faculties/branch/{branchId}
            // — use it directly so the list is scoped server-side too,
            // not just filtered after the fact on the client.
            const response = await api.get(
                `/faculties/branch/${user.branchId}`
            );

            setFaculties(response.data?.data ?? []);

        } catch (err) {

            console.error("Unable to load faculty:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load faculty."
            );

        } finally {

            setLoading(false);

        }

    };

    useEffect(() => {

        loadData();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.branchId]);

    const handleSubmit = async (faculty) => {

        // Force the branch regardless of what the dialog submits -
        // an admin should never be able to create/move faculty into
        // a different department.
        const payload = {
            ...faculty,
            branchId: user.branchId
        };

        if (selectedFaculty) {

            await updateFaculty(selectedFaculty.id, payload);

        } else {

            await createFaculty(payload);

        }

        await loadData();

        setSelectedFaculty(null);
        setOpen(false);

    };

    const handleAdd = () => {

        setSelectedFaculty(null);
        setOpen(true);

    };

    const handleEdit = (faculty) => {

        setSelectedFaculty(faculty);
        setOpen(true);

    };

    const handleActivate = async (faculty) => {

        await activateFaculty(faculty.id);
        await loadData();

    };

    const handleDeactivate = async (faculty) => {

        await deactivateFaculty(faculty.id);
        await loadData();

    };

    const handleSetCredentials = async (faculty) => {

        let username = faculty.username;

        if (!username) {

            username = prompt(
                `Set a username for ${faculty.name} to log in with:`
            );

            if (!username) return;

        }

        const newPassword = prompt(
            faculty.username
                ? `Enter a new password for ${faculty.name}:`
                : `Set a temporary password for ${faculty.name}:`
        );

        if (!newPassword) return;

        try {

            await setFacultyCredentials(faculty.id, {
                username,
                newPassword
            });

            toast.success(
                faculty.username
                    ? "Password reset successfully."
                    : "Login created successfully."
            );

            await loadData();

        } catch (err) {

            toast.error(
                err.response?.data?.message ||
                err.message ||
                "Unable to update login credentials."
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

        <>

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Box>

                    <Typography variant="h4" fontWeight="bold">
                        Faculty Management
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
                    Add Faculty
                </Button>

            </Stack>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <FacultyTable
                faculties={faculties}
                onEdit={handleEdit}
                onActivate={handleActivate}
                onDeactivate={handleDeactivate}
                onSetCredentials={handleSetCredentials}
            />

            <FacultyFormDialog
                open={open}
                faculty={selectedFaculty}
                branches={ownBranch}
                onSubmit={handleSubmit}
                onClose={() => {

                    setOpen(false);
                    setSelectedFaculty(null);

                }}
            />

        </>

    );

};

export default AdminFacultyManagement;