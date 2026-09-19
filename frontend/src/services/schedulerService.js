import api from "../api/api";

const BASE_URL = "/admin/scheduler";


// ============================================================
// GENERATE TIMETABLE
// ============================================================

// ============================================================
// APPROVE / REJECT TIMETABLE
// ============================================================

export const approveTimetable = async ({
    studentClassId,
    academicYearId,
    approved,
    remarks
}) => {

    const response = await api.put(
        `${BASE_URL}/approve`,
        {
            studentClassId: Number(studentClassId),
            academicYearId: Number(academicYearId),
            approved: Boolean(approved),
            remarks: remarks || ""
        }
    );

    return response.data;
};


// ============================================================
// SUBMIT TIMETABLE TO HOD
// ============================================================

export const submitTimetable = async ({
    studentClassId,
    academicYearId,
    remarks
}) => {

    const response = await api.post(
        `${BASE_URL}/submit`,
        {
            studentClassId: Number(studentClassId),
            academicYearId: Number(academicYearId),
            remarks: remarks?.trim() || ""
        }
    );

    return response.data;
};

export const generateTimetable = async (
    academicYearId,
    studentClassId
) => {

    const response = await api.post(
        `${BASE_URL}/generate`,
        {
            academicYearId: Number(academicYearId),
            studentClassId: Number(studentClassId)
        }
    );

    /*
     * Backend now returns { entries, failures } instead of a bare
     * array, so a subject that couldn't be scheduled (e.g. every
     * eligible faculty already at their weekly cap, or no free
     * periods left in the week) is reported instead of silently
     * missing from the timetable.
     */
    return response.data.data;
};


// ============================================================
// GET TIMETABLE
// ============================================================

export const getTimetable = async (
    studentClassId,
    academicYearId
) => {

    const response = await api.get(
        BASE_URL,
        {
            params: {
                studentClassId: Number(studentClassId),
                academicYearId: Number(academicYearId)
            }
        }
    );

    return response.data.data;
};


// ============================================================
// DELETE TIMETABLE
// ============================================================

export const deleteTimetable = async (
    studentClassId,
    academicYearId
) => {

    const response = await api.delete(
        BASE_URL,
        {
            params: {
                studentClassId: Number(studentClassId),
                academicYearId: Number(academicYearId)
            }
        }
    );

    return response.data;
};


// ============================================================
// DELETE ALL PREVIOUS TIMETABLES FOR A DEPARTMENT
// ============================================================

export const deleteAllTimetablesForBranch = async (branchId) => {

    const response = await api.delete(
        `${BASE_URL}/branch/${Number(branchId)}/all`
    );

    return response.data;
};


// ============================================================
// VALIDATE TIMETABLE
// ============================================================

export const validateTimetable = async (
    studentClassId,
    academicYearId
) => {

    const response = await api.get(
        `${BASE_URL}/validate`,
        {
            params: {
                studentClassId: Number(studentClassId),
                academicYearId: Number(academicYearId)
            }
        }
    );

    return response.data.data;
};