package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.upgrad.quora.service.util.DateUtils.isBeforeNow;
import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;

@Service
public class QuestionService {

    private final QuestionDao questionDao;
    private final UserDao userDao;

    @Autowired
    public QuestionService(QuestionDao questionDao, UserDao userDao) {
        this.questionDao = questionDao;
        this.userDao = userDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity create(QuestionEntity question, String token) throws AuthorizationFailedException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        if (isNull(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.USER_NOT_SIGNED_IN.getCode(),
                    ValidationErrors.USER_NOT_SIGNED_IN.getReason());
        }
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.POST_A_QUESTION_SIGNED_OUT.getCode(),
                    ValidationErrors.POST_A_QUESTION_SIGNED_OUT.getReason());
        }
        question.setUser(authToken.getUser());
        return questionDao.createQuestion(question);
    }

    private boolean isSignedOut(UserAuthTokenEntity authToken) {
        return nonNull(authToken.getLogoutAt()) || isBeforeNow(authToken.getExpiresAt());
    }

}
