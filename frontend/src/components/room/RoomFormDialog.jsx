import { useEffect, useState } from "react";

import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    MenuItem,
    Grid,
    FormControlLabel,
    Switch
} from "@mui/material";

const ROOM_TYPES = [
    {
        value: "CLASSROOM",
        label: "Classroom"
    },
    {
        value: "LAB",
        label: "Lab"
    },
    {
        value: "SEMINAR_HALL",
        label: "Seminar Hall"
    },
    {
        value: "PROJECT_LAB",
        label: "Project Lab"
    }
];

const emptyForm = {
    roomNumber: "",
    blockId: "",
    floor: 0,
    capacity: 10,
    branchId: "",
    smartRoom: false,
    roomType: "CLASSROOM"
};

const RoomFormDialog = ({
    open,
    onClose,
    onSubmit,
    room,
    blocks,
    branches
}) => {

    const [formData, setFormData] = useState(emptyForm);

    useEffect(() => {

        if (room) {

            setFormData({
                roomNumber: room.roomNumber ?? "",
                blockId: room.blockId ?? "",
                floor: room.floor ?? 0,
                capacity: room.capacity ?? 10,
                branchId: room.branchId ?? "",
                smartRoom: room.smartRoom ?? false,
                roomType: room.roomType ?? "CLASSROOM"
            });

        } else {

            setFormData(emptyForm);

        }

    }, [room, open]);

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value
        }));

    };

    const handleSwitchChange = (event) => {

        setFormData((previous) => ({
            ...previous,
            smartRoom: event.target.checked
        }));

    };

    const handleSubmit = (event) => {

        event.preventDefault();

        const payload = {
            roomNumber: formData.roomNumber.trim(),
            blockId: Number(formData.blockId),
            floor: Number(formData.floor),
            capacity: Number(formData.capacity),
            branchId:
                formData.branchId === ""
                    ? null
                    : Number(formData.branchId),
            smartRoom: Boolean(formData.smartRoom),
            roomType: formData.roomType
        };

        onSubmit(payload);

    };

    return (

        <Dialog
            open={open}
            onClose={onClose}
            fullWidth
            maxWidth="sm"
        >

            <DialogTitle>
                {room ? "Edit Room" : "Add Room"}
            </DialogTitle>

            <form onSubmit={handleSubmit}>

                <DialogContent>

                    <Grid
                        container
                        spacing={2}
                        sx={{ mt: 0.5 }}
                    >

                        <Grid size={{ xs: 12 }}>

                            <TextField
                                fullWidth
                                required
                                label="Room Number"
                                name="roomNumber"
                                value={formData.roomNumber}
                                onChange={handleChange}
                                placeholder="A101"
                            />

                        </Grid>

                        <Grid size={{ xs: 12, sm: 6 }}>

                            <TextField
                                select
                                fullWidth
                                required
                                label="Block"
                                name="blockId"
                                value={formData.blockId}
                                onChange={handleChange}
                            >

                                {blocks.map((block) => (

                                    <MenuItem
                                        key={block.id}
                                        value={block.id}
                                    >
                                        {block.name}
                                    </MenuItem>

                                ))}

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, sm: 6 }}>

                            <TextField
                                fullWidth
                                required
                                type="number"
                                label="Floor"
                                name="floor"
                                value={formData.floor}
                                onChange={handleChange}
                                slotProps={{
                                    htmlInput: {
                                        min: 0
                                    }
                                }}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, sm: 6 }}>

                            <TextField
                                fullWidth
                                required
                                type="number"
                                label="Capacity"
                                name="capacity"
                                value={formData.capacity}
                                onChange={handleChange}
                                slotProps={{
                                    htmlInput: {
                                        min: 10
                                    }
                                }}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, sm: 6 }}>

                            <TextField
                                select
                                fullWidth
                                required
                                label="Room Type"
                                name="roomType"
                                value={formData.roomType}
                                onChange={handleChange}
                            >

                                {ROOM_TYPES.map((type) => (

                                    <MenuItem
                                        key={type.value}
                                        value={type.value}
                                    >
                                        {type.label}
                                    </MenuItem>

                                ))}

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12 }}>

                            <TextField
                                select
                                fullWidth
                                label="Branch"
                                name="branchId"
                                value={formData.branchId}
                                onChange={handleChange}
                                helperText="Leave empty for a shared room"
                            >

                                <MenuItem value="">
                                    Shared / No Branch
                                </MenuItem>

                                {branches.map((branch) => (

                                    <MenuItem
                                        key={branch.id}
                                        value={branch.id}
                                    >
                                        {branch.code} - {branch.name}
                                    </MenuItem>

                                ))}

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12 }}>

                            <FormControlLabel
                                control={
                                    <Switch
                                        checked={formData.smartRoom}
                                        onChange={handleSwitchChange}
                                    />
                                }
                                label="Smart Room"
                            />

                        </Grid>

                    </Grid>

                </DialogContent>

                <DialogActions>

                    <Button onClick={onClose}>
                        Cancel
                    </Button>

                    <Button
                        type="submit"
                        variant="contained"
                    >
                        {room ? "Update" : "Save"}
                    </Button>

                </DialogActions>

            </form>

        </Dialog>

    );

};

export default RoomFormDialog;