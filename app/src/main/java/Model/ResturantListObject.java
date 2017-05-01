package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Utils.Jsonkey;

/**
 * Created by Creadigol on 7/26/2016.
 */
public class ResturantListObject extends BaseObject {
    ArrayList<ResturantListItem> resturantListItems;

    public ArrayList<ResturantListItem> getResturantListItems() {
        return resturantListItems;
    }

    public void setResturantListItems(JSONArray jsonLocationArray) {
        resturantListItems = new ArrayList<>();
        if (jsonLocationArray != null) {
            for (int i = 0; i < jsonLocationArray.length(); i++) {
                ResturantListItem findResturantItem = new ResturantListItem();
                try {
                    JSONObject jsonObjectOutlet = jsonLocationArray.getJSONObject(i);

                    findResturantItem.setResturantId(jsonObjectOutlet.optString(Jsonkey.resturantId));
                    findResturantItem.setResturantName(jsonObjectOutlet.optString(Jsonkey.resturantName));
                    findResturantItem.setResturantAddress(jsonObjectOutlet.optString(Jsonkey.resturantAddress));
                    findResturantItem.setResturantImage(jsonObjectOutlet.optString(Jsonkey.resturantPhoto));
                    findResturantItem.setResturantlatitiute(jsonObjectOutlet.optString(Jsonkey.latitute));
                    findResturantItem.setResturantLongitute(jsonObjectOutlet.optString(Jsonkey.longitut));
                    findResturantItem.setResturantDistance(jsonObjectOutlet.optString(Jsonkey.distance));

                    resturantListItems.add(findResturantItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
