import { useEffect, useState } from "react";

import {
    Button,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import DashboardLayout from "../../components/layout/DashboardLayout";
import FacultyTable from "../../components/faculty/FacultyTable";
import FacultyFormDialog from "../../components/faculty/FacultyFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";

import toast from "react-hot-toast";

import {
    getFaculties,
    createFaculty,
    updateFaculty,
    activateFaculty,
    deactivateFaculty,
    setFacultyCredentials
} from "../../services/facultyService";

import {
    getBranches
} from "../../services/branchService";

const FacultyManagement = () => {

    const [faculties, setFaculties] = useState([]);
    const [branches, setBranches] = useState([]);

    const [open, setOpen] = useState(false);
    const [deleteOpen, setDeleteOpen] = useState(false);

    const [selectedFaculty, setSelectedFaculty] = useState(null);
    const [facultyToDelete, setFacultyToDelete] = useState(null);

    const loadData = async () => {

        const facultyData = await getFaculties();
        const branchData = await getBranches();

        setFaculties(facultyData);
        setBranches(branchData);

    };

    useEffect(() => {
        loadData();
    }, []);

    const handleSubmit = async (faculty) => {

        if (selectedFaculty) {

            await updateFaculty(selectedFaculty.id, faculty);

        } else {

            await createFaculty(faculty);

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

    const handleDelete = (faculty) => {

        setFacultyToDelete(faculty);
        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteFaculty(facultyToDelete.id);

        setDeleteOpen(false);
        setFacultyToDelete(null);

        await loadData();

    };

    const handleDeactivate = async (faculty) => {

    await deactivateFaculty(faculty.id);

    await loadData();

};

const handleActivate = async (faculty) => {

    await activateFaculty(faculty.id);

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

    return (
 <DashboardLayout>
        <>

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Typography
                    variant="h4"
                    fontWeight="bold"
                >
                    Faculty Management
                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleAdd}
                >
                    Add Faculty
                </Button>

            </Stack>

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
                branches={branches}
                onSubmit={handleSubmit}
                onClose={() => {

                    setOpen(false);
                    setSelectedFaculty(null);

                }}
            />

            <DeleteConfirmDialog
                open={deleteOpen}
                title="Delete Faculty"
                message={`Delete ${facultyToDelete?.name}?`}
                onCancel={() => {

                    setDeleteOpen(false);
                    setFacultyToDelete(null);

                }}
                onConfirm={confirmDelete}
            />

        </>
        </DashboardLayout>

    );

};

export default FacultyManagement;