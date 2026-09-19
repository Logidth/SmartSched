import { useEffect } from "react";
import { useForm } from "react-hook-form";

import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField
} from "@mui/material";

const AcademicYearFormDialog = ({
    open,
    onClose,
    academicYear,
    onSubmit
}) => {

    const {

        register,
        handleSubmit,
        reset

    } = useForm({

        defaultValues:{

            name:""

        }

    });

    useEffect(()=>{

        if(academicYear){

            reset({

                name: academicYear.name

            });

        }else{

            reset({

                name:""

            });

        }

    },[academicYear,reset]);

    return(

        <Dialog

            open={open}

            onClose={onClose}

            fullWidth

            maxWidth="sm"

        >

            <DialogTitle>

                {

                    academicYear

                    ?

                    "Edit Academic Year"

                    :

                    "Add Academic Year"

                }

            </DialogTitle>

            <form onSubmit={handleSubmit(onSubmit)}>

                <DialogContent>

                    <TextField

                        fullWidth

                        label="Academic Year"

                        placeholder="2025-2026"

                        helperText="Format: YYYY-YYYY"

                        {...register("name",{

                            required:true,

                            pattern:/^\d{4}-\d{4}$/

                        })}

                    />

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

export default AcademicYearFormDialog;