import { useEffect, useState } from "react";

import {
    Button,
    Stack,
    Typography
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import StudentClassTable from "../../components/studentClass/StudentClassTable";
import StudentClassFormDialog from "../../components/studentClass/StudentClassFormDialog";
import DeleteConfirmDialog from "../../components/common/DeleteConfirmDialog";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {
    getStudentClasses,
    createStudentClass,
    updateStudentClass,
    activateStudentClass,
    deactivateStudentClass,
    deleteStudentClass
} from "../../services/studentClassService";

import { getBranches } from "../../services/branchService";
import { getRegulations } from "../../services/regulationService";
import { getAcademicYears } from "../../services/academicYearService";

const StudentClassManagement = () => {

    const [studentClasses, setStudentClasses] = useState([]);

    const [branches, setBranches] = useState([]);

    const [regulations, setRegulations] = useState([]);

    const [academicYears, setAcademicYears] = useState([]);

    const [open, setOpen] = useState(false);

    const [selectedStudentClass, setSelectedStudentClass] = useState(null);

    const [deleteOpen, setDeleteOpen] = useState(false);

    const [studentClassToDelete, setStudentClassToDelete] = useState(null);

    const loadData = async () => {

        const classes = await getStudentClasses();

        const branchList = await getBranches();

        const regulationList = await getRegulations();

        const academicYearList = await getAcademicYears();

        setStudentClasses(classes);

        setBranches(branchList);

        setRegulations(regulationList);

        setAcademicYears(academicYearList);

    };

    useEffect(() => {

        loadData();

    }, []);

    const handleSave = async (studentClass) => {

        if (selectedStudentClass) {

            await updateStudentClass(

                selectedStudentClass.id,

                studentClass

            );

        } else {

            await createStudentClass(studentClass);

        }

        await loadData();

        setSelectedStudentClass(null);

        setOpen(false);

    };

    const handleAdd = () => {

        setSelectedStudentClass(null);

        setOpen(true);

    };

    const handleEdit = (studentClass) => {

        setSelectedStudentClass(studentClass);

        setOpen(true);

    };

    const handleDelete = (studentClass) => {

        setStudentClassToDelete(studentClass);

        setDeleteOpen(true);

    };

    const confirmDelete = async () => {

        await deleteStudentClass(studentClassToDelete.id);

        setDeleteOpen(false);

        setStudentClassToDelete(null);

        await loadData();

    };

    const handleActivate = async (studentClass) => {

        await activateStudentClass(studentClass.id);

        await loadData();

    };

    const handleDeactivate = async (studentClass) => {

        await deactivateStudentClass(studentClass.id);

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

                    Student Class Management

                </Typography>

                <Button

                    variant="contained"

                    startIcon={<AddIcon />}

                    onClick={handleAdd}

                >

                    Add Student Class

                </Button>

            </Stack>

            <StudentClassTable

                studentClasses={studentClasses}

                onEdit={handleEdit}

                onDelete={handleDelete}

                onActivate={handleActivate}

                onDeactivate={handleDeactivate}

            />

            <StudentClassFormDialog

                open={open}

                onClose={() => {

                    setOpen(false);

                    setSelectedStudentClass(null);

                }}

                onSubmit={handleSave}

                studentClass={selectedStudentClass}

                branches={branches}

                regulations={regulations}

                academicYears={academicYears}

            />

            <DeleteConfirmDialog

                open={deleteOpen}

                title="Delete Student Class"

                message={`Delete ${studentClassToDelete?.branchName} ${studentClassToDelete?.section}?`}

                onCancel={() => {

                    setDeleteOpen(false);

                    setStudentClassToDelete(null);

                }}

                onConfirm={confirmDelete}

            />

        </>
        </DashboardLayout>

    );

};

export default StudentClassManagement;