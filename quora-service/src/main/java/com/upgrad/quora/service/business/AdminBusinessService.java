package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {
    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity userDelete(final String userId, final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(authorization);
        if(authToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        UserAuthTokenEntity authTokenNew = userDao.getAuthTokenByAccessTokenAndLogout(authorization);
        if(authTokenNew == null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        if(authTokenNew.getUser().getRole() == "non-admin") {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        UserEntity user = userDao.getUser(userId);
        if(user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        userDao.deleteUser(user);
        return user;
    }
}
