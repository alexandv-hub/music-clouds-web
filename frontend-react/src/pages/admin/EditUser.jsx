import React, { useState } from 'react';
import Dialog from '@mui/material/Dialog';
import { DialogActions, DialogContent, DialogTitle, Button, IconButton, Stack, TextField } from '@mui/material/';
import EditIcon from '@mui/icons-material/Edit';

function EditUser(props) {
  const [open, setOpen] = useState(false);
  const [user, setUser] = useState({
    firstName: '', lastName: '', email: '', username: '',
  });

  // Open the modal form and update the user state
  const handleClickOpen = () => {
    setUser({
      firstName: props.data.row.firstName,
      lastName: props.data.row.lastName,
      email: props.data.row.email,
      username: props.data.row.username,
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
    props.updateUser(user, props.data.id);
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
              label="username"
              name="username"
              variant="standard"
              value={user.username}
              onChange={handleChange}
            />
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
