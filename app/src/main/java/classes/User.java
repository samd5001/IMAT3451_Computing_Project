package classes;

public class User {
    
    private String email;
    private String name;
    private String dob;
    private int gender;
    private float height;
    private float weight;
    private int goal;
    
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

    public void setGender(int gender) {
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

    public String getName() {
        return this.name;
    }

    public String getDob() {
        return this.dob;
    }

    public int getGender() {
        return this.gender;
    }

    public float getHeight() {
        return this.height;
    }

    public float getWeight() {
        return this.weight;
    }

    public int getGoal() {
        return this.goal;
    }
}
