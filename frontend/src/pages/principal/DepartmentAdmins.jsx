import { useEffect, useState } from "react";
import {
  Box,
  Button,
  Typography,
  CircularProgress,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import DashboardLayout from "../../components/layout/DashboardLayout";
import UserTable from "../../components/principal/UserTable";
import AdminDialog from "../../components/principal/AdminDialog";

import {
  getDepartmentAdmins,
  enableUser,
  disableUser,
  resetPassword,
} from "../../services/principalService";

import toast from "react-hot-toast";

const DepartmentAdmins = () => {

  const [users, setUsers] = useState([]);

  const [loading, setLoading] = useState(true);

  const [open, setOpen] = useState(false);

  const loadAdmins = async () => {

    try {

      setLoading(true);

      const response = await getDepartmentAdmins();

      /*
       * getDepartmentAdmins() already returns the plain array -
       * GET /principal/users/department-admin responds with
       * List<UserResponse> directly, not an ApiResponse wrapper.
       * Reaching for `.data` here returned undefined and crashed
       * UserTable's `.map()`, leaving this page blank.
       */
      setUsers(Array.isArray(response) ? response : []);

    } catch {

      toast.error("Unable to load Department Admins");

    } finally {

      setLoading(false);

    }

  };

  useEffect(() => {

    loadAdmins();

  }, []);

  const handleEnable = async (id) => {

    await enableUser(id);

    toast.success("User Enabled");

    loadAdmins();

  };

  const handleDisable = async (id) => {

    await disableUser(id);

    toast.success("User Disabled");

    loadAdmins();

  };

  const handleReset = async (id) => {

    const password = prompt("Enter New Password");

    if (!password) return;

    await resetPassword(id, {

      newPassword: password,

    });

    toast.success("Password Reset");

  };

  return (
 <DashboardLayout>
    <Box p={3}>

      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
      >

        <Typography variant="h4">

          Department Admins

        </Typography>

        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpen(true)}
        >

          Create Department Admin

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

      <AdminDialog

        open={open}

        onClose={() => {

          setOpen(false);

          loadAdmins();

        }}

      />

    </Box>

    </DashboardLayout>

  );

};

export default DepartmentAdmins;