package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonBusinessService {
    @Autowired
    private UserDao userDao;


    public UserEntity getUser(String uuid, String authorization) throws UserNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(authorization);
        if(authToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        UserAuthTokenEntity authTokenNew = userDao.getAuthTokenByAccessTokenAndLogout(authorization);
        if(authTokenNew == null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        UserEntity user = userDao.getUser(uuid);
        if(user == null) {
            throw new UserNotFoundException("USER-001", "User with entered uuid does not exist");
        }

        return user;
    }
}
