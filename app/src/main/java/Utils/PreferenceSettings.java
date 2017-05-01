package Utils;

/**
 * Created by ravi on 26-10-2016.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceSettings {
    private String TIPPER = "Tipper";
    private String preIsLogin = "is_login";
    private String GoProfile = "profile";
    private String UserLogin = "true";
    private String LoginTypeSocial = "login";
    private String FullName = "name";
    private String UserName = "userName";

    private String EmailId = "email";
    private String Password = "password";
    private String ProfilePic = "user_pic";
    private String UserId = "User_Id";
    private String SocialId = "socialid";
    private String ResturantId = "resturantId";
    private String ResturantName = "resturantName";
    private String ResturantAddrees = "resturantAddress";
    private String ResturantEmail = "";
    private String ResturantWebsite = "";
    private String ResturantNo = "";

    private String serverName = "";
    private String LoginType = "";


    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setLoginType(String loginType) {
        this.LoginType = loginType;
    }

    public String getLoginType() {
        return LoginType;
    }


    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        this.FullName = fullName;
    }

    public String getResturantId() {
        return ResturantId;
    }

    public void setResturantId(String resturantId) {
        this.ResturantId = resturantId;
    }

    public String getResturantName() {
        return ResturantName;
    }

    public void setResturantName(String resturantName) {
        this.ResturantName = resturantName;
    }

    public String getResturantAddrees() {
        return ResturantAddrees;
    }

    public void setResturantAddrees(String resturantAddrees) {
        this.ResturantAddrees = resturantAddrees;
    }

    public String getResturantEmail() {
        return ResturantEmail;
    }

    public void setResturantEmail(String resturantEmail) {
        this.ResturantEmail = resturantEmail;
    }


    public String getResturantWebsite() {
        return ResturantWebsite;
    }

    public void setResturantWebsite(String resturantWebsite) {
        this.ResturantWebsite = resturantWebsite;
    }

    public String getResturantNo() {
        return ResturantNo;
    }

    public void setResturantNo(String resturantNo) {
        this.ResturantNo = resturantNo;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setUserId(String userId) {
        editor.putString(UserId, userId).commit();
    }

    public String getUserId() {
        return sp.getString(UserId, "");
    }

    public void setSocialId(String socialId) {
        editor.putString(SocialId, socialId).commit();
    }

    public String getSocialId() {
        return sp.getString(SocialId, "");
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String PRE_EMAIL) {
        this.EmailId = PRE_EMAIL;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public void setUserPic(String pic) {
        editor.putString(ProfilePic, pic).commit();
    }

    public String getUserPic() {
        return sp.getString(ProfilePic, "");
    }

    public void setIslogin(boolean flag) {
        editor.putBoolean(preIsLogin, flag).commit();
    }

    public boolean getIsLogin() {
        return sp.getBoolean(preIsLogin, false);
    }

    public void setUserLogin(boolean flag) {
        editor.putBoolean(UserLogin, flag).commit();
    }

    public boolean getUserLogin() {
        return sp.getBoolean(UserLogin, true);
    }

    public void setLoginTypeSocial(boolean flag) {
        editor.putBoolean(LoginTypeSocial, flag).commit();
    }

    public boolean getLoginTypeSocial() {
        return sp.getBoolean(LoginTypeSocial, false);
    }

    public void setGoProfile(boolean flag) {
        editor.putBoolean(GoProfile, flag).commit();
    }

    public boolean getGoProfile() {
        return sp.getBoolean(GoProfile, false);
    }

    Context _context;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    public PreferenceSettings(Context context) {
        this._context = context;
        sp = _context.getSharedPreferences(TIPPER, context.MODE_PRIVATE);
        editor = sp.edit();
    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}