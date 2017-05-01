package creadigol.com.tipper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import Utils.PreferenceSettings;

/**
 * Created by Ashfaq on 10/31/2015.
 */
public class DisplayCropingImage extends Activity implements View.OnClickListener {

    Button mButtonOk, mButtonCancel;
    CropImageView cropImageView;
    ImageView mImageView;
    PreferenceSettings preferenceSettings;
    public static File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropingprofile);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        mButtonOk = (Button) findViewById(R.id.buttonOk);
        mButtonCancel = (Button) findViewById(R.id.buttonCancel);
        mImageView = (ImageView) findViewById(R.id.image);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setGuidelines(1);
        if (preferenceSettings.getUserLogin()) {
            if (UserProfileActivity.mBitmap != null) {
                cropImageView.setImageBitmap(UserProfileActivity.mBitmap);
            }
        } else if (!preferenceSettings.getUserLogin()) {
            if (ProfileActivity.mBitmap != null) {
                cropImageView.setImageBitmap(ProfileActivity.mBitmap);
            }
        }
        mButtonOk.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                ProfileActivity.mBitmapCrop = cropImageView.getCroppedImage();
                f = new File(getApplicationContext().getCacheDir(), "file.jpg");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//Convert bitmap to byte array

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ProfileActivity.mBitmapCrop.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(bitmapdata);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                break;
            case R.id.buttonCancel:
                finish();
        }
    }
}
