import { useEffect, useState } from "react";
import {
  Box,
  Grid,
  Paper,
  Typography,
  CircularProgress,
  LinearProgress,
} from "@mui/material";

import PeopleIcon from "@mui/icons-material/People";
import SchoolIcon from "@mui/icons-material/School";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import EventBusyIcon from "@mui/icons-material/EventBusy";
import PendingActionsIcon from "@mui/icons-material/PendingActions";
import HowToRegIcon from "@mui/icons-material/HowToReg";

import toast from "react-hot-toast";

import DashboardLayout from "../../components/layout/DashboardLayout";
import { getPrincipalDashboard } from "../../services/dashboardService";

const StatCard = ({ title, value, icon, color }) => (
  <Paper
    elevation={3}
    sx={{
      p: 3,
      display: "flex",
      alignItems: "center",
      gap: 2,
      height: "100%",
    }}
  >
    <Box
      sx={{
        width: 56,
        height: 56,
        borderRadius: "50%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        bgcolor: `${color}22`,
        color: color,
      }}
    >
      {icon}
    </Box>

    <Box>
      <Typography variant="body2" color="text.secondary">
        {title}
      </Typography>

      <Typography variant="h5" fontWeight="bold">
        {value}
      </Typography>
    </Box>
  </Paper>
);

const PrincipalDashboard = () => {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadDashboard = async () => {
    try {
      setLoading(true);

      const response = await getPrincipalDashboard();

      setSummary(response.data);
    } catch {
      toast.error("Unable to load dashboard");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard();
  }, []);

  return (
    <DashboardLayout>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Principal Dashboard
      </Typography>

      {loading || !summary ? (
        <Box mt={5} textAlign="center">
          <CircularProgress />
        </Box>
      ) : (
        <>
          <Grid container spacing={3} mt={1}>
            <Grid item xs={12} sm={6} md={4} lg={2.4}>
              <StatCard
                title="Branches"
                value={summary.branchCount}
                icon={<SchoolIcon />}
                color="#0D47A1"
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4} lg={2.4}>
              <StatCard
                title="Faculty"
                value={summary.facultyCount}
                icon={<PeopleIcon />}
                color="#1565C0"
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4} lg={2.4}>
              <StatCard
                title="Classes"
                value={summary.classCount}
                icon={<SchoolIcon />}
                color="#2E7D32"
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4} lg={2.4}>
              <StatCard
                title="Subjects"
                value={summary.subjectCount}
                icon={<MenuBookIcon />}
                color="#EF6C00"
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4} lg={2.4}>
              <StatCard
                title="Today's Attendance"
                value={summary.todayAttendance}
                icon={<HowToRegIcon />}
                color="#00838F"
              />
            </Grid>
          </Grid>

          <Grid container spacing={3} mt={0.5}>
            <Grid item xs={12} sm={6}>
              <StatCard
                title="Pending Leave Requests"
                value={summary.pendingLeaves}
                icon={<EventBusyIcon />}
                color="#C62828"
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <StatCard
                title="Timetables Awaiting Approval"
                value={summary.pendingTimetables}
                icon={<PendingActionsIcon />}
                color="#6A1B9A"
              />
            </Grid>
          </Grid>

          <Paper elevation={3} sx={{ p: 3, mt: 3 }}>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
              Quick Overview
            </Typography>

            <Typography variant="body2" color="text.secondary" gutterBottom>
              {summary.pendingTimetables > 0
                ? `${summary.pendingTimetables} timetable(s) are waiting for your approval.`
                : "All timetables are up to date."}
            </Typography>

            <LinearProgress
              variant="determinate"
              value={
                summary.classCount === 0
                  ? 0
                  : Math.min(
                      100,
                      ((summary.classCount - summary.pendingTimetables) /
                        Math.max(summary.classCount, 1)) *
                        100
                    )
              }
              sx={{ height: 10, borderRadius: 5, mt: 1 }}
            />
          </Paper>
        </>
      )}
    </DashboardLayout>
  );
};

export default PrincipalDashboard;
