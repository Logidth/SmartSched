import api from "../api/api";

/**
 * Shared across every logged-in role - the backend resolves "my
 * notifications" from the auth token, so there's nothing role-scoped
 * to pass here.
 */
const BASE_URL = "/notifications";

export const getMyNotifications = async () => {

    const response = await api.get(BASE_URL);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

export const getUnreadCount = async () => {

    const response = await api.get(`${BASE_URL}/unread-count`);

    return typeof response.data?.data === "number"
        ? response.data.data
        : 0;
};

export const markNotificationAsRead = async (id) => {

    const response = await api.put(`${BASE_URL}/${id}/read`);

    return response.data;
};

export const markAllNotificationsAsRead = async () => {

    const response = await api.put(`${BASE_URL}/read-all`);

    return response.data;
};

const notificationService = {
    getMyNotifications,
    getUnreadCount,
    markNotificationAsRead,
    markAllNotificationsAsRead
};

export default notificationService;
