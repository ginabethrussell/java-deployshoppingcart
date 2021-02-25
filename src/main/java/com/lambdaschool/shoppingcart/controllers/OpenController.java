package com.lambdaschool.shoppingcart.controllers;

import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.models.UserMinimum;
import com.lambdaschool.shoppingcart.models.UserRoles;
import com.lambdaschool.shoppingcart.services.RoleService;
import com.lambdaschool.shoppingcart.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class OpenController
{
    // http://localhost:2019/createnewuser
    // simplified user body
    // body -> username, password, primary email
    // need a pojo to hold this info independent of the db user

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @PostMapping(value = "/createnewuser", consumes ="application/json", produces = "application/json")
    public ResponseEntity<?> addSelf(HttpServletRequest httpServletRequest, @RequestBody
                                     UserMinimum newminuser)
    {
        // create the new user
        User newuser = new User();
        newuser.setUsername(newminuser.getUsername());
        newuser.setPassword(newminuser.getPassword());
        newuser.setPrimaryemail(newminuser.getPrimaryemail());
        Set<UserRoles>  newRoles = new HashSet<>();
        newRoles.add(new UserRoles(newuser, roleService.findByName("USER")));
        newuser = userService.save(newuser);

        HttpHeaders responseHeaders = new HttpHeaders();

        //http://localhost:2019/users/user/id
        URI newUserURI = ServletUriComponentsBuilder.fromUriString(httpServletRequest.getServerName() + ":"
        + httpServletRequest.getLocalPort() + "/users/user/{userid")
            .buildAndExpand(newuser.getUserid()).toUri();
        responseHeaders.setLocation(newUserURI);

        // login -> client will get back an access token, now I need to be a client to my own system
        RestTemplate restTemplate = new RestTemplate();
        String requestURI = "http://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getLocalPort() + "/login";

        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(acceptableMediaTypes);
        headers.setBasicAuth(System.getenv("OAUTHCLIENTID"), System.getenv("OAUTHCLIENTSECRET"));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("scope", "read write trust");
        map.add("username", newminuser.getUsername());
        map.add("password", newminuser.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        String theToken = restTemplate.postForObject(requestURI, request, String.class);

        return new ResponseEntity<>(theToken,
            responseHeaders,
            HttpStatus.CREATED);
    }
}
