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

const BlockTable = ({
    blocks,
    onEdit,
    onDelete,
    onActivate,
    onDeactivate
}) => {

    const columns = [

        {
            field: "name",
            headerName: "Block",
            flex: 1,
            minWidth: 150
        },

        {
            field: "description",
            headerName: "Description",
            flex: 2,
            minWidth: 250,

            renderCell: (params) =>
                params.value || "-"
        },

        {
            field: "status",
            headerName: "Status",
            width: 130,

            renderCell: (params) => (

                <Chip
                    label={params.value}
                    size="small"
                    color={
                        params.value === "ACTIVE"
                            ? "success"
                            : "error"
                    }
                />

            )
        },

        {
            field: "actions",
            headerName: "Actions",
            width: 180,
            sortable: false,
            filterable: false,

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
                                    onDeactivate(
                                        params.row
                                    )
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
                                    onActivate(
                                        params.row
                                    )
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
            rows={blocks}
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

export default BlockTable;