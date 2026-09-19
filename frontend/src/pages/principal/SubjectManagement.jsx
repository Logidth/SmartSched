import { useEffect, useState } from "react";

import {
    Button,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import SubjectTable from "../../components/subject/SubjectTable";
import SubjectFormDialog from "../../components/subject/SubjectFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {
    getSubjects,
    createSubject,
    updateSubject,
    deleteSubject,
    activateSubject,
    deactivateSubject
} from "../../services/subjectService";

import {
    getRegulations
} from "../../services/regulationService";

const SubjectManagement = () => {

    const [subjects, setSubjects] = useState([]);
    const [regulations, setRegulations] = useState([]);

    const [open, setOpen] = useState(false);

    const [selectedSubject, setSelectedSubject] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);

    const [subjectToDelete, setSubjectToDelete] = useState(null);

    const loadData = async () => {

        const subjectData = await getSubjects();

        const regulationData = await getRegulations();

        setSubjects(subjectData);

        setRegulations(regulationData);

    };

    useEffect(() => {

        loadData();

    }, []);

    const handleSave = async (subject) => {

        if (selectedSubject) {

            await updateSubject(selectedSubject.id, subject);

        } else {

            await createSubject(subject);

        }

        await loadData();

        setSelectedSubject(null);

        setOpen(false);

    };

    const handleAdd = () => {

        setSelectedSubject(null);

        setOpen(true);

    };

    const handleEdit = (subject) => {

        setSelectedSubject(subject);

        setOpen(true);

    };

    const handleDelete = (subject) => {

        setSubjectToDelete(subject);

        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteSubject(subjectToDelete.id);

        setDeleteOpen(false);

        setSubjectToDelete(null);

        await loadData();

    };

    const handleActivate = async (subject) => {

        await activateSubject(subject.id);

        await loadData();

    };

    const handleDeactivate = async (subject) => {

        await deactivateSubject(subject.id);

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

                    Subject Management

                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleAdd}
                >

                    Add Subject

                </Button>

            </Stack>

            <SubjectTable

                subjects={subjects}

                onEdit={handleEdit}

                onDelete={handleDelete}

                onActivate={handleActivate}

                onDeactivate={handleDeactivate}

            />

            <SubjectFormDialog

                open={open}

                onClose={() => {

                    setOpen(false);

                    setSelectedSubject(null);

                }}

                onSubmit={handleSave}

                subject={selectedSubject}

                regulations={regulations}

            />

            <DeleteConfirmDialog

                open={deleteOpen}

                title="Delete Subject"

                message={`Delete ${subjectToDelete?.subjectName}?`}

                onCancel={() => {

                    setDeleteOpen(false);

                    setSubjectToDelete(null);

                }}

                onConfirm={confirmDelete}

            />

        </>
</DashboardLayout>
    );

};

export default SubjectManagement;