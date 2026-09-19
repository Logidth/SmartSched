import { useEffect, useState } from "react";

import {
    Button,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import AcademicYearTable from "../../components/academicyear/AcademicYearTable";
import AcademicYearFormDialog from "../../components/academicyear/AcademicYearFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {

    getAcademicYears,
    createAcademicYear,
    updateAcademicYear,
    activateAcademicYear,
    deactivateAcademicYear,
    deleteAcademicYear

} from "../../services/academicYearService";

const AcademicYearManagement = () => {

    const [academicYears, setAcademicYears] = useState([]);

    const [open, setOpen] = useState(false);

    const [selectedAcademicYear, setSelectedAcademicYear] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);

    const [academicYearToDelete, setAcademicYearToDelete] = useState(null);

    const loadData = async () => {

        const data = await getAcademicYears();

        setAcademicYears(data);

    };

    useEffect(() => {

        loadData();

    }, []);

    const handleSave = async (academicYear) => {

        if (selectedAcademicYear) {

            await updateAcademicYear(

                selectedAcademicYear.id,

                academicYear

            );

        } else {

            await createAcademicYear(academicYear);

        }

        await loadData();

        setSelectedAcademicYear(null);

        setOpen(false);

    };

    const handleAdd = () => {

        setSelectedAcademicYear(null);

        setOpen(true);

    };

    const handleEdit = (academicYear) => {

        setSelectedAcademicYear(academicYear);

        setOpen(true);

    };

    const handleDelete = (academicYear) => {

        setAcademicYearToDelete(academicYear);

        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteAcademicYear(

            academicYearToDelete.id

        );

        setDeleteOpen(false);

        setAcademicYearToDelete(null);

        await loadData();

    };

    const handleActivate = async (academicYear) => {

        await activateAcademicYear(academicYear.id);

        await loadData();

    };

    const handleDeactivate = async (academicYear) => {

        await deactivateAcademicYear(academicYear.id);

        await loadData();

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

                    Academic Year Management

                </Typography>

                <Button

                    variant="contained"

                    startIcon={<AddIcon />}

                    onClick={handleAdd}

                >

                    Add Academic Year

                </Button>

            </Stack>

            <AcademicYearTable

                academicYears={academicYears}

                onEdit={handleEdit}

                onDelete={handleDelete}

                onActivate={handleActivate}

                onDeactivate={handleDeactivate}

            />

            <AcademicYearFormDialog

                open={open}

                onClose={() => {

                    setOpen(false);

                    setSelectedAcademicYear(null);

                }}

                academicYear={selectedAcademicYear}

                onSubmit={handleSave}

            />

            <DeleteConfirmDialog

                open={deleteOpen}

                title="Delete Academic Year"

                message={`Delete ${academicYearToDelete?.name}?`}

                onCancel={() => {

                    setDeleteOpen(false);

                    setAcademicYearToDelete(null);

                }}

                onConfirm={confirmDelete}

            />

        </>
        </DashboardLayout>

    );

};

export default AcademicYearManagement;