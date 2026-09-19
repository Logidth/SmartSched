import { useEffect } from "react";
import { useForm, Controller } from "react-hook-form";

import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Grid,
    TextField,
    MenuItem,
    FormControlLabel,
    Checkbox
} from "@mui/material";

const subjectTypes = [
    "THEORY",
    "LAB",
    "THEORY_LAB"
];

const subjectCategories = [
    "CORE",
    "PROFESSIONAL_ELECTIVE",
    "OPEN_ELECTIVE",
    "MANDATORY"
];

const SubjectFormDialog = ({
    open,
    onClose,
    onSubmit,
    subject,
    regulations
}) => {

    const {

        control,
        register,
        handleSubmit,
        watch,
        reset,
        setValue

    } = useForm({

        defaultValues: {

            subjectCode: "",
            subjectName: "",
            regulationId: "",
            credits: 3,
            theoryHours: 0,
            labHours: 0,
            totalHours: 0,
            hoursPerWeek: 0,
            subjectType: "THEORY",
            subjectCategory: "CORE",
            requiresLabRoom: false

        }

    });

    const theory = Number(watch("theoryHours")) || 0;
    const lab = Number(watch("labHours")) || 0;

    useEffect(() => {

        setValue("totalHours", theory + lab);

    }, [theory, lab, setValue]);

    useEffect(() => {

        if (subject) {

            reset({

                subjectCode: subject.subjectCode,
                subjectName: subject.subjectName,
                regulationId: subject.regulationId,
                credits: subject.credits,
                theoryHours: subject.theoryHours,
                labHours: subject.labHours,
                totalHours: subject.totalHours,
                hoursPerWeek: subject.hoursPerWeek
                    ?? subject.totalHours,
                subjectType: subject.subjectType,
                subjectCategory: subject.subjectCategory,
                requiresLabRoom: subject.requiresLabRoom

            });

        } else {

            reset({

                subjectCode: "",
                subjectName: "",
                regulationId: "",
                credits: 3,
                theoryHours: 0,
                labHours: 0,
                totalHours: 0,
                hoursPerWeek: 0,
                subjectType: "THEORY",
                subjectCategory: "CORE",
                requiresLabRoom: false

            });

        }

    }, [subject, reset]);

    const submit = (data) => {

        data.regulationId = Number(data.regulationId);
        data.credits = Number(data.credits);
        data.theoryHours = Number(data.theoryHours);
        data.labHours = Number(data.labHours);
        data.totalHours = Number(data.totalHours);
        data.hoursPerWeek = Number(data.hoursPerWeek);

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

                {subject ? "Edit Subject" : "Add Subject"}

            </DialogTitle>

            <form onSubmit={handleSubmit(submit)}>

                <DialogContent>

                    <Grid container spacing={2}>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                fullWidth
                                label="Subject Code"
                                {...register("subjectCode", {
                                    required: true
                                })}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                fullWidth
                                label="Subject Name"
                                {...register("subjectName", {
                                    required: true
                                })}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                select
                                fullWidth
                                label="Regulation"
                                {...register("regulationId", {
                                    required: true
                                })}
                            >

                                {regulations.map(reg => (

                                    <MenuItem
                                        key={reg.id}
                                        value={reg.id}
                                    >

                                        {reg.code}

                                    </MenuItem>

                                ))}

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                fullWidth
                                type="number"
                                label="Credits"
                                {...register("credits")}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 4 }}>

                            <TextField
                                fullWidth
                                type="number"
                                label="Theory Hours"
                                {...register("theoryHours")}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 4 }}>

                            <TextField
                                fullWidth
                                type="number"
                                label="Lab Hours"
                                {...register("labHours")}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 4 }}>

                            <TextField
                                fullWidth
                                disabled
                                label="Total Hours"
                                helperText="Theory + Lab (semester load)"
                                {...register("totalHours")}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 4 }}>

                            <TextField
                                fullWidth
                                type="number"
                                label="Hours / Week"
                                helperText="Used by the scheduler to allocate weekly periods"
                                inputProps={{ min: 1 }}
                                {...register("hoursPerWeek", {
                                    required: true,
                                    min: 1
                                })}
                            />

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                select
                                fullWidth
                                label="Subject Type"
                                {...register("subjectType")}
                            >

                                {subjectTypes.map(type => (

                                    <MenuItem
                                        key={type}
                                        value={type}
                                    >

                                        {type.replaceAll("_", " ")}

                                    </MenuItem>

                                ))}

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>

                            <TextField
                                select
                                fullWidth
                                label="Subject Category"
                                {...register("subjectCategory")}
                            >

                                {subjectCategories.map(category => (

                                    <MenuItem
                                        key={category}
                                        value={category}
                                    >

                                        {category.replaceAll("_", " ")}

                                    </MenuItem>

                                ))}

                            </TextField>

                        </Grid>

                        <Grid size={{ xs: 12 }}>

                            <Controller

                                control={control}

                                name="requiresLabRoom"

                                render={({ field }) => (

                                    <FormControlLabel

                                        control={

                                            <Checkbox

                                                checked={field.value}

                                                onChange={(e) =>
                                                    field.onChange(e.target.checked)
                                                }

                                            />

                                        }

                                        label="Requires Lab Room"

                                    />

                                )}

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

export default SubjectFormDialog;