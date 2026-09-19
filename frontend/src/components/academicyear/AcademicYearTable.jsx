import { DataGrid } from "@mui/x-data-grid";
import {
    Chip,
    IconButton,
    Stack,
    Switch
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const AcademicYearTable = ({
    academicYears,
    onEdit,
    onDelete,
    onActivate,
    onDeactivate
}) => {

    const columns = [

        {
            field: "name",
            headerName: "Academic Year",
            flex: 1,
            minWidth: 250
        },

        {
            field: "status",
            headerName: "Status",
            width: 130,

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
            width: 120,
            sortable: false,

            renderCell: (params) => (

                <Stack direction="row">

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

            rows={academicYears}

            columns={columns}

            autoHeight

            disableRowSelectionOnClick

            pageSizeOptions={[5,10,20]}

            initialState={{
                pagination:{
                    paginationModel:{
                        pageSize:10
                    }
                }
            }}

        />

    );

};

export default AcademicYearTable;