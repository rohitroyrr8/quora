package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.upgrad.quora.service.util.DateUtils.isBeforeNow;
import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;

@Service
public class QuestionService {

    private static final String ADMIN = "admin";

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
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.POST_A_QUESTION_SIGNED_OUT.getCode(),
                    ValidationErrors.POST_A_QUESTION_SIGNED_OUT.getReason());
        }
        question.setUser(authToken.getUser());
        return questionDao.createQuestion(question);
    }

    public List<QuestionEntity> getAll(String token) throws AuthorizationFailedException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.GET_ALL_QUESTIONS_SIGNED_OUT.getCode(),
                    ValidationErrors.GET_ALL_QUESTIONS_SIGNED_OUT.getReason());
        }
        return questionDao.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(String uuid, String token) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.DELETE_QUESTION_SIGNED_OUT.getCode(),
                    ValidationErrors.DELETE_QUESTION_SIGNED_OUT.getReason());
        }
        QuestionEntity question = get(uuid);
        if (isAdmin(authToken.getUser()) || isOwner(authToken.getUser(), question)) {
            questionDao.delete(question);
        } else {
            throw new AuthorizationFailedException(ValidationErrors.QUESTION_OWNER_ADMIN_ONLY_CAN_DELETE.getCode(),
                    ValidationErrors.QUESTION_OWNER_ADMIN_ONLY_CAN_DELETE.getReason());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void update(String uuid, String token, String detail) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.EDIT_QUESTION_SIGNED_OUT.getCode(),
                    ValidationErrors.EDIT_QUESTION_SIGNED_OUT.getReason());
        }
        QuestionEntity question = get(uuid);
        if (isOwner(authToken.getUser(), question)) {
            question.setContent(detail);
            questionDao.update(question);
        } else {
            throw new AuthorizationFailedException(ValidationErrors.OWNER_ONLY_CAN_EDIT.getCode(),
                    ValidationErrors.OWNER_ONLY_CAN_EDIT.getReason());
        }
    }

    public List<QuestionEntity> getAllByUser(String userId, String token) throws UserNotFoundException, AuthorizationFailedException {
        UserEntity user = userDao.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException(ValidationErrors.USER_NOT_FOUND_QUESTIONS.getCode(),
                    ValidationErrors.USER_NOT_FOUND_QUESTIONS.getReason());
        }
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.GET_ALL_QUESTIONS_USER_SIGNED_OUT.getCode(),
                    ValidationErrors.GET_ALL_QUESTIONS_USER_SIGNED_OUT.getReason());
        }
        return questionDao.findAllByUser(user);
    }

    private QuestionEntity get(String uuid) throws InvalidQuestionException {
        return questionDao.findQuestionByUUID(uuid)
                .orElseThrow(() -> new InvalidQuestionException(ValidationErrors.INVALID_QUESTION.getCode(),
                        ValidationErrors.INVALID_QUESTION.getReason()));
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

    private boolean isAdmin(UserEntity user) {
        return ADMIN.equals(user.getRole());
    }

    private boolean isOwner(UserEntity userEntity, QuestionEntity questionEntity) {
        return userEntity.getUuid().equals(questionEntity.getUser().getUuid());
    }
}
