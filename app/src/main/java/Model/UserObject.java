package Model;

/**
 * Created by Ashfaq on 7/16/2016.
 */

public class UserObject extends BaseObject {

    private String name,resturantName,resturantId, userName, email, userid, profilePic, mobileNo, address, type;

    private String id_google, id_fb, dob, password;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneno() {
        return mobileNo;
    }

    public void setPhoneno(String phoneno) {
        this.mobileNo = phoneno;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return name;
    }

    public void setFName(String fname) {
        this.name = fname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String lname) {
        this.userName = lname;
    }

    public String getResturantName() {
        return resturantName;
    }

    public void setResturantName(String resturantName) {
        this.resturantName = resturantName;
    }

    public String getResturantId() {
        return resturantId;
    }

    public void setResturantId(String resturantId) {
        this.resturantId= resturantId;
    }

    public String getMobile() {
        return mobileNo;
    }

    public void setMobile(String mobile) {
        this.mobileNo = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String user_image) {
        this.profilePic = user_image;
    }


    public String getId_google() {
        return id_google;
    }

    public void setId_google(String id_google) {
        this.id_google = id_google;
    }

    public String getId_fb() {
        return id_fb;
    }

    public void setId_fb(String id_fb) {
        this.id_fb = id_fb;
    }

    public UserObject(String name, String userName, String mobile, String email, String userid, String profilePic, String address, String password) {
        this.name = name;
        this.userName = userName;
        this.mobileNo = mobile;
        this.email = email;
        this.password = password;
        this.userid = userid;
        this.profilePic = profilePic;
        this.address = address;
    }

    public UserObject() {

    }
}
