import { DataGrid } from "@mui/x-data-grid";

import {
    Chip,
    IconButton
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import BlockIcon from "@mui/icons-material/Block";

const StudentClassTable = ({
    studentClasses,
    onEdit,
    onDelete,
    onActivate,
    onDeactivate
}) => {

    const columns = [

        {
            field: "branchName",
            headerName: "Branch",
            width: 220
        },

        {
    field: "regulation",
    headerName: "Regulation",
    width: 150
},

{
    field: "academicYear",
    headerName: "Academic Year",
    width: 170
},

        {
            field: "year",
            headerName: "Year",
            width: 90
        },

        {
            field: "semester",
            headerName: "Semester",
            width: 110
        },

        {
            field: "section",
            headerName: "Section",
            width: 100
        },

        {
            field: "strength",
            headerName: "Strength",
            width: 110
        },

        {
            field: "status",
            headerName: "Status",
            width: 120,

            renderCell: (params) => (

                <Chip
                    label={params.value}
                    color={
                        params.value === "ACTIVE"
                            ? "success"
                            : "error"
                    }
                    size="small"
                />

            )

        },

        {
            field: "actions",
            headerName: "Actions",
            width: 220,
            sortable: false,

            renderCell: (params) => (

                <>

                    <IconButton
                        color="primary"
                        onClick={() => onEdit(params.row)}
                    >
                        <EditIcon />
                    </IconButton>

                    {

                        params.row.status === "ACTIVE"

                            ?

                            <IconButton
                                color="warning"
                                onClick={() => onDeactivate(params.row)}
                            >
                                <BlockIcon />
                            </IconButton>

                            :

                            <IconButton
                                color="success"
                                onClick={() => onActivate(params.row)}
                            >
                                <CheckCircleIcon />
                            </IconButton>

                    }

                    <IconButton
                        color="error"
                        onClick={() => onDelete(params.row)}
                    >
                        <DeleteIcon />
                    </IconButton>

                </>

            )

        }

    ];

    return (

        <DataGrid

            rows={studentClasses}

            columns={columns}

            autoHeight

            disableRowSelectionOnClick

            pageSizeOptions={[5,10,20]}

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

export default StudentClassTable;