package classes;

public class User {
    
    private String email;
    private String name;
    private String dob;
    private String gender;
    private String height;
    private String weight;
    private String goal;
    
    public User(String email, String name, String dob, String gender, String height, String weight, String goal) {
        this.email = email;
        this.name = name;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setGender(Boolean gender) {
        this.gender = String.valueOf(gender);
    }

    public void setHeight(Float height) {
        this.height = String.valueOf(height);
    }

    public void setWeight(Float weight) {
        this.weight = String.valueOf(weight);
    }

    public void setGoal(Integer goal) {
        this.goal = String.valueOf(goal);
    }
    
    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getDob() {
        return this.dob;
    }

    public Boolean getGender() {
        return Boolean.valueOf(this.gender);
    }

    public float getHeight() {
        return Float.valueOf(this.height);
    }

    public float getWeight() {
        return Float.valueOf(this.weight);
    }

    public int getGoal() {
        return Integer.valueOf(this.goal);
    }
}
