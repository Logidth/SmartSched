import { useEffect, useState } from "react";

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  MenuItem,
} from "@mui/material";

import toast from "react-hot-toast";

import api from "../../api/api";

import { createHod } from "../../services/principalService";

const initialForm = {
  username: "",
  password: "",
  name: "",
  employeeId: "",
  email: "",
  phone: "",
  branchId: "",
};

const HodDialog = ({ open, onClose }) => {

  const [branches, setBranches] = useState([]);

  const [form, setForm] = useState(initialForm);

  useEffect(() => {
    if (!open) return;
    loadBranches();
  }, [open]);

  const loadBranches = async () => {
    try {
      const response = await api.get("/branches");
      setBranches(response.data.data);
    } catch {
      toast.error("Unable to load branches");
    }
  };

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async () => {
    try {
      await createHod(form);
      toast.success("Hod Created");
      setForm(initialForm);
      onClose();
    } catch (err) {
      toast.error(
        err.response?.data?.message ||
        "Unable to create Hod"
      );
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle>Create HOD</DialogTitle>

      <DialogContent>
        <TextField
          margin="normal"
          fullWidth
          label="Full Name"
          name="name"
          value={form.name}
          onChange={handleChange}
        />

        <TextField
          margin="normal"
          fullWidth
          label="Employee ID"
          name="employeeId"
          value={form.employeeId}
          onChange={handleChange}
        />

        <TextField
          margin="normal"
          fullWidth
          label="Username"
          name="username"
          value={form.username}
          onChange={handleChange}
        />

        <TextField
          margin="normal"
          fullWidth
          label="Email"
          name="email"
          value={form.email}
          onChange={handleChange}
        />

        <TextField
          margin="normal"
          fullWidth
          label="Phone"
          name="phone"
          value={form.phone}
          onChange={handleChange}
        />

        <TextField
          margin="normal"
          fullWidth
          type="password"
          label="Password"
          name="password"
          value={form.password}
          onChange={handleChange}
        />

        <TextField
          select
          margin="normal"
          fullWidth
          label="Branch"
          name="branchId"
          value={form.branchId}
          onChange={handleChange}
        >
          {branches.map((branch) => (
            <MenuItem key={branch.id} value={branch.id}>
              {branch.name}
            </MenuItem>
          ))}
        </TextField>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button variant="contained" onClick={handleSubmit}>
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default HodDialog;