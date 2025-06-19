package com.example.libraryprojectmusmid.model.user;

import com.example.libraryprojectmusmid.model.user.Role;

public abstract class User {
    protected String username;
    protected String password;
    protected Role role;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
}

