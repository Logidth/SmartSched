import { useEffect } from "react";

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

const years = [1, 2, 3, 4];

const semesters = [1, 2, 3, 4, 5, 6, 7, 8];

const StudentClassFormDialog = ({
    open,
    onClose,
    onSubmit,
    studentClass,
    branches,
    regulations,
    academicYears
}) => {

    const {

        register,

        handleSubmit,

        reset

    } = useForm();

    useEffect(() => {

        if (studentClass) {

            reset({

                branchId: studentClass.branchId,

                regulationId: studentClass.regulationId,

                academicYearId: studentClass.academicYearId,

                year: studentClass.year,

                semester: studentClass.semester,

                section: studentClass.section,

                strength: studentClass.strength

            });

        } else {

            reset({

                branchId: "",

                regulationId: "",

                academicYearId: "",

                year: 1,

                semester: 1,

                section: "",

                strength: 60

            });

        }

    }, [studentClass, reset]);

    const submit = (data) => {

        data.branchId = Number(data.branchId);

        data.regulationId = Number(data.regulationId);

        data.academicYearId = Number(data.academicYearId);

        data.year = Number(data.year);

        data.semester = Number(data.semester);

        data.strength = Number(data.strength);

        onSubmit(data);

    };

    return (

        <Dialog
            open={open}
            onClose={onClose}
            fullWidth
            maxWidth="md"
        >

            <DialogTitle>

                {

                    studentClass

                        ? "Edit Student Class"

                        : "Add Student Class"

                }

            </DialogTitle>

            <form onSubmit={handleSubmit(submit)}>

                <DialogContent>

                    <Grid container spacing={2}>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField

                                select

                                fullWidth

                                label="Branch"

                                {...register("branchId")}

                            >

                                {

                                    branches.map(branch => (

                                        <MenuItem

                                            key={branch.id}

                                            value={branch.id}

                                        >

                                            {branch.name}

                                        </MenuItem>

                                    ))

                                }

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField

                                select

                                fullWidth

                                label="Regulation"

                                {...register("regulationId")}

                            >

                                {

                                    regulations.map(regulation => (

                                        <MenuItem

                                            key={regulation.id}

                                            value={regulation.id}

                                        >

                                            {regulation.code}

                                        </MenuItem>

                                    ))

                                }

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField

                                select

                                fullWidth

                                label="Academic Year"

                                {...register("academicYearId")}

                            >

                                {

                                    academicYears.map(year => (

                                        <MenuItem

                                            key={year.id}

                                            value={year.id}

                                        >

                                            {year.name}

                                        </MenuItem>

                                    ))

                                }

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField

                                select

                                fullWidth

                                label="Year"

                                {...register("year")}

                            >

                                {

                                    years.map(item => (

                                        <MenuItem

                                            key={item}

                                            value={item}

                                        >

                                            Year {item}

                                        </MenuItem>

                                    ))

                                }

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField

                                select

                                fullWidth

                                label="Semester"

                                {...register("semester")}

                            >

                                {

                                    semesters.map(item => (

                                        <MenuItem

                                            key={item}

                                            value={item}

                                        >

                                            Semester {item}

                                        </MenuItem>

                                    ))

                                }

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField

                                fullWidth

                                label="Section"

                                {...register("section")}

                            />

                        </Grid>

                        <Grid size={{ xs: 12 }}>

                            <TextField

                                fullWidth

                                type="number"

                                label="Student Strength"

                                {...register("strength")}

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

                        Save

                    </Button>

                </DialogActions>

            </form>

        </Dialog>

    );

};

export default StudentClassFormDialog;