import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Button,
    CircularProgress
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import toast from "react-hot-toast";

import UserTable from "../../components/principal/UserTable";
import HodDialog from "../../components/principal/HodDialog";

import {
    getHods,
    enableUser,
    disableUser,
    resetPassword
} from "../../services/principalService";

export default function HodManagement(){

    const [users,setUsers]=useState([]);

    const [loading,setLoading]=useState(true);

    const [open,setOpen]=useState(false);

    const loadData=async()=>{

        try{

            setLoading(true);

            const res=await getHods();

            setUsers(res.data);

        }
        catch{

            toast.error("Unable to load HODs");

        }
        finally{

            setLoading(false);

        }

    };

    useEffect(()=>{

        loadData();

    },[]);

    const handleEnable=async(id)=>{

        await enableUser(id);

        toast.success("Enabled");

        loadData();

    };

    const handleDisable=async(id)=>{

        await disableUser(id);

        toast.success("Disabled");

        loadData();

    };

    const handleReset=async(id)=>{

        const password=prompt("New Password");

        if(!password) return;

        await resetPassword(id,{
            newPassword:password
        });

        toast.success("Password Reset");

    };

    return(

        <Box p={3}>

            <Box
                display="flex"
                justifyContent="space-between"
                alignItems="center"
            >

                <Typography variant="h4">

                    HOD Management

                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon/>}
                    onClick={()=>setOpen(true)}
                >

                    Create HOD

                </Button>

            </Box>

            {

                loading ?

                <Box
                    mt={5}
                    textAlign="center"
                >

                    <CircularProgress/>

                </Box>

                :

                <UserTable

                    users={users}

                    onEnable={handleEnable}

                    onDisable={handleDisable}

                    onReset={handleReset}

                />

            }

            <HodDialog

                open={open}

                onClose={()=>{
                    setOpen(false);
                    loadData();
                }}

            />

        </Box>

    );

}