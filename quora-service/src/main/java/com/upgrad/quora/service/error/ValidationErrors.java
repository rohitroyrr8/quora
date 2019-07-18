package com.upgrad.quora.service.error;

public enum ValidationErrors {
    USER_NOT_SIGNED_IN("ATHR-001", "User has not signed in"),
    POST_A_QUESTION_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to post a question"),
    POST_A_ANSWER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to post an answer"),
    GET_ALL_QUESTIONS_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to get all questions"),
    GET_ALL_QUESTIONS_USER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user"),
    EDIT_QUESTION_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to edit the question"),
    EDIT_ANSWER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to edit the answer"),
    DELETE_QUESTION_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to delete a question"),
    DELETE_ANSWER_SIGNED_OUT("ATHR-002", "User is signed out.Sign in first to delete an answer"),
    OWNER_ONLY_CAN_EDIT_QUESTION("ATHR-003", "Only the question owner can edit the question"),
    OWNER_ONLY_CAN_EDIT_ANSWER("ATHR-003", "Only the question owner can edit the question"),
    QUESTION_OWNER_ADMIN_ONLY_CAN_DELETE("ATHR-003", "Only the question owner or admin can delete the question"),
    ANSWER_OWNER_ADMIN_ONLY_CAN_DELETE("ATHR-003", "Only the answer owner or admin can delete the answer"),

    INVALID_QUESTION("QUES-001", "Entered question uuid does not exist"),
    INVALID_ANSWER("ANS-001", "Entered answer uuid does not exist"),

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
