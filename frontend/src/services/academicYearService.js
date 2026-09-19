import api from "../api/api";

const BASE_URL = "/v1/academic-years";

export const getAcademicYears = async () => {
    const response = await api.get(BASE_URL);
    return response.data;
};

export const getAcademicYear = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
};

export const createAcademicYear = async (payload) => {
    const response = await api.post(BASE_URL, payload);
    return response.data;
};

export const updateAcademicYear = async (id, payload) => {
    const response = await api.put(`${BASE_URL}/${id}`, payload);
    return response.data;
};

export const activateAcademicYear = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/activate`);
    return response.data;
};

export const deactivateAcademicYear = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/deactivate`);
    return response.data;
};

export const deleteAcademicYear = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};