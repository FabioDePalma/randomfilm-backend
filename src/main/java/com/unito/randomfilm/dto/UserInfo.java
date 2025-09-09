package com.unito.randomfilm.dto;

public class UserInfo {
    private Long userId;
    private String username;
    private String email;
    private String name;

    public UserInfo() {}

    public UserInfo(Long userId, String username, String email, String name) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getName() { return name; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }

}