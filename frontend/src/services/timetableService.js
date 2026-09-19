import api from "../api/api";

const BASE_URL = "/admin/scheduler";

/**
 * Generate timetable
 */
export const generateTimetable = async (academicYearId, studentClassId) => {
    const response = await api.post(`${BASE_URL}/generate`, {
        academicYearId: Number(academicYearId),
        studentClassId: Number(studentClassId)
    });

    return response.data;
};

/**
 * Get timetable
 */
export const getTimetable = async (
    studentClassId,
    academicYearId
) => {
    const response = await api.get(BASE_URL, {
        params: {
            studentClassId: Number(studentClassId),
            academicYearId: Number(academicYearId)
        }
    });

    return response.data;
};

/**
 * Delete timetable
 */
export const deleteTimetable = async (
    studentClassId,
    academicYearId
) => {
    const response = await api.delete(BASE_URL, {
        params: {
            studentClassId: Number(studentClassId),
            academicYearId: Number(academicYearId)
        }
    });

    return response.data;
};

const timetableService = {
    generateTimetable,
    getTimetable,
    deleteTimetable
};

export default timetableService;