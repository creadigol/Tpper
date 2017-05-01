package creadigol.com.tipper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Model.UserObject;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by ravi on 07-03-2017.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static Twitter twitter;
    private static RequestToken requestToken;
    SharedPreferences mSharedPreferences;
    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    ImageView passwordShow;
    CallbackManager callbackManager;
    EditText edtEmail, edtPassword;
    private final String TAG = LoginActivity.class.getSimpleName();
    ProgressDialog pDialog;
    boolean showpass = true;
    RadioButton rbTipper, rbServer;
    String loginType = "", type = "", strEmail = "", strPassword = "";
    private static GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;
    public static String selectType = "";
    PreferenceSettings mPreferenceSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mPreferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        if (mPreferenceSettings.getIsLogin()) {
            if (mPreferenceSettings.getUserLogin()) {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(LoginActivity.this, ServerHomeActivity.class);
                startActivity(i);
                finish();
            }
        } else if (mPreferenceSettings.getGoProfile()) {
            Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(i);
            finish();
        } else {
            LoginProcess(); //for first time login
            AppEventsLogger.activateApp(LoginActivity.this);
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            getHashkey();//get Key hash for pc
            setFacebookLogin();
            twitterLogin();
            GooglePlus();
        }
    }

    public void getHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("creadigol.com.tipper",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("Login", "KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void LoginProcess() {
        loginType = Constants.TYPE_USER;
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        passwordShow = (ImageView) findViewById(R.id.showPass);
        rbTipper = (RadioButton) findViewById(R.id.rbTipper);
        rbServer = (RadioButton) findViewById(R.id.rbServer);
        rbTipper.setOnClickListener(this);
        rbServer.setOnClickListener(this);
        passwordShow.setOnClickListener(this);
        LinearLayout llTipper = (LinearLayout) findViewById(R.id.llTipper);
        LinearLayout llServer = (LinearLayout) findViewById(R.id.llServer);

        llServer.setOnClickListener(this);
        llTipper.setOnClickListener(this);

        ImageView imgLogin = (ImageView) findViewById(R.id.imgLogin);

        imgLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checkvalidation();
            }
        });
        TextView tvSighup = (TextView) findViewById(R.id.tvSighup);
        tvSighup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE) {
                    Checkvalidation();
                    return true;
                }
                return false;
            }
        });
    }


    private void Checkvalidation() {
        boolean isValid = true;

        strEmail = edtEmail.getText().toString().trim();
        strPassword = edtPassword.getText().toString().trim();

        if (strEmail.equals("")) {
            if (loginType.equalsIgnoreCase(Constants.TYPE_USER)) {
                showPhoneErrorText("Enter Email");
            } else if (loginType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                showPhoneErrorText("Enter userName or Email");
            }
            isValid = false;
        } else if (!strEmail.equals("")) {
            if (loginType.equalsIgnoreCase(Constants.TYPE_USER)) {
                if (!CommonUtils.isValidEmail(strEmail)) {
                    showPhoneErrorText("Enter correnct Email ");
                    isValid = false;
                } else if (strPassword.equals("")) {
                    isValid = false;
                    showPasswordErrorText("Enter password");
                }
            } else if (strPassword.equals("")) {
                isValid = false;
                showPasswordErrorText("Enter password");
            }
        } else if (strPassword.equals("")) {
            isValid = false;
            showPasswordErrorText("Enter password");
        }
        if (isValid) {
            type = Constants.TYPE_LOGIN_LOC;
            reqUserLogin(type);
        }
    }

    void showPasswordErrorText(String text) {
        edtPassword.setError(text);
    }

    void showPhoneErrorText(String text) {
        edtEmail.setError(text);
    }


    private void showProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("loading");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
        }
        pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectType != null && selectType.equalsIgnoreCase(Constants.TYPE_USER)) {
            edtEmail.setHint("Email");
            loginType = Constants.TYPE_USER;
            rbServer.setChecked(false);
            rbTipper.setChecked(true);
        } else if (selectType != null && selectType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
            edtEmail.setHint("UserName Or Email");
            loginType = Constants.TYPE_SERVER;
            rbServer.setChecked(true);
            rbTipper.setChecked(false);
        }
    }

    private void reqUserLogin(final String type) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        HttpsTrustManager.allowAllSSL();
        String url;
        if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_LOC)) {
            url = AppUrl.URL_LOGIN;
        } else {
            url = AppUrl.URL_REGISTRATION;
        }

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "Response: " + response);
                    UserObject userObject = new UserObject();
                    try {
                        userObject.setStatus_code(jsonObject.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        userObject.setStatus_code(0);
                    }

                    userObject.setMessage(jsonObject.optString(Jsonkey.message));

                    if (userObject.getStatus_code() == 1) {
                        JSONObject userJsonObject = jsonObject.getJSONObject(Jsonkey.historyKey);
                        userObject.setUserid(userJsonObject.optString(Jsonkey.userId));
                        userObject.setFName(userJsonObject.optString(Jsonkey.fullName));
                        userObject.setEmail(userJsonObject.optString(Jsonkey.emailId));
                        userObject.setProfilePic(userJsonObject.optString(Jsonkey.profilePic));
                        if (loginType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                            mPreferenceSettings.setUserLogin(false);
                            mPreferenceSettings.setLoginType(Constants.TYPE_SERVER);
                            userObject.setUserName(userJsonObject.optString(Jsonkey.userName));
                            userObject.setResturantId(userJsonObject.optString(Jsonkey.resId));
                            userObject.setResturantName(userJsonObject.optString(Jsonkey.resName));
                        } else {
                            mPreferenceSettings.setUserLogin(true);
                            mPreferenceSettings.setLoginType(Constants.TYPE_USER);
                        }
                        saveUserDetail(userObject);
                        if (type == Constants.TYPE_LOGIN_LOC) {
                            mPreferenceSettings.setLoginTypeSocial(false);
                        } else {
                            mPreferenceSettings.setLoginTypeSocial(true);
                        }
                        if (loginType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                            Intent i = new Intent(LoginActivity.this, ServerHomeActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else if (userObject.getStatus_code() == 2) {
                        CommonUtils.showToast(userObject.getMessage());
                        pDialog.dismiss();
                    } else if (userObject.getStatus_code() == 3) {
                        mPreferenceSettings.setGoProfile(true);
                        JSONObject userJsonObject = jsonObject.getJSONObject(Jsonkey.historyKey);
                        userObject.setUserid(userJsonObject.optString(Jsonkey.userId));
                        userObject.setFName(userJsonObject.optString(Jsonkey.fullName));
                        userObject.setEmail(userJsonObject.optString(Jsonkey.emailId));
                        userObject.setProfilePic(userJsonObject.optString(Jsonkey.profilePic));
                        userObject.setUserName(userJsonObject.optString(Jsonkey.userName));
                        userObject.setResturantId(userJsonObject.optString(Jsonkey.resId));
                        mPreferenceSettings.setUserId(userObject.getUserid());
                        mPreferenceSettings.setLoginType(Constants.TYPE_SERVER);
                        if (type == Constants.TYPE_LOGIN_LOC) {
                            mPreferenceSettings.setLoginTypeSocial(false);
                        } else {
                            mPreferenceSettings.setLoginTypeSocial(true);
                        }
                        mPreferenceSettings.setUserLogin(false);
                        Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                        startActivity(i);
                        finish();
                    } else if (userObject.getStatus_code() == 0) {
                        CommonUtils.showToast(userObject.getMessage());
                        CookieSyncManager.createInstance(LoginActivity.this);
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeSessionCookie();
                        LoginManager.getInstance().logOut();
                        pDialog.dismiss();
                    } else if (userObject.getStatus_code() == 9) {
                        CommonUtils.showToast(userObject.getMessage());
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CookieSyncManager.createInstance(LoginActivity.this);
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeSessionCookie();
                    LoginManager.getInstance().logOut();
                    CommonUtils.showToast("Something went wrong!.please try again");
                    Log.e("Error_in", "catch");
                    pDialog.dismiss();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.e("Error_in", "onErrorResponse");
                error.printStackTrace();
                CookieSyncManager.createInstance(LoginActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                LoginManager.getInstance().logOut();
                showTryAgainAlert("Network Error", "Please check your network and Retry", type);
                pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_LOC)) {
                    params.put(ParamsKey.KeyEmail, strEmail);
                    params.put(ParamsKey.KeyPassword, strPassword);
                    params.put(ParamsKey.KeyLoginType, loginType);
                } else if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_FB)) {
                    params.put(ParamsKey.Key_fb_id, mPreferenceSettings.getSocialId());
                    params.put(ParamsKey.KeyName, mPreferenceSettings.getFullName());
                    params.put(ParamsKey.KeyEmail, mPreferenceSettings.getEmailId());
                    params.put(ParamsKey.KeyLoginType, Constants.TYPE_LOGIN_FB);
                    params.put(ParamsKey.KeyImage, mPreferenceSettings.getUserPic());
                    params.put(ParamsKey.KeyRegistrationSignupType, loginType);
                } else if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_TW)) {
                    params.put(ParamsKey.Key_tw_id, mPreferenceSettings.getSocialId());
                    params.put(ParamsKey.KeyName, mPreferenceSettings.getFullName());
                    params.put(ParamsKey.KeyLoginType, Constants.TYPE_LOGIN_TW);
                    params.put(ParamsKey.KeyRegistrationSignupType, loginType);
                    params.put(ParamsKey.KeyImage, mPreferenceSettings.getUserPic());
                } else if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_GC)) {
                    params.put(ParamsKey.Key_gc_id, mPreferenceSettings.getSocialId());
                    params.put(ParamsKey.KeyName, mPreferenceSettings.getFullName());
                    params.put(ParamsKey.KeyEmail, mPreferenceSettings.getEmailId());
                    params.put(ParamsKey.KeyLoginType, Constants.TYPE_LOGIN_GC);
                    params.put(ParamsKey.KeyRegistrationSignupType, loginType);
                    params.put(ParamsKey.KeyImage, mPreferenceSettings.getUserPic());
                }
                Log.e(TAG, "reqUserLogin params: " + params.toString());

                return params;
            }
        };
        // Adding request to request queue
        TipperActivity.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    public void saveUserDetail(UserObject userObject) {
        mPreferenceSettings.setIslogin(true);
        mPreferenceSettings.setFullName(userObject.getFname());
        mPreferenceSettings.setEmailId(userObject.getEmail());
        mPreferenceSettings.setUserId(userObject.getUserid());
        mPreferenceSettings.setUserPic(userObject.getProfilePic());
        mPreferenceSettings.setPassword(strPassword);
        if (loginType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
            mPreferenceSettings.setUserName(userObject.getUserName());
            mPreferenceSettings.setResturantId(userObject.getResturantId());
            mPreferenceSettings.setResturantName(userObject.getResturantName());
        }
    }

    public void showTryAgainAlert(String title, String message, final String type) {
        CommonUtils.showAlertWithNegativeButton(LoginActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CommonUtils.isNetworkAvailable())
                    reqUserLogin(type);
                else
                    CommonUtils.showToast("Please check your network");
            }
        });
    }

    public void twitterLogin() {
        initTwitterConfigs();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
            CommonUtils.showToast("Twitter key and secret not configured");
            return;
        }

        ImageView iv_tw = (ImageView) findViewById(R.id.btn_login);
        mSharedPreferences = getSharedPreferences(Constants.PREF_NAME, 0);
        final boolean isLoggedIn = mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);


        if (!isLoggedIn) {
            Uri uri = getIntent().getData();

            if (uri != null && uri.toString().startsWith(callbackUrl)) {

                String verifier = uri.getQueryParameter(oAuthVerifier);

                try {

					/* Getting oAuth authentication token */
                    twitter4j.auth.AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    /* Getting user id form access token */
                    long userID = accessToken.getUserId();
                    final User user = twitter.showUser(userID);
                } catch (Exception e) {
                    Log.e("Failed to ", "login Twitter!!" + e.getMessage());
                }
            }
        }
        iv_tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoggedIn) {
                    final ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(consumerKey);
                    builder.setOAuthConsumerSecret(consumerSecret);

                    final Configuration configuration = builder.build();
                    final TwitterFactory factory = new TwitterFactory(configuration);
                    twitter = factory.getInstance();

                    try {
                        requestToken = twitter.getOAuthRequestToken(callbackUrl);
                        final Intent intent = new Intent(LoginActivity.this, WebTwitterActivity.class);
                        intent.putExtra(WebTwitterActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                        startActivityForResult(intent, Constants.WEBVIEW_REQUEST_CODE);

                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                } else {
                    CommonUtils.showToast("login failed try again");
                }

            }
        });
    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    public void setFacebookLogin() {
        if (!CommonUtils.isNetworkAvailable()) {
            CommonUtils.showToast("No Internet Connected");
        } else {
            LoginManager.getInstance().logOut();
            ImageView iv_fb = (ImageView) findViewById(R.id.fb);

            iv_fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "user_birthday", "user_status"));
                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            showProgressDialog();
                            AccessToken accessToken = loginResult.getAccessToken();
                            String userId = loginResult.getAccessToken().getUserId();
                            final String profileImgUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";

                            Log.e(TAG, "facebook: " + profileImgUrl);

                            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        type = Constants.TYPE_LOGIN_FB;
                                        String id, name, email;
                                        id = object.optString("id");
                                        name = object.optString("name");
                                        email = object.optString("email");
                                        mPreferenceSettings.setEmailId(email);
                                        mPreferenceSettings.setFullName(name);
                                        mPreferenceSettings.setUserPic(profileImgUrl);
                                        mPreferenceSettings.setSocialId(id);
                                        reqUserLogin(type);
                                        hideProgressDialog();
                                    } catch (Exception e) {
                                        Log.e("Error_in", "catch");
                                        CommonUtils.showToast("login failed try again");
                                        LoginManager.getInstance().logOut();
                                        e.printStackTrace();
                                        hideProgressDialog();
                                    }
                                }

                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,email");
                            Log.e("pare", "" + parameters);
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            CommonUtils.showToast("Cancel");
                            LoginManager.getInstance().logOut();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e(TAG, "exception: " + exception);
                            Log.e("Error_in", "onErrorResponse");
                            CommonUtils.showToast("Failed");
                            LoginManager.getInstance().logOut();
                        }
                    });
                }
            });

        }
    }

    public void GooglePlus() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ImageView imgGooglePlus = (ImageView) findViewById(R.id.imgGooglePlus);
        imgGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = com.google.android.gms.auth.api.Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = com.google.android.gms.auth.api.Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                reqUserLogin(type);
            } else {
            }

        }
        if (resultCode == Activity.RESULT_OK) {
            String verifier = data.getExtras().getString(oAuthVerifier);
            try {
                type = Constants.TYPE_LOGIN_TW;
                twitter4j.auth.AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                long userID = accessToken.getUserId();
                final User user = twitter.showUser(userID);
                mPreferenceSettings.setFullName(user.getName());
                mPreferenceSettings.setUserPic(user.getProfileImageURL());
                mPreferenceSettings.setSocialId(String.valueOf(user.getId()));
                reqUserLogin(type);
            } catch (Exception e) {
                Log.e("Twitter Login Failed", e.getMessage());
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("result", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            type = Constants.TYPE_LOGIN_GC;
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mPreferenceSettings.setSocialId(acct.getId());
            mPreferenceSettings.setEmailId(acct.getEmail());
            mPreferenceSettings.setFullName(acct.getDisplayName());
            if (acct.getPhotoUrl() != null) {
                mPreferenceSettings.setUserPic(String.valueOf(acct.getPhotoUrl()));
            } else {
                mPreferenceSettings.setUserPic("");
            }
        } else {
            Log.e("result", "failed");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llTipper:
                edtEmail.setHint("Email");
                loginType = Constants.TYPE_USER;
                rbServer.setChecked(false);
                rbTipper.setChecked(true);
                selectType = Constants.TYPE_USER;
                break;
            case R.id.llServer:
                edtEmail.setHint("UserName Or Email");
                loginType = Constants.TYPE_SERVER;
                rbServer.setChecked(true);
                rbTipper.setChecked(false);
                selectType = Constants.TYPE_SERVER;
                break;

            case R.id.rbTipper:
                edtEmail.setHint("Email");
                loginType = Constants.TYPE_USER;
                rbServer.setChecked(false);
                rbTipper.setChecked(true);
                selectType = Constants.TYPE_USER;
                break;
            case R.id.rbServer:
                edtEmail.setHint("UserName Or Email");
                loginType = Constants.TYPE_SERVER;
                rbServer.setChecked(true);
                rbTipper.setChecked(false);
                selectType = Constants.TYPE_SERVER;
                break;
            case R.id.showPass:
                if (showpass) {
                    showpass = false;
                    edtPassword.setTransformationMethod(null);
                    passwordShow.setImageResource(R.drawable.hidepassword);
                } else {
                    showpass = true;
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    passwordShow.setImageResource(R.drawable.showpassword);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TipperActivity.getInstance().cancelPendingRequests(TAG);
    }
}
