import React, { useEffect, useState } from 'react';
import {
  DataGrid, GridToolbarContainer, GridToolbarExport,
  gridClasses,
} from '@mui/x-data-grid';
import { Stack, Snackbar, IconButton } from '@mui/material/';
import DeleteIcon from '@mui/icons-material/Delete';

// Import server url (named import)
import { REACT_APP_SERVER_URL } from '../../assets/constants.js';

import AddUser from './AddUser';
import EditUser from './EditUser';

function CustomToolbar() {
  return (
    <GridToolbarContainer
      className={gridClasses.toolbarContainer}
    >
      <GridToolbarExport />
    </GridToolbarContainer>
  );
}

function UsersManagement() {
  const [users, setUsers] = useState([]);
  const [open, setOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');

  const fetchUsers = () => {
    fetch(`${REACT_APP_SERVER_URL}api/v1/users`)
      .then((response) => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then((data) => setUsers(data))
      .catch((error) => {
        console.error('There was a problem with the fetch operation:', error.message);
        alert('Something went wrong!');
      });
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  // Update user
  const updateUser = (user, link) => {
    console.log('starting UPDATE USER PUT... ', user, link);
    fetch(
      link,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user),
      },
    )
      .then((response) => {
        console.log(response);
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then((data) => {
        fetchUsers();
        setOpen(true);
        setSnackbarMessage('User updated');
        console.log('successful UPDATE USER PUT ', user, link);
      })
      .catch((err) => {
        console.error(err);
        alert('Something went wrong!');
      });
  };

  // Delete user
  const onDelClick = (url) => {
    console.log(`starting DELETE... ${url}`);
    if (window.confirm('Are you sure to delete?')) {
      fetch(url, { method: 'DELETE' })
        .then((response) => {
          if (response.ok) {
            console.log(response);
            fetchUsers();
            setOpen(true);
            setSnackbarMessage('User deleted');
            console.log(`successful DELETE ${url}`);
          } else {
            alert('Something went wrong!');
          }
        })
        .catch((err) => console.error(err));
    }
  };

  const columns = [
    { field: 'id', headerName: 'id', width: 80 },
    { field: 'firstName', headerName: 'firstName', width: 150 },
    { field: 'lastName', headerName: 'lastName', width: 150 },
    { field: 'email', headerName: 'email', width: 150 },
    { field: 'username', headerName: 'username', width: 150 },
    {
      field: 'links[0].user.href',
      headerName: '',
      sortable: false,
      filterable: false,
      renderCell: (row) => (
        <EditUser
          data={row}
          updateUser={updateUser}
        />
      ) },
    {
      field: 'links[0].self',
      headerName: '',
      sortable: false,
      filterable: false,

      renderCell: (row) => (
        <IconButton
          onClick={() => onDelClick(row.id)}
        >
          <DeleteIcon color="error" />
        </IconButton>
      ) },
  ];

  // Add a new user
  const addUser = (user) => {
    console.log('starting ADD USER... ', user, 'api/v1/users/register');
    fetch(
      `${REACT_APP_SERVER_URL}api/v1/users/register`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user),
      },
    )
      .then((response) => {
        console.log('response: ', response);
        if (!response.ok) {
          console.error('response: ', response);
          throw new Error('Network response was not ok');
        }

        // Check if response is empty
        if (response.status === 204
            || response.headers.get('content-length') === '0'
            || !response.headers.get('content-type').includes('application/json')) {
          return null;
        }
        return response.json();
      })
      .then((data) => {
        fetchUsers();
        setOpen(true); // open snackbar
        setSnackbarMessage('User created');
        console.log('successful ADD USER ', user, 'api/v1/users/register');
      })
      .catch((err) => {
        console.error(err);
        alert('Something went wrong!');
      });
  };

  return (
    <>
      <div className="flex flex-col">
        <h2 className="font-bold text-3xl text-white text-left mt-4 mb-10">
          Users management
        </h2>
      </div>

      <Stack mt={2} mb={2}>
        <AddUser addUser={addUser} />
      </Stack>

      <div className="font-bold text-xl white-text text-left bg-white">
        <DataGrid
          rows={users}
          columns={columns}
          getRowId={(row) => row.links[0].href}
          components={{ Toolbar: CustomToolbar }}
        />
      </div>
      <Snackbar
        open={open}
        autoHideDuration={3000}
        onClose={() => setOpen(false)}
        message={snackbarMessage}
      />
    </>
  );
}

export default UsersManagement;
