package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
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

    public List<QuestionEntity> findAll() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Optional<QuestionEntity> findQuestionByUUID(String questionId) {
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
