import api from "../api/api";

const BASE_URL = "/rooms";

export const getRooms = async () => {
    const response = await api.get(BASE_URL);
    return response.data.data;
};

export const getRoomById = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data.data;
};

export const getRoomsByBranch = async (branchId) => {
    const response = await api.get(
        `${BASE_URL}/branch/${branchId}`
    );

    return response.data.data;
};

export const getRoomsByType = async (roomType) => {
    const response = await api.get(
        `${BASE_URL}/type/${roomType}`
    );

    return response.data.data;
};

export const createRoom = async (room) => {
    const response = await api.post(
        BASE_URL,
        room
    );

    return response.data.data;
};

export const updateRoom = async (id, room) => {
    const response = await api.put(
        `${BASE_URL}/${id}`,
        room
    );

    return response.data.data;
};

export const activateRoom = async (id) => {
    const response = await api.patch(
        `${BASE_URL}/${id}/activate`
    );

    return response.data.data;
};

export const deactivateRoom = async (id) => {
    const response = await api.patch(
        `${BASE_URL}/${id}/deactivate`
    );

    return response.data.data;
};

export const deleteRoom = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};