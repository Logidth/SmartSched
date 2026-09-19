import api from "../api/api";

// Department Admin

export const getDepartmentAdmins = async () => {
  const response = await api.get("/principal/users/department-admin");
  return response.data;
};

export const createDepartmentAdmin = async (data) => {
  const response = await api.post(
    "/principal/users/department-admin",
    data
  );
  return response.data;
};

// HOD

export const getHods = async () => {
  const response = await api.get("/principal/users/hod");
  return response.data;
};

export const createHod = async (data) => {
  const response = await api.post(
    "/principal/users/hod",
    data
  );
  return response.data;
};

// User Actions

export const enableUser = async (id) => {
  const response = await api.put(
    `/principal/users/${id}/enable`
  );
  return response.data;
};

export const disableUser = async (id) => {
  const response = await api.put(
    `/principal/users/${id}/disable`
  );
  return response.data;
};

export const resetPassword = async (id, data) => {
  const response = await api.put(
    `/principal/users/${id}/reset-password`,
    data
  );
  return response.data;
};
// ==============================
// HOD
// ==============================


export const getFaculties = async () => {

    const response = await api.get("/principal/users/faculty");

    return response.data;

};

export const createFaculty = async (data) => {

    const response = await api.post(

        "/principal/users/faculty",

        data

    );

    return response.data;

};