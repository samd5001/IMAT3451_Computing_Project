package classes;

public class Plan {
    private String name;
    private String description;
    private String days;
    private boolean userMade;

    public Plan(String name, String description, String days, boolean userMade) {
        this.name = name;
        this.description = description;
        this.days = days;
        this.userMade = userMade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public boolean isUserMade() {
        return userMade;
    }
}
