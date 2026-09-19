import api from "../api/api";

const BASE_URL = "/student-classes";

export const getStudentClasses = async () => {
    const response = await api.get(BASE_URL);
    return response.data.data;
};

export const getStudentClass = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data.data;
};

export const createStudentClass = async (studentClass) => {
    const response = await api.post(BASE_URL, studentClass);
    return response.data.data;
};

export const updateStudentClass = async (id, studentClass) => {
    const response = await api.put(`${BASE_URL}/${id}`, studentClass);
    return response.data.data;
};

export const activateStudentClass = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/activate`);
    return response.data.data;
};

export const deactivateStudentClass = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/deactivate`);
    return response.data.data;
};

export const deleteStudentClass = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};

const studentClassService = {
    getStudentClasses,
    getStudentClass,
    createStudentClass,
    updateStudentClass,
    activateStudentClass,
    deactivateStudentClass,
    deleteStudentClass
};

export default studentClassService;