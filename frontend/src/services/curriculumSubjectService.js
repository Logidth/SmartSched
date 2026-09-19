import api from "../api/api";

const BASE_URL = "/v1/curriculum-subjects";

export const getCurriculumSubjects = async () => {
    const response = await api.get(BASE_URL);
    return response.data;
};

export const getByCurriculum = async (curriculumId) => {
    const response = await api.get(
        `${BASE_URL}/curriculum/${curriculumId}`
    );
    return response.data;
};

/**
 * Resolve ONLY the subjects actually mapped (via Curriculum Subject
 * Management) to a student class's branch, regulation, academic year,
 * year and semester - not every subject that merely shares the same
 * regulation.
 */
export const getSubjectsForStudentClass = async ({
    branchId,
    regulationId,
    academicYearId,
    year,
    semester
}) => {
    const response = await api.get(`${BASE_URL}/for-class`, {
        params: {
            branchId,
            regulationId,
            academicYearId,
            year,
            semester
        }
    });
    return response.data;
};

export const getCurriculumSubject = async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
};

export const createCurriculumSubject = async (payload) => {
    const response = await api.post(BASE_URL, payload);
    return response.data;
};

export const updateCurriculumSubject = async (id, payload) => {
    const response = await api.put(`${BASE_URL}/${id}`, payload);
    return response.data;
};

export const activateCurriculumSubject = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/activate`);
    return response.data;
};

export const deactivateCurriculumSubject = async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/deactivate`);
    return response.data;
};

export const deleteCurriculumSubject = async (id) => {
    await api.delete(`${BASE_URL}/${id}`);
};