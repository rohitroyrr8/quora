package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(path = "/userprofile/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@RequestHeader final String authorization, @PathVariable("userId") String userId) throws UserNotFoundException, AuthorizationFailedException {

        UserEntity user = commonBusinessService.getUser(userId, authorization);
        UserDetailsResponse response = new UserDetailsResponse()
                                            .firstName(user.getFirstName())
                                            .lastName(user.getLastName())
                                            .userName(user.getUsername())
                                            .emailAddress(user.getEmail())
                                            .country(user.getCountry())
                                            .aboutMe(user.getAboutMe())
                                            .dob(user.getDob())
                                            .contactNumber(user.getContactNumber());

        return new ResponseEntity<UserDetailsResponse>(response, HttpStatus.OK);
    }

}
