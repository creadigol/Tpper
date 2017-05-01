package creadigol.com.tipper;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Rating;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.text.TextRecognizer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.philjay.valuebar.ValueBar;
import com.philjay.valuebar.ValueBarSelectionListener;
import com.philjay.valuebar.colors.RedToGreenFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Halper.MyApplication;
import Model.RatingItem;
import Model.ReviewHistoryItem;
import Model.UserObject;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.EnglishNumberToWords;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;

/**
 * Created by ravi on 23-03-2017.
 */

public class
ProfileActivity extends AppCompatActivity implements ValueBarSelectionListener, View.OnClickListener {
    ImageView profilepic, BackprofilePic;
    public static ImageView editProfileDeatils;
    private ValueBar[] mValueBars = new ValueBar[4];
    private static final int ACTION_TAKE_PHOTO = 1;
    private static final int ACTION_SELECT_PHOTO = 2;
    private String mCameraPhotoPath, mGallaryPhotoPath;
    File f, mProFile;
    RelativeLayout rvPassword;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    boolean isValid = true;
    public static boolean isSave = false;
    EditText edServername, edServerUsername, edServerEmailId, edServerPassword;
    LinearLayout llResturantDetails, llEmail, llResturantContact, llResturantWebsite, llValueBars;
    CardView cvRatingBar, cvRatingCount;
    Button selectResturant;
    PreferenceSettings preferenceSettings;
    TextView resturantName, resturantAddress, resturantEmail, resturantNumber, resturantWebsite, totalTip, totalReview, totalReview1, avrageRating, avrageRatingRound, serverName;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static Bitmap mBitmap = null, mBitmapCrop = null;
    public static String imagePath, resturantId = "", ServerName, stServerUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pofile);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        profilepic = (ImageView) findViewById(R.id.profilepic);
        BackprofilePic = (ImageView) findViewById(R.id.BackprofilePic);
        editProfileDeatils = (ImageView) findViewById(R.id.editProfileDeatils);
        resturantName = (TextView) findViewById(R.id.resturantName);
        resturantAddress = (TextView) findViewById(R.id.resturantAddress);
        resturantEmail = (TextView) findViewById(R.id.emailAddress);
        resturantWebsite = (TextView) findViewById(R.id.website);
        resturantNumber = (TextView) findViewById(R.id.telephone);
        totalTip = (TextView) findViewById(R.id.totalTips);
        totalReview = (TextView) findViewById(R.id.totalReview);
        totalReview1 = (TextView) findViewById(R.id.totalReview1);
        avrageRating = (TextView) findViewById(R.id.avrageRating);
        avrageRatingRound = (TextView) findViewById(R.id.avrageRatingRound);
        serverName = (TextView) findViewById(R.id.serverName);
        llResturantDetails = (LinearLayout) findViewById(R.id.llResturantDetails);
        edServername = (EditText) findViewById(R.id.edServerName);
        edServerUsername = (EditText) findViewById(R.id.edServerUserName);
        edServerEmailId = (EditText) findViewById(R.id.edServerEmailId);
        edServerPassword = (EditText) findViewById(R.id.edServerPassword);
        rvPassword = (RelativeLayout) findViewById(R.id.rvPassword);
        llEmail = (LinearLayout) findViewById(R.id.llEmail);
        llResturantContact = (LinearLayout) findViewById(R.id.llRaturantContact);
        llResturantWebsite = (LinearLayout) findViewById(R.id.llResturantWebsite);
        llValueBars = (LinearLayout) findViewById(R.id.llValueBars);

        ImageView ivServerName = (ImageView) findViewById(R.id.editServerName);
        ImageView ivServerUsername = (ImageView) findViewById(R.id.editServerUserName);
        ImageView ivServerPassword = (ImageView) findViewById(R.id.editserverPassword);
        ImageView ivServerEmail = (ImageView) findViewById(R.id.editServerEmail);
        ImageView ivLogout = (ImageView) findViewById(R.id.logout);
        ivServerName.setOnClickListener(this);
        ivServerUsername.setOnClickListener(this);
        ivServerPassword.setOnClickListener(this);
        ivServerEmail.setOnClickListener(this);
        ivLogout.setOnClickListener(this);

        profilepic.setOnClickListener(this);
        editProfileDeatils.setOnClickListener(this);
        ImageView ivResturant = (ImageView) findViewById(R.id.editResturant);
        ivResturant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FindResturantActivity.class);
                startActivity(intent);
            }
        });
        selectResturant = (Button) findViewById(R.id.selectResturant);
        selectResturant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FindResturantActivity.class);
                startActivity(intent);
            }
        });
        mValueBars[0] = (ValueBar) findViewById(R.id.valueBar1);
        mValueBars[1] = (ValueBar) findViewById(R.id.valueBar2);
        mValueBars[2] = (ValueBar) findViewById(R.id.valueBar3);
        mValueBars[3] = (ValueBar) findViewById(R.id.valueBar4);
        toolbar();

        //for marshmallaw permission when first time server login
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ArrayList<String> permissionsList = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.CAMERA);
            }

            if (permissionsList != null && permissionsList.size() > 0) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.permission_dailog);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

                TextView permissionNo = (TextView) dialog.findViewById(R.id.permissionNo);
                // if decline button is clicked, close the custom dialog
                permissionNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                        finishAffinity();
                    }
                });
                TextView permissionYes = (TextView) dialog.findViewById(R.id.permissionYes);
                // if decline button is clicked, close the custom dialog
                permissionYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 100);
                    }
                });
                dialog.getWindow().getAttributes().windowAnimations =
                        R.style.dialog_animation;

                dialog.show();

            } else {
                getProfileDetails();
            }
        } else {
            getProfileDetails();
        }

        if (preferenceSettings.getLoginTypeSocial() == true) {
            rvPassword.setVisibility(View.GONE);
        } else {
            rvPassword.setVisibility(View.VISIBLE);
        }
        if (preferenceSettings.getPassword() != null && !preferenceSettings.getPassword().equalsIgnoreCase("")) {
            edServerPassword.setText(preferenceSettings.getPassword());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            final ArrayList<String> permissionsList = new ArrayList<>();
            if (grantResults != null && grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    Log.e("In Permission", " BottomTabActivity " + permissions[i] + " result " + grantResults[i]);
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("sss", "fdg" + grantResults[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionsList.add(permissions[i]);
                    }
                }
            } else {
            }

            if (permissionsList != null && permissionsList.size() > 0) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.permission_dailog);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

                TextView permissionNo = (TextView) dialog.findViewById(R.id.permissionNo);
                // if decline button is clicked, close the custom dialog
                permissionNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                        finishAffinity();
                    }
                });
                TextView permissionYes = (TextView) dialog.findViewById(R.id.permissionYes);
                // if decline button is clicked, close the custom dialog
                permissionYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 100);
                    }
                });
                dialog.getWindow().getAttributes().windowAnimations =
                        R.style.dialog_animation;

                dialog.show();

            } else {
                getProfileDetails();
            }
        } else {
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
            alert.setTitle("Sorry");
            alert.setCancelable(false);
            alert.setMessage("Please give all permision ");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
                }
            });

            alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finishAffinity();
                }
            });
            alert.show();
        }
    }

    public void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    public void onSelectionUpdate(float v, float v1, float v2, ValueBar valueBar) {

    }

    @Override
    public void onValueSelected(float v, float v1, float v2, ValueBar valueBar) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setprofile();
        if (resturantId != null && !resturantId.equalsIgnoreCase("")) {
            llResturantDetails.setVisibility(View.VISIBLE);
            selectResturant.setVisibility(View.GONE);
        } else {
            llResturantDetails.setVisibility(View.GONE);
            selectResturant.setVisibility(View.VISIBLE);
        }
        if (mBitmapCrop != null) {
            profilepic.setImageBitmap(mBitmapCrop);
            BackprofilePic.setImageBitmap(mBitmapCrop);
        } else if (mBitmap != null) {
            profilepic.setImageBitmap(mBitmap);
            BackprofilePic.setImageBitmap(mBitmap);
        }
        if (preferenceSettings.getResturantName() != null && !preferenceSettings.getResturantName().equalsIgnoreCase("")) {
            resturantName.setText(preferenceSettings.getResturantName());
        } else {
            resturantName.setText("N/A");
        }
        if (preferenceSettings.getResturantAddrees() != null && !preferenceSettings.getResturantAddrees().equalsIgnoreCase("")) {
            resturantAddress.setText(preferenceSettings.getResturantAddrees());
        } else {
            resturantAddress.setText("N/A");
        }
        if (preferenceSettings.getResturantEmail() != null && !preferenceSettings.getResturantEmail().equalsIgnoreCase("")) {
            llEmail.setVisibility(View.VISIBLE);
            resturantEmail.setText(preferenceSettings.getResturantEmail());
        } else {
            llEmail.setVisibility(View.GONE);
        }
        if (preferenceSettings.getResturantNo() != null && !preferenceSettings.getResturantNo().equalsIgnoreCase("")) {
            llResturantContact.setVisibility(View.VISIBLE);
            resturantNumber.setText(preferenceSettings.getResturantNo());
        } else {
            llResturantContact.setVisibility(View.GONE);
        }
        if (preferenceSettings.getResturantWebsite() != null && !preferenceSettings.getResturantWebsite().equalsIgnoreCase("")) {
            llResturantWebsite.setVisibility(View.VISIBLE);
            resturantWebsite.setText(preferenceSettings.getResturantWebsite());
        } else {
            llResturantWebsite.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profilepic:
                showEditProfileDialogDailog();
                break;
            case R.id.editProfileDeatils:
                imagePath = mCameraPhotoPath;
                ServerName = edServername.getText().toString();
                stServerUserName = edServerUsername.getText().toString();
                if (resturantId.equalsIgnoreCase("")) {
                    checkData("Please choose your restaurant.try next time");
                } else if (isValid == false) {
                    checkData("userName alreay exits.try diffrent");
                } else if (stServerUserName.equalsIgnoreCase("")) {
                    checkData("Please enter userName.try next time");
                } else if (ServerName.equalsIgnoreCase("")) {
                    checkData("Please enter serverName.try next time");
                } else {
                    EditProfile(0);
                }

                break;
            case R.id.editServerName:
                editProfileDeatils.setVisibility(View.VISIBLE);
                isSave = true;
                edServername.setEnabled(true);
                edServername.setFocusable(true);
                edServerUsername.setEnabled(false);
                edServerEmailId.setEnabled(false);
                edServername.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edServername, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.editServerUserName:
                editProfileDeatils.setVisibility(View.VISIBLE);
                isSave = true;
                edServerUsername.setEnabled(true);
                edServerUsername.setFocusable(true);
                edServername.setEnabled(false);
                edServerEmailId.setEnabled(false);
                edServerUsername.requestFocus();
                InputMethodManager i = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                i.showSoftInput(edServerUsername, InputMethodManager.SHOW_FORCED);
                break;

            case R.id.editServerEmail:
                editProfileDeatils.setVisibility(View.VISIBLE);
                isSave = true;
                edServerEmailId.setEnabled(true);
                edServerEmailId.setFocusable(true);
                edServername.setEnabled(false);
                edServerUsername.setEnabled(false);
                edServerEmailId.requestFocus();
                InputMethodManager id = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                id.showSoftInput(edServerEmailId, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.editserverPassword:
                changePassword();
                break;
            case R.id.logout:
                logoutAlear();
                break;
        }
    }

    private void logoutAlear() {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.logout_dailog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialog.show();

        TextView tvNo = (TextView) dialog.findViewById(R.id.tvNo);
        // if decline button is clicked, close the custom dialog
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        TextView tvYes = (TextView) dialog.findViewById(R.id.tvYes);
        // if decline button is clicked, close the custom dialog
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                preferenceSettings.clearSession();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                CommonUtils.showToast("Logout successfully");
                finish();

            }
        });
        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation;

        dialog.show();
    }

    public void changePassword() {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_password);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText oldText = (EditText) dialog.findViewById(R.id.oldPassword);
        final EditText newText = (EditText) dialog.findViewById(R.id.newPassword);
        final EditText confirmText = (EditText) dialog.findViewById(R.id.confPassword);

        Button mButtonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogButton = (Button) dialog.findViewById(R.id.buttonOk);
        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation;

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String old_pwd = oldText.getText().toString();
                String new_pwd = newText.getText().toString();
                String confirm_pwd = confirmText.getText().toString();
                if (old_pwd.equals("")) {
                    oldText.setError("Enter password");
                } else if (new_pwd.equals("")) {
                    newText.setError("Enter password ");
                } else if (new_pwd.equals(old_pwd)) {
                    newText.setError("New password and old password not to be same");
                } else if (!confirm_pwd.equals(new_pwd) | confirm_pwd.equals("")) {
                    confirmText.setError("Confirm password not match");
                } else {
                    setNewPassword(old_pwd, new_pwd);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    void setNewPassword(final String oldpassword, final String newPassword) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("change Password...");
        pDialog.setCancelable(false);
        pDialog.show();
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_CHANGEPASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                try {
                    JSONObject jsonObj = new JSONObject(Response);

                    String status_code = jsonObj.optString("status_code");
                    String massage = jsonObj.optString("message");
                    if (status_code.equalsIgnoreCase("1")) {
                        pDialog.dismiss();
                        edServerPassword.setText(newPassword);
                        preferenceSettings.setPassword(newPassword);
                        editProfileDeatils.setVisibility(View.VISIBLE);
                        isSave = true;
                        CommonUtils.showToast(massage);
                    } else if (status_code.equalsIgnoreCase("0")) {
                        pDialog.dismiss();
                        Log.e("Error_in", "else");
                        CommonUtils.showToast(massage);
                    } else {
                        pDialog.dismiss();
                        Log.e("Error_in", "else");
                    }
                } catch (JSONException e) {
                    pDialog.dismiss();
                    Log.e("Error_in", "onErrorResponse");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("Error_in", "onErrorResponse");
                showTryAgainAlert("Network error", "please check your network try again");
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_SERVER);
                params.put(ParamsKey.KeyOldPassword, oldpassword);
                params.put(ParamsKey.KeyNewPassword, newPassword);
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "userData");

    }

    public void setprofile() {
        mProFile = MyApplication.getInstance(ProfileActivity.this).getProfile();
        Log.e("profile in PullZoom", mProFile.getAbsolutePath());
        if (mProFile.exists()) {
            Bitmap bMap = BitmapFactory.decodeFile(mProFile.getAbsolutePath());
            profilepic.setImageBitmap(bMap);
            BackprofilePic.setImageBitmap(bMap);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private void showEditProfileDialogDailog() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            ACTION_SELECT_PHOTO);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (actionCode) {
            case ACTION_TAKE_PHOTO:
                File f = null;

                try {
                    f = setUpCameraPhotoFile();
                    mCameraPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCameraPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpCameraPhotoFile() throws IOException {

        f = createImageFile();
        mCameraPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
                albumF);
        return imageF;
    }

    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name),
                    "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == ProfileActivity.this.RESULT_OK) {
                    handleBigCameraPhoto();
                }

                break;
            } // ACTION_TAKE_PHOTO

            case ACTION_SELECT_PHOTO: {
                if (resultCode == ProfileActivity.this.RESULT_OK) {
                    try {

                        Uri pickedImage = data.getData();
                        // Let's read picked image path using content resolver
                        String[] filePath = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(pickedImage,
                                filePath, null, null, null);
                        cursor.moveToFirst();
                        String imagePath = cursor.getString(cursor
                                .getColumnIndex(filePath[0]));

                        // Now we need to set the GUI ImageView data with data read from
                        // the picked file.
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        setGalleryPic(bitmap);
                        mCameraPhotoPath = imagePath;

                        // At the end remember to close the cursor or you will end with
                        // the RuntimeException!
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            }

        }

    }

    private void handleBigCameraPhoto() {

        if (mCameraPhotoPath != null) {
            setCameraPic();
            // galleryAddPic(mCameraPhotoPath);
        }
    }

    private void setCameraPic() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        f = new File(mCameraPhotoPath);
        if (f.exists()) {
            CommonUtils.showToast("Image file is exist");
            Log.e("", "mCameraPhotoPath :: " + mCameraPhotoPath);
        }
        Bitmap bitmap = BitmapFactory.decodeFile(mCameraPhotoPath);
