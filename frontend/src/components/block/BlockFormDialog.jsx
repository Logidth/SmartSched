import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField
} from "@mui/material";

import { useEffect, useState } from "react";

const EMPTY_FORM = {
    name: "",
    description: ""
};

const BlockFormDialog = ({
    open,
    onClose,
    onSubmit,
    block
}) => {

    const [formData, setFormData] =
        useState(EMPTY_FORM);

    useEffect(() => {

        if (block) {

            setFormData({
                name: block.name ?? "",
                description: block.description ?? ""
            });

        } else {

            setFormData(EMPTY_FORM);

        }

    }, [block, open]);

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value
        }));

    };

    const handleSubmit = (event) => {

        event.preventDefault();

        const payload = {
            name: formData.name.trim(),
            description:
                formData.description.trim()
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
                {block
                    ? "Edit Block"
                    : "Create Block"}
            </DialogTitle>

            <form onSubmit={handleSubmit}>

                <DialogContent>

                    <TextField
                        fullWidth
                        required
                        autoFocus
                        margin="normal"
                        label="Block Name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        placeholder="A"
                        inputProps={{
                            maxLength: 100
                        }}
                    />

                    <TextField
                        fullWidth
                        multiline
                        rows={3}
                        margin="normal"
                        label="Description"
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        placeholder="Main academic block"
                        inputProps={{
                            maxLength: 250
                        }}
                    />

                </DialogContent>

                <DialogActions>

                    <Button
                        onClick={onClose}
                    >
                        Cancel
                    </Button>

                    <Button
                        type="submit"
                        variant="contained"
                    >
                        {block
                            ? "Update"
                            : "Create"}
                    </Button>

                </DialogActions>

            </form>

        </Dialog>
    );
};

export default BlockFormDialog;