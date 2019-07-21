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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    /**
     * This method is used to create an answer for a given question uuid
     *
     * @param answer : answer requedt which need to be added
     * @param token : authorization token for logged-in user
     * @param questionID : question uuid ofr which answer need to be added
     *
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     *
     * @return answerEntity which is just created
     */
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

    /**
     * This method is used to update an answer for a given answer uuid
     *
     * @param detail : answer request which need to be updated
     * @param token : authorization token for logged-in user
     * @param answerId : answer uuid for which answer need to be updated
     *
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(String detail, final String token, final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
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

    /**
     * This method is used to delete an answer for a given answer uuid
     *
     * @param answerId : answer request which need to be added
     * @param token : authorization token for logged-in user
     *
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
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

    /**
     * This method is used to get all the answers for a given question uuid
     * @param token : authorization token for logged-in user
     * @param uuid : question uuid for which answer need to be fetched
     *
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     * @return list of all answer for a given question uuid
     */

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
        return answerDao.findAllAnswerByQuestion(question);
    }

    /**
     * This method is used to create an answer for a given question uuid
     *
     * @param authToken : authorization token for logged-in user
     *
     * @throws AuthorizationFailedException
     */
    private void throwErrorIfTokenNotExist(UserAuthTokenEntity authToken) throws AuthorizationFailedException {
        if (isNull(authToken)) {
            throw new AuthorizationFailedException(ValidationErrors.USER_NOT_SIGNED_IN.getCode(),
                    ValidationErrors.USER_NOT_SIGNED_IN.getReason());
        }
    }

    /**
     * This method is to check whether user in logged-in or logged-out
     *
     * @param authToken : authorization token for logged-in user
     * @return whether user with given token is logged-in or not
     */
    private boolean isSignedOut(UserAuthTokenEntity authToken) {
        return nonNull(authToken.getLogoutAt()) || isBeforeNow(authToken.getExpiresAt());
    }

    /**
     * This method is to check whether user's role is adminor not
     *
     * @param user : logged-in user
     * @return whether logged-in user's role is admin or not
     */
    private boolean isAdmin(UserEntity user) {
        return ADMIN.equals(user.getRole());
    }

    /**
     * This method is to check whether given user is the owner of given answer or not
     *
     * @param authToken : authorrization token for logged-in user
     * @param answerEntity : answer which need to be check
     *
     * @return whether user is owner of given answer or not
     */
    private boolean isOwner(UserAuthTokenEntity authToken, AnswerEntity answerEntity) {
        return authToken.getUser().getUuid().equals(answerEntity.getUser().getUuid());
    }
}
