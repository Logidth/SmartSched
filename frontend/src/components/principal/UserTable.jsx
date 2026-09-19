import {
  Paper,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Chip,
  IconButton,
  Tooltip,
} from "@mui/material";

import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";
import LockResetIcon from "@mui/icons-material/LockReset";

const UserTable = ({
  users,
  onEnable,
  onDisable,
  onReset,
}) => {
  return (
    <Paper elevation={3} sx={{ mt: 3 }}>

      <Table>

        <TableHead>

          <TableRow>

            <TableCell>Username</TableCell>

            <TableCell>Role</TableCell>

            <TableCell>Branch</TableCell>

            <TableCell>Status</TableCell>

            <TableCell>First Login</TableCell>

            <TableCell align="center">

              Actions

            </TableCell>

          </TableRow>

        </TableHead>

        <TableBody>

          {(users || []).map((user) => (

            <TableRow key={user.id} hover>

              <TableCell>{user.username}</TableCell>

              <TableCell>{user.role}</TableCell>

              <TableCell>

                {user.branch || "-"}

              </TableCell>

              <TableCell>

                <Chip

                  color={user.active ? "success" : "error"}

                  label={user.active ? "Active" : "Disabled"}

                />

              </TableCell>

              <TableCell>

                <Chip

                  color={user.firstLogin ? "warning" : "primary"}

                  label={user.firstLogin ? "YES" : "NO"}

                />

              </TableCell>

              <TableCell align="center">

                <Tooltip title="Enable">

                  <IconButton
                    color="success"
                    onClick={() => onEnable(user.id)}
                  >

                    <CheckCircleIcon />

                  </IconButton>

                </Tooltip>

                <Tooltip title="Disable">

                  <IconButton
                    color="error"
                    onClick={() => onDisable(user.id)}
                  >

                    <CancelIcon />

                  </IconButton>

                </Tooltip>

                <Tooltip title="Reset Password">

                  <IconButton
                    color="primary"
                    onClick={() => onReset(user.id)}
                  >

                    <LockResetIcon />

                  </IconButton>

                </Tooltip>

              </TableCell>

            </TableRow>

          ))}

        </TableBody>

      </Table>

    </Paper>
  );
};

export default UserTable;