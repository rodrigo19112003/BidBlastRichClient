package api.responses.auctions;

public class UserJSONResponse {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatar;
    private boolean isRemovable;

    public UserJSONResponse() {
    }

    public UserJSONResponse(int id, String fullName, String phoneNumber, String email, String avatar, boolean isRemovable) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.avatar = avatar;
        this.isRemovable = isRemovable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isIsRemovable() {
        return isRemovable;
    }

    public void setIsRemovable(boolean isRemovable) {
        this.isRemovable = isRemovable;
    }
}
