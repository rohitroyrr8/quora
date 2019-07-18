package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import static com.upgrad.quora.service.util.DateUtils.isBeforeNow;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class AnswerBusinessService {

    private static final String ADMIN = "admin";

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity create(AnswerEntity answer, final String token, String questionID) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.POST_A_ANSWER_SIGNED_OUT.getCode(),
                    ValidationErrors.POST_A_ANSWER_SIGNED_OUT.getReason());
        }
        QuestionEntity question = questionDao.findQuestionByUUID(questionID);
        if(question == null) {
            throw new InvalidQuestionException(ValidationErrors.INVALID_QUESTION.getCode(), ValidationErrors.INVALID_QUESTION.getReason());
        }

        answer.setUser(authToken.getUser());
        answer.setDate(LocalDateTime.now());
        answer.setQuestion(question);
        return answerDao.createAnswer(answer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void update(String detail, final String token, final String answerId) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.EDIT_ANSWER_SIGNED_OUT.getCode(),
                    ValidationErrors.EDIT_ANSWER_SIGNED_OUT.getReason());
        }
        AnswerEntity tempAnswer = answerDao.findByUuid(answerId);
        if(tempAnswer == null) {
            throw new AnswerNotFoundException(ValidationErrors.INVALID_ANSWER.getCode(), ValidationErrors.INVALID_ANSWER.getReason());
        }
        if(!isOwner(authToken, tempAnswer)){
            throw new AuthorizationFailedException(ValidationErrors.OWNER_ONLY_CAN_EDIT_ANSWER.getCode(), ValidationErrors.OWNER_ONLY_CAN_EDIT_ANSWER.getReason());
        }
        tempAnswer.setAnswer(detail);
        answerDao.update(tempAnswer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(final String answerId, final String token) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.DELETE_ANSWER_SIGNED_OUT.getCode(),
                    ValidationErrors.DELETE_ANSWER_SIGNED_OUT.getReason());
        }
        AnswerEntity tempAnswer = answerDao.findByUuid(answerId);
        UserEntity user = authToken.getUser();
        if(tempAnswer == null) {
            throw new AnswerNotFoundException(ValidationErrors.INVALID_ANSWER.getCode(), ValidationErrors.INVALID_ANSWER.getReason());
        }
        if(!isOwner(authToken, tempAnswer) && !isAdmin(user)) {
                throw new AuthorizationFailedException(ValidationErrors.ANSWER_OWNER_ADMIN_ONLY_CAN_DELETE.getCode(), ValidationErrors.ANSWER_OWNER_ADMIN_ONLY_CAN_DELETE.getReason());
        }
        answerDao.delete(tempAnswer);
    }


    public List<AnswerEntity> getAllAnswerByQuestion(final String uuid, final String token) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity authToken = userDao.getAuthTokenByAccessToken(token);
        throwErrorIfTokenNotExist(authToken);
        if (isSignedOut(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.DELETE_ANSWER_SIGNED_OUT.getCode(),
                    ValidationErrors.DELETE_ANSWER_SIGNED_OUT.getReason());
        }
        QuestionEntity question = questionDao.findQuestionByUUID(uuid);
        if(question == null) {
            throw new InvalidQuestionException(ValidationErrors.INVALID_QUESTION.getCode(), ValidationErrors.INVALID_QUESTION.getReason());
        }
        return answerDao.findAllAnswerByQuestionId(uuid);
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

    private boolean isOwner(UserAuthTokenEntity authToken, AnswerEntity answerEntity) {
        return authToken.getUser().getUuid().equals(answerEntity.getUser().getUuid());
    }
}
