import { DataGrid } from "@mui/x-data-grid";
import {
    Chip,
    IconButton,
    Switch,
    Stack
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const SubjectTable = ({
    subjects,
    onEdit,
    onDelete,
    onActivate,
    onDeactivate
}) => {

    const columns = [

        {
            field: "subjectCode",
            headerName: "Code",
            width: 120
        },

        {
            field: "subjectName",
            headerName: "Subject Name",
            flex: 1,
            minWidth: 220
        },

        {
            field: "regulation",
            headerName: "Regulation",
            width: 170
        },

        {
            field: "credits",
            headerName: "Credits",
            width: 90
        },

        {
            field: "theoryHours",
            headerName: "Theory",
            width: 90
        },

        {
            field: "labHours",
            headerName: "Lab",
            width: 80
        },

        {
            field: "totalHours",
            headerName: "Total",
            width: 90
        },

        {
            field: "hoursPerWeek",
            headerName: "Hrs/Week",
            width: 100
        },

        {
            field: "subjectType",
            headerName: "Type",
            width: 150,

            renderCell: (params) => (

                <Chip
                    size="small"
                    label={params.value.replaceAll("_", " ")}
                    color="primary"
                />

            )

        },

        {
            field: "subjectCategory",
            headerName: "Category",
            width: 220,

            renderCell: (params) => (

                <Chip
                    size="small"
                    label={params.value.replaceAll("_", " ")}
                    color="secondary"
                />

            )

        },

        {
            field: "requiresLabRoom",
            headerName: "Lab Required",
            width: 130,

            renderCell: (params) => (

                <Chip
                    size="small"
                    label={params.value ? "YES" : "NO"}
                    color={params.value ? "warning" : "default"}
                />

            )

        },

        {
            field: "status",
            headerName: "Status",
            width: 120,

            renderCell: (params) => (

                <Chip
                    size="small"
                    label={params.value}
                    color={
                        params.value === "ACTIVE"
                            ? "success"
                            : "error"
                    }
                />

            )

        },

        {
            field: "active",
            headerName: "Enable",
            width: 110,
            sortable: false,

            renderCell: (params) => (

                <Switch

                    checked={params.row.status === "ACTIVE"}

                    onChange={(e) => {

                        if (e.target.checked) {

                            onActivate(params.row);

                        } else {

                            onDeactivate(params.row);

                        }

                    }}

                />

            )

        },

        {
            field: "actions",
            headerName: "Actions",
            width: 130,
            sortable: false,

            renderCell: (params) => (

                <Stack
                    direction="row"
                    spacing={1}
                >

                    <IconButton
                        color="primary"
                        onClick={() => onEdit(params.row)}
                    >
                        <EditIcon />
                    </IconButton>

                    <IconButton
                        color="error"
                        onClick={() => onDelete(params.row)}
                    >
                        <DeleteIcon />
                    </IconButton>

                </Stack>

            )

        }

    ];

    return (

        <DataGrid

            rows={subjects}

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

export default SubjectTable;