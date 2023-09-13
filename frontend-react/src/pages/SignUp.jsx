import React from 'react';
import { Button, Stack, TextField } from '@mui/material';

function handleChange() {
  // todo
}

const handleSignUp = () => {
  console.log('Sign up ');
};

const SignUp = () => (
  <>
    <div>
      <h2 className="font-bold text-3xl text-white text-center mt-4 mb-10">
        Sign up for Music-Clouds
      </h2>
    </div>
    <div>
      <div className="bg-white p-5 rounded-md max-w-[400px] w-full mx-auto mt-10">
        <Stack className="mt-10 w-full space-y-2">
          <TextField
            name="First Name"
            label="First Name"
                      // value={user.firstName}
            onChange={handleChange}
          />
          <TextField
            name="Last Name"
            label="Last Name"
                      // value={user.lastName}
            onChange={handleChange}
          />
          <TextField
            name="Username"
            label="Username"
                      // value={user.userName}
            onChange={handleChange}
          />
          <TextField
            name="Email Address"
            label="Email Address"
                      // value={user.email}
            onChange={handleChange}
          />
          <TextField
            name="Create Password"
            label="Create Password"
            type="password"
                      // value={user.password}
            onChange={handleChange}
          />
          <Button
            style={{ backgroundColor: 'forestgreen', marginTop: 50 }}
            variant="contained"
            onClick={handleSignUp}
          >
            Create an account
          </Button>
        </Stack>
      </div>
    </div>
  </>
);

export default SignUp;
