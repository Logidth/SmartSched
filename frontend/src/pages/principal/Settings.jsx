import { useEffect, useState } from "react";
import {
  Box,
  Button,
  Typography,
  CircularProgress,
  Tabs,
  Tab,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";

import toast from "react-hot-toast";

import DashboardLayout from "../../components/layout/DashboardLayout";
import UserTable from "../../components/principal/UserTable";
import AdminDialog from "../../components/principal/AdminDialog";
import HodDialog from "../../components/principal/HodDialog";

import {
  getDepartmentAdmins,
  getHods,
  enableUser,
  disableUser,
  resetPassword,
} from "../../services/principalService";

const Settings = () => {
  const [tab, setTab] = useState(0);

  const [admins, setAdmins] = useState([]);
  const [hods, setHods] = useState([]);

  const [loading, setLoading] = useState(true);

  const [adminDialogOpen, setAdminDialogOpen] = useState(false);
  const [hodDialogOpen, setHodDialogOpen] = useState(false);

  const loadAdmins = async () => {
    try {
      /*
       * getDepartmentAdmins() (services/principalService.js) already
       * unwraps the axios response with `return response.data` - the
       * backend's GET /principal/users/department-admin returns the
       * user list directly (List<UserResponse>), not wrapped in the
       * {success, message, data, timestamp} ApiResponse envelope
       * some other endpoints use. So what comes back here already
       * IS the array; reaching for `.data` again returns undefined
       * and crashes UserTable's `.map()`, which is why this page
       * showed blank.
       */
      const response = await getDepartmentAdmins();
      setAdmins(Array.isArray(response) ? response : []);
    } catch {
      toast.error("Unable to load Department Admins");
    }
  };

  const loadHods = async () => {
    try {
      const response = await getHods();
      setHods(Array.isArray(response) ? response : []);
    } catch {
      toast.error("Unable to load HODs");
    }
  };

  const loadAll = async () => {
    setLoading(true);
    await Promise.all([loadAdmins(), loadHods()]);
    setLoading(false);
  };

  useEffect(() => {
    loadAll();
  }, []);

  const handleEnable = async (id) => {
    await enableUser(id);
    toast.success("User Enabled");
    loadAll();
  };

  const handleDisable = async (id) => {
    await disableUser(id);
    toast.success("User Disabled");
    loadAll();
  };

  const handleReset = async (id) => {
    const password = prompt("Enter New Password");

    if (!password) return;

    await resetPassword(id, { newPassword: password });

    toast.success("Password Reset");
  };

  return (
    <DashboardLayout>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Settings
      </Typography>

      <Typography variant="body2" color="text.secondary" gutterBottom>
        Manage Department Admin and HOD accounts.
      </Typography>

      <Box sx={{ borderBottom: 1, borderColor: "divider", mt: 2 }}>
        <Tabs value={tab} onChange={(e, value) => setTab(value)}>
          <Tab label="Department Admins" />
          <Tab label="HODs" />
        </Tabs>
      </Box>

      {loading ? (
        <Box mt={5} textAlign="center">
          <CircularProgress />
        </Box>
      ) : (
        <Box mt={3}>
          {tab === 0 && (
            <>
              <Box
                display="flex"
                justifyContent="flex-end"
                alignItems="center"
              >
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={() => setAdminDialogOpen(true)}
                >
                  Create Department Admin
                </Button>
              </Box>

              <UserTable
                users={admins}
                onEnable={handleEnable}
                onDisable={handleDisable}
                onReset={handleReset}
              />
            </>
          )}

          {tab === 1 && (
            <>
              <Box
                display="flex"
                justifyContent="flex-end"
                alignItems="center"
              >
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={() => setHodDialogOpen(true)}
                >
                  Create HOD
                </Button>
              </Box>

              <UserTable
                users={hods}
                onEnable={handleEnable}
                onDisable={handleDisable}
                onReset={handleReset}
              />
            </>
          )}
        </Box>
      )}

      <AdminDialog
        open={adminDialogOpen}
        onClose={() => {
          setAdminDialogOpen(false);
          loadAll();
        }}
      />

      <HodDialog
        open={hodDialogOpen}
        onClose={() => {
          setHodDialogOpen(false);
          loadAll();
        }}
      />
    </DashboardLayout>
  );
};

export default Settings;
