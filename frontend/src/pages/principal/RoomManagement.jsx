import { useEffect, useState } from "react";

import {
    Alert,
    Button,
    CircularProgress,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import RoomTable from "../../components/room/RoomTable";
import RoomFormDialog from "../../components/room/RoomFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";
import DashboardLayout from "../../components/layout/DashboardLayout";

import {
    getRooms,
    createRoom,
    updateRoom,
    activateRoom,
    deactivateRoom,
    deleteRoom
} from "../../services/roomService";

import {
    getBranches
} from "../../services/branchService";

import api from "../../api/api";

const RoomManagement = () => {

    const [rooms, setRooms] = useState([]);

    const [blocks, setBlocks] = useState([]);

    const [branches, setBranches] = useState([]);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");

    const [open, setOpen] = useState(false);

    const [selectedRoom, setSelectedRoom] =
        useState(null);

    const [deleteOpen, setDeleteOpen] =
        useState(false);

    const [roomToDelete, setRoomToDelete] =
        useState(null);

    const loadData = async () => {

        try {

            setLoading(true);
            setError("");

            const [
                roomData,
                blockResponse,
                branchData
            ] = await Promise.all([

                getRooms(),

                api.get("/blocks"),

                getBranches()

            ]);

            setRooms(
                Array.isArray(roomData)
                    ? roomData
                    : []
            );

            setBlocks(
                blockResponse.data?.data ?? []
            );

            setBranches(
                Array.isArray(branchData)
                    ? branchData
                    : []
            );

        } catch (err) {

            console.error(
                "Unable to load room data:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to load room management data."
            );

        } finally {

            setLoading(false);

        }

    };

    useEffect(() => {

        loadData();

    }, []);

    const handleAdd = () => {

        setSelectedRoom(null);

        setOpen(true);

    };

    const handleEdit = (room) => {

        setSelectedRoom(room);

        setOpen(true);

    };

    const handleSave = async (roomData) => {

        try {

            setError("");

            if (selectedRoom) {

                await updateRoom(
                    selectedRoom.id,
                    roomData
                );

            } else {

                await createRoom(roomData);

            }

            await loadData();

            setOpen(false);

            setSelectedRoom(null);

        } catch (err) {

            console.error(
                "Unable to save room:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to save room."
            );

        }

    };

    const handleActivate = async (room) => {

        try {

            setError("");

            await activateRoom(room.id);

            await loadData();

        } catch (err) {

            console.error(
                "Unable to activate room:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to activate room."
            );

        }

    };

    const handleDeactivate = async (room) => {

        try {

            setError("");

            await deactivateRoom(room.id);

            await loadData();

        } catch (err) {

            console.error(
                "Unable to deactivate room:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to deactivate room."
            );

        }

    };

    const handleDelete = (room) => {

        setRoomToDelete(room);

        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        if (!roomToDelete) {
            return;
        }

        try {

            setError("");

            await deleteRoom(
                roomToDelete.id
            );

            setDeleteOpen(false);

            setRoomToDelete(null);

            await loadData();

        } catch (err) {

            console.error(
                "Unable to delete room:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to delete room."
            );

        }

    };

    if (loading) {

        return (

            <DashboardLayout>

                <Stack
                    alignItems="center"
                    justifyContent="center"
                    sx={{
                        minHeight: 300
                    }}
                >

                    <CircularProgress />

                </Stack>

            </DashboardLayout>

        );

    }

    return (

        <DashboardLayout>

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
                    Room Management
                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleAdd}
                >
                    Add Room
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

            <RoomTable

                rooms={rooms}

                onEdit={handleEdit}

                onDelete={handleDelete}

                onActivate={handleActivate}

                onDeactivate={handleDeactivate}

            />

            <RoomFormDialog

                open={open}

                onClose={() => {

                    setOpen(false);

                    setSelectedRoom(null);

                }}

                onSubmit={handleSave}

                room={selectedRoom}

                blocks={blocks}

                branches={branches}

            />

            <DeleteConfirmDialog

                open={deleteOpen}

                title="Delete Room"

                message={
                    `Delete room ${roomToDelete?.roomNumber}?`
                }

                onCancel={() => {

                    setDeleteOpen(false);

                    setRoomToDelete(null);

                }}

                onConfirm={confirmDelete}

            />

        </DashboardLayout>

    );

};

export default RoomManagement;