import { useEffect, useState } from "react";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {
    Button,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import CurriculumTable from "../../components/curriculum/CurriculumTable";
import CurriculumFormDialog from "../../components/curriculum/CurriculumFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";

import {
    getCurriculums,
    createCurriculum,
    updateCurriculum,
    activateCurriculum,
    deactivateCurriculum,
    deleteCurriculum
} from "../../services/curriculumService";

import { getBranches } from "../../services/branchService";
import { getRegulations } from "../../services/regulationService";
import { getAcademicYears } from "../../services/academicYearService";

const CurriculumManagement = () => {

    const [curriculums, setCurriculums] = useState([]);

    const [branches, setBranches] = useState([]);

    const [regulations, setRegulations] = useState([]);

    const [academicYears, setAcademicYears] = useState([]);

    const [open, setOpen] = useState(false);

    const [selectedCurriculum, setSelectedCurriculum] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);

    const [curriculumToDelete, setCurriculumToDelete] = useState(null);

    const loadData = async () => {

        const curriculumData = await getCurriculums();

        const branchData = await getBranches();

        const regulationData = await getRegulations();

        const academicYearData = await getAcademicYears();

        setCurriculums(curriculumData);

        setBranches(branchData);

        setRegulations(regulationData);

        setAcademicYears(academicYearData);

    };

    useEffect(() => {

        loadData();

    }, []);

    const handleSave = async (curriculum) => {

        if (selectedCurriculum) {

            await updateCurriculum(selectedCurriculum.id, curriculum);

        } else {

            await createCurriculum(curriculum);

        }

        await loadData();

        setSelectedCurriculum(null);

        setOpen(false);

    };

    const handleEdit = (curriculum) => {

        setSelectedCurriculum(curriculum);

        setOpen(true);

    };

    const handleAdd = () => {

        setSelectedCurriculum(null);

        setOpen(true);

    };

    const handleActivate = async (curriculum) => {

        await activateCurriculum(curriculum.id);

        await loadData();

    };

    const handleDeactivate = async (curriculum) => {

        await deactivateCurriculum(curriculum.id);

        await loadData();

    };

    const handleDelete = (curriculum) => {

        setCurriculumToDelete(curriculum);

        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteCurriculum(curriculumToDelete.id);

        setDeleteOpen(false);

        setCurriculumToDelete(null);

        await loadData();

    };

    return (
<DashboardLayout>
        <>

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Typography
                    variant="h4"
                    fontWeight="bold"
                >

                    Curriculum Management

                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleAdd}
                >

                    Add Curriculum

                </Button>

            </Stack>

            <CurriculumTable

                curriculums={curriculums}

                onEdit={handleEdit}

                onDelete={handleDelete}

                onActivate={handleActivate}

                onDeactivate={handleDeactivate}

            />

            <CurriculumFormDialog

                open={open}

                onClose={() => {

                    setOpen(false);

                    setSelectedCurriculum(null);

                }}

                onSubmit={handleSave}

                curriculum={selectedCurriculum}

                branches={branches}

                regulations={regulations}

                academicYears={academicYears}

            />

            <DeleteConfirmDialog

                open={deleteOpen}

                title="Delete Curriculum"

                message={`Delete curriculum for ${curriculumToDelete?.branch}?`}

                onCancel={() => {

                    setDeleteOpen(false);

                    setCurriculumToDelete(null);

                }}

                onConfirm={confirmDelete}

            />

        </>
        </DashboardLayout>

    );

};

export default CurriculumManagement;