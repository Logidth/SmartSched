import { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Paper,
  CircularProgress,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Chip,
  Grid,
} from "@mui/material";

import toast from "react-hot-toast";

import DashboardLayout from "../../components/layout/DashboardLayout";

import {
  getBranchReports,
  getFacultyWorkloadReport,
  getLeaveReport,
} from "../../services/reportService";

const SummaryCard = ({ title, value, color }) => (
  <Paper elevation={3} sx={{ p: 3 }}>
    <Typography variant="body2" color="text.secondary">
      {title}
    </Typography>
    <Typography variant="h4" fontWeight="bold" color={color}>
      {value}
    </Typography>
  </Paper>
);

const Reports = () => {
  const [branches, setBranches] = useState([]);
  const [faculty, setFaculty] = useState([]);
  const [leave, setLeave] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadReports = async () => {
    try {
      setLoading(true);

      const [branchRes, facultyRes, leaveRes] = await Promise.all([
        getBranchReports(),
        getFacultyWorkloadReport(),
        getLeaveReport(),
      ]);

      setBranches(branchRes.data);
      setFaculty(facultyRes.data);
      setLeave(leaveRes.data);
    } catch {
      toast.error("Unable to load reports");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReports();
  }, []);

  return (
    <DashboardLayout>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Reports
      </Typography>

      <Typography variant="body2" color="text.secondary" gutterBottom>
        Branch-wise, faculty workload and leave summaries.
      </Typography>

      {loading ? (
        <Box mt={5} textAlign="center">
          <CircularProgress />
        </Box>
      ) : (
        <Box mt={2}>
          {leave && (
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6} md={3}>
                <SummaryCard
                  title="Total Leave Requests"
                  value={leave.totalRequests}
                  color="text.primary"
                />
              </Grid>

              <Grid item xs={12} sm={6} md={3}>
                <SummaryCard
                  title="Pending"
                  value={leave.pending}
                  color="warning.main"
                />
              </Grid>

              <Grid item xs={12} sm={6} md={3}>
                <SummaryCard
                  title="Approved"
                  value={leave.approved}
                  color="success.main"
                />
              </Grid>

              <Grid item xs={12} sm={6} md={3}>
                <SummaryCard
                  title="Rejected"
                  value={leave.rejected}
                  color="error.main"
                />
              </Grid>
            </Grid>
          )}

          <Paper elevation={3} sx={{ mt: 4, p: 3 }}>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
              Branch-wise Report
            </Typography>

            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Branch</TableCell>
                  <TableCell>Faculty</TableCell>
                  <TableCell>Classes</TableCell>
                  <TableCell>Subjects</TableCell>
                  <TableCell>Rooms</TableCell>
                  <TableCell>Approved TT</TableCell>
                  <TableCell>Pending TT</TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {branches.map((b) => (
                  <TableRow key={b.branchId} hover>
                    <TableCell>
                      {b.branchName} ({b.branchCode})
                    </TableCell>
                    <TableCell>{b.facultyCount}</TableCell>
                    <TableCell>{b.classCount}</TableCell>
                    <TableCell>{b.subjectCount}</TableCell>
                    <TableCell>{b.roomCount}</TableCell>
                    <TableCell>{b.approvedTimetables}</TableCell>
                    <TableCell>{b.pendingTimetables}</TableCell>
                  </TableRow>
                ))}

                {branches.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={7} align="center">
                      No branches found
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </Paper>

          <Paper elevation={3} sx={{ mt: 4, p: 3 }}>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
              Faculty Workload Report
            </Typography>

            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Employee ID</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>Branch</TableCell>
                  <TableCell>Designation</TableCell>
                  <TableCell>Weekly Periods</TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {faculty.map((f) => (
                  <TableRow key={f.facultyId} hover>
                    <TableCell>{f.employeeId}</TableCell>
                    <TableCell>{f.facultyName}</TableCell>
                    <TableCell>{f.branchName || "-"}</TableCell>
                    <TableCell>
                      <Chip
                        size="small"
                        label={
                          f.designation
                            ? f.designation.replaceAll("_", " ")
                            : "-"
                        }
                      />
                    </TableCell>
                    <TableCell>{f.weeklyPeriods}</TableCell>
                  </TableRow>
                ))}

                {faculty.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={5} align="center">
                      No faculty found
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </Paper>
        </Box>
      )}
    </DashboardLayout>
  );
};

export default Reports;