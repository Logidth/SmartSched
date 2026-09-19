import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Avatar,
  Button,
} from "@mui/material";

import LogoutIcon from "@mui/icons-material/Logout";

import { useAuth } from "../../context/AuthContext";
import NotificationBell from "./NotificationBell";

const Topbar = () => {

  const { user, logout } = useAuth();

  return (
    <AppBar
      position="static"
      elevation={1}
      color="inherit"
    >
      <Toolbar>

        <Typography
          variant="h6"
          sx={{ flexGrow: 1 }}
        >
          Welcome, {user?.username}
        </Typography>

        <NotificationBell />

        <Avatar
          sx={{
            bgcolor: "#1565C0",
            mr: 2,
          }}
        >
          {user?.username?.charAt(0)?.toUpperCase()}
        </Avatar>

        <Button
          color="error"
          startIcon={<LogoutIcon />}
          onClick={() => {
            logout();
            window.location.href = "/login";
          }}
        >
          Logout
        </Button>

      </Toolbar>
    </AppBar>
  );
};

export default Topbar;