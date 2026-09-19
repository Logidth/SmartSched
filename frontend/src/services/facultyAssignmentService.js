import api from "../api/api";

const BASE_URL = "/admin/faculty-assignments";

export const getFacultySubjectAssignments = async () => {
    const response = await api.get(BASE_URL);
    return response.data;
};

export const getFacultySubjectAssignment = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
};

/**
 * Get curriculum subjects filtered by student class.
 * 
 * Returns ONLY the subjects that belong to the student class's
 * curriculum (by Branch + Regulation + AcademicYear + Year + Semester).
 * 
 * This prevents the dropdown from showing unrelated subjects from
 * other curricula.
 * 
 * @param studentClassId ID of the student class
 * @returns Promise resolving to list of curriculum subjects for that class
 */
export const getCurriculumSubjectsForClass = async (studentClassId) => {
    const response = await api.get(
        `${BASE_URL}/curriculum-subjects/by-class/${studentClassId}`
    );
    return response.data;
};

export const createFacultySubjectAssignment = async (payload) => {
    const response = await api.post(BASE_URL, payload);
    return response.data;
};

export const deleteFacultySubjectAssignment = async (id) => {
    const response = await api.delete(`${BASE_URL}/${id}`);
    return response.data;
};
