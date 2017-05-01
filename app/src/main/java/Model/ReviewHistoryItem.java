package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Utils.Jsonkey;

/**
 * Created by Creadigol on 22-09-2016.
 */

public class ReviewHistoryItem extends BaseObject {
    String tip, menu_knowledge, hospitality, appearance, average, total_review, total_tip, serverName, serverUserName, serverEmail, serverImage, restaurantName, restaurantAddress, restaurantWebsite, restaurantNumber, restaurantId, restaurantEmail;

    String userReview;

    public String getUserReview() {
        return userReview;
    }

    public void setUserReview(String userReview) {
        this.userReview = userReview;
    }

    public String getTip() {
        return tip;
    }

    public String getRestaurantEmail() {
        return restaurantEmail;
    }

    public void setRestaurantEmail(String restaurantEmail) {
        this.restaurantEmail = restaurantEmail;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantNumber() {
        return restaurantNumber;
    }

    public void setRestaurantNumber(String restaurantNumber) {
        this.restaurantNumber = restaurantNumber;
    }

    public String getRestaurantWebsite() {
        return restaurantWebsite;
    }

    public void setRestaurantWebsite(String restaurantWebsite) {
        this.restaurantWebsite = restaurantWebsite;
    }

    public String getServerUserName() {
        return serverUserName;
    }

    public void setServerUserName(String serverUserName) {
        this.serverUserName = serverUserName;
    }

    public String getServerEmail() {
        return serverEmail;
    }

    public void setServerEmail(String serverEmail) {
        this.serverEmail = serverEmail;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerImage() {
        return serverImage;
    }

    public void setServerImage(String serverImage) {
        this.serverImage = serverImage;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getMenu_knowledge() {
        return menu_knowledge;
    }

    public void setMenu_knowledge(String menu_knowledge) {
        this.menu_knowledge = menu_knowledge;
    }

    public String getHospitality() {
        return hospitality;
    }

    public void setHospitality(String hospitality) {
        this.hospitality = hospitality;
    }

    public String getAppearance() {
        return appearance;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getTotal_review() {
        return total_review;
    }

    public void setTotal_review(String total_review) {
        this.total_review = total_review;
    }

    public String getTotal_tip() {
        return total_tip;
    }

    public void setTotal_tip(String total_tip) {
        this.total_tip = total_tip;
    }

    ArrayList<RatingItem> ratingItems;

    public ArrayList<RatingItem> getReviewItem() {
        return ratingItems;
    }

    public void setReviewItem(JSONObject jsonLocationObject) {
        ratingItems = new ArrayList<>();
        if (jsonLocationObject != null) {
            RatingItem searchitem = new RatingItem();
            searchitem.setRating1(jsonLocationObject.optString(Jsonkey.rating1));
            searchitem.setRating2(jsonLocationObject.optString(Jsonkey.rating2));
            searchitem.setRating3(jsonLocationObject.optString(Jsonkey.rating3));
            searchitem.setRating4(jsonLocationObject.optString(Jsonkey.rating4));
            ratingItems.add(searchitem);
        }
    }

    private ArrayList<UserReviewDetailsItems> userReviewDetailsItemses;

    public ArrayList<UserReviewDetailsItems> getUserReviewDetailsItemses() {
        return userReviewDetailsItemses;
    }

    public void setUserReviewDetailsItemses(JSONArray jsonDeals) {
        userReviewDetailsItemses = new ArrayList<UserReviewDetailsItems>();

        if (jsonDeals != null && jsonDeals.length() > 0) {
            for (int i = 0; i < jsonDeals.length(); i++) {
                UserReviewDetailsItems userReviewDetailsItems = new UserReviewDetailsItems();
                try {
                    JSONObject jsonObjectOutlet = jsonDeals.getJSONObject(i);

                    userReviewDetailsItems.setReviweId(jsonObjectOutlet.optString(Jsonkey.reviewId));
                    userReviewDetailsItems.setUserName(jsonObjectOutlet.optString(Jsonkey.fullName));
                    userReviewDetailsItems.setUserImage(jsonObjectOutlet.optString(Jsonkey.profilePic));
                    userReviewDetailsItems.setDate(jsonObjectOutlet.optLong(Jsonkey.date));
                    userReviewDetailsItems.setAvarage(jsonObjectOutlet.optString(Jsonkey.average));
                    userReviewDetailsItems.setReview(jsonObjectOutlet.optString(Jsonkey.review));
                    userReviewDetailsItems.setServerReplayDesc(jsonObjectOutlet.optString(Jsonkey.serverReplay));
                    userReviewDetailsItems.setKnowledge(jsonObjectOutlet.optString(Jsonkey.menu_knowledge));
                    userReviewDetailsItems.setHospitality(jsonObjectOutlet.optString(Jsonkey.hospitality));
                    userReviewDetailsItems.setAppereance(jsonObjectOutlet.optString(Jsonkey.appearance));
                    userReviewDetailsItems.setTips(jsonObjectOutlet.optString(Jsonkey.tip));

                    userReviewDetailsItemses.add(userReviewDetailsItems);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
