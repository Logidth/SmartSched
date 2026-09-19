import api from "../api/api";

const BASE_URL = "/v1/curriculums";

export const getCurriculums = async () => {
    const response = await api.get(BASE_URL);
    return response.data;
};

export const getCurriculum = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
};

export const createCurriculum = async (data) => {
    const response = await api.post(BASE_URL, data);
    return response.data;
};

export const updateCurriculum = async (id, data) => {
    const response = await api.put(`${BASE_URL}/${id}`, data);
    return response.data;
};

export const activateCurriculum = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/activate`);
    return response.data;
};

export const deactivateCurriculum = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/deactivate`);
    return response.data;
};

export const deleteCurriculum = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};