import api from "../api/api";

/**
 * All endpoints here are scoped server-side to the logged-in HOD's
 * own department (branch) - the backend resolves the HOD -> Faculty
 * -> Branch chain from the auth token, so the frontend never has to
 * (and never should) pass a branchId for these calls.
 */
const BASE_URL = "/hod/timetable";

// ============================================================
// PENDING TIMETABLES (submitted by the department Admin,
// awaiting this HOD's decision)
// ============================================================

export const getPendingTimetables = async () => {

    const response = await api.get(`${BASE_URL}/pending`);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

// ============================================================
// APPROVE / REJECT
// ============================================================

export const approveTimetable = async ({
    studentClassId,
    academicYearId,
    remarks
}) => {

    const response = await api.put(
        `${BASE_URL}/approve`,
        {
            studentClassId: Number(studentClassId),
            academicYearId: Number(academicYearId),
            remarks: remarks?.trim() || ""
        }
    );

    return response.data;
};

export const rejectTimetable = async ({
    studentClassId,
    academicYearId,
    remarks
}) => {

    const response = await api.put(
        `${BASE_URL}/reject`,
        {
            studentClassId: Number(studentClassId),
            academicYearId: Number(academicYearId),
            remarks: remarks?.trim() || ""
        }
    );

    return response.data;
};

// ============================================================
// APPROVED / REJECTED TIMETABLE HISTORY
// (grouped one entry per class + academic year, most recently
// decided first - mirrors what /pending returns, but for
// timetables this HOD has already acted on)
// ============================================================

export const getApprovedTimetables = async () => {

    const response = await api.get(`${BASE_URL}/approved`);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

export const getRejectedTimetables = async () => {

    const response = await api.get(`${BASE_URL}/rejected`);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

// ============================================================
// DEPARTMENT STUDENT CLASSES
// (reuses the same branch-scoped endpoint the department Admin
// uses - a HOD only ever sees their own department's classes)
// ============================================================

export const getDepartmentStudentClasses = async (branchId) => {

    const response = await api.get(
        `/student-classes/branch/${branchId}`
    );

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

// ============================================================
// SUBJECTS ACTUALLY MAPPED TO A CLASS'S CURRICULUM
// (branch + regulation + academic year + year + semester) -
// NOT every subject that merely shares the class's regulation.
// ============================================================

export const getSubjectsForClass = async (studentClass) => {

    const response = await api.get(
        "/v1/curriculum-subjects/for-class",
        {
            params: {
                branchId: studentClass.branchId,
                regulationId: studentClass.regulationId,
                academicYearId: studentClass.academicYearId,
                year: studentClass.year,
                semester: studentClass.semester
            }
        }
    );

    return Array.isArray(response.data)
        ? response.data
        : [];
};

// ============================================================
// LEAVE REQUESTS (raised by faculty, awaiting this HOD's decision)
// ============================================================

export const getPendingLeaves = async () => {

    const response = await api.get(`${BASE_URL}/leave/pending`);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

export const approveLeave = async ({ leaveRequestId, remarks }) => {

    const response = await api.put(
        `${BASE_URL}/leave/approve`,
        {
            leaveRequestId: Number(leaveRequestId),
            approved: true,
            remarks: remarks?.trim() || ""
        }
    );

    return response.data;
};

export const rejectLeave = async ({ leaveRequestId, remarks }) => {

    const response = await api.put(
        `${BASE_URL}/leave/reject`,
        {
            leaveRequestId: Number(leaveRequestId),
            approved: false,
            remarks: remarks?.trim() || ""
        }
    );

    return response.data;
};

// ============================================================
// DEPARTMENT FACULTY
// (faculty belonging to the HOD's own department only)
// ============================================================

export const getDepartmentFaculty = async () => {

    const response = await api.get(`${BASE_URL}/department/faculty`);

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

export const getDepartmentFacultyDetails = async (facultyId) => {

    const response = await api.get(
        `${BASE_URL}/department/faculty/${facultyId}`
    );

    return response.data?.data ?? null;
};

export const getFacultyLeaves = async (facultyId) => {

    const response = await api.get(
        `${BASE_URL}/department/faculty/${facultyId}/leaves`
    );

    return Array.isArray(response.data?.data)
        ? response.data.data
        : [];
};

const hodService = {
    getPendingTimetables,
    approveTimetable,
    rejectTimetable,
    getApprovedTimetables,
    getRejectedTimetables,
    getDepartmentStudentClasses,
    getSubjectsForClass,
    getPendingLeaves,
    approveLeave,
    rejectLeave,
    getDepartmentFaculty,
    getDepartmentFacultyDetails,
    getFacultyLeaves
};

export default hodService;