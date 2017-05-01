package Model;

/**
 * Created by ravi on 06-04-2017.
 */

public class MyReviewsListItem {
    String reviewId,serverId, serverName, serverResName, serverImage, avgRatingCount, totalRatingCount, userReviewDesc, totalTips;

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerResName() {
        return serverResName;
    }

    public void setServerResName(String serverResName) {
        this.serverResName = serverResName;
    }

    public String getServerImage() {
        return serverImage;
    }

    public void setServerImage(String serverImage) {
        this.serverImage = serverImage;
    }

    public String getAvgRatingCount() {
        return avgRatingCount;
    }

    public void setAvgRatingCount(String avgRatingCount) {
        this.avgRatingCount = avgRatingCount;
    }

    public String getTotalRatingCount() {
        return totalRatingCount;
    }

    public void setTotalRatingCount(String totalRatingCount) {
        this.totalRatingCount = totalRatingCount;
    }

    public String getUserReviewDesc() {
        return userReviewDesc;
    }

    public void setUserReviewDesc(String userReviewDesc) {
        this.userReviewDesc = userReviewDesc;
    }

    public String getTotalTips() {
        return totalTips;
    }

    public void setTotalTips(String totalTips) {
        this.totalTips = totalTips;
    }
}
