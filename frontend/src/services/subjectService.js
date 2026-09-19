import api from "../api/api";

const BASE_URL = "/v1/subjects";

export const getSubjects = async () => {
    const response = await api.get(BASE_URL);
    return response.data;
};

export const getSubjectById = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
};

export const getSubjectsByRegulation = async (regulationId) => {
    const response = await api.get(
        `${BASE_URL}/regulation/${regulationId}`
    );
    return response.data;
};

export const createSubject = async (subject) => {
    const response = await api.post(BASE_URL, subject);
    return response.data;
};

export const updateSubject = async (id, subject) => {
    const response = await api.put(`${BASE_URL}/${id}`, subject);
    return response.data;
};

export const activateSubject = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/activate`);
    return response.data;
};

export const deactivateSubject = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/deactivate`);
    return response.data;
};

export const deleteSubject = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};