package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @RequestMapping(path = "/question/create", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionResponse> create(final QuestionRequest request, @RequestHeader("authorization") final String token)
            throws AuthorizationFailedException, BadRequestException {

        if (StringUtils.isBlank(request.getContent())) {
            throw new BadRequestException(ValidationErrors.NO_DETAIL_IN_QUESTION.getCode(),
                    ValidationErrors.NO_DETAIL_IN_QUESTION.getReason());
        }

        QuestionEntity questionEntity = new QuestionEntity(UUID.randomUUID(), request.getContent(), LocalDateTime.now());
        QuestionEntity createdQuestion = questionService.create(questionEntity, token);
        QuestionResponse response = new QuestionResponse().id(createdQuestion.getUuid().toString()).status("CREATED - Question created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
