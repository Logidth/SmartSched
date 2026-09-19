import { DataGrid } from "@mui/x-data-grid";

import {
    Chip,
    IconButton
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import BlockIcon from "@mui/icons-material/Block";

const FacultySubjectAssignmentTable = ({
    assignments,
    onEdit,
    onDelete,
    onActivate,
    onDeactivate
}) => {

    const columns = [

    {
        field: "facultyName",
        headerName: "Faculty",
        flex: 1.4
    },

    {
        field: "employeeCode",
        headerName: "Employee ID",
        width: 140
    },

    {
        field: "subjectCode",
        headerName: "Subject Code",
        width: 140
    },

    {
        field: "subjectName",
        headerName: "Subject",
        flex: 1.5
    },

    {
        field: "branch",
        headerName: "Branch",
        width: 120
    },

    {
        field: "year",
        headerName: "Year",
        width: 80
    },

    {
        field: "semester",
        headerName: "Semester",
        width: 100
    },

    {
        field: "section",
        headerName: "Section",
        width: 90
    },

    {
        field: "actions",
        headerName: "Actions",
        width: 100,
        sortable: false,

        renderCell: (params) => (

            <IconButton
                color="error"
                onClick={() => onDelete(params.row)}
            >
                <DeleteIcon />
            </IconButton>

        )

    }

];

    return (

        <DataGrid

            rows={assignments}

            columns={columns}

            autoHeight

            disableRowSelectionOnClick

            pageSizeOptions={[5, 10, 20]}

            initialState={{

                pagination: {

                    paginationModel: {

                        pageSize: 10

                    }

                }

            }}

        />

    );

};

export default FacultySubjectAssignmentTable;