import { useEffect, useState } from "react";

import {
    Button,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import RegulationTable from "../../components/regulation/RegulationTable";
import RegulationFormDialog from "../../components/regulation/RegulationFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {
    getRegulations,
    createRegulation,
    updateRegulation,
    activateRegulation,
    deactivateRegulation,
    deleteRegulation
} from "../../services/regulationService";

const RegulationManagement = () => {

    const [regulations, setRegulations] = useState([]);

    const [open, setOpen] = useState(false);

    const [selectedRegulation, setSelectedRegulation] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);

    const [regulationToDelete, setRegulationToDelete] = useState(null);

    const loadData = async () => {

        const data = await getRegulations();

        setRegulations(data);

    };

    useEffect(() => {

        loadData();

    }, []);

    const handleSave = async (regulation) => {

        if (selectedRegulation) {

            await updateRegulation(
                selectedRegulation.id,
                regulation
            );

        } else {

            await createRegulation(regulation);

        }

        await loadData();

        setSelectedRegulation(null);

        setOpen(false);

    };

    const handleAdd = () => {

        setSelectedRegulation(null);

        setOpen(true);

    };

    const handleEdit = (regulation) => {

        setSelectedRegulation(regulation);

        setOpen(true);

    };

    const handleDelete = (regulation) => {

        setRegulationToDelete(regulation);

        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteRegulation(regulationToDelete.id);

        setDeleteOpen(false);

        setRegulationToDelete(null);

        await loadData();

    };

    const handleActivate = async (regulation) => {

        await activateRegulation(regulation.id);

        await loadData();

    };

    const handleDeactivate = async (regulation) => {

        await deactivateRegulation(regulation.id);

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

                    Regulation Management

                </Typography>

                <Button

                    variant="contained"

                    startIcon={<AddIcon />}

                    onClick={handleAdd}

                >

                    Add Regulation

                </Button>

            </Stack>

            <RegulationTable

                regulations={regulations}

                onEdit={handleEdit}

                onDelete={handleDelete}

                onActivate={handleActivate}

                onDeactivate={handleDeactivate}

            />

            <RegulationFormDialog

                open={open}

                onClose={() => {

                    setOpen(false);

                    setSelectedRegulation(null);

                }}

                regulation={selectedRegulation}

                onSubmit={handleSave}

            />

            <DeleteConfirmDialog

                open={deleteOpen}

                title="Delete Regulation"

                message={`Delete ${regulationToDelete?.code}?`}

                onCancel={() => {

                    setDeleteOpen(false);

                    setRegulationToDelete(null);

                }}

                onConfirm={confirmDelete}

            />

        </>
        </DashboardLayout>

    );

};

export default RegulationManagement;