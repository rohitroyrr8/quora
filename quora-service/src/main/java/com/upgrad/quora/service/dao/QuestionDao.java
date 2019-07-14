package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity question) {
        entityManager.persist(question);
        return question;
    }

    public Optional<QuestionEntity> findQuestionByUUID(UUID questionId) {
        try {
            QuestionEntity result = entityManager.createNamedQuery("questionByUUID", QuestionEntity.class)
                    .setParameter("uuid", questionId)
                    .getSingleResult();
            return Optional.of(result);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
