package creadigol.com.tipper;


import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import Utils.Constants;
import Utils.ParamsKey;
import Utils.PreferenceSettings;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;

public class MultipartRequest extends Request<String> {

    private HttpEntity mHttpEntity;
    private Response.Listener mListener;
    PreferenceSettings preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
    private String types;

    public MultipartRequest(String url, String type,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        types = type;
        mHttpEntity = buildMultipartEntity();

    }

    private HttpEntity buildMultipartEntity() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody(ParamsKey.KeyUserId, preferenceSettings.getUserId());
        Log.e(ParamsKey.KeyUserId, "" + preferenceSettings.getUserId());
        builder.addTextBody(ParamsKey.KeyRegistrationType, types);
        Log.e(ParamsKey.KeyRegistrationType, "" + types);

        if (types.equalsIgnoreCase(Constants.TYPE_USER)) {
            builder.addTextBody(ParamsKey.KeyResturant, "");
            builder.addTextBody(ParamsKey.KeyName, UserProfileActivity.userName.toString().trim());
            String final_path;
            if (UserProfileActivity.imagePath != null) {
                final_path = UserProfileActivity.imagePath;
                File file = new File(final_path);
                FileBody Fbody = new FileBody(file);
                builder.addPart(ParamsKey.Keyimage, Fbody);
            } else {
                builder.addTextBody(ParamsKey.Keyimage, "");
            }
        } else {
            builder.addTextBody(ParamsKey.KeyResturant, ProfileActivity.resturantId);
            Log.e(ParamsKey.KeyResturant, "" + ProfileActivity.resturantId);
            builder.addTextBody(ParamsKey.KeyName, ProfileActivity.ServerName.toString().trim());
            Log.e(ParamsKey.KeyName, "" + ProfileActivity.ServerName);
            builder.addTextBody(ParamsKey.KeyUserName, ProfileActivity.stServerUserName.toString().trim());
            Log.e(ParamsKey.KeyUserName, "" + ProfileActivity.stServerUserName);

            String final_path;
            if (ProfileActivity.imagePath != null) {
                final_path = ProfileActivity.imagePath;
                File file = new File(final_path);
                FileBody Fbody = new FileBody(file);
                builder.addPart(ParamsKey.Keyimage, Fbody);
            } else {
                builder.addTextBody(ParamsKey.Keyimage, "");
            }
        }
        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, "UTF-8"),
                    getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            return Response.success(new String(response.data), getCacheEntry());
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}