//        bitmap = resize(bitmap);
        createFileFromBitmap(bitmap, mCameraPhotoPath);
        mBitmap = bitmap;
        if (mBitmap != null) {
            Intent intent = new Intent(getApplicationContext(), DisplayCropingImage.class);
            startActivity(intent);
            isSave = true;
        }
        editProfileDeatils.setVisibility(View.VISIBLE);


    }

    public void setGalleryPic(Bitmap bitmap) {
        try {
            f = setUpGalleryPhotoFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mGallaryPhotoPath != null && mGallaryPhotoPath.trim().length() > 0) {
//            bitmap = resize(bitmap);
            createFileFromBitmap(bitmap, mGallaryPhotoPath);
            mBitmap = bitmap;
            if (mBitmap != null) {
                Intent intent = new Intent(getApplicationContext(), DisplayCropingImage.class);
                startActivity(intent);
                isSave = true;
            }
            editProfileDeatils.setVisibility(View.VISIBLE);
        }
    }

    public void createFileFromBitmap(Bitmap _bitmapScaled, String imgPath) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        _bitmapScaled.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        // you can create a new file name "test.jpg" in sdcard folder.
        if (imgPath != null && imgPath.trim().length() > 0) {
            try {
                f = new File(imgPath);
                if (!f.exists())
                    f.createNewFile();
                // write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());

                // remember close de FileOutput
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            galleryAddPic(imgPath);
        }

    }

    private void galleryAddPic(String imgPath) {
        Intent mediaScanIntent = new Intent(
                "android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(imgPath);
        if (f.exists()) {
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }
    }

    public Bitmap resize(Bitmap btmp) {
        int bitmapWidth = btmp.getWidth();
        Bitmap bitmap = null;

        int targetW = profilepic.getWidth();
        int targetH = profilepic.getHeight();

        // scale According to WIDTH
        int scaledWidth;
        if (targetW > 0) {
            scaledWidth = targetW;
        } else {
            scaledWidth = bitmapWidth / 2;
        }

        int scaledHeight = (scaledWidth * btmp.getHeight()) / bitmapWidth;
        try {
            bitmap = Bitmap.createScaledBitmap(btmp, scaledWidth, scaledHeight,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private File setUpGalleryPhotoFile() throws IOException {
        f = createImageFile();
        mGallaryPhotoPath = f.getAbsolutePath();
        return f;
    }


    void getProfileDetails() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    ReviewHistoryItem reviewHistoryItem = new ReviewHistoryItem();

                    try {
                        reviewHistoryItem.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        reviewHistoryItem.setStatus_code(0);

                    }
                    reviewHistoryItem.setMessage(jsonObj.getString(Jsonkey.message));

                    if (reviewHistoryItem.getStatus_code() == 1) {
                        JSONObject jsonObjectOutlet = jsonObj.getJSONObject(Jsonkey.historyKey);

                        reviewHistoryItem.setServerName(jsonObjectOutlet.optString(Jsonkey.fullName));
                        reviewHistoryItem.setServerEmail(jsonObjectOutlet.optString(Jsonkey.emailId));
                        reviewHistoryItem.setServerUserName(jsonObjectOutlet.optString(Jsonkey.uName));
                        reviewHistoryItem.setServerImage(jsonObjectOutlet.optString(Jsonkey.image));
                        reviewHistoryItem.setRestaurantName(jsonObjectOutlet.optString(Jsonkey.restaurantName));
                        reviewHistoryItem.setRestaurantAddress(jsonObjectOutlet.optString(Jsonkey.restaurantAddress));
                        reviewHistoryItem.setRestaurantWebsite(jsonObjectOutlet.optString(Jsonkey.restaurantWebsite));
                        reviewHistoryItem.setRestaurantNumber(jsonObjectOutlet.optString(Jsonkey.restaurantNumber));
                        reviewHistoryItem.setRestaurantEmail(jsonObjectOutlet.optString(Jsonkey.restaurantEmail));
                        reviewHistoryItem.setTip(jsonObjectOutlet.optString(Jsonkey.tip));
                        reviewHistoryItem.setMenu_knowledge(jsonObjectOutlet.optString(Jsonkey.menu_knowledge));
                        reviewHistoryItem.setHospitality(jsonObjectOutlet.optString(Jsonkey.hospitality));
                        reviewHistoryItem.setAppearance(jsonObjectOutlet.optString(Jsonkey.appearance));
                        reviewHistoryItem.setAverage(jsonObjectOutlet.optString(Jsonkey.average));
                        reviewHistoryItem.setTotal_review(jsonObjectOutlet.optString(Jsonkey.total_review));
                        reviewHistoryItem.setTotal_tip(jsonObjectOutlet.optString(Jsonkey.total_tip));
                        reviewHistoryItem.setRestaurantId(jsonObjectOutlet.optString(Jsonkey.restaurantId));
                        try {
                            JSONObject Rating = jsonObj.getJSONObject(Jsonkey.ratingcount);
                            reviewHistoryItem.setReviewItem(Rating);
                        } catch (JSONException e) {
                            reviewHistoryItem.setReviewItem(null);
                        }
                        setResultView(reviewHistoryItem);
                        pDialog.dismiss();
                    } else if (reviewHistoryItem.getStatus_code() == 0) {
                        Log.e("Error_in", "else");
                        pDialog.dismiss();
                        CommonUtils.showToast(reviewHistoryItem.getMessage());
                    } else {
                        pDialog.dismiss();
                        Log.e("Error_in", "else");
                    }
                } catch (JSONException e) {
                    pDialog.dismiss();
                    Log.e("Error_in", "onErrorResponse");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("Error_in", "onErrorResponse");
                showTryAgainAlert("Network error", "please check your network try again");
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_SERVER);
                Log.e("search", "Posting params: " + params.toString());
                return params;
            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "review");
    }

    public void showTryAgainAlert(String title, String message) {
        CommonUtils.showAlertWithNegativeButton(ProfileActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonUtils.isNetworkAvailable())
                    getProfileDetails();
                else
                    CommonUtils.showToast("Please check your internet connection");
            }
        });

    }

    public void setResultView(final ReviewHistoryItem resultView) {

        RatingBar ratingCount = (RatingBar) findViewById(R.id.ratingCount);

        if (resultView.getRestaurantId() != null && !resultView.getRestaurantId().equalsIgnoreCase("")) {
            resturantId = resultView.getRestaurantId();
            llResturantDetails.setVisibility(View.VISIBLE);
            selectResturant.setVisibility(View.GONE);
        } else {
            llResturantDetails.setVisibility(View.GONE);
            selectResturant.setVisibility(View.VISIBLE);
        }
        serverName.setText(CommonUtils.getCapitalize(resultView.getServerName()));
        edServername.setText(CommonUtils.getCapitalize(resultView.getServerName()));
        ImageView ivServerUserName = (ImageView) findViewById(R.id.editServerUserName);
        if (resultView.getServerUserName().equalsIgnoreCase("")) {
            ivServerUserName.setVisibility(View.VISIBLE);
        } else {
            ivServerUserName.setVisibility(View.GONE);
            edServerUsername.setText(resultView.getServerUserName());
        }
        edServerEmailId.setText(resultView.getServerEmail());
        resturantName.setText(resultView.getRestaurantName());
        resturantAddress.setText(resultView.getRestaurantAddress());


        if (resultView.getRestaurantEmail() != null && !resultView.getRestaurantEmail().equalsIgnoreCase("")) {
            llEmail.setVisibility(View.VISIBLE);
            resturantEmail.setText(resultView.getRestaurantEmail());
        } else {
            llEmail.setVisibility(View.GONE);
        }
        if (resultView.getRestaurantNumber() != null && !resultView.getRestaurantNumber().equalsIgnoreCase("")) {
            llResturantContact.setVisibility(View.VISIBLE);
            resturantNumber.setText(resultView.getRestaurantNumber());
        } else {
            llResturantContact.setVisibility(View.GONE);
        }
        if (resultView.getRestaurantWebsite() != null && !resultView.getRestaurantWebsite().equalsIgnoreCase("")) {
            llResturantWebsite.setVisibility(View.VISIBLE);
            resturantWebsite.setText(resultView.getRestaurantWebsite());
        } else {
            llResturantWebsite.setVisibility(View.GONE);
        }
        preferenceSettings.setUserPic(resultView.getServerImage());
        preferenceSettings.setEmailId(resultView.getServerEmail());
        setProfileImage(resultView.getServerImage(), profilepic);
        setProfileBack(resultView.getServerImage(), BackprofilePic);
        TextView totalRatingCount = (TextView) findViewById(R.id.totalRatingCount);
        cvRatingBar = (CardView) findViewById(R.id.cvRatingBar);
        cvRatingCount = (CardView) findViewById(R.id.cvRatingCount);
        if (resultView.getTotal_tip() != null && !resultView.getTotal_tip().equalsIgnoreCase("")) {
            if (resultView.getTotal_tip().toString().length() == 3) {
                totalRatingCount.setText("Hundred");
                String x = resultView.getTotal_tip();
                x = x.substring(0, 1) + "." + x.substring(1, 2);
                totalTip.setText(x);
            } else if (resultView.getTotal_tip().toString().length() == 4) {
                totalRatingCount.setText("thousand");
                String x = resultView.getTotal_tip();
                x = x.substring(0, 1) + "." + x.substring(1, 2);
                totalTip.setText(x);
            } else if (resultView.getTotal_tip().toString().length() == 5) {
                totalRatingCount.setText("Lakh");
                String x = resultView.getTotal_tip();
                x = x.substring(0, 1) + "." + x.substring(1, 2);
                totalTip.setText(x);
            } else {
                totalTip.setText(resultView.getTotal_tip());
                totalRatingCount.setText(CommonUtils.getCapitalize(EnglishNumberToWords.convertLessThanOneThousand(Integer.parseInt(resultView.getTotal_tip()))));
            }
        } else {
            totalTip.setText("0");
        }
        if (resultView.getTotal_review() != null && !resultView.getTotal_review().equalsIgnoreCase("")) {
            cvRatingCount.setVisibility(View.VISIBLE);
            if (resultView.getTotal_review().length() == 4) {
                String x = resultView.getTotal_review();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                totalReview.setText(x);
                totalReview1.setText(x);
            } else if (resultView.getTotal_review().length() == 5) {
                String x = resultView.getTotal_review();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                totalReview.setText(x);
                totalReview1.setText(x);
            } else if (resultView.getTotal_review().length() == 6) {
                String x = resultView.getTotal_review();
                x = x.substring(0, 1) + "," + x.substring(1, 3) + "," + x.substring(3, x.length());
                totalReview.setText(x);
                totalReview1.setText(x);
            } else {
                totalReview.setText(resultView.getTotal_review());
                totalReview1.setText(resultView.getTotal_review());
            }
        } else {
            cvRatingCount.setVisibility(View.GONE);
        }
        if (resultView.getAverage() != null && !resultView.getAverage().equalsIgnoreCase("")) {
            cvRatingBar.setVisibility(View.VISIBLE);
            avrageRating.setText(resultView.getAverage());
        } else {
            cvRatingBar.setVisibility(View.GONE);
        }
        if (resultView.getAverage() != null && !resultView.getAverage().equalsIgnoreCase("")) {
            avrageRatingRound.setText(resultView.getAverage());
        } else {
            avrageRatingRound.setText("0");
        }
        if (resultView.getAverage() != null && !resultView.getAverage().equalsIgnoreCase("")) {
            ratingCount.setVisibility(View.VISIBLE);
            float rating = Float.parseFloat(resultView.getAverage());
            if (rating >= 4) {
                ratingCount.setRating(rating + 1);
            } else {
                ratingCount.setRating(rating);
            }
        } else {
            ImageView ivEmptyDoller = (ImageView) findViewById(R.id.ivEmptyDoller);
            ivEmptyDoller.setVisibility(View.VISIBLE);
            ratingCount.setVisibility(View.GONE);
        }

        if (resultView.getReviewItem() != null && resultView.getReviewItem().size() > 0) {
            llValueBars.setVisibility(View.VISIBLE);
            setServerReview(resultView.getReviewItem());
        }
        edServerUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edServerUsername.getText().length() > 0) {
                    checkUsername(edServerUsername.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    void setServerReview(ArrayList<RatingItem> resultView) {
        for (RatingItem ratingItem : resultView) {
            for (ValueBar bar : mValueBars) {
                bar.setMinMax(0, 100);
                bar.setInterval(1f);
                bar.setClickable(false);
                bar.setSelected(false);
                bar.setDrawBorder(false);
                bar.setEnabled(false);
                bar.setTouchEnabled(false);
                bar.setValueBarSelectionListener(this);
                bar.setValueTextFormatter(new MyCustomValueTextFormatter());
                bar.setColorFormatter(new RedToGreenFormatter());
                bar.setOverlayColor(Color.BLACK);
                bar.setDrawMinMaxText(bar.isDrawMinMaxTextEnabled() ? false : true);
                bar.setDrawValueText(bar.isDrawValueTextEnabled() ? false : true);
                bar.invalidate();
            }
            mValueBars[0].animate(0, Float.parseFloat(ratingItem.getRating1()), 1500);
            mValueBars[1].animate(0, Float.parseFloat(ratingItem.getRating2()), 1500);
            mValueBars[2].animate(0, Float.parseFloat(ratingItem.getRating3()), 1500);
            mValueBars[3].animate(0, Float.parseFloat(ratingItem.getRating4()), 1500);
        }
    }

    void EditProfile(final int type) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Update Profile...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        MultipartRequest jsonObjectRequest = new MultipartRequest(AppUrl.URL_EDITPROFILE, Constants.TYPE_SERVER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("User update response", response);

                mProgressDialog.dismiss();

                try {
                    JSONObject jsonObj = new JSONObject(response);

                    String status_code = jsonObj.optString("status_code");
                    String massage = jsonObj.optString("message");
                    if (status_code.equalsIgnoreCase("1")) {
                        String imagePath = jsonObj.optString("photo");
                        CommonUtils.showToast(massage);
                        if (!imagePath.equalsIgnoreCase("") && imagePath != null) {
                            preferenceSettings.setUserPic(imagePath);
                        }
                        preferenceSettings.setUserName(ServerName);
                        preferenceSettings.setEmailId(edServerEmailId.getText().toString());
                        if (type == 1) {
                            finish();
                        } else {
                            preferenceSettings.setIslogin(true);
                            preferenceSettings.setUserLogin(false);
                            Intent intent = new Intent(ProfileActivity.this, ServerHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    } else if (status_code.equalsIgnoreCase("0")) {
                        mProgressDialog.dismiss();
                        Log.e("Error_in", "else");
                        CommonUtils.showToast(massage);
                    } else {
                        mProgressDialog.dismiss();
                        Log.e("Error_in", "else");
                    }
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    Log.e("Error_in", "catch");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                error.printStackTrace();
                Log.e("Error_in", "onErrorResponse" + error.getMessage());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "userProfile");
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
                        edServerUsername.setTextColor(Color.BLACK);
                    } else if (userObject.getStatus_code() == 0) {
                        isValid = false;
                        edServerUsername.setTextColor(Color.RED);
                        edServerUsername.setError(userObject.getMessage());
                    } else {
                    }

                } catch (JSONException e) {
                    CommonUtils.showToast("something went wrong!!,please try later");
                    Log.e("Error_in", "catch");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RegistrationActivity", "Error Response: " + error.getMessage());
                showTryAgainAlert("Network Error", "Please check your network and Retry");
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

    public void setProfileImage(String userImageUrl, ImageView imageView) {
        com.nostra13.universalimageloader.core.ImageLoader imageLoader = TipperActivity.getInstance().getImageLoader();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.profile)
                .showImageOnFail(R.drawable.profile)
                .showImageOnLoading(R.drawable.profile)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //download and display image from url
        imageLoader.displayImage(userImageUrl, imageView, options);

    }

    public void setProfileBack(String userImageUrl, ImageView imageView) {
        com.nostra13.universalimageloader.core.ImageLoader imageLoader = TipperActivity.getInstance().getImageLoader();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.color.black1)
                .showImageOnFail(R.color.black1)
                .showImageOnLoading(R.color.black1)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //download and display image from url
        imageLoader.displayImage(userImageUrl, imageView, options);

    }

    @Override
    public void onBackPressed() {
        if (isSave == true) {
            showAlear();
        } else {
            finish();
        }
    }

    private void showAlear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Alert!");
        builder.setMessage("Save your deatils or discard them?");

        String positiveText = getResources().getString(R.string.save);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        EditProfile(1);
                    }
                });

        String negativeText = getResources().getString(R.string.discard);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public void checkData(final String errorMassage) {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.save_dailog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.errorMassgae);
        text.setText("" + errorMassage);

        dialog.show();

        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        // if decline button is clicked, close the custom dialog
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation;

        dialog.show();

    }
}

