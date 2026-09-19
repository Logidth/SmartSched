import api from "../api/api";

const BASE_URL = "/branches";

export const getBranches = async () => {

    const response = await api.get(BASE_URL);

    return response.data.data;

};

export const getBranchById = async (id) => {

    const response = await api.get(`${BASE_URL}/${id}`);

    return response.data.data;

};

export const createBranch = async (branch) => {

    const response = await api.post(BASE_URL, branch);

    return response.data.data;

};

export const updateBranch = async (id, branch) => {

    const response = await api.put(`${BASE_URL}/${id}`, branch);

    return response.data.data;

};

export const activateBranch = async (id) => {

    const response = await api.patch(`${BASE_URL}/${id}/activate`);

    return response.data.data;

};

export const deactivateBranch = async (id) => {

    const response = await api.patch(`${BASE_URL}/${id}/deactivate`);

    return response.data.data;

};

export const deleteBranch = async (id) => {

    await api.delete(`${BASE_URL}/${id}`);

};