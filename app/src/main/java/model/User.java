package model;

import java.util.Date;

public class User {
    
    private String email;
    private String password;
    private String name;
    private Date dob;
    private Boolean gender;
    private Float height;
    private Float weight;
    private Integer goal;
    
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }
    
    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public Date getDob() {
        return this.dob;
    }

    public Boolean getGender() {
        return this.gender;
    }

    public Float getHeight() {
        return this.height;
    }

    public Float getWeight() {
        return this.weight;
    }

    public Integer getGoal() {
        return this.goal;
    }
}
