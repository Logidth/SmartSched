import { DataGrid } from "@mui/x-data-grid";

import {
    Chip,
    IconButton,
    Stack,
    Tooltip
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import ToggleOnIcon from "@mui/icons-material/ToggleOn";
import ToggleOffIcon from "@mui/icons-material/ToggleOff";

const RoomTable = ({
    rooms,
    onEdit,
    onDelete,
    onActivate,
    onDeactivate
}) => {

    const columns = [

        {
            field: "roomNumber",
            headerName: "Room",
            width: 120
        },

        {
            field: "blockName",
            headerName: "Block",
            width: 110
        },

        {
            field: "floor",
            headerName: "Floor",
            width: 90
        },

        {
            field: "capacity",
            headerName: "Capacity",
            width: 110
        },

        {
            field: "roomType",
            headerName: "Room Type",
            flex: 1.2
        },

        {
            field: "branchName",
            headerName: "Branch",
            flex: 1.2,

            renderCell: (params) =>
                params.value || "Shared"
        },

        {
            field: "smartRoom",
            headerName: "Smart Room",
            width: 120,

            renderCell: (params) => (

                <Chip
                    label={
                        params.value
                            ? "Yes"
                            : "No"
                    }
                    color={
                        params.value
                            ? "success"
                            : "default"
                    }
                    size="small"
                />

            )
        },

        {
            field: "status",
            headerName: "Status",
            width: 110,

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
            width: 190,
            sortable: false,

            renderCell: (params) => (

                <Stack
                    direction="row"
                    spacing={0.5}
                >

                    <Tooltip title="Edit">

                        <IconButton
                            color="primary"
                            onClick={() =>
                                onEdit(params.row)
                            }
                        >
                            <EditIcon />
                        </IconButton>

                    </Tooltip>

                    {params.row.status === "ACTIVE" ? (

                        <Tooltip title="Deactivate">

                            <IconButton
                                color="warning"
                                onClick={() =>
                                    onDeactivate(params.row)
                                }
                            >
                                <ToggleOffIcon />
                            </IconButton>

                        </Tooltip>

                    ) : (

                        <Tooltip title="Activate">

                            <IconButton
                                color="success"
                                onClick={() =>
                                    onActivate(params.row)
                                }
                            >
                                <ToggleOnIcon />
                            </IconButton>

                        </Tooltip>

                    )}

                    <Tooltip title="Delete">

                        <IconButton
                            color="error"
                            onClick={() =>
                                onDelete(params.row)
                            }
                        >
                            <DeleteIcon />
                        </IconButton>

                    </Tooltip>

                </Stack>

            )
        }

    ];

    return (

        <DataGrid

            rows={rooms}

            columns={columns}

            autoHeight

            disableRowSelectionOnClick

            pageSizeOptions={[
                5,
                10,
                20
            ]}

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

export default RoomTable;