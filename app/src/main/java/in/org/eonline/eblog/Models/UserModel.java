package in.org.eonline.eblog.Models;

public class UserModel {

    public String userId;
    public String userFName;
    public String userLName;
    public String userImageUrl;
    public String userEmail;
    public String userContact;
    public Object AllFollow;
    public Object AllFollowing;

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public Object getAllFollow() {
        return AllFollow;
    }

    public void setAllFollow(Object allFollow) {
        AllFollow = allFollow;
    }

    public Object getAllFollowing() {
        return AllFollowing;
    }

    public void setAllFollowing(Object allFollowing) {
        AllFollowing = allFollowing;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFName() {
        return userFName;
    }

    public void setUserFName(String userFName) {
        this.userFName = userFName;
    }

    public String getUserLName() {
        return userLName;
    }

    public void setUserLName(String userLName) {
        this.userLName = userLName;
    }

    public String getUserImage() {
        return userImageUrl;
    }

    public void setUserImage(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

}
