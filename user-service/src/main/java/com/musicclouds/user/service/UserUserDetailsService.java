package com.musicclouds.user.service;

import com.musicclouds.user.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    public UserUserDetailsService(@Qualifier("jpa") UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.info("Starting loadUserByUsername... identifier:" + identifier);

        // Try fetching the user either by username or email. You'll need to adjust this according to your UserDao's methods.
        return userDao.selectUserByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User with identifier " + identifier + " not found"));
    }

}
