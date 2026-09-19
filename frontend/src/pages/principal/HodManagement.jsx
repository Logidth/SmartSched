import { useEffect, useState } from "react";
import {
  Box,
  Button,
  Typography,
  CircularProgress,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";

import UserTable from "../../components/principal/UserTable";
import HodDialog from "../../components/principal/HodDialog";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {
  getHods,
  enableUser,
  disableUser,
  resetPassword,
} from "../../services/principalService";

import toast from "react-hot-toast";

const HodManagement = () => {

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);

  const loadHods = async () => {
    try {
      setLoading(true);

      const response = await getHods();

      // GET /principal/users/hod responds with List<UserResponse>
      // directly, not an ApiResponse wrapper (same as department-admin).
      setUsers(Array.isArray(response) ? response : []);
    } catch {
      toast.error("Unable to load HODs");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHods();
  }, []);

  const handleEnable = async (id) => {
    await enableUser(id);
    toast.success("User Enabled");
    loadHods();
  };

  const handleDisable = async (id) => {
    await disableUser(id);
    toast.success("User Disabled");
    loadHods();
  };

  const handleReset = async (id) => {
    const password = prompt("Enter New Password");
    if (!password) return;

    await resetPassword(id, { newPassword: password });
    toast.success("Password Reset");
  };

  return (
    <DashboardLayout>
    <Box p={3}>
      <Box display="flex" justifyContent="space-between" alignItems="center">
        <Typography variant="h4">HODs</Typography>

        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpen(true)}
        >
          Create HOD
        </Button>
      </Box>

      {loading ? (
        <Box mt={5} textAlign="center">
          <CircularProgress />
        </Box>
      ) : (
        <UserTable
          users={users}
          onEnable={handleEnable}
          onDisable={handleDisable}
          onReset={handleReset}
        />
      )}

      <HodDialog
        open={open}
        onClose={() => {
          setOpen(false);
          loadHods();
        }}
      />
    </Box>
    </DashboardLayout>
  );
};

export default HodManagement;