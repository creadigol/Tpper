package creadigol.com.tipper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by ravi on 23-03-2017.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    LinearLayout llRegTipper, llRegServer;
    ImageView ivRegister, showPassword;
    CardView cvusername;
    EditText edtName, edtUserName, edtEmail, edtPassword;
    String type = "", regType = "", fullName, userName, email, password;
    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;
    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    ProgressDialog pDialog;
    RadioButton rbrTipper, rbrServer;
    boolean isValid = true, clickPass = true;
    CallbackManager callbackManager;
    private static GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;
    PreferenceSettings mPreferenceSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        mPreferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        llRegTipper = (LinearLayout) findViewById(R.id.llRegTipper);
        llRegServer = (LinearLayout) findViewById(R.id.llRegServer);
        cvusername = (CardView) findViewById(R.id.cvusername);
        edtName = (EditText) findViewById(R.id.edtName);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        ivRegister = (ImageView) findViewById(R.id.ivRegister);
        showPassword = (ImageView) findViewById(R.id.showPassword);
        rbrTipper = (RadioButton) findViewById(R.id.rbrTipper);
        rbrServer = (RadioButton) findViewById(R.id.rbrServer);
        ivRegister.setOnClickListener(this);
        showPassword.setOnClickListener(this);
        llRegTipper.setOnClickListener(this);
        llRegServer.setOnClickListener(this);
        rbrTipper.setOnClickListener(this);
        rbrServer.setOnClickListener(this);
        if (LoginActivity.selectType != null && !LoginActivity.selectType.equalsIgnoreCase("")) {
            if (LoginActivity.selectType.equalsIgnoreCase(Constants.TYPE_USER)) {
                cvusername.setVisibility(View.GONE);
                rbrServer.setChecked(false);
                rbrTipper.setChecked(true);
            } else if (LoginActivity.selectType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                cvusername.setVisibility(View.VISIBLE);
                rbrServer.setChecked(true);
                rbrTipper.setChecked(false);
            }
            regType = LoginActivity.selectType;
        } else {
            regType = Constants.TYPE_USER;
        }
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtUserName.getText().length() > 0) {
                    checkUsername(edtUserName.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        AppEventsLogger.activateApp(RegistrationActivity.this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setFacebookLogin();
        twitterLogin();
        GooglePlus();
        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE) {
                    checkValidation();
                    return true;
                }
                return false;
            }
        });
    }

    public void checkValidation() {

        fullName = edtName.getText().toString();
        userName = edtUserName.getText().toString();
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();

        if (fullName.equalsIgnoreCase("")) {
            edtName.setError("Enter FullName");
        } else if (regType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
            if (userName.equalsIgnoreCase("")) {
                edtUserName.setError("Enter UserName");
            }
        }
        if (!CommonUtils.isValidEmail(email)) {
            edtEmail.setError("Enter Proper Email Id");
        } else if (password.length() < 6) {
            edtPassword.setError("Password must be grater then 6 digit");
        } else {
            type = Constants.TYPE_LOGIN_LOC;
            reqRegistration(type);
        }


    }

    public void checkUsername(final String username) {
        StringRequest strReq = new StringRequest(Request.Method.POST, AppUrl.URL_CHECKUSERNAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    Log.e(TAG, "Response: " + response);
                    UserObject userObject = new UserObject();
                    try {
                        userObject.setStatus_code(responseObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        userObject.setStatus_code(0);
                    }
                    userObject.setMessage(responseObj.optString(Jsonkey.message));
                    if (userObject.getStatus_code() == 1) {
                        isValid = true;
                        edtUserName.setTextColor(Color.BLACK);
                    } else if (userObject.getStatus_code() == 0) {
                        isValid = false;
                        edtUserName.setTextColor(Color.RED);
                        edtUserName.setError(userObject.getMessage());
                    } else {
                    }

                } catch (JSONException e) {
                    CommonUtils.showToast("something went wrong !! .please try again");
                    Log.e("Error_in", "catch");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RegistrationActivity", "Error Response: " + error.getMessage());
                showTryAgainAlert("Network Error", "Please check your network and Retry", type);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserName, username);
                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };


        TipperActivity.getInstance().addToRequestQueue(strReq, TAG);
    }

    public void reqRegistration(final String type) {
        hideProgressDialog();
        final String url = AppUrl.URL_REGISTRATION;
        final ProgressDialog mProgressDialog = new ProgressDialog(RegistrationActivity.this);
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                mProgressDialog.dismiss();

                try {
                    JSONObject responseObj = new JSONObject(response);
                    Log.e(TAG, "Response: " + response);
                    UserObject userObject = new UserObject();
                    try {
                        userObject.setStatus_code(responseObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        userObject.setStatus_code(0);
                    }
                    userObject.setMessage(responseObj.optString(Jsonkey.message));
                    if (userObject.getStatus_code() == 1) {
                        JSONObject userJsonObject = responseObj.getJSONObject(Jsonkey.historyKey);
                        userObject.setUserid(userJsonObject.optString(Jsonkey.userId));
                        userObject.setFName(userJsonObject.optString(Jsonkey.fullName));
                        userObject.setEmail(userJsonObject.optString(Jsonkey.emailId));
                        userObject.setProfilePic(userJsonObject.optString(Jsonkey.profilePic));
                        if (regType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                            mPreferenceSettings.setUserLogin(false);
                            mPreferenceSettings.setLoginType(Constants.TYPE_SERVER);
                            userObject.setUserName(userJsonObject.optString(Jsonkey.userName));
                            userObject.setResturantName(userJsonObject.optString(Jsonkey.resName));
                            userObject.setResturantId(userJsonObject.optString(Jsonkey.resId));
                            mPreferenceSettings.setUserName(userObject.getUserName());
                            mPreferenceSettings.setResturantId(userObject.getResturantId());
                            mPreferenceSettings.setResturantName(userObject.getResturantName());
                        } else {
                            mPreferenceSettings.setUserLogin(true);
                            mPreferenceSettings.setLoginType(Constants.TYPE_USER);
                        }
                        mPreferenceSettings.setFullName(userObject.getFname());
                        mPreferenceSettings.setEmailId(userObject.getEmail());
                        mPreferenceSettings.setUserId(userObject.getUserid());
                        mPreferenceSettings.setIslogin(true);
                        CommonUtils.showToast(userObject.getMessage());
                        if (type == Constants.TYPE_LOGIN_LOC) {
                            mPreferenceSettings.setLoginTypeSocial(false);
                        } else {
                            mPreferenceSettings.setLoginTypeSocial(true);
                        }
                        if (regType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                            Intent i = new Intent(RegistrationActivity.this, ServerHomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(RegistrationActivity.this, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }

                    } else if (userObject.getStatus_code() == 2) {
                        CommonUtils.showToast(userObject.getMessage());
                        mProgressDialog.dismiss();
                    } else if (userObject.getStatus_code() == 3) {
                        JSONObject userJsonObject = responseObj.getJSONObject(Jsonkey.historyKey);
                        userObject.setUserid(userJsonObject.optString(Jsonkey.userId));
                        userObject.setFName(userJsonObject.optString(Jsonkey.fullName));
                        userObject.setEmail(userJsonObject.optString(Jsonkey.emailId));
                        userObject.setProfilePic(userJsonObject.optString(Jsonkey.profilePic));
                        if (regType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                            mPreferenceSettings.setLoginType(Constants.TYPE_SERVER);
                            userObject.setUserName(userJsonObject.optString(Jsonkey.userName));
                            userObject.setResturantName(userJsonObject.optString(Jsonkey.resName));
                            userObject.setResturantId(userJsonObject.optString(Jsonkey.resId));
                        }
                        mPreferenceSettings.setFullName(userObject.getFname());
                        if (regType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                            mPreferenceSettings.setUserName(userObject.getUserName());
                            mPreferenceSettings.setUserLogin(false);
                            mPreferenceSettings.setGoProfile(true);
                        }
                        mPreferenceSettings.setEmailId(userObject.getEmail());
                        mPreferenceSettings.setUserId(userObject.getUserid());
                        mPreferenceSettings.setPassword(password);
                        if (type == Constants.TYPE_LOGIN_LOC) {
                            mPreferenceSettings.setLoginTypeSocial(false);
                        } else {
                            mPreferenceSettings.setLoginTypeSocial(true);
                        }
                        Intent i = new Intent(RegistrationActivity.this, ProfileActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else if (userObject.getStatus_code() == 0) {
                        mProgressDialog.dismiss();
                        CookieSyncManager.createInstance(RegistrationActivity.this);
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeSessionCookie();
                        LoginManager.getInstance().logOut();
                        CommonUtils.showToast(userObject.getMessage());
                    } else {
                        mProgressDialog.dismiss();
                        Log.e("Error_in", "else");
                    }

                } catch (JSONException e) {
                    mProgressDialog.dismiss();
                    CookieSyncManager.createInstance(RegistrationActivity.this);
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeSessionCookie();
                    LoginManager.getInstance().logOut();
                    CommonUtils.showToast("something went wrong,Please try again");
                    Log.e("Error_in", "catch");
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RegistrationActivity", "Error Response: " + error.getMessage());
                mProgressDialog.hide();
                Log.e("Error_in", "onErrorResponse");
                CookieSyncManager.createInstance(RegistrationActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                LoginManager.getInstance().logOut();
                showTryAgainAlert("Network Error", "Please check your network and Retry", type);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_LOC)) {
                    params.put(ParamsKey.KeyLoginType, type);
                    params.put(ParamsKey.KeyRegistrationSignupType, regType);
                    params.put(ParamsKey.KeyName, fullName.toString().trim());
                    if (regType.equalsIgnoreCase(Constants.TYPE_SERVER)) {
                        params.put(ParamsKey.KeyUserName, userName.toString().trim());
                    }
                    params.put(ParamsKey.KeyEmail, email);
                    params.put(ParamsKey.KeyPassword, password);
                } else if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_FB)) {
                    params.put(ParamsKey.Key_fb_id, mPreferenceSettings.getSocialId());
                    params.put(ParamsKey.KeyName, mPreferenceSettings.getFullName());
                    params.put(ParamsKey.KeyEmail, mPreferenceSettings.getEmailId());
                    params.put(ParamsKey.KeyLoginType, Constants.TYPE_LOGIN_FB);
                    params.put(ParamsKey.KeyRegistrationSignupType, regType);
                    params.put(ParamsKey.KeyImage, mPreferenceSettings.getUserId());
                } else if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_TW)) {
                    params.put(ParamsKey.Key_tw_id, mPreferenceSettings.getSocialId());
                    params.put(ParamsKey.KeyName, mPreferenceSettings.getFullName());
                    params.put(ParamsKey.KeyLoginType, Constants.TYPE_LOGIN_TW);
                    params.put(ParamsKey.KeyRegistrationSignupType, regType);
                    params.put(ParamsKey.KeyImage, mPreferenceSettings.getUserId());
                } else if (type.equalsIgnoreCase(Constants.TYPE_LOGIN_GC)) {
                    params.put(ParamsKey.Key_gc_id, mPreferenceSettings.getSocialId());
                    params.put(ParamsKey.KeyName, mPreferenceSettings.getFullName());
                    params.put(ParamsKey.KeyEmail, mPreferenceSettings.getEmailId());
                    params.put(ParamsKey.KeyLoginType, Constants.TYPE_LOGIN_GC);
                    params.put(ParamsKey.KeyRegistrationSignupType, regType);
                }

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };


        TipperActivity.getInstance().addToRequestQueue(strReq, TAG);
    }

    public void showTryAgainAlert(String title, String message, final String type) {
        CommonUtils.showAlertWithNegativeButton(RegistrationActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CommonUtils.isNetworkAvailable())
                    reqRegistration(type);
                else
                    CommonUtils.showToast("Please check your network");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llRegTipper:
                cvusername.setVisibility(View.GONE);
                regType = Constants.TYPE_SERVER;
                rbrServer.setChecked(false);
                rbrTipper.setChecked(true);
                break;
            case R.id.llRegServer:
                cvusername.setVisibility(View.VISIBLE);
                regType = Constants.TYPE_SERVER;
                rbrServer.setChecked(true);
                rbrTipper.setChecked(false);
                break;

            case R.id.rbrTipper:
                cvusername.setVisibility(View.GONE);
                regType = Constants.TYPE_USER;
                rbrServer.setChecked(false);
                rbrTipper.setChecked(true);
                break;
            case R.id.rbrServer:
                cvusername.setVisibility(View.VISIBLE);
                regType = Constants.TYPE_SERVER;
                rbrServer.setChecked(true);
                rbrTipper.setChecked(false);
                break;
            case R.id.ivRegister:
                checkValidation();
                break;
            case R.id.showPassword:
                if (clickPass) {
                    clickPass = false;
                    edtPassword.setTransformationMethod(null);
                    showPassword.setImageResource(R.drawable.hidepassword);
                } else {
                    clickPass = true;
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    showPassword.setImageResource(R.drawable.showpassword);
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    public void setFacebookLogin() {

        if (!CommonUtils.isNetworkAvailable()) {
            CommonUtils.showToast("No Internet Connected");
        } else {
            LoginManager.getInstance().logOut();
            ImageView iv_fb = (ImageView) findViewById(R.id.btn_fb);
            iv_fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logInWithReadPermissions(RegistrationActivity.this, Arrays.asList("email", "user_birthday", "user_status"));
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
                                        reqRegistration(type);
                                    } catch (Exception e) {
                                        Log.e("Error_in", "catch");
                                        CommonUtils.showToast("Login failed,Please try again");
                                        LoginManager.getInstance().logOut();
                                        e.printStackTrace();
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
                            LoginManager.getInstance().logOut();
                            CommonUtils.showToast("Cancel");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e(TAG, "exception: " + exception);
                            Log.e("Error_in", "onErrorResponse");
                            CommonUtils.showToast("Login failed,Please try again");
                            LoginManager.getInstance().logOut();
                        }
                    });
                }
            });

        }
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

        ImageView iv_tw = (ImageView) findViewById(R.id.btn_tw);
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
                    final String username = user.getName();
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
                        final Intent intent = new Intent(RegistrationActivity.this, WebTwitterActivity.class);
                        intent.putExtra(WebTwitterActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                        startActivityForResult(intent, Constants.WEBVIEW_REQUEST_CODE);

                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                } else {
                    CommonUtils.showToast("Login failed,Please try again");
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
                showProgressDialog();
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
                reqRegistration(type);
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
                reqRegistration(type);
            } catch (Exception e) {
                Log.e("Twitter Login Failed", e.getMessage());
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("result", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            type = Constants.TYPE_LOGIN_GC;
            GoogleSignInAccount acct = result.getSignInAccount();
            mPreferenceSettings.setSocialId(acct.getId());
            mPreferenceSettings.setEmailId(acct.getEmail());
            mPreferenceSettings.setFullName(acct.getDisplayName());
            mPreferenceSettings.setUserPic(String.valueOf(acct.getPhotoUrl()));
        } else {
            Log.e("result", "failed");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
