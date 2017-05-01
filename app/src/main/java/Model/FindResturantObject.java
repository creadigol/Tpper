package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Utils.Jsonkey;

/**
 * Created by Creadigol on 7/26/2016.
 */
public class FindResturantObject extends BaseObject {
    ArrayList<FindResturantItem> findResturantItems;

    public ArrayList<FindResturantItem> getFindResturantItems() {
        return findResturantItems;
    }

    public void setFindResturantItems(JSONArray jsonLocationArray) {
        findResturantItems = new ArrayList<>();
        if (jsonLocationArray != null) {
            for (int i = 0; i < jsonLocationArray.length(); i++) {
                FindResturantItem searchitem = new FindResturantItem();
                try {
                    JSONObject jsonObjectOutlet = jsonLocationArray.getJSONObject(i);

                    searchitem.setResturantId(jsonObjectOutlet.optString(Jsonkey.resturantId));
                    searchitem.setResturantName(jsonObjectOutlet.optString(Jsonkey.resturantName));
                    searchitem.setResturantAddress(jsonObjectOutlet.optString(Jsonkey.resturantAddress));
                    searchitem.setResturantPhoto(jsonObjectOutlet.optString(Jsonkey.resturantPhoto));
                    searchitem.setResturantEmail(jsonObjectOutlet.optString(Jsonkey.resturantEmail));
                    searchitem.setResturantNumber(jsonObjectOutlet.optString(Jsonkey.resturantPhone));
                    searchitem.setDistance(jsonObjectOutlet.optString(Jsonkey.distance));
                    searchitem.setLatitute(jsonObjectOutlet.optString(Jsonkey.latitute));
                    searchitem.setLongitut(jsonObjectOutlet.optString(Jsonkey.longitut));

                    findResturantItems.add(searchitem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
