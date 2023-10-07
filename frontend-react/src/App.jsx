import { useSelector } from 'react-redux';
import { Navigate, Route, Routes, useLocation } from 'react-router-dom';

import React, { useEffect, useState } from 'react';
import jwtDecode from 'jwt-decode';
import { Searchbar, Sidebar, MusicPlayer, TopPlay } from './components';
import { ArtistDetails, TopArtists, AroundYou, Discover, Search, SongDetails, TopCharts, SignUp } from './pages';
import UsersManagement from './pages/admin/UsersManagement';
import SignIn from './pages/SignIn';
import MyTracks from './components/MyTracks';

function TestComponent() {
  return <div>Test</div>;
}

function NotFoundComponent() {
  return (
    <div>
      <h2>Error 404</h2>
      <p>Page not found!</p>
    </div>
  );
}

function decodeToken(jwtToken) {
  try {
    const decodedToken = jwtDecode(jwtToken);
    console.log('decodedToken: ', decodedToken);
    if (decodedToken.roles) {
      return (decodedToken.roles && decodedToken.roles[0]) || 'ROLE_USER';
    }
    console.log("No userRole extracted from decodedToken. Setting up default 'ROLE_USER'");
    return 'ROLE_USER';
  } catch (error) {
    console.error('Failed to decode the authJwtToken:', error.message);
    // This is where you might handle the error, e.g., by redirecting to a SignIn page
    // For now, just return a null user role which can be checked later
    return null;
  }
}

function ProtectedRoute({ token, children, protectedPaths = [], authorizedRoles = [], nonAuthenticatedPath }) {
  console.log("Starting function ProtectedRoute...'");
  const location = useLocation(); // <-- Use the hook here

  // if (!token && protectedPaths.includes(location.pathname)) {
  //   return <Navigate to={nonAuthenticatedPath} />;
  // }

  const userRole = decodeToken(token);
  console.log('userRole from decoded token: ', userRole);

  // If no user role is returned, it means token was invalid or missing
  if (!userRole) {
    console.info('Invalid or missing token. Redirecting to SignIn page...');
    return <SignIn />;
  }

  // If user is authenticated and trying to access a non-authenticated path
  if (nonAuthenticatedPath && protectedPaths.includes(location.pathname)) {
    console.info(`Authenticated user trying to access ${location.pathname}. Redirecting to ${nonAuthenticatedPath}...`);
    return <Navigate to={nonAuthenticatedPath} />;
  }

  console.log('authorizedRoles: ', authorizedRoles);
  const modifiedAuthorizedRoles = authorizedRoles.map((role) => `ROLE_${role}`);
  console.log('modifiedAuthorizedRoles: ', modifiedAuthorizedRoles);

  if (!protectedPaths.includes(location.pathname) || modifiedAuthorizedRoles.includes(userRole)) {
    return children;
  }

  // If it's a protected path and there's no valid user role, render NotFoundComponent.
  console.info('Redirecting to NotFoundComponent page...');
  return <NotFoundComponent />;
}

const App = () => {
  const authJwtToken = localStorage.getItem('access_token');
  const { activeSong } = useSelector((state) => state.player);
  const location = useLocation();
  const [isUsersManagementActive, setIsUsersManagementActive] = useState(false);

  useEffect(() => {
    if (location.pathname === '/users-management'
        || location.pathname === '/sign-in'
        || location.pathname === '/sign-up') {
      setIsUsersManagementActive(true);
    } else {
      setIsUsersManagementActive(false);
    }
  }, [location]);

  return (
    <div className="relative flex">
      <Sidebar />
      <div className="flex-1 flex flex-col bg-gradient-to-br from-black to-[#434355]">

        {!isUsersManagementActive && (
        <div className="xl:sticky relative top-0 h-fit">
          <Searchbar />
        </div>
        )}

        <div className="px-6 h-[calc(100vh-72px)] overflow-y-scroll hide-scrollbar flex xl:flex-row flex-col-reverse">
          <div className="flex-1 h-fit pb-40">
            <Routes>
              <Route
                path="/"
                element={(
                  <ProtectedRoute
                    token={authJwtToken}
                    nonAuthenticatedPath="/sign-in"
                    protectedPaths={['/']}
                  >
                    <Discover />
                  </ProtectedRoute>
                )}
              />
              <Route path="/discover" element={<Discover />} />
              <Route path="/top-artists" element={<TopArtists />} />
              <Route path="/top-charts" element={<TopCharts />} />
              <Route path="/around-you" element={<AroundYou />} />
              <Route path="/artists/:id" element={<ArtistDetails />} />
              <Route path="/songs/:songid" element={<SongDetails />} />
              <Route path="/search/:searchTerm" element={<Search />} />

              <Route
                path="/users-management"
                element={(
                  <ProtectedRoute token={authJwtToken} authorizedRoles={['ADMIN']} protectedPaths={['/users-management']}>
                    <UsersManagement />
                  </ProtectedRoute>
                )}
              />

              <Route
                path="/sign-in"
                element={(
                  <ProtectedRoute token={authJwtToken} nonAuthenticatedPath="/discover" protectedPaths={['/sign-in']}>
                    <SignIn />
                  </ProtectedRoute>
                )}
              />

              <Route path="/sign-up" element={<SignUp />} />
              <Route path="/my-tracks" element={<MyTracks />} />
              <Route path="*" element={<NotFoundComponent />} />
            </Routes>
          </div>

          {!isUsersManagementActive && (
          <div className="xl:sticky relative top-0 h-fit">
            <TopPlay />
          </div>
          )}
        </div>
      </div>

      {activeSong?.title && (
        <div className="absolute h-28 bottom-0 left-0 right-0 flex animate-slideup bg-gradient-to-br from-white/10 to-[#2a2a80] backdrop-blur-lg rounded-t-3xl z-10">
          <MusicPlayer />
        </div>
      )}
    </div>
  );
};

export default App;
