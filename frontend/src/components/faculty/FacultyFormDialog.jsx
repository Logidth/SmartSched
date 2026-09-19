import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    MenuItem,
    Grid
} from "@mui/material";

import { useForm } from "react-hook-form";
import { useEffect } from "react";

const designations = [
    "PROFESSOR",
    "ASSOCIATE_PROFESSOR",
    "ASSISTANT_PROFESSOR",
    "LECTURER"
];

const FacultyFormDialog = ({
    open,
    onClose,
    branches,
    onSubmit,
    faculty
}) => {

    const {
        register,
        handleSubmit,
        reset
    } = useForm();

    useEffect(() => {

        if (faculty) {

            reset({
                name: faculty.name,
                email: faculty.email,
                phone: faculty.phone,
                branchId: faculty.branchId,
                designation: faculty.designation,
                maxWeeklyHours: faculty.maxWeeklyHours
            });

        } else {

            reset({
                username: "",
                password: "",
                name: "",
                email: "",
                phone: "",
                branchId: "",
                designation: "",
                maxWeeklyHours: 18
            });

        }

    }, [faculty, reset]);

    const submit = (data) => {

        data.branchId = Number(data.branchId);
        data.maxWeeklyHours = Number(data.maxWeeklyHours);

        onSubmit(data);
    };

    return (

        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="md"
            fullWidth
        >

            <DialogTitle>

                {faculty ? "Edit Faculty" : "Add Faculty"}

            </DialogTitle>

            <form onSubmit={handleSubmit(submit)}>

                <DialogContent>

                    <Grid container spacing={2} sx={{ mt: 1 }}>

                        {faculty && (

                            <Grid size={{ xs: 12, md: 6 }}>

                                <TextField
                                    fullWidth
                                    disabled
                                    label="Employee ID"
                                    value={faculty.employeeId}
                                />

                            </Grid>

                        )}

                        {faculty ? (

                            <Grid size={{ xs: 12, md: 6 }}>

                                <TextField
                                    fullWidth
                                    disabled
                                    label="Username"
                                    value={faculty.username || "—"}
                                />

                            </Grid>

                        ) : (

                            <>

                                <Grid size={{ xs: 12, md: 6 }}>

                                    <TextField
                                        fullWidth
                                        label="Username"
                                        helperText="Used by this faculty member to log in"
                                        {...register("username")}
                                    />

                                </Grid>

                                <Grid size={{ xs: 12, md: 6 }}>

                                    <TextField
                                        fullWidth
                                        type="password"
                                        label="Temporary Password"
                                        helperText="They'll be asked to change it on first login"
                                        {...register("password")}
                                    />

                                </Grid>

                            </>

                        )}

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                fullWidth
                                label="Faculty Name"
                                {...register("name")}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                fullWidth
                                label="Email"
                                {...register("email")}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                fullWidth
                                label="Phone"
                                {...register("phone")}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                select
                                fullWidth
                                label="Branch"
                                {...register("branchId")}
                            >

                                {branches.map(branch => (

                                    <MenuItem
                                        key={branch.id}
                                        value={branch.id}
                                    >
                                        {branch.name}
                                    </MenuItem>

                                ))}

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                select
                                fullWidth
                                label="Designation"
                                {...register("designation")}
                            >

                                {designations.map(item => (

                                    <MenuItem
                                        key={item}
                                        value={item}
                                    >
                                        {item.replaceAll("_", " ")}
                                    </MenuItem>

                                ))}

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12 }}>

                            <TextField
                                fullWidth
                                type="number"
                                label="Maximum Weekly Hours"
                                {...register("maxWeeklyHours")}
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
                        {faculty ? "Update" : "Save"}
                    </Button>

                </DialogActions>

            </form>

        </Dialog>

    );

};

export default FacultyFormDialog;