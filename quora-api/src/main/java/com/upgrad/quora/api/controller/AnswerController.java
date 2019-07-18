package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.error.ValidationErrors;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.BadRequestException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @RequestMapping(path = "/question/{questionId}/answer/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader final String autorization, @PathVariable("questionId") final String questinoId, final AnswerRequest request) throws BadRequestException, AuthorizationFailedException, InvalidQuestionException {

        if (StringUtils.isBlank(request.getContent())) {
            throw new BadRequestException(ValidationErrors.NO_CONTENT_IN_ANSWER.getCode(), ValidationErrors.NO_CONTENT_IN_ANSWER.getReason());
        }

        AnswerEntity entity = new AnswerEntity();
        entity.setUuid(UUID.randomUUID().toString());
        entity.setAnswer(request.getContent());

        AnswerEntity createdAnswer = answerBusinessService.create(entity, autorization, questinoId);
        AnswerResponse response = new AnswerResponse()
                                            .id(createdAnswer.getUuid())
                                            .status("Answer created successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/answer/edit/{answerId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> editAnswerContent(final AnswerEditRequest request, @PathVariable("answerId") final String answerId, @RequestHeader String authorization) throws BadRequestException, AuthorizationFailedException, AnswerNotFoundException, InvalidQuestionException {

        if (StringUtils.isBlank(request.getContent())) {
            throw new BadRequestException(ValidationErrors.NO_CONTENT_IN_ANSWER.getCode(), ValidationErrors.NO_CONTENT_IN_ANSWER.getReason());
        }

        answerBusinessService.update(request.getContent(), authorization, answerId);
        AnswerResponse response = new AnswerResponse()
                                            .id(answerId)
                                            .status("Answer changed successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/answer/delete/{answerId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> deleteAnswer(@PathVariable("answerId") final String answerId, @RequestHeader String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        answerBusinessService.delete(answerId, authorization);

        AnswerResponse response = new AnswerResponse()
                                            .id(answerId)
                                            .status("Answer deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
