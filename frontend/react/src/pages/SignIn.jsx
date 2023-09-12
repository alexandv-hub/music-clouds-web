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
            <Button
              variant="contained"
              onClick={handleSignIn}
              style={{ backgroundColor: 'forestgreen', marginTop: 50 }}
            >
              Sign in
            </Button>
            <div className="text-center">
              <Link
                href="#"
                className="underline text-musicCloudsCustomGreen hover:text-green-500"
                onClick={() => console.log('Go to recover password')}
                to="/sign-up"
              >
                Forgot your password?
              </Link>
              <div className="mt-8">
                Don't have an account?{'  '}
                <Link
                  href="#"
                  className="underline text-musicCloudsCustomGreen hover:text-green-500"
                  to="/sign-up"
                >
                  Sign up now
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
