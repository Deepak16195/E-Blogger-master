package in.org.eonline.eblog.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class GetCommentsList {


    @Expose
    @SerializedName("Userid")
    private String Userid;
    @Expose
    @SerializedName("Comments")
    private String Comments;
    @Expose
    @SerializedName("UserImage")
    private String UserImage;
    @Expose
    @SerializedName("UserName")
    private String UserName;

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String Userid) {
        this.Userid = Userid;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String UserImage) {
        this.UserImage = UserImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }
}
