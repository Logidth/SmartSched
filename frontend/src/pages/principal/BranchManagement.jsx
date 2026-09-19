import { useEffect, useState } from "react";

import {
    Button,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import BranchTable from "../../components/branch/BranchTable";
import BranchFormDialog from "../../components/branch/BranchFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {
    getBranches,
    createBranch,
    updateBranch,
    activateBranch,
    deactivateBranch,
    deleteBranch
} from "../../services/branchService";

const BranchManagement = () => {

    const [branches, setBranches] = useState([]);

    const [open, setOpen] = useState(false);

    const [selectedBranch, setSelectedBranch] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);

    const [branchToDelete, setBranchToDelete] = useState(null);

    const loadBranches = async () => {

        const data = await getBranches();

        setBranches(data);

    };

    useEffect(() => {

        loadBranches();

    }, []);

    const handleSave = async (branch) => {

        if (selectedBranch) {

            await updateBranch(selectedBranch.id, branch);

        } else {

            await createBranch(branch);

        }

        await loadBranches();

        setOpen(false);

        setSelectedBranch(null);

    };

    const handleEdit = (branch) => {

        setSelectedBranch(branch);

        setOpen(true);

    };

    const handleAdd = () => {

        setSelectedBranch(null);

        setOpen(true);

    };

    const handleActivate = async (branch) => {

        await activateBranch(branch.id);

        await loadBranches();

    };

    const handleDeactivate = async (branch) => {

        await deactivateBranch(branch.id);

        await loadBranches();

    };

    const handleDelete = (branch) => {

        setBranchToDelete(branch);

        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteBranch(branchToDelete.id);

        setDeleteOpen(false);

        setBranchToDelete(null);

        await loadBranches();

    };

    return (

        <>
 <DashboardLayout>           <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Typography
                    variant="h4"
                    fontWeight="bold"
                >
                    Branch Management
                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleAdd}
                >
                    Add Branch
                </Button>

            </Stack>

            <BranchTable

                branches={branches}

                onEdit={handleEdit}

                onDelete={handleDelete}

                onActivate={handleActivate}

                onDeactivate={handleDeactivate}

            />

            <BranchFormDialog

                open={open}

                onClose={() => {

                    setOpen(false);

                    setSelectedBranch(null);

                }}

                branch={selectedBranch}

                onSubmit={handleSave}

            />

            <DeleteConfirmDialog

                open={deleteOpen}

                title="Delete Branch"

                message={`Delete ${branchToDelete?.name}?`}

                onCancel={() => {

                    setDeleteOpen(false);

                    setBranchToDelete(null);

                }}

                onConfirm={confirmDelete}

            />
</DashboardLayout>
 
        </>

    );

};

export default BranchManagement;