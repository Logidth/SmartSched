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

const CurriculumSubjectFormDialog = ({
    open,
    onClose,
    onSubmit,
    curriculumSubject,
    curriculums,
    subjects
}) => {

    const [form, setForm] = useState({

        curriculumId: "",

        subjectId: "",

        year: "",

        semester: "",

        displayOrder: "",

        hoursPerWeek: ""

    });

    useEffect(() => {

        if (curriculumSubject) {

            setForm({

                curriculumId: curriculumSubject.curriculumId,

                subjectId: curriculumSubject.subjectId,

                year: curriculumSubject.year,

                semester: curriculumSubject.semester,

                displayOrder: curriculumSubject.displayOrder,

                hoursPerWeek: curriculumSubject.hoursPerWeek ?? ""

            });

        } else {

            setForm({

                curriculumId: "",

                subjectId: "",

                year: "",

                semester: "",

                displayOrder: "",

                hoursPerWeek: ""

            });

        }

    }, [curriculumSubject, open]);

    // Returns valid semesters for selected year
    const getSemesters = (year) => {

        switch (Number(year)) {

            case 1:
                return [1, 2];

            case 2:
                return [3, 4];

            case 3:
                return [5, 6];

            case 4:
                return [7, 8];

            default:
                return [];

        }

    };

    const handleChange = (e) => {

        const { name, value } = e.target;

        // Reset semester whenever year changes
        if (name === "year") {

            setForm({

                ...form,

                year: value,

                semester: ""

            });

            return;

        }

        setForm({

            ...form,

            [name]: value

        });

    };

    const handleSubmit = () => {

        onSubmit({

            curriculumId: Number(form.curriculumId),

            subjectId: Number(form.subjectId),

            year: Number(form.year),

            semester: Number(form.semester),

            displayOrder: Number(form.displayOrder),

            hoursPerWeek: form.hoursPerWeek === ""
                ? null
                : Number(form.hoursPerWeek)

        });

    };

    return (

        <Dialog
            open={open}
            onClose={onClose}
            fullWidth
            maxWidth="sm"
        >

            <DialogTitle>

                {curriculumSubject
                    ? "Edit Curriculum Subject"
                    : "Add Curriculum Subject"}

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
                            label="Curriculum"
                            name="curriculumId"
                            value={form.curriculumId}
                            onChange={handleChange}
                        >

                            {

                                curriculums.map(curriculum => (

                                    <MenuItem
                                        key={curriculum.id}
                                        value={curriculum.id}
                                    >

                                        {curriculum.branch} - {curriculum.regulation} - {curriculum.academicYear}

                                    </MenuItem>

                                ))

                            }

                        </TextField>

                    </Grid>

                    <Grid size={12}>

                        <TextField
                            select
                            fullWidth
                            label="Subject"
                            name="subjectId"
                            value={form.subjectId}
                            onChange={handleChange}
                        >

                            {

                                subjects.map(subject => (

                                    <MenuItem
                                        key={subject.id}
                                        value={subject.id}
                                    >

                                        {subject.subjectCode} - {subject.subjectName}

                                    </MenuItem>

                                ))

                            }

                        </TextField>

                    </Grid>

                    <Grid size={6}>

                        <TextField
                            select
                            fullWidth
                            label="Year"
                            name="year"
                            value={form.year}
                            onChange={handleChange}
                        >

                            {

                                [1, 2, 3, 4].map(year => (

                                    <MenuItem
                                        key={year}
                                        value={year}
                                    >

                                        Year {year}

                                    </MenuItem>

                                ))

                            }

                        </TextField>

                    </Grid>

                    <Grid size={6}>

                        <TextField
                            select
                            fullWidth
                            label="Semester"
                            name="semester"
                            value={form.semester}
                            onChange={handleChange}
                            disabled={!form.year}
                        >

                            {

                                getSemesters(form.year).map(semester => (

                                    <MenuItem
                                        key={semester}
                                        value={semester}
                                    >

                                        Semester {semester}

                                    </MenuItem>

                                ))

                            }

                        </TextField>

                    </Grid>

                    <Grid size={12}>

                        <TextField
                            fullWidth
                            type="number"
                            label="Display Order"
                            name="displayOrder"
                            value={form.displayOrder}
                            onChange={handleChange}
                            inputProps={{
                                min: 1
                            }}
                        />

                    </Grid>

                    <Grid size={12}>

                        <TextField
                            fullWidth
                            type="number"
                            label="Hours / Week Override (optional)"
                            name="hoursPerWeek"
                            value={form.hoursPerWeek}
                            onChange={handleChange}
                            helperText="Leave blank to use the subject's default hours/week for scheduling"
                            inputProps={{
                                min: 1
                            }}
                        />

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

                    {curriculumSubject ? "Update" : "Save"}

                </Button>

            </DialogActions>

        </Dialog>

    );

};

export default CurriculumSubjectFormDialog;