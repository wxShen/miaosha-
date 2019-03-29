package com.example.moocmiaosha.controller.viewobject;

import java.io.Serializable;

public class TestRedisUser implements Serializable {

    private static final long serialVersionUID = 8655851615465363473L;
    private Long id;
    private String username;
    private String password;


    public TestRedisUser(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
