import api from "../api/api";

const BASE_URL = "/blocks";

export const getBlocks = async () => {
    const response = await api.get(BASE_URL);
    return response.data.data;
};

export const getBlockById = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data.data;
};

export const createBlock = async (block) => {
    const response = await api.post(BASE_URL, block);
    return response.data.data;
};

export const updateBlock = async (id, block) => {
    const response = await api.put(
        `${BASE_URL}/${id}`,
        block
    );

    return response.data.data;
};

export const activateBlock = async (id) => {
    const response = await api.patch(
        `${BASE_URL}/${id}/activate`
    );

    return response.data.data;
};

export const deactivateBlock = async (id) => {
    const response = await api.patch(
        `${BASE_URL}/${id}/deactivate`
    );

    return response.data.data;
};

export const deleteBlock = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};