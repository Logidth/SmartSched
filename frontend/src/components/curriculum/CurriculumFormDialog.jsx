import { useEffect, useState } from "react";

import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Grid,
    TextField,
    MenuItem
} from "@mui/material";

const CurriculumFormDialog = ({
    open,
    onClose,
    onSubmit,
    curriculum,
    branches,
    regulations,
    academicYears
}) => {

    const [form, setForm] = useState({

        branchId: "",

        regulationId: "",

        academicYearId: ""

    });

    useEffect(() => {

        if (curriculum) {

            setForm({

                branchId: curriculum.branchId,

                regulationId: curriculum.regulationId,

                academicYearId: curriculum.academicYearId

            });

        } else {

            setForm({

                branchId: "",

                regulationId: "",

                academicYearId: ""

            });

        }

    }, [curriculum, open]);

    const handleChange = (e) => {

        setForm({

            ...form,

            [e.target.name]: e.target.value

        });

    };

    const handleSubmit = () => {

        onSubmit(form);

    };

    return (

        <Dialog
            open={open}
            onClose={onClose}
            fullWidth
            maxWidth="sm"
        >

            <DialogTitle>

                {curriculum ? "Edit Curriculum" : "Add Curriculum"}

            </DialogTitle>

            <DialogContent>

                <Grid
                    container
                    spacing={2}
                    mt={1}
                >

                    <Grid size={12}>

                        <TextField
                            select
                            fullWidth
                            label="Branch"
                            name="branchId"
                            value={form.branchId}
                            onChange={handleChange}
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

                    <Grid size={12}>

                        <TextField
                            select
                            fullWidth
                            label="Regulation"
                            name="regulationId"
                            value={form.regulationId}
                            onChange={handleChange}
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

                    <Grid size={12}>

                        <TextField
                            select
                            fullWidth
                            label="Academic Year"
                            name="academicYearId"
                            value={form.academicYearId}
                            onChange={handleChange}
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

                </Grid>

            </DialogContent>

            <DialogActions>

                <Button
                    onClick={onClose}
                >
                    Cancel
                </Button>

                <Button
                    variant="contained"
                    onClick={handleSubmit}
                >

                    {curriculum ? "Update" : "Save"}

                </Button>

            </DialogActions>

        </Dialog>

    );

};

export default CurriculumFormDialog;