import api from "../api/api";

/**
 * All endpoints here are scoped server-side to the logged-in
 * faculty member's own record — the backend resolves the faculty
 * from the auth token (see FacultySelfController), so the frontend
 * never needs to know or pass a facultyId for any of these calls.
 *
 * Not to be confused with facultyService.js, which is the
 * Admin/Principal CRUD service for managing OTHER faculty
 * (/api/faculties).
 */
const BASE_URL = "/faculty/me";

// ============================================================
// PROFILE
// ============================================================

export const getMyProfile = async () => {

    const response = await api.get(BASE_URL);

    return response.data?.data ?? null;
};

// ============================================================
// DASHBOARD
// ============================================================

export const getMyDashboard = async () => {

    const response = await api.get(`${BASE_URL}/dashboard`);

    return response.data?.data ?? null;
};

// ============================================================
// MY SUBJECT ASSIGNMENTS
// ============================================================

export const getMyAssignments = async () => {

    const response = await api.get(`${BASE_URL}/assignments`);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

// ============================================================
// MY TIMETABLE
// ============================================================

export const getMyTimetable = async () => {

    const response = await api.get(`${BASE_URL}/timetable`);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

// ============================================================
// MY LEAVE REQUESTS
// ============================================================

export const getMyLeaves = async () => {

    const response = await api.get(`${BASE_URL}/leaves`);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

export const applyLeave = async ({ leaveType, fromDate, toDate, reason }) => {

    const response = await api.post(`${BASE_URL}/leaves`, {
        leaveType,
        fromDate,
        toDate,
        reason: reason?.trim() || ""
    });

    return response.data;
};

const facultySelfService = {
    getMyProfile,
    getMyDashboard,
    getMyAssignments,
    getMyTimetable,
    getMyLeaves,
    applyLeave
};

export default facultySelfService;