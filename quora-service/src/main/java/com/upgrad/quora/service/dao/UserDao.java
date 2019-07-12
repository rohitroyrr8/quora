package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity user) {
        entityManager.persist(user);
        return user;
    }

    public UserEntity getUser(final String userUuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", userUuid)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email) {
        try{
            return entityManager.createNamedQuery("userByEmail", UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }catch(Exception e) {
            return null;
        }
    }

    public UserEntity getUserByUsername(final String username) {
        try{
            return entityManager.createNamedQuery("userByUsername", UserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult();
        }catch(Exception e) {
            return null;
        }
    }

    public void updateUser(UserEntity user) {
        entityManager.merge(user);
    }

    public UserAuthTokenEntity createAuthToken(UserAuthTokenEntity authToken) {
        entityManager.persist(authToken);
        return authToken;
    }

    public UserAuthTokenEntity getAuthTokenByAccessToken(String authToken) {
        try{
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", authToken)
                    .getSingleResult();
        }catch(Exception e) {
            return null;
        }
    }

    public UserAuthTokenEntity getAuthTokenByAccessTokenAndLogout(String authToken) {
        try{
            return entityManager.createNamedQuery("userAuthTokenByAccessTokenAndLogout", UserAuthTokenEntity.class)
                    .setParameter("accessToken", authToken)
                    .getSingleResult();
        }catch(Exception e) {
            return null;
        }
    }
}
