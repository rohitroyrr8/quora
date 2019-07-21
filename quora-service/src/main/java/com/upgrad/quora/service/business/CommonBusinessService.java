package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.upgrad.quora.service.util.DateUtils.isBeforeNow;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class CommonBusinessService {
    @Autowired
    private UserDao userDao;

    /**
     * This method is to fetch profile a logged-in user
     *
     * @param uuid of user
     * @param authorization token
     * @return user details
     * @throws UserNotFoundException
     * @throws AuthorizationFailedException
     */
    public UserEntity getUser(String uuid, String authorization) throws UserNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(authorization);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.GET_USER_DETAILS_SIGNED_OUT.getCode(),
                    ValidationErrors.GET_USER_DETAILS_SIGNED_OUT.getReason());
        }

        UserEntity user = userDao.getUser(uuid);
        if(user == null) {
            throw new UserNotFoundException(ValidationErrors.INVALID_USER.getCode(), ValidationErrors.INVALID_USER.getReason());
        }

        return user;
    }

    private void throwErrorIfTokenNotExist(UserAuthTokenEntity authToken) throws AuthorizationFailedException {
        if (isNull(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.USER_NOT_SIGNED_IN.getCode(),
                    ValidationErrors.USER_NOT_SIGNED_IN.getReason());
        }
    }

    private boolean isSignedOut(UserAuthTokenEntity authToken) {
        return nonNull(authToken.getLogoutAt()) || isBeforeNow(authToken.getExpiresAt());
    }
}
