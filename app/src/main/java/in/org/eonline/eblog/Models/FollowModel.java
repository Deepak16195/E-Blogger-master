package in.org.eonline.eblog.Models;

public class FollowModel {
    public String UserEmailId;
    public String UserFirstName;
    public String UserLastName;
    public String UserImageUrl;
    public boolean Folloers;

    public String getUserEmailId() {
        return UserEmailId;
    }

    public void setUserEmailId(String userEmailId) {
        UserEmailId = userEmailId;
    }

    public String getUserFirstName() {
        return UserFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        UserFirstName = userFirstName;
    }

    public String getUserLastName() {
        return UserLastName;
    }

    public void setUserLastName(String userLastName) {
        UserLastName = userLastName;
    }

    public boolean isFolloers() {
        return Folloers;
    }

    public void setFolloers(boolean folloers) {
        Folloers = folloers;
    }

    public String getUserImageUrl() {
        return UserImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        UserImageUrl = userImageUrl;
    }
}
