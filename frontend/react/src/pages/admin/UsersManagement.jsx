import React, { useEffect, useState } from 'react';
import {
  DataGrid, GridToolbarContainer, GridToolbarExport,
  gridClasses,
} from '@mui/x-data-grid';
import { Stack, Snackbar, IconButton } from '@mui/material/';
import DeleteIcon from '@mui/icons-material/Delete';

// Import server url (named import)
import { SERVER_URL } from '../../assets/constants.js';

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

  const fetchUsers = () => {
    fetch(`${SERVER_URL}api/v1/users`)
      .then((response) => response.json())
      .then((data) => setUsers(data))
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  // Update user
  const updateUser = (user, link) => {
    console.log('UPDATE USER PUT ', user, link);
    fetch(
      link,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user),
      },
    )
      .then((response) => {
        if (response.ok) {
          fetchUsers();
        } else {
          alert('Something went wrong!');
        }
      })
      .catch((err) => console.error(err));
  };

  const onDelClick = (url) => {
    console.log(`DELETE ${url}`);
    if (window.confirm('Are you sure to delete?')) {
      fetch(url, { method: 'DELETE' })
        .then((response) => {
          if (response.ok) {
            fetchUsers();
            setOpen(true);
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
    console.log('ADD USER ', user);
    fetch(
      `${SERVER_URL}api/v1/users/register`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user),
      },
    )
      .then((response) => {
        if (response.ok) {
          fetchUsers();
        } else {
          alert('Something went wrong!');
        }
      })
      .catch((err) => console.error(err));
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
        autoHideDuration={2000}
        onClose={() => setOpen(false)}
        message="User deleted"
      />
    </>
  );
}

export default UsersManagement;
