package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {
    @Autowired
    UserDao userDao;

    /**
     * This method is used to delete a user for given user uuid
     *
     * @param userId : uuid of a user which need to be deleted
     * @param authorization : token for a logged-in user
     * @return user which is just deleted
     *
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity userDelete(final String userId, final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(authorization);
        if(authToken == null) {
            throw new AuthorizationFailedException(ValidationErrors.USER_NOT_SIGNED_IN.getCode(), ValidationErrors.USER_NOT_SIGNED_IN.getReason());
        }

        UserAuthTokenEntity authTokenNew = userDao.getAuthTokenByAccessTokenAndLogout(authorization);
        if(authTokenNew == null) {
            throw new AuthorizationFailedException(ValidationErrors.GET_USER_SIGNED_OUT.getCode(), ValidationErrors.GET_USER_SIGNED_OUT.getReason());
        }
        //to check if given user is admin or not
        if(!isAdmin(authTokenNew)){
            throw new AuthorizationFailedException(ValidationErrors.USER_NOT_ADMIN.getCode(), ValidationErrors.USER_NOT_ADMIN.getReason());
        }

        UserEntity user = userDao.getUser(userId);
        if(user == null) {
            throw new UserNotFoundException(ValidationErrors.ADMIN_DELETE_USER_NOT_EXISTS.getCode(), ValidationErrors.ADMIN_DELETE_USER_NOT_EXISTS.getReason());
        }

        userDao.deleteUser(user);
        return user;
    }

    public boolean isAdmin(UserAuthTokenEntity authToken) throws AuthorizationFailedException {
        if(authToken.getUser().getRole() == "non-admin") {
            return false;
        }
        return true;
    }
}
