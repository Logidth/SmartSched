import api from "../api/api";

export const getFaculties = async () => {
    const response = await api.get("/faculties");
    return response.data.data;
};

export const createFaculty = async (faculty) => {
    const response = await api.post("/faculties", faculty);
    return response.data.data;
};

export const updateFaculty = async (id, faculty) => {
    const response = await api.put(`/faculties/${id}`, faculty);
    return response.data.data;
};

export const activateFaculty = async (id) => {
    const response = await api.patch(`/faculties/${id}/activate`);
    return response.data.data;
};

export const deactivateFaculty = async (id) => {
    const response = await api.patch(`/faculties/${id}/deactivate`);
    return response.data.data;
};

export const deleteFaculty = async (id) => {
    const response = await api.delete(`/faculties/${id}`);
    return response.data;
};

// Creates a login for a faculty member who doesn't have one yet
// (pass `username`), or resets the password for one that already
// does (username is ignored server-side in that case).
export const setFacultyCredentials = async (id, { username, newPassword }) => {
    const response = await api.put(`/faculties/${id}/credentials`, {
        username,
        newPassword
    });
    return response.data.data;
};