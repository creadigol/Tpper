package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Utils.Jsonkey;

/**
 * Created by ravi on 03-04-2017.
 */

public class ResturantDetailsItem extends BaseObject {
    String id, name, address, photo, lat, log,distance;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    ArrayList<ResturantServeritem> resturantServeritems;

    public ArrayList<ResturantServeritem> getResturantServeritems() {
        return resturantServeritems;
    }

    public void setResturantServeritems(JSONArray jsonLocationArray) {
        resturantServeritems = new ArrayList<>();
        if (jsonLocationArray != null) {
            for (int i = 0; i < jsonLocationArray.length(); i++) {
                ResturantServeritem resturantServeritem = new ResturantServeritem();
                try {
                    JSONObject jsonObjectOutlet = jsonLocationArray.getJSONObject(i);

                    resturantServeritem.setServerId(jsonObjectOutlet.optString(Jsonkey.serverId));
                    resturantServeritem.setServerName(jsonObjectOutlet.optString(Jsonkey.serverName));
                    resturantServeritem.setServerPhoto(jsonObjectOutlet.optString(Jsonkey.serverImage));
                    resturantServeritem.setServerAvgRating(jsonObjectOutlet.optString(Jsonkey.serverAvgRating));
                    resturantServeritem.setServerTotalTip(jsonObjectOutlet.optString(Jsonkey.serverTotalTip));
                    resturantServeritem.setServerRating(jsonObjectOutlet.optString(Jsonkey.totalRating));
                    resturantServeritem.setServerDesc(jsonObjectOutlet.optString(Jsonkey.serverDesc));


                    resturantServeritems.add(resturantServeritem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
