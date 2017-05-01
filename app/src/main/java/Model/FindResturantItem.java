package Model;

/**
 * Created by Creadigol on 22-09-2016.
 */

public class FindResturantItem extends BaseObject {
    String resturantId,resturantName,resturantAddress,resturantPhoto,latitute,longitut,resturantEmail,resturantNumber,resturantWebsite,distance;

    public String getResturantId() {
        return resturantId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getResturantWebsite() {
        return resturantWebsite;
    }

    public void setResturantWebsite(String resturantWebsite) {
        this.resturantWebsite = resturantWebsite;
    }

    public String getResturantNumber() {
        return resturantNumber;
    }

    public void setResturantNumber(String resturantNumber) {
        this.resturantNumber = resturantNumber;
    }

    public String getResturantEmail() {
        return resturantEmail;
    }

    public void setResturantEmail(String resturantEmail) {
        this.resturantEmail = resturantEmail;
    }

    public void setResturantId(String resturantId) {
        this.resturantId = resturantId;
    }

    public String getResturantName() {
        return resturantName;
    }

    public void setResturantName(String resturantName) {
        this.resturantName = resturantName;
    }

    public String getResturantAddress() {
        return resturantAddress;
    }

    public void setResturantAddress(String resturantAddress) {
        this.resturantAddress = resturantAddress;
    }

    public String getResturantPhoto() {
        return resturantPhoto;
    }

    public void setResturantPhoto(String resturantPhoto) {
        this.resturantPhoto = resturantPhoto;
    }

    public String getLatitute() {
        return latitute;
    }

    public void setLatitute(String latitute) {
        this.latitute = latitute;
    }

    public String getLongitut() {
        return longitut;
    }

    public void setLongitut(String longitut) {
        this.longitut = longitut;
    }
}
