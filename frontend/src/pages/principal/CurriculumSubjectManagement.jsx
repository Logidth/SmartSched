import { useEffect, useState } from "react";

import {
    Button,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import CurriculumSubjectTable from "../../components/curriculumsubject/CurriculumSubjectTable";
import CurriculumSubjectFormDialog from "../../components/curriculumsubject/CurriculumSubjectFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {
    getCurriculumSubjects,
    createCurriculumSubject,
    updateCurriculumSubject,
    activateCurriculumSubject,
    deactivateCurriculumSubject,
    deleteCurriculumSubject
} from "../../services/curriculumSubjectService";

import { getCurriculums } from "../../services/curriculumService";
import { getSubjects } from "../../services/subjectService";

const CurriculumSubjectManagement = () => {

    const [curriculumSubjects, setCurriculumSubjects] = useState([]);

    const [curriculums, setCurriculums] = useState([]);

    const [subjects, setSubjects] = useState([]);

    const [open, setOpen] = useState(false);

    const [selectedCurriculumSubject, setSelectedCurriculumSubject] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);

    const [curriculumSubjectToDelete, setCurriculumSubjectToDelete] = useState(null);

    const loadData = async () => {

        const [
            curriculumSubjectData,
            curriculumData,
            subjectData
        ] = await Promise.all([

            getCurriculumSubjects(),

            getCurriculums(),

            getSubjects()

        ]);

        setCurriculumSubjects(curriculumSubjectData);

        setCurriculums(curriculumData);

        setSubjects(subjectData);

    };

    useEffect(() => {

        loadData();

    }, []);

    const handleSave = async (data) => {

        if (selectedCurriculumSubject) {

            await updateCurriculumSubject(
                selectedCurriculumSubject.id,
                data
            );

        } else {

            await createCurriculumSubject(data);

        }

        await loadData();

        setSelectedCurriculumSubject(null);

        setOpen(false);

    };

    const handleAdd = () => {

        setSelectedCurriculumSubject(null);

        setOpen(true);

    };

    const handleEdit = (row) => {

        setSelectedCurriculumSubject(row);

        setOpen(true);

    };

    const handleActivate = async (row) => {

        await activateCurriculumSubject(row.id);

        await loadData();

    };

    const handleDeactivate = async (row) => {

        await deactivateCurriculumSubject(row.id);

        await loadData();

    };

    const handleDelete = (row) => {

        setCurriculumSubjectToDelete(row);

        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteCurriculumSubject(curriculumSubjectToDelete.id);

        setDeleteOpen(false);

        setCurriculumSubjectToDelete(null);

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

                    Curriculum Subject Management

                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleAdd}
                >

                    Add Curriculum Subject

                </Button>

            </Stack>

            <CurriculumSubjectTable

                curriculumSubjects={curriculumSubjects}

                onEdit={handleEdit}

                onDelete={handleDelete}

                onActivate={handleActivate}

                onDeactivate={handleDeactivate}

            />

            <CurriculumSubjectFormDialog

                open={open}

                onClose={() => {

                    setOpen(false);

                    setSelectedCurriculumSubject(null);

                }}

                onSubmit={handleSave}

                curriculumSubject={selectedCurriculumSubject}

                curriculums={curriculums}

                subjects={subjects}

            />

            <DeleteConfirmDialog

                open={deleteOpen}

                title="Delete Curriculum Subject"

                message={`Delete ${curriculumSubjectToDelete?.subjectName}?`}

                onCancel={() => {

                    setDeleteOpen(false);

                    setCurriculumSubjectToDelete(null);

                }}

                onConfirm={confirmDelete}

            />

        </>
        </DashboardLayout>

    );

};

export default CurriculumSubjectManagement;