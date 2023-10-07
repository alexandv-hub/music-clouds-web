import React, { useState } from 'react';
import { Stack, TextField, Button, InputLabel, Select, MenuItem, FormControlLabel, Checkbox, InputAdornment, IconButton } from '@mui/material';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import { useNavigate } from 'react-router-dom'; // Import useNavigate for redirection
import { REACT_APP_SERVER_URL } from '../assets/constants';

const SignUp = () => {
  const navigate = useNavigate(); // Hook for navigation
  const [confirmedTerms, setConfirmedTerms] = useState(false);

  const [showPassword, setShowPassword] = useState(false); // State to toggle password visibility

  const handleClickShowPassword = () => {
    setShowPassword((prevShowPassword) => !prevShowPassword);
  };

  const [userDetails, setUserDetails] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    username: '',
    age: '',
    gender: '',
    role: 'USER',
  });

  const [errors, setErrors] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    username: '',
    age: '',
    gender: '',
    role: 'USER',
  });

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    if (name === 'email') {
      if (!validateEmail(value)) {
        setErrors((prev) => ({ ...prev, email: 'Invalid email format.' }));
      } else {
        setErrors((prev) => ({ ...prev, email: '' }));
      }
    }

    if (name === 'password') {
      if (value.length > 20) {
        setErrors((prev) => ({ ...prev, password: 'Password should not exceed 20 characters.' }));
      } else {
        setErrors((prev) => ({ ...prev, password: '' }));
      }
    }

    setUserDetails((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  // Register a new user
  const registerUser = (user) => {
    console.log('starting registerUser... ', user, '/api/v1/users/auth/register');
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
        console.log('Successfully registered User: ', user, 'api/v1/users/auth/register');
        navigate('/sign-in'); // Redirect to sign-in page after successful registration
      })
      .catch((err) => {
        console.error(err);
        alert('Something went wrong!');
      });
  };

  const handleSignUp = (event) => {
    event.preventDefault();
    console.log('Attempting Sign up...');

    // TODO: Your sign up logic here
    registerUser(userDetails);
  };

  return (
    <>
      <div>
        <h2 className="font-bold text-3xl text-white text-center mt-4 mb-10">
          Sign up for Music-Clouds
        </h2>
      </div>
      <div className="bg-white p-5 rounded-md max-w-[400px] w-full mx-auto">
        <form onSubmit={handleSignUp}>
          <Stack className="mt-1 w-full space-y-4">
            <TextField
              label="First Name"
              name="firstName"
              value={userDetails.firstName}
              onChange={handleChange}
              helperText={errors.firstName}
              error={!!errors.firstName}
            />
            <TextField
              label="Last Name"
              name="lastName"
              value={userDetails.lastName}
              onChange={handleChange}
              helperText={errors.lastName}
              error={!!errors.lastName}
            />
            <TextField
              label="Username"
              name="username"
              value={userDetails.username}
              onChange={handleChange}
              helperText={errors.username}
              error={!!errors.username}
            />
            <TextField
              label="Email Address"
              name="email"
              value={userDetails.email}
              onChange={handleChange}
              helperText={errors.email}
              error={!!errors.email}
            />
            <TextField
              label="Password"
              name="password"
              type={showPassword ? 'text' : 'password'}
              value={userDetails.password}
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
            <TextField
              label="Age"
              name="age"
              type="number"
              value={userDetails.age}
              onChange={handleChange}
              helperText={errors.age}
              error={!!errors.age}
              inputProps={{
                min: 16,
                // Uncomment the next line if you wish to have an upper limit
                max: 130,
              }}
            />
            <InputLabel id="gender-label">Gender</InputLabel>
            <Select
              labelId="gender-label"
              label="Gender"
              name="gender"
              value={userDetails.gender}
              onChange={handleChange}
              error={!!errors.gender}
              helperText={errors.gender}
            >
              <MenuItem value="MALE">Male</MenuItem>
              <MenuItem value="FEMALE">Female</MenuItem>
            </Select>
            <FormControlLabel
              control={(
                <Checkbox
                  checked={confirmedTerms}
                  onChange={(e) => setConfirmedTerms(e.target.checked)}
                  color="primary"
                />
              )}
              label="I agree to the terms and conditions"
            />
            <Button
              type="submit"
              variant="contained"
              style={{ backgroundColor: 'forestgreen', marginTop: 30 }}
              disabled={!confirmedTerms} // disable button if terms not confirmed
            >
              Sign up
            </Button>
          </Stack>
        </form>
      </div>
    </>
  );
};

export default SignUp;
