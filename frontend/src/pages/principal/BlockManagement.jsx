import { useEffect, useState } from "react";

import {
    Alert,
    Box,
    Button,
    CircularProgress,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import DashboardLayout from "../../components/layout/DashboardLayout";
import BlockTable from "../../components/block/BlockTable";
import BlockFormDialog from "../../components/block/BlockFormDialog";

import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";

import {
    getBlocks,
    createBlock,
    updateBlock,
    activateBlock,
    deactivateBlock,
    deleteBlock
} from "../../services/blockService";

const BlockManagement = () => {

    const [blocks, setBlocks] = useState([]);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const [dialogOpen, setDialogOpen] =
        useState(false);

    const [selectedBlock, setSelectedBlock] =
        useState(null);

    const [deleteDialogOpen, setDeleteDialogOpen] =
        useState(false);

    const [blockToDelete, setBlockToDelete] =
        useState(null);

    const loadBlocks = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getBlocks();

            setBlocks(
                Array.isArray(data)
                    ? data
                    : []
            );

        } catch (err) {

            console.error(
                "Failed to load blocks:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to load blocks."
            );

        } finally {

            setLoading(false);

        }
    };

    useEffect(() => {

        loadBlocks();

    }, []);

    const handleAdd = () => {

        setSelectedBlock(null);
        setDialogOpen(true);

    };

    const handleEdit = (block) => {

        setSelectedBlock(block);
        setDialogOpen(true);

    };

    const handleSave = async (payload) => {

        try {

            setError("");

            if (selectedBlock) {

                await updateBlock(
                    selectedBlock.id,
                    payload
                );

            } else {

                await createBlock(payload);

            }

            setDialogOpen(false);
            setSelectedBlock(null);

            await loadBlocks();

        } catch (err) {

            console.error(
                "Failed to save block:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to save block."
            );

        }

    };

    const handleActivate = async (block) => {

        try {

            setError("");

            await activateBlock(block.id);

            await loadBlocks();

        } catch (err) {

            console.error(
                "Failed to activate block:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to activate block."
            );

        }

    };

    const handleDeactivate = async (block) => {

        try {

            setError("");

            await deactivateBlock(block.id);

            await loadBlocks();

        } catch (err) {

            console.error(
                "Failed to deactivate block:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to deactivate block."
            );

        }

    };

    const handleDelete = (block) => {

        setBlockToDelete(block);
        setDeleteDialogOpen(true);

    };

    const handleDeleteConfirm = async () => {

        if (!blockToDelete) {
            return;
        }

        try {

            setError("");

            await deleteBlock(
                blockToDelete.id
            );

            setDeleteDialogOpen(false);
            setBlockToDelete(null);

            await loadBlocks();

        } catch (err) {

            console.error(
                "Failed to delete block:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to delete block."
            );

        }

    };

    if (loading) {

        return (
            <DashboardLayout>
            <Box
                display="flex"
                justifyContent="center"
                alignItems="center"
                minHeight={300}
            >
                <CircularProgress />
            </Box>
            </DashboardLayout>
        );

    }

    return (
<DashboardLayout>
        <Box p={3}>

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
                    Block Management
                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleAdd}
                >
                    Add Block
                </Button>

            </Stack>

            {error && (

                <Alert
                    severity="error"
                    sx={{ mb: 2 }}
                    onClose={() =>
                        setError("")
                    }
                >
                    {error}
                </Alert>

            )}

            <BlockTable
                blocks={blocks}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onActivate={handleActivate}
                onDeactivate={handleDeactivate}
            />

            <BlockFormDialog
                open={dialogOpen}
                onClose={() => {

                    setDialogOpen(false);
                    setSelectedBlock(null);

                }}
                onSubmit={handleSave}
                block={selectedBlock}
            />

            <DeleteConfirmDialog
                open={deleteDialogOpen}
                title="Delete Block"
                message={
                    `Are you sure you want to delete block "${blockToDelete?.name}"?`
                }
                onCancel={() => {

                    setDeleteDialogOpen(false);
                    setBlockToDelete(null);

                }}
                onConfirm={handleDeleteConfirm}
            />

        </Box>
        </DashboardLayout>
    );
};

export default BlockManagement;