import { useEffect } from "react";

import { useForm } from "react-hook-form";

import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Grid,
    TextField
} from "@mui/material";

const RegulationFormDialog = ({
    open,
    onClose,
    regulation,
    onSubmit
}) => {

    const {

        register,
        handleSubmit,
        reset

    } = useForm({

        defaultValues:{

            code:"",
            description:""

        }

    });

    useEffect(()=>{

        if(regulation){

            reset({

                code: regulation.code,
                description: regulation.description

            });

        }else{

            reset({

                code:"",
                description:""

            });

        }

    },[regulation,reset]);

    return(

        <Dialog

            open={open}

            onClose={onClose}

            fullWidth

            maxWidth="sm"

        >

            <DialogTitle>

                {

                    regulation

                    ?

                    "Edit Regulation"

                    :

                    "Add Regulation"

                }

            </DialogTitle>

            <form onSubmit={handleSubmit(onSubmit)}>

                <DialogContent>

                    <Grid container spacing={2}>

                        <Grid size={{xs:12}}>

                            <TextField

                                fullWidth

                                label="Regulation Code"

                                {...register("code",{

                                    required:true

                                })}

                            />

                        </Grid>

                        <Grid size={{xs:12}}>

                            <TextField

                                fullWidth

                                multiline

                                rows={4}

                                label="Description"

                                {...register("description",{

                                    required:true

                                })}

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

export default RegulationFormDialog;