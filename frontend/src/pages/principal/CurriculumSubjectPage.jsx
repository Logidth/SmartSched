import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import DashboardLayout from "../../components/layout/DashboardLayout";
import {
    Box,
    Typography,
    CircularProgress,
    Alert,
    Paper,
    Table,
    TableHead,
    TableBody,
    TableRow,
    TableCell,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    MenuItem
} from "@mui/material";

import {
    getCurriculum
} from "../../services/curriculumService";

import {
    getByCurriculum,
    createCurriculumSubject,
    updateCurriculumSubject,
    deleteCurriculumSubject
} from "../../services/curriculumSubjectService";

import {
    getSubjectsByRegulation
} from "../../services/subjectService";
const CurriculumSubjectPage = () => {

    const { curriculumId } = useParams();

    const [curriculum, setCurriculum] = useState(null);
    const [subjects, setSubjects] = useState([]);
    const [availableSubjects, setAvailableSubjects] = useState([]);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

const [selectedDeleteId, setSelectedDeleteId] = useState(null);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [openDialog, setOpenDialog] = useState(false);

    const [formData, setFormData] = useState({
        subjectId: "",
        year: "",
        semester: "",
        displayOrder: "",
        status: "ACTIVE"
    });
    const [editingId, setEditingId] = useState(null);

    useEffect(() => {
        loadPage();
    }, [curriculumId]);

    const loadPage = async () => {

    try {

        setLoading(true);

        const curriculumData = await getCurriculum(curriculumId);

        console.log("Curriculum:", curriculumData);

        setCurriculum(curriculumData);

        const curriculumSubjects = await getByCurriculum(curriculumId);

        console.log("Curriculum Subjects:", curriculumSubjects);

        setSubjects(curriculumSubjects);

        const available = await getSubjectsByRegulation(
            curriculumData.regulationId
        );

        console.log("Available Subjects:", available);

        setAvailableSubjects(available);

    } catch (err) {

        console.error(err);

        setError("Unable to load curriculum.");

    } finally {

        setLoading(false);

    }

};

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

    };

    const handleSaveSubject = async () => {

    try {

        const payload = {

            curriculumId: Number(curriculumId),
            subjectId: Number(formData.subjectId),
            year: Number(formData.year),
            semester: Number(formData.semester),
            displayOrder: Number(formData.displayOrder),
            status: formData.status

        };

        if (editingId) {

            await updateCurriculumSubject(editingId, payload)

        } else {

            await createCurriculumSubject(payload);

        }

        setOpenDialog(false);

        setEditingId(null);

        setFormData({
            subjectId: "",
            year: "",
            semester: "",
            displayOrder: "",
            status: "ACTIVE"
        });

        loadPage();

    } catch (err) {

        console.error(err);

        alert("Unable to save subject.");

    }

};
const handleDeleteSubject = async () => {

    try {

        await deleteCurriculumSubject(selectedDeleteId)
        setDeleteDialogOpen(false);

        setSelectedDeleteId(null);

        loadPage();

    } catch (err) {

        console.error(err);

        alert("Unable to delete subject.");

    }

};

    if (loading)
        return <CircularProgress />;

    if (error)
        return <Alert severity="error">{error}</Alert>;

    const groupedSubjects = subjects.reduce((acc, subject) => {

        if (!acc[subject.semester]) {
            acc[subject.semester] = [];
        }

        acc[subject.semester].push(subject);

        return acc;

    }, {});

    return (
        <DashboardLayout>

        <Box p={3}>

            <Box
                display="flex"
                justifyContent="flex-end"
                mb={3}
            >

                <Button
                    variant="contained"
                    onClick={() => {

    setEditingId(null);

    setFormData({
        subjectId: "",
        year: "",
        semester: "",
        displayOrder: "",
        status: "ACTIVE"
    });

    setOpenDialog(true);

}}
                >
                    Add Subject
                </Button>

            </Box>

            <Box mb={4}>

                <Typography variant="h4" gutterBottom>
                    Curriculum Subject Management
                </Typography>

                <Typography>
                    <strong>Branch :</strong> {curriculum.branch}
                </Typography>

                <Typography>
                    <strong>Regulation :</strong> {curriculum.regulation}
                </Typography>

                <Typography>
                    <strong>Academic Year :</strong> {curriculum.academicYear}
                </Typography>

                <Typography>
                    <strong>Status :</strong> {curriculum.status}
                </Typography>

            </Box>

            {Object.keys(groupedSubjects).map((semester) => (

                <Paper
                    key={semester}
                    sx={{ mb: 4, p: 2 }}
                >

                    <Typography
                        variant="h6"
                        gutterBottom
                    >
                        Semester {semester}
                    </Typography>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell>Order</TableCell>
                                <TableCell>Subject Code</TableCell>
                                <TableCell>Subject Name</TableCell>
                                <TableCell>Year</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell align="center">
                                    Actions
                                </TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {groupedSubjects[semester].map((row) => (

                                <TableRow key={row.id}>

                                    <TableCell>
                                        {row.displayOrder}
                                    </TableCell>

                                    <TableCell>
                                        {row.subjectCode}
                                    </TableCell>

                                    <TableCell>
                                        {row.subjectName}
                                    </TableCell>

                                    <TableCell>
                                        {row.year}
                                    </TableCell>

                                    <TableCell>
                                        {row.status}
                                    </TableCell>

                                    <TableCell align="center">

                                       <Button
    size="small"
    variant="outlined"
    onClick={() => {

        setEditingId(row.id);

        setFormData({
            subjectId: row.subjectId,
            year: row.year,
            semester: row.semester,
            displayOrder: row.displayOrder,
            status: row.status
        });

        setOpenDialog(true);

    }}
>
    Edit
</Button>

                                       <Button
    size="small"
    color="error"
    variant="outlined"
    sx={{ ml: 1 }}
    onClick={() => {

        setSelectedDeleteId(row.id);

        setDeleteDialogOpen(true);

    }}
>
    Delete
</Button>

                                    </TableCell>

                                </TableRow>

                            ))}

                        </TableBody>

                    </Table>

                </Paper>

            ))}

            <Dialog
                open={openDialog}
                onClose={() => {

    setOpenDialog(false);

    setEditingId(null);

    setFormData({
        subjectId: "",
        year: "",
        semester: "",
        displayOrder: "",
        status: "ACTIVE"
    });

}}
                fullWidth
                maxWidth="sm"
            >

              <DialogTitle>
    {editingId ? "Edit Subject" : "Add Subject"}
</DialogTitle>

                <DialogContent>

                    <TextField
                        select
                        fullWidth
                        margin="normal"
                        label="Subject"
                        name="subjectId"
                        value={formData.subjectId}
                        onChange={handleChange}
                    >

                        {availableSubjects.map(subject => (

                            <MenuItem
                                key={subject.id}
                                value={subject.id}
                            >

                                {subject.subjectCode} - {subject.subjectName}

                            </MenuItem>

                        ))}

                    </TextField>

                    <TextField
                        fullWidth
                        margin="normal"
                        type="number"
                        label="Year"
                        name="year"
                        value={formData.year}
                        onChange={handleChange}
                    />

                    <TextField
                        fullWidth
                        margin="normal"
                        type="number"
                        label="Semester"
                        name="semester"
                        value={formData.semester}
                        onChange={handleChange}
                    />

                    <TextField
                        fullWidth
                        margin="normal"
                        type="number"
                        label="Display Order"
                        name="displayOrder"
                        value={formData.displayOrder}
                        onChange={handleChange}
                    />

                </DialogContent>

                <DialogActions>

                    <Button
                        onClick={() => {

    setOpenDialog(false);

    setEditingId(null);

    setFormData({
        subjectId: "",
        year: "",
        semester: "",
        displayOrder: "",
        status: "ACTIVE"
    });

}}
                    >
                        Cancel
                    </Button>

                    <Button
    variant="contained"
    onClick={handleSaveSubject}
    disabled={
        !formData.subjectId ||
        !formData.year ||
        !formData.semester ||
        !formData.displayOrder
    }
>  {editingId ? "Update" : "Save"}</Button>

                </DialogActions>

            </Dialog>
            <Dialog
    open={deleteDialogOpen}
    onClose={() => {

        setDeleteDialogOpen(false);

        setSelectedDeleteId(null);

    }}
>

    <DialogTitle>
        Delete Curriculum Subject
    </DialogTitle>

    <DialogContent>

        <Typography>

            Are you sure you want to delete this curriculum subject?

        </Typography>

    </DialogContent>

    <DialogActions>

        <Button
            onClick={() => {

                setDeleteDialogOpen(false);

                setSelectedDeleteId(null);

            }}
        >
            Cancel
        </Button>

        <Button
            color="error"
            variant="contained"
            onClick={handleDeleteSubject}
        >
            Delete
        </Button>

    </DialogActions>

</Dialog>

        </Box>

        </DashboardLayout>

    );

};

export default CurriculumSubjectPage;