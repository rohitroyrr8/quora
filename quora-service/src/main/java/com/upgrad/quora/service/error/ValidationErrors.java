package com.upgrad.quora.service.error;

public enum ValidationErrors {
    USER_NOT_SIGNED_IN("ATHR-001", "User has not signed in"),
    USER_NOT_ADMIN("ATHR-003", "Unauthorized Access, Entered user is not an admin"),
    POST_A_QUESTION_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to post a question"),
    POST_A_ANSWER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to post an answer"),
    GET_USER_DETAILS_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to get user details"),
    GET_ALL_QUESTIONS_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to get all questions"),
    GET_ALL_QUESTIONS_USER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user"),
    EDIT_QUESTION_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to edit the question"),
    EDIT_ANSWER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to edit the answer"),
    GET_USER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to get user details"),
    DELETE_QUESTION_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to delete a question"),
    DELETE_ANSWER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to delete an answer"),
    OWNER_ONLY_CAN_EDIT_QUESTION("ATHR-003", "Only the question owner can edit the question"),
    OWNER_ONLY_CAN_EDIT_ANSWER("ATHR-003", "Only the question owner can edit the question"),
    QUESTION_OWNER_ADMIN_ONLY_CAN_DELETE("ATHR-003", "Only the question owner or admin can delete the question"),
    ANSWER_OWNER_ADMIN_ONLY_CAN_DELETE("ATHR-003", "Only the answer owner or admin can delete the answer"),
    ADMIN_DELETE_USER_NOT_EXISTS("USR-001", "User with entered uuid to be deleted does not exist"),

    INVALID_QUESTION("QUES-001", "Entered question uuid does not exist"),
    INVALID_ANSWER("ANS-001", "Entered answer uuid does not exist"),
    INVALID_USER("USR-001", "Entered user uuid does not exist"),
    INVALID_USERNAME("ATH-001", "This username does not exist"),
    INVALID_PASSWORD("ATH-002", "Password failed"),

    USERNAME_ALREADY_TAKEN("SGR-001", "Try any other Username, this Username has already been taken"),
    USERNAME_ALREADY_REGISTERED("SGR-002", "This user has already been registered, try with any other emailId"),


    USER_NOT_FOUND_QUESTIONS("USR-001", "User with entered uuid whose question details are to be seen does not exist"),

    NO_CONTENT_IN_QUESTION("BR-001","Question should have content"),
    NO_CONTENT_IN_ANSWER("BR-001", "Answer should have content");

    private String code;
    private String reason;

    ValidationErrors(String code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
