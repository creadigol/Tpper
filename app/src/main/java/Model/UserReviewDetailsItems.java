package Model;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Utils.CommonUtils;

/**
 * Created by ravi on 05-04-2017.
 */

public class UserReviewDetailsItems {
    String userName, userImage, avarage, review, reviweId, serverReplayDesc,knowledge,hospitality,appereance,tips;
    long date;

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getHospitality() {
        return hospitality;
    }

    public void setHospitality(String hospitality) {
        this.hospitality = hospitality;
    }

    public String getAppereance() {
        return appereance;
    }

    public void setAppereance(String appereance) {
        this.appereance = appereance;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getServerReplayDesc() {
        return serverReplayDesc;
    }

    public void setServerReplayDesc(String serverReplayDesc) {
        this.serverReplayDesc = serverReplayDesc;
    }

    public String getReviweId() {
        return reviweId;
    }

    public void setReviweId(String reviweId) {
        this.reviweId = reviweId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAvarage() {
        return avarage;
    }

    public void setAvarage(String avarage) {
        this.avarage = avarage;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
