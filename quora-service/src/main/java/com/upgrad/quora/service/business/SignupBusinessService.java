package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * This method is need to be create a new user
     *
     * @param user that need to be sign up
     * @return
     * @throws SignUpRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity user) throws SignUpRestrictedException {
        if(userDao.getUserByUsername(user.getUsername()) != null) {
            throw new SignUpRestrictedException(ValidationErrors.USERNAME_ALREADY_TAKEN.getCode(), ValidationErrors.USERNAME_ALREADY_TAKEN.getReason());
        }
        if(userDao.getUserByEmail(user.getEmail()) != null) {
            throw new SignUpRestrictedException(ValidationErrors.USERNAME_ALREADY_REGISTERED.getCode(), ValidationErrors.USERNAME_ALREADY_REGISTERED.getReason());
        }

        String[] encrytedPassowrd = cryptographyProvider.encrypt(user.getPassword());
        user.setSalt(encrytedPassowrd[0]);
        user.setPassword(encrytedPassowrd[1]);

        return userDao.createUser(user);
    }
}
