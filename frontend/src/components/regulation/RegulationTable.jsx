import { DataGrid } from "@mui/x-data-grid";
import {
    Chip,
    IconButton,
    Switch,
    Stack
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const RegulationTable = ({
    regulations,
    onEdit,
    onDelete,
    onActivate,
    onDeactivate
}) => {

    const columns = [

        {
            field: "code",
            headerName: "Code",
            width: 180
        },

        {
            field: "description",
            headerName: "Description",
            flex: 1,
            minWidth: 350
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
            width: 120,
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

            rows={regulations}

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

export default RegulationTable;