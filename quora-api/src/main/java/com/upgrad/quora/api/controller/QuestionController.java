package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.BadRequestException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

        QuestionEntity questionEntity = new QuestionEntity(UUID.randomUUID().toString(), request.getContent(), LocalDateTime.now());
        QuestionEntity createdQuestion = questionService.create(questionEntity, token);
        QuestionResponse response = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/question/all", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAll(@RequestHeader("authorization") final String token) throws AuthorizationFailedException {
        List<QuestionEntity> allQuestions = questionService.getAll(token);
        List<QuestionDetailsResponse> response = allQuestions.stream()
                .map(question -> new QuestionDetailsResponse().id(question.getUuid()).content(question.getContent()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/question/delete/{questionId}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionResponse> delete(@PathVariable("questionId") String uuid,
                                                   @RequestHeader("authorization") final String token)
            throws AuthorizationFailedException, InvalidQuestionException {
        questionService.delete(uuid, token);
        QuestionResponse response = new QuestionResponse().id(uuid).status("QUESTION DELETED");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
