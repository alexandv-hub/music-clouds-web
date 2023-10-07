import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { HiOutlineMenu } from 'react-icons/hi';
import { RiCloseLine } from 'react-icons/ri';

import { logo } from '../assets';
import { links } from '../assets/constants';

import { useAuth } from './context/AuthContext';

const NavLinks = ({ handleClick }) => {
  const { user, logOut } = useAuth();

  // Handle actions when a navigation link is clicked
  const handleNavLinkClick = (shouldLogOut) => {
    // If the link is for 'Logout', execute the logout action
    if (shouldLogOut) {
      logOut();
    }
    // If an additional handleClick function is provided, execute it
    if (typeof handleClick === 'function') {
      handleClick();
    }
  };

  return (
    <div className="mt-10">
      {links.map((item) => {
        // Check if the user has an admin role
        const isUserAdmin = user && user.roles && user.roles.includes('ROLE_ADMIN');

        // Conditions for not rendering the link
        // 1. Hide 'Users management' link for non-admin users
        // 2. Hide 'Sign in' link for authenticated users
        // 3. Hide 'Logout', 'My Tracks', and 'My Friends' links for unauthenticated users
        if (
          (item.name === 'Users management' && !isUserAdmin)
          || (item.name === 'Sign in' && user)
          || (['Logout', 'My Tracks', 'My Friends'].includes(item.name) && !user)
        ) {
          return null;
        }

        // Determine if the link requires logging out the user
        const shouldLogOut = item.name === 'Logout';

        // Render the navigation link
        return (
          <NavLink
            key={item.name}
            to={item.to}
            className="flex flex-row justify-start items-center my-8 text-sm font-medium text-gray-400"
            onClick={() => handleNavLinkClick(shouldLogOut)}
          >
            <item.icon className="w-6 h-6 mr-2" />
            {item.name}
          </NavLink>
        );
      })}
    </div>
  );
};

const Sidebar = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { user } = useAuth(); // Assuming the user object contains a role property, adjust as needed

  return (
    <>
      <div className="md:flex hidden flex-col w-[240px] py-10 px-4 bg-[#474451]">
        <NavLink to="/discover">
          <img src={logo} alt="logo" className="w-full h-20 object-contain cursor-pointer" />
        </NavLink>        <NavLinks userRole={user?.role} />
      </div>

      {/* Mobile sidebar */}
      <div className="absolute md:hidden block top-6 right-3">
        {!mobileMenuOpen ? (
          <HiOutlineMenu className="w-6 h-6 mr-2 text-white" onClick={() => setMobileMenuOpen(true)} />
        ) : (
          <RiCloseLine className="w-6 h-6 mr-2 text-white" onClick={() => setMobileMenuOpen(false)} />
        )}
      </div>
      <div className={`absolute top-0 h-screen w-2/3 bg-gradient-to-tl from-white/10 to-[#483D8B] backdrop-blur-lg z-10 p-6 md:hidden 
                      smooth-transition ${mobileMenuOpen ? 'left-0' : '-left-full'}`}
      >
        <img src={logo} alt="logo" className="w-full h-`````` object-contain" />
        <NavLinks handleClick={() => setMobileMenuOpen(false)} userRole={user?.role} />
      </div>
    </>
  );
};

export default Sidebar;
