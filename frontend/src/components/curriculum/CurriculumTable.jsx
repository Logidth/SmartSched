import { DataGrid } from "@mui/x-data-grid";

import {
    Chip,
    IconButton
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import BlockIcon from "@mui/icons-material/Block";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import { Tooltip} from "@mui/material";
import { useNavigate } from "react-router-dom";

const CurriculumTable = ({
    curriculums,
    onEdit,
    onDelete,
    onActivate,
    onDeactivate
}) => {
    const navigate = useNavigate();

    const columns = [

        {
            field: "branch",
            headerName: "Branch",
            flex: 1.2
        },

        {
            field: "regulation",
            headerName: "Regulation",
            flex: 1
        },

        {
            field: "academicYear",
            headerName: "Academic Year",
            flex: 1
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
                    <Tooltip title="Manage Subjects">
    <IconButton
        color="primary"
        onClick={() =>
            navigate(`/curriculum/${params.row.id}/subjects`)
        }
    >
        <MenuBookIcon />
    </IconButton>
</Tooltip>

                </>

            )

        }

    ];

    return (

        <DataGrid

            rows={curriculums}

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

export default CurriculumTable;