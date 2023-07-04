package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private UserDao dao;

    public UserController(UserDao dao) {
        this.dao = dao;
    }

    @GetMapping
    public List<UserDTO> getListOfUsers() {
        return dao.findAll();
    }


}
