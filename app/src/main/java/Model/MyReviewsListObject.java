package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Utils.Jsonkey;

/**
 * Created by ravi on 06-04-2017.
 */

public class MyReviewsListObject extends BaseObject {
    ArrayList<MyReviewsListItem> myReviewsListItems;

    public ArrayList<MyReviewsListItem> getMyReviewsListItems() {
        return myReviewsListItems;
    }

    public void setMyReviewsListItems(JSONArray jsonLocationArray) {
        myReviewsListItems = new ArrayList<>();
        if (jsonLocationArray != null) {
            for (int i = 0; i < jsonLocationArray.length(); i++) {
                MyReviewsListItem myReviewsListItem = new MyReviewsListItem();
                try {
                    JSONObject jsonObjectOutlet = jsonLocationArray.getJSONObject(i);

                    myReviewsListItem.setServerId(jsonObjectOutlet.optString(Jsonkey.serverId));
                    myReviewsListItem.setReviewId(jsonObjectOutlet.optString(Jsonkey.userId));
                    myReviewsListItem.setServerName(jsonObjectOutlet.optString(Jsonkey.serverName));
                    myReviewsListItem.setServerImage(jsonObjectOutlet.optString(Jsonkey.serverImage));
                    myReviewsListItem.setServerResName(jsonObjectOutlet.optString(Jsonkey.resName));
                    myReviewsListItem.setAvgRatingCount(jsonObjectOutlet.optString(Jsonkey.average));
                    myReviewsListItem.setTotalRatingCount(jsonObjectOutlet.optString(Jsonkey.totalRating));
                    myReviewsListItem.setTotalTips(jsonObjectOutlet.optString(Jsonkey.tip));
                    myReviewsListItem.setUserReviewDesc(jsonObjectOutlet.optString(Jsonkey.review));


                    myReviewsListItems.add(myReviewsListItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
