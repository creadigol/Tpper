package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Utils.Jsonkey;

/**
 * Created by Creadigol on 7/26/2016.
 */
public class ServerListObject extends BaseObject {
    ArrayList<ServerListItem> serverListItems;

    public ArrayList<ServerListItem> getServerListItems() {
        return serverListItems;
    }

    public void setServerListItems(JSONArray jsonLocationArray) {
        serverListItems = new ArrayList<>();
        if (jsonLocationArray != null) {
            for (int i = 0; i < jsonLocationArray.length(); i++) {
                ServerListItem serverListItem = new ServerListItem();
                try {
                    JSONObject jsonObjectOutlet = jsonLocationArray.getJSONObject(i);

                    serverListItem.setServerId(jsonObjectOutlet.optString(Jsonkey.serverId));
                    serverListItem.setServerName(jsonObjectOutlet.optString(Jsonkey.serverName));
                    serverListItem.setServerImage(jsonObjectOutlet.optString(Jsonkey.serverImage));
                    serverListItem.setServerResturantName(jsonObjectOutlet.optString(Jsonkey.restaurantName));
                    serverListItem.setServerRating(jsonObjectOutlet.optString(Jsonkey.totalRating));
                    serverListItem.setServerTip(jsonObjectOutlet.optString(Jsonkey.serverTotalTip));
                    serverListItem.setServerAvgRating(jsonObjectOutlet.optString(Jsonkey.serverAvgRating));

                    serverListItems.add(serverListItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
