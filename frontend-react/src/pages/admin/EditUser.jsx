import React, { useState } from 'react';
import Dialog from '@mui/material/Dialog';
import { DialogActions, DialogContent, DialogTitle, Button, IconButton, Stack, TextField, InputLabel, MenuItem, Select } from '@mui/material/';
import EditIcon from '@mui/icons-material/Edit';

function EditUser({ data, updateUser }) {
  const [open, setOpen] = useState(false);
  const [user, setUser] = useState({
    firstName: '', lastName: '', email: '', password: '', username: '', age: '', gender: '', role: '',
  });

  // Open the modal form and update the user state
  const handleClickOpen = () => {
    setUser({
      firstName: data.row.firstName,
      lastName: data.row.lastName,
      email: data.row.email,
      password: data.row.password,
      username: data.row.username,
      age: data.row.age,
      gender: data.row.gender,
      role: data.row.role,
    });
    setOpen(true);
  };
  // Close the modal form
  const handleClose = () => {
    setOpen(false);
  };
  const handleChange = (event) => {
    setUser({
      ...user,
      [event.target.name]: event.target.value,
    });
  };
  // Update user and close modal form
  const handleSave = () => {
    updateUser(user, data.id);
    handleClose();
  };

  return (
    <div>
      <IconButton onClick={handleClickOpen}>
        <EditIcon color="primary" />
      </IconButton>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Edit User</DialogTitle>
        <DialogContent>
          <Stack spacing={2} mt={1}>
            <TextField
              label="First Name"
              name="firstName"
              autoFocus
              variant="standard"
              value={user.firstName}
              onChange={handleChange}
            />
            <TextField
              label="Last Name"
              name="lastName"
              variant="standard"
              value={user.lastName}
              onChange={handleChange}
            />
            <TextField
              label="email"
              name="email"
              variant="standard"
              value={user.email}
              onChange={handleChange}
            />
            <TextField
              label="password"
              name="password"
              variant="standard"
              value={user.password}
              onChange={handleChange}
            />
            <TextField
              label="username"
              name="username"
              variant="standard"
              value={user.username}
              onChange={handleChange}
            />
            <TextField
              label="age"
              name="age"
              variant="standard"
              value={user.age}
              onChange={handleChange}
            />
            <InputLabel id="gender-label">Gender</InputLabel>
            <Select
              labelId="gender-label"
              label="Gender"
              name="gender"
              value={user.gender}
              onChange={handleChange}
            >
              <MenuItem value="MALE">MALE</MenuItem>
              <MenuItem value="FEMALE">FEMALE</MenuItem>
            </Select>
            <InputLabel id="role-label">Role</InputLabel>
            <Select
              labelId="role-label"
              label="Role"
              name="role"
              value={user.role}
              onChange={handleChange}
            >
              <MenuItem value="ADMIN">ADMIN</MenuItem>
              <MenuItem value="MANAGER">MANAGER</MenuItem>
              <MenuItem value="USER">USER</MenuItem>
            </Select>
          </Stack>
          <br />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}> Cancel</Button>
          <Button onClick={handleSave}>Save</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default EditUser;
