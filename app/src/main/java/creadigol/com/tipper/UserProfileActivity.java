package creadigol.com.tipper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Halper.MyApplication;
import Model.ReviewHistoryItem;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;

/**
 * Created by ravi on 05-04-2017.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int ACTION_TAKE_PHOTO = 1;
    private static final int ACTION_SELECT_PHOTO = 2;
    private String mCameraPhotoPath, mGallaryPhotoPath;
    File f, mProFile;
    PreferenceSettings preferenceSettings;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static Bitmap mBitmap = null, mBitmapCrop = null;
    ImageView userProfilePic, userProfileTrasprent, changePassword;
    EditText edUserName, edUserEmail, edUserPassword;
    LinearLayout llPassword;
    public static String userName = "", userEmail = "", userPassword = "", imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_activity);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        userProfileTrasprent = (ImageView) findViewById(R.id.userProfileTrasprent);
        userProfilePic = (ImageView) findViewById(R.id.userProfilePic);
        edUserName = (EditText) findViewById(R.id.edUserName);
        edUserEmail = (EditText) findViewById(R.id.edUserEmail);
        edUserPassword = (EditText) findViewById(R.id.edUserPassword);
        changePassword = (ImageView) findViewById(R.id.changePassword);
        llPassword = (LinearLayout) findViewById(R.id.llPassword);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        userProfilePic.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        toolbar();
        if (preferenceSettings.getLoginTypeSocial() == true) {
            llPassword.setVisibility(View.GONE);
        } else {
            llPassword.setVisibility(View.VISIBLE);
        }
        edUserName.setText(preferenceSettings.getFullName());
        edUserEmail.setText(preferenceSettings.getEmailId());
        setProfileImage(preferenceSettings.getUserPic(), userProfilePic);
        setProfileBack(preferenceSettings.getUserPic(), userProfileTrasprent);

        if (preferenceSettings.getPassword() != null && !preferenceSettings.getPassword().equalsIgnoreCase("")) {
            edUserPassword.setText(preferenceSettings.getPassword());
        }
    }

    public void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_userProfile);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                checkValidation();
                break;
            case R.id.userProfilePic:
                showEditProfileDialogDailog();
                break;
            case R.id.changePassword:
                changePassword();
                break;
        }
    }

    public void changePassword() {
        final Dialog dialog = new Dialog(UserProfileActivity.this);
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


    public void checkValidation() {
        userName = edUserName.getText().toString();
        userEmail = edUserEmail.getText().toString();
        userPassword = edUserPassword.getText().toString();

        if (userName.equalsIgnoreCase("")) {
            edUserName.setError("userName not b empty");
        } else if (userEmail.equalsIgnoreCase("")) {
            edUserEmail.setError("userEmail not b empty");
        } else if (userPassword.equalsIgnoreCase("")) {
            edUserPassword.setError("userPassword not b empty");
        } else {
            sendUserData();
        }
    }

    /*void getProfileDetails() {
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    String status_code = jsonObj.optString("status_code");
                    String massage = jsonObj.optString("message");
                    if (status_code.equalsIgnoreCase("1")) {
                        JSONObject jsonObject = jsonObj.getJSONObject(Jsonkey.historyKey);
                        String name = jsonObject.optString("name");
                        String email = jsonObject.optString("emailId");
                        String image = jsonObject.optString("image");
                        edUserName.setText(name);
                        edUserEmail.setText(email);
                        setProfileImage(image, userProfilePic);
                        setProfileBack(image, userProfileTrasprent);
                        preferenceSettings.setUserPic(image);
                        preferenceSettings.setFullName(name);
                        preferenceSettings.setEmailId(email);
                    } else if (status_code.equalsIgnoreCase("0")) {
                        Log.e("Error_in", "else");
                        Toast.makeText(UserProfileActivity.this, "" + massage, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Error_in", "else");
                    }
                } catch (JSONException e) {
                    Log.e("Error_in", "onErrorResponse");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_USER);
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "review");
    }*/

    void sendUserData() {
        imagePath = mCameraPhotoPath;
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Update Profile...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        MultipartRequest jsonObjectRequest = new MultipartRequest(AppUrl.URL_EDITPROFILE, "user", new Response.Listener<String>() {
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

                        preferenceSettings.setFullName(userName);
                        preferenceSettings.setEmailId(userEmail);
                        preferenceSettings.setPassword(userPassword);
                        if (imagePath != null && !imagePath.equalsIgnoreCase("")) {
                            preferenceSettings.setUserPic(imagePath);
                        }
                        mProgressDialog.dismiss();
                        CommonUtils.showToast(massage);
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
                showTryAgainAlert("check your internet connection", "try again");
                Log.e("Error_in", "onErrorResponse" + error.getMessage());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "userProfile");
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
                        edUserPassword.setText(newPassword);
                        preferenceSettings.setPassword(newPassword);
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
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_USER);
                params.put(ParamsKey.KeyOldPassword, oldpassword);
                params.put(ParamsKey.KeyNewPassword, newPassword);
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "userData");

    }

    public void showTryAgainAlert(String title, String message) {
        CommonUtils.showAlertWithNegativeButton(UserProfileActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonUtils.isNetworkAvailable())
                    sendUserData();
                else
                    CommonUtils.showToast("Please check your internet connection");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setprofile();
        if (mBitmapCrop != null) {
            userProfilePic.setImageBitmap(mBitmapCrop);
            userProfileTrasprent.setImageBitmap(mBitmapCrop);
        } else if (mBitmap != null) {
            userProfilePic.setImageBitmap(mBitmap);
            userProfileTrasprent.setImageBitmap(mBitmap);
        }
    }

    public void setprofile() {
        mProFile = MyApplication.getInstance(UserProfileActivity.this).getProfile();
        Log.e("profile in PullZoom", mProFile.getAbsolutePath());
        if (mProFile.exists()) {
            Bitmap bMap = BitmapFactory.decodeFile(mProFile.getAbsolutePath());
            userProfilePic.setImageBitmap(bMap);
            userProfileTrasprent.setImageBitmap(bMap);
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
                UserProfileActivity.this);
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
                if (resultCode == UserProfileActivity.this.RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO

            case ACTION_SELECT_PHOTO: {
                if (resultCode == UserProfileActivity.this.RESULT_OK) {
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
        }
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
            }
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

        int targetW = userProfilePic.getWidth();
        int targetH = userProfilePic.getHeight();

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
                .showImageForEmptyUri(R.color.trasprantblack)
                .showImageOnFail(R.color.trasprantblack)
                .showImageOnLoading(R.color.trasprantblack)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //download and display image from url
        imageLoader.displayImage(userImageUrl, imageView, options);

    }
}
