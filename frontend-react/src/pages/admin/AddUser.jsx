import React, { useState } from 'react';
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Button,
  TextField,
  Stack,
  Select,
  MenuItem, InputLabel
} from '@mui/material';

function AddUser(props) {
  const [open, setOpen] = useState(false);
  const [user, setUser] = useState({
    firstName: '',
    lastName: '',
    email: '',
    username: '',
    age: '',
    gender: '',
  });

  // Open the modal form
  const handleClickOpen = () => {
    setOpen(true);
  };
  // Close the modal form
  const handleClose = () => {
    setOpen(false);
  };

  // Save user and close modal form
  const handleSave = () => {
    props.addUser(user);
    handleClose();
  };

  function handleChange(event) {
    setUser({
      ...user,
      [event.target.name]:
      event.target.value,
    });
  }

  return (
    <div>
      <div className="flex text-center mt-4 mb-10">
        <div>
          <Button
            variant="contained"
            onClick={handleClickOpen}
          >
            New User
          </Button>
          <Dialog open={open} onClose={handleClose}>
            <DialogTitle>New User</DialogTitle>
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
                  <MenuItem value={"Male"}>Male</MenuItem>
                  <MenuItem value={"Female"}>Female</MenuItem>
                </Select>
              </Stack>
            </DialogContent>
            <DialogActions>
              <Button onClick={handleClose}>Cancel</Button>
              <Button onClick={handleSave}>Save</Button>
            </DialogActions>
          </Dialog>
        </div>
      </div>
    </div>
  );
}

export default AddUser;
