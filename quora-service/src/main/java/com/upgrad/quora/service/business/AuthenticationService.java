package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity user = userDao.getUserByUsername(username);
        if(user == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");

        }
        final String encryptPassword = PasswordCryptographyProvider.encrypt(password, user.getSalt());
        if(encryptPassword.equals(user.getPassword())) {
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
        throw new AuthenticationFailedException("ATH-002", "Password failed");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity logout(final String accessToken) throws SignOutRestrictedException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(accessToken);
        if(authToken == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        authToken.setLogoutAt(ZonedDateTime.now());
        return authToken;

    }
}
