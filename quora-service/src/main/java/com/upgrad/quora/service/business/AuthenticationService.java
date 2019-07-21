package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.upgrad.quora.service.util.DateUtils.isBeforeNow;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    /**
     * This method is user to perform login operation for given username and passsword
     *
     * @param username
     * @param password
     * @return auth token for successful login otherwise null
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity user = userDao.getUserByUsername(username);
        if (user == null) {
            throw new AuthenticationFailedException(ValidationErrors.INVALID_USERNAME.getCode(), ValidationErrors.INVALID_USERNAME.getReason());
        }
        final String encryptPassword = PasswordCryptographyProvider.encrypt(password, user.getSalt());
        if (encryptPassword.equals(user.getPassword())) {
            JwtTokenProvider tokenProvider = new JwtTokenProvider(encryptPassword);
            UserAuthTokenEntity authToken = new UserAuthTokenEntity();
            authToken.setUser(user);
            authToken.setUuid(UUID.randomUUID().toString());

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            authToken.setAccessToken(tokenProvider.generateToken(user.getUuid(), now, expiresAt));

            authToken.setLoginAt(now);
            authToken.setExpiresAt(expiresAt);

            userDao.createAuthToken(authToken);
            userDao.updateUser(user);
            return authToken;
        }
        throw new AuthenticationFailedException(ValidationErrors.INVALID_PASSWORD.getCode(), ValidationErrors.INVALID_PASSWORD.getReason());
    }

    /**
     * This method is used to let user to logout
     *
     * @param accessToken :token for a logged-in user
     * @return auth token after successful logout
     *
     * @throws SignOutRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity logout(final String accessToken) throws SignOutRestrictedException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(accessToken);
        throwErrorIfTokenNotExist(authToken);

        authToken.setLogoutAt(ZonedDateTime.now());
        return authToken;

    }

    private void throwErrorIfTokenNotExist(UserAuthTokenEntity authToken) throws SignOutRestrictedException {
        if (isNull(authToken)) {
            throw new SignOutRestrictedException(ValidationErrors.USER_NOT_SIGNED_IN.getCode(),
                    ValidationErrors.USER_NOT_SIGNED_IN.getReason());
        }
    }


}
