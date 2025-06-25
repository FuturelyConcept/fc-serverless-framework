package com.fc.serverless.sample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User data POJO for validation
 */
public class UserData {
    private String name;
    private String email;
    private int age;

    public UserData() {}

    @JsonCreator
    public UserData(@JsonProperty("name") String name,
                    @JsonProperty("email") String email,
                    @JsonProperty("age") int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                email != null && email.contains("@") &&
                age >= 18;
    }

    // Getters and setters
    @JsonProperty("name")
    public String getName() { return name; }

    @JsonProperty("name")
    public void setName(String name) { this.name = name; }

    @JsonProperty("email")
    public String getEmail() { return email; }

    @JsonProperty("email")
    public void setEmail(String email) { this.email = email; }

    @JsonProperty("age")
    public int getAge() { return age; }

    @JsonProperty("age")
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return "UserData{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}