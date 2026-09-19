import { DataGrid } from "@mui/x-data-grid";
import {
    Chip,
    IconButton
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import BlockIcon from "@mui/icons-material/Block";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import VpnKeyIcon from "@mui/icons-material/VpnKey";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import Tooltip from "@mui/material/Tooltip";

const FacultyTable = ({
    faculties,
    onEdit,
    onActivate,
    onDeactivate,
    onSetCredentials
}) =>  {

    const columns = [

        {
            field: "employeeId",
            headerName: "Employee ID",
            width: 130
        },

        {
            field: "name",
            headerName: "Name",
            flex: 1
        },

        {
            field: "username",
            headerName: "Username",
            width: 150,
            renderCell: (params) => params.value || "—"
        },

        {
            field: "branchName",
            headerName: "Branch",
            width: 230
        },

        {
            field: "designation",
            headerName: "Designation",
            width: 190
        },

        {
            field: "maxWeeklyHours",
            headerName: "Hours",
            width: 100
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

            {onSetCredentials && (

                <Tooltip
                    title={
                        params.row.username
                            ? "Reset Password"
                            : "Create Login"
                    }
                >

                    <IconButton
                        color={params.row.username ? "default" : "secondary"}
                        onClick={() => onSetCredentials(params.row)}
                    >
                        {params.row.username
                            ? <VpnKeyIcon />
                            : <PersonAddIcon />}
                    </IconButton>

                </Tooltip>

            )}

            {params.row.status === "ACTIVE" ? (

                <IconButton
                    color="warning"
                    onClick={() => onDeactivate(params.row)}
                >
                    <BlockIcon />
                </IconButton>

            ) : (

                <IconButton
                    color="success"
                    onClick={() => onActivate(params.row)}
                >
                    <CheckCircleIcon />
                </IconButton>

            )}

        </>

    )

}

    ];

    return (

        <DataGrid

            rows={faculties}

            columns={columns}

            pageSizeOptions={[5,10,20]}

            initialState={{
                pagination: {
                    paginationModel: {
                        pageSize: 10
                    }
                }
            }}

            disableRowSelectionOnClick

            autoHeight

        />

    );

};

export default FacultyTable;