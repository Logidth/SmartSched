import api from "../api/api";

const BASE_URL = "/v1/regulations";

export const getRegulations = async () => {

    const response = await api.get(BASE_URL);

    return response.data;

};

export const getRegulationById = async (id) => {

    const response = await api.get(`${BASE_URL}/${id}`);

    return response.data;

};

export const createRegulation = async (regulation) => {

    const response = await api.post(BASE_URL, regulation);

    return response.data;

};

export const updateRegulation = async (id, regulation) => {

    const response = await api.put(`${BASE_URL}/${id}`, regulation);

    return response.data;

};

export const activateRegulation = async (id) => {

    const response = await api.patch(`${BASE_URL}/${id}/activate`);

    return response.data;

};

export const deactivateRegulation = async (id) => {

    const response = await api.patch(`${BASE_URL}/${id}/deactivate`);

    return response.data;

};

export const deleteRegulation = async (id) => {

    await api.delete(`${BASE_URL}/${id}`);

};