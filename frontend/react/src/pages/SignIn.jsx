import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Stack, TextField, Button } from '@mui/material';

const SignIn = () => {
  const [credentials, setCredentials] = useState({
    login: '',
    password: '',
  });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setCredentials((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSignIn = () => {
    // todo
    console.log('Sign in ');
  };

  return (
    <>
      <div>
        <h2 className="font-bold text-3xl text-white text-center mt-4 mb-10">
          Sign in to Music-Clouds
        </h2>
      </div>
      <div className="bg-white p-5 rounded-md max-w-[400px] w-full mx-auto">
        <div>
          <Stack className="mt-1 w-full space-y-2">
            <TextField
              label="Email or username"
              name="login"
              variant="standard"
              value={credentials.login}
              onChange={handleChange}
            />
            <TextField
              label="Password"
              name="password"
              type="password"
              variant="standard"
              value={credentials.password}
              onChange={handleChange}
            />
            <Link
              className="underline text-musicCloudsCustomGreen hover:text-green-500 text-right"
              to="/sign-up"
              onClick={() => console.log('Go to recover password')}
            >
              Forgot your password?
            </Link>
            <Button
              variant="contained"
              onClick={handleSignIn}
              style={{ backgroundColor: 'forestgreen', marginTop: 30 }}
            >
              Sign in
            </Button>
            <div className="text-center">
              <div className="mt-8">
                Don't have an account?{'  '}
                <Link
                  className="underline text-musicCloudsCustomGreen hover:text-green-500"
                  to="/sign-up"
                  onClick={() => console.log('Go to sign up')}
                >
                  Sign up
                </Link>
              </div>
            </div>
          </Stack>
        </div>
      </div>
    </>
  );
};

export default SignIn;
