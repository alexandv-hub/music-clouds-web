import React, { useEffect, useState } from 'react';
import {
  DataGrid, GridToolbarContainer, GridToolbarExport,
  gridClasses,
} from '@mui/x-data-grid';
import { Stack, Snackbar, IconButton } from '@mui/material/';
import DeleteIcon from '@mui/icons-material/Delete';

// Import server url (named import)
import { REACT_APP_SERVER_URL } from '../../assets/constants.js';
import { getUsers, deleteUser, updateUser } from '../../services/client.js';

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

  const fetchUsers = async () => {
    try {
      const data = await getUsers();
      setUsers(data.data); // axios wraps the response data in a .data property
    } catch (error) {
      console.error('There was a problem with the fetch operation:', error.message);
      alert('Something went wrong!');
    }
  };

  useEffect(() => {
    (async () => {
      try {
        const data = await getUsers();
        setUsers(data.data); // axios wraps the response data in a .data property
      } catch (error) {
        console.error('There was a problem with the fetch operation:', error.message);
        alert('Something went wrong!');
      }
    })();
  }, []);

  // Update user
  const updateUserFunc = async (user, url) => {
    console.log('starting UPDATE USER PUT... ', user, url);
    console.log('Type of URL:', typeof url, 'Value:', url);

    // Extract the user ID from the URL. This assumes the URL is of the format ".../api/v1/users/<ID>"
    const userId = url.split('/').pop();

    try {
      const response = await updateUser(userId, user);

      // Check if the PUT operation was successful using axios response status
      if (response.status === 200) {
        console.log(response);
        await fetchUsers();
        setOpen(true);
        setSnackbarMessage('User updated');
        console.log('successful UPDATE USER PUT ', user, url);
      } else {
        alert('Something went wrong!');
      }
    } catch (error) {
      console.error('There was an error with the UPDATE operation:', error.message);
      alert('Something went wrong!');
    }
  };

  // Delete user
  const onDelClick = async (url) => {
    console.log(`starting DELETE... ${url}`);

    // Extract the user ID from the URL. This assumes the URL is of the format ".../api/v1/users/<ID>"
    const userId = url.split('/').pop();

    if (window.confirm('Are you sure to delete?')) {
      try {
        const response = await deleteUser(userId);

        // Check if the DELETE operation was successful using axios response status
        if (response.status === 200) {
          console.log(response);
          await fetchUsers();
          setOpen(true);
          setSnackbarMessage('User deleted');
          console.log(`successful DELETE ${url}`);
        } else {
          alert('Something went wrong!');
        }
      } catch (error) {
        console.error('There was an error with the DELETE operation:', error.message);
        alert('Something went wrong!');
      }
    }
  };

  const columns = [
    { field: 'id', headerName: 'id', width: 80 },
    { field: 'firstName', headerName: 'firstName', width: 150 },
    { field: 'lastName', headerName: 'lastName', width: 150 },
    { field: 'email', headerName: 'email', width: 150 },
    { field: 'password', headerName: 'password', width: 150 },
    { field: 'username', headerName: 'username', width: 150 },
    { field: 'age', headerName: 'age', width: 80 },
    { field: 'gender', headerName: 'gender', width: 80 },
    { field: 'role', headerName: 'role', width: 100 },
    {
      field: 'links[0].user.href',
      headerName: '',
      width: 50,
      sortable: false,
      filterable: false,
      renderCell: (row) => (
        <EditUser
          data={row}
          updateUser={updateUserFunc}
        />
      ) },
    {
      field: 'links[0].self',
      headerName: '',
      width: 70,
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
    console.log('starting ADD USER... ', user, '/api/v1/users/auth/register');
    fetch(
      `${REACT_APP_SERVER_URL}/api/v1/users/auth/register`,
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
      .then(() => {
        fetchUsers();
        setOpen(true); // open snackbar
        setSnackbarMessage('User created');
        console.log('successful ADD USER ', user, 'api/v1/users/auth/register');
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
