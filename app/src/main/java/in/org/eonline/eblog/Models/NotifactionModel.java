package in.org.eonline.eblog.Models;

public class NotifactionModel {

    public String BlogId;
    public String LikeTimeStamp;
    public String NotifactionData;
    public String Userid;

    public String getBlogId() {
        return BlogId;
    }

    public void setBlogId(String blogId) {
        BlogId = blogId;
    }

    public String getLikeTimeStamp() {
        return LikeTimeStamp;
    }

    public void setLikeTimeStamp(String likeTimeStamp) {
        LikeTimeStamp = likeTimeStamp;
    }

    public String getNotifactionData() {
        return NotifactionData;
    }

    public void setNotifactionData(String notifactionData) {
        NotifactionData = notifactionData;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }
}
