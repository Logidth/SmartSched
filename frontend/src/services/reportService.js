import api from "../api/api";

export const getBranchReports = async () => {
  const response = await api.get("/principal/reports/branches");
  return response.data;
};

export const getFacultyWorkloadReport = async () => {
  const response = await api.get("/principal/reports/faculty-workload");
  return response.data;
};

export const getLeaveReport = async () => {
  const response = await api.get("/principal/reports/leaves");
  return response.data;
};
