package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.*;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;


    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity findByUuid(String answerId) {
        try{
            return entityManager.createNamedQuery("answerByUUID", AnswerEntity.class)
                                    .setParameter("uuid", answerId)
                                    .getSingleResult();
        }catch(Exception e) {
            return null;
        }
    }

    public void update(AnswerEntity answer) {
        entityManager.merge(answer);
    }

    public void delete(AnswerEntity answer) {
        entityManager.remove(answer);
    }

     public List<AnswerEntity> findAllAnswerByQuestion(QuestionEntity question) {
        try{
            return entityManager.createNamedQuery("answerByQuestionId", AnswerEntity.class)
                    .setParameter("question", question)
                    .getResultList();
        }catch (Exception e) {
            return new ArrayList<>();
        }
     }


}
