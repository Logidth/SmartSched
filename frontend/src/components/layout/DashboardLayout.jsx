import { Box } from "@mui/material";
import Sidebar from "./SideBar";
import Topbar from "./TopBar";

const DashboardLayout = ({ children }) => {
  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      <Sidebar />

      <Box
        sx={{
          flexGrow: 1,
          background: "#F4F7FC",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <Topbar />

        <Box
          sx={{
            p: 3,
            flexGrow: 1,
          }}
        >
          {children}
        </Box>
      </Box>
    </Box>
  );
};

export default DashboardLayout;