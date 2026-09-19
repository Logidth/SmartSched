import { useState } from "react";
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { useAuth } from "../../context/AuthContext";

const Login = () => {

  const { login } = useAuth();

  const navigate = useNavigate();

  const [username, setUsername] = useState("");

  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      setLoading(true);

      const response = await login(username, password);

      toast.success("Login Successful");

      if (response.firstLogin) {

        navigate("/change-password");

        return;

      }

      switch (response.role) {

        case "PRINCIPAL":
          navigate("/principal/dashboard");
          break;

        case "HOD":
          navigate("/hod/dashboard");
          break;

        case "FACULTY":
          navigate("/faculty/dashboard");
          break;

        case "ADMIN":
          navigate("/admin/dashboard");
          break;

        default:
          navigate("/");
      }

    } catch (err) {

      toast.error("Invalid Username or Password");

    } finally {

      setLoading(false);

    }

  };

  return (

    <Box
    sx={{
        minHeight: "100vh",
        width: "100vw",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#f4f6f8",
        overflow: "hidden"
    }}
>
      <Paper
        elevation={6}
        sx={{
          width: 400,
          p: 4,
          borderRadius: 3,
        }}
      >

        <Typography
          variant="h4"
          align="center"
          mb={3}
        >
          SmartSched
        </Typography>

        <form onSubmit={handleSubmit}>

          <TextField
            label="Username"
            fullWidth
            margin="normal"
            value={username}
            onChange={(e) =>
              setUsername(e.target.value)
            }
          />

          <TextField
            label="Password"
            type="password"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) =>
              setPassword(e.target.value)
            }
          />

          <Button
            type="submit"
            variant="contained"
            fullWidth
            sx={{ mt: 3 }}
            disabled={loading}
          >
            {loading ? "Signing In..." : "Login"}
          </Button>

        </form>

      </Paper>

    </Box>

  );

};

export default Login;