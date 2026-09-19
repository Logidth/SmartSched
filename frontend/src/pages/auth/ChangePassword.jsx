import { useState } from "react";
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
} from "@mui/material";
import toast from "react-hot-toast";
import authApiClient from "../../api/authApiClient";
import { useAuth } from "../../context/AuthContext";

const ChangePassword = () => {
    const { user } = useAuth();

  const [oldPassword, setOldPassword] = useState("");

  const [newPassword, setNewPassword] = useState("");

  const [confirmPassword, setConfirmPassword] = useState("");

  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {

    e.preventDefault();

    if (newPassword !== confirmPassword) {

      toast.error("Passwords do not match");

      return;

    }

    try {

      setLoading(true);

      await authApiClient.post("/auth/change-password",{
    username: user.username,

    oldPassword,

    newPassword

});

      toast.success("Password changed successfully");

      window.location.href="/login";

    } catch {

      toast.error("Unable to change password");

    } finally {

      setLoading(false);

    }

  };

  return (

    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      minHeight="100vh"
      bgcolor="#f4f6f8"
    >

      <Paper sx={{width:450,p:4,borderRadius:3}}>

        <Typography variant="h5" mb={3} align="center">
          Change Password
        </Typography>

        <form onSubmit={handleSubmit}>

          <TextField
            fullWidth
            label="Current Password"
            type="password"
            margin="normal"
            value={oldPassword}
            onChange={(e)=>setOldPassword(e.target.value)}
          />

          <TextField
            fullWidth
            label="New Password"
            type="password"
            margin="normal"
            value={newPassword}
            onChange={(e)=>setNewPassword(e.target.value)}
          />

          <TextField
            fullWidth
            label="Confirm Password"
            type="password"
            margin="normal"
            value={confirmPassword}
            onChange={(e)=>setConfirmPassword(e.target.value)}
          />

          <Button
            fullWidth
            variant="contained"
            sx={{mt:3}}
            type="submit"
            disabled={loading}
          >
            {loading ? "Updating..." : "Update Password"}
          </Button>

        </form>

      </Paper>

    </Box>

  );

};

export default ChangePassword;