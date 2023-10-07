import axios from 'axios';
import { REACT_APP_SERVER_URL } from '../assets/constants';

const getAuthConfig = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem('access_token')}`,
  },
});

export const getUsers = async () => {
  try {
    return await axios.get(
      `${REACT_APP_SERVER_URL}/api/v1/users`,
      getAuthConfig(),
    );
  } catch (e) {
    throw e;
  }
};

export const saveUser = async (user) => {
  try {
    return await axios.post(
      `${REACT_APP_SERVER_URL}/api/v1/users`,
      user,
    );
  } catch (e) {
    throw e;
  }
};

export const updateUser = async (id, update) => {
  try {
    return await axios.put(
      `${REACT_APP_SERVER_URL}/api/v1/users/${id}`,
      update,
      getAuthConfig(),
    );
  } catch (e) {
    throw e;
  }
};

export const deleteUser = async (id) => {
  try {
    return await axios.delete(
      `${REACT_APP_SERVER_URL}/api/v1/users/${id}`,
      getAuthConfig(),
    );
  } catch (e) {
    throw e;
  }
};

export const login = async (usernameAndPassword) => {
  console.log(usernameAndPassword); // Add this line
  try {
    return await axios.post(
      `${REACT_APP_SERVER_URL}/api/v1/users/auth/login`,
      usernameAndPassword,
    );
  } catch (e) {
    throw e;
  }
};
