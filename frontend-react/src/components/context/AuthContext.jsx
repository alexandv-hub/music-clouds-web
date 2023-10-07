import {
  createContext,
  useContext,
  useEffect,
  useState,
} from 'react';
import jwtDecode from 'jwt-decode';
import { login as performLogin } from '../../services/client.js';

const AuthContext = createContext({});

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);

  useEffect(() => {
    let token = localStorage.getItem('access_token');
    if (token) {
      console.log('Token to be decoded:', token);
      try {
        token = jwtDecode(token);
      } catch (error) {
        console.error('Failed to decode the token:', error.message);
      }
      setUser({
        username: token.sub,
        roles: token.roles,
      });
      console.log('user from decoded jwt: ', user);
    } else {
      console.warn('No token provided for decoding.');
    }
  }, []);

  const login = async (usernameAndPassword) => {
    console.log(usernameAndPassword);

    return new Promise((resolve, reject) => {
      performLogin(usernameAndPassword).then((res) => {
        const jwtToken = res.data.accessToken; // Extract accessToken from response data
        localStorage.setItem('access_token', jwtToken);

        const decodedToken = jwtDecode(jwtToken);
        setUser({
          username: decodedToken.sub,
          roles: decodedToken.roles,
        });
        console.log('user: ', user);
        resolve(res);
      }).catch((err) => {
        reject(err);
      });
    });
  };

  const logOut = () => {
    localStorage.removeItem('access_token');
    setUser(null);
  };

  const isUserAuthenticated = () => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      return false;
    }
    const { exp: expiration } = jwtDecode(token);
    if (Date.now() > expiration * 1000) {
      logOut();
      return false;
    }
    return true;
  };

  return (
    <AuthContext.Provider value={{
      user,
      login,
      logOut,
      isUserAuthenticated,
    }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
