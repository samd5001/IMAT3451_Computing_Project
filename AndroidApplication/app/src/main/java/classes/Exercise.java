package classes;

public class Exercise {
    private String name;
    private String description;
    private int type;
    private String imageURL;
    private int minThreshold;
    private int maxThreshold;
    private String areasWorked;
    private boolean userMade;

    public Exercise(String name, String description, int type, String imageURL, int minThreshold, int maxThreshold, String areasWorked, boolean userMade) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.imageURL = imageURL;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.areasWorked = areasWorked;
        this.userMade = userMade;
    }

    public Exercise(String name, String description, int type, String areas) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.imageURL = "http://86.1.54.2/media/noimage.png";
        this.minThreshold = 1;
        this.maxThreshold = 1000;
        this.areasWorked = areas;
        this.userMade = true;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public int getMaxThreshold() {
        return maxThreshold;
    }

    public String getAreasWorked() {
        return areasWorked;
    }

    public boolean isUserMade() {
        return userMade;
    }
}
