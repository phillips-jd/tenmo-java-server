package com.techelevator.tenmo.model;

import javax.validation.constraints.NotBlank;

public class UserDTO {

    @NotBlank
    private String username;

    public UserDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
