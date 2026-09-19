import { useEffect } from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Grid
} from "@mui/material";

import { useForm } from "react-hook-form";

const BranchFormDialog = ({
    open,
    onClose,
    onSubmit,
    branch
}) => {

    const {
        register,
        handleSubmit,
        reset
    } = useForm({

        defaultValues: {

            name: "",
            code: "",
            description: ""

        }

    });

    useEffect(() => {

        if (branch) {

            reset({

                name: branch.name,
                code: branch.code,
                description: branch.description

            });

        } else {

            reset({

                name: "",
                code: "",
                description: ""

            });

        }

    }, [branch, reset]);

    const submit = (data) => {

        onSubmit(data);

        reset();

    };

    return (

        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="sm"
            fullWidth
        >

            <DialogTitle>

                {branch ? "Edit Branch" : "Add Branch"}

            </DialogTitle>

            <form onSubmit={handleSubmit(submit)}>

                <DialogContent>

                    <Grid container spacing={2} sx={{ mt: 1 }}>

                        <Grid size={{ xs: 12 }}>

                            <TextField
                                fullWidth
                                label="Branch Name"
                                {...register("name", {
                                    required: true
                                })}
                            />

                        </Grid>

                        <Grid size={{ xs: 12 }}>

                            <TextField
                                fullWidth
                                label="Branch Code"
                                {...register("code", {
                                    required: true
                                })}
                            />

                        </Grid>

                        <Grid size={{ xs: 12 }}>

                            <TextField
                                fullWidth
                                multiline
                                rows={3}
                                label="Description"
                                {...register("description")}
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
                        {branch ? "Update" : "Save"}
                    </Button>

                </DialogActions>

            </form>

        </Dialog>

    );

};

export default BranchFormDialog;