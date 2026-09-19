import api from "../api/api";

export const getPrincipalDashboard = async () => {
  const response = await api.get("/dashboard/principal");
  return response.data;
};

export const getHodDashboard = async (branchId) => {
  const response = await api.get(`/dashboard/hod/${branchId}`);
  return response.data;
};

export const getFacultyDashboard = async (facultyId) => {
  const response = await api.get(`/dashboard/faculty/${facultyId}`);
  return response.data;
};
