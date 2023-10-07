import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Stack, TextField, Button, InputAdornment, IconButton, Checkbox, FormControlLabel } from '@mui/material';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import Visibility from '@mui/icons-material/Visibility';
import { useAuth } from '../components/context/AuthContext';
import { errorNotification } from '../services/notification';
import { login } from '../services/client';

const SignIn = () => {
  const [credentials, setCredentials] = useState({
    login: '',
    password: '',
  });

  const [errors, setErrors] = useState({
    login: '',
    password: '',
  });

  const { login } = useAuth(); // Get the login method from the Auth context
  const navigate = useNavigate();

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const [isUnauthorized, setIsUnauthorized] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setIsUnauthorized(false);

    // Validation
    if (name === 'login') {
      if (!validateEmail(value)) {
        setErrors((prev) => ({ ...prev, login: 'Invalid email format.' }));
      } else {
        setErrors((prev) => ({ ...prev, login: '' }));
      }
    }

    if (name === 'password') {
      if (value.length > 20) {
        setErrors((prev) => ({ ...prev, password: 'Password should not exceed 20 characters.' }));
      } else {
        setErrors((prev) => ({ ...prev, password: '' }));
      }
    }

    setCredentials((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSignIn = async () => {
    event.preventDefault(); // Prevents default form submission
    console.log('Attempting Sign in...');

    const { login: email, password } = credentials;

    if (errors.login || errors.password || !credentials.login || !credentials.password) {
      return;
    }

    login({ email, password })
      .then(() => {
        console.log('Successfully logged in user: ', email);
        navigate('/discover'); // Redirect to the desired page after successful login
      })
      .catch((err) => {
        if (err.response && err.response.status === 401) {
          setIsUnauthorized(true);
        } else {
          errorNotification(
            err.code,
            err.response && err.response.data && err.response.data.message,
          );
        }
      });
  };

  const [showPassword, setShowPassword] = useState(false); // State to toggle password visibility

  const handleClickShowPassword = () => {
    setShowPassword((prevShowPassword) => !prevShowPassword);
  };

  const isFormIncomplete = !credentials.login || !credentials.password;

  const [rememberMe, setRememberMe] = useState(false);

  return (
    <>
      <div>
        <h2 className="font-bold text-3xl text-white text-center mt-4 mb-10">
          Sign in to Music-Clouds
        </h2>
      </div>
      <div className="bg-white p-5 rounded-md max-w-[400px] w-full mx-auto">
        <form onSubmit={handleSignIn}>
          <Stack className="mt-1 w-full space-y-2">
            <TextField
              label="Email"
              name="login"
              variant="standard"
              value={credentials.login}
              onChange={handleChange}
              helperText={errors.login}
              error={!!errors.login}
            />
            <TextField
              label="Password"
              name="password"
              variant="standard"
              type={showPassword ? 'text' : 'password'}
              value={credentials.password}
              onChange={handleChange}
              helperText={errors.password}
              error={!!errors.password}
              InputProps={{ // This is the added part for the visibility icon
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowPassword}
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
            <Link
              className="underline text-musicCloudsCustomGreen hover:text-green-500 text-right"
              to="/sign-up"
              onClick={() => console.log('Go to recover password')}
            >
              Forgot your password?
            </Link>
            {isUnauthorized && (
              <div className="text-red-600 mt-2">
                Invalid credentials.
              </div>
            )}
            <FormControlLabel
              control={(
                <Checkbox
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                  name="rememberMe"
                  color="primary"
                />
              )}
              label="Remember Me"
            />
            <Button
              type="submit"
              variant="contained"
              onClick={handleSignIn}
              disabled={isFormIncomplete}
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
        </form>
      </div>
    </>
  );
};

export default SignIn;
