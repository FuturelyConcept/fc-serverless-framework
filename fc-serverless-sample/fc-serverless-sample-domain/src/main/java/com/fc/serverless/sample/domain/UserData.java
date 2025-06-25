package com.fc.serverless.sample.domain;

/**
 * User data POJO for validation
 */
public class UserData {
    private String name;
    private String email;
    private int age;
    
    public UserData() {}
    
    public UserData(String name, String email, int age) {
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
