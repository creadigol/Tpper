package Model;

/**
 * Created by ravi on 03-04-2017.
 */

public class ServerListItem extends BaseObject {
    String serverId,serverName, serverResturantName, serverImage, serverAvgRating, serverRating, serverTip;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerResturantName() {
        return serverResturantName;
    }

    public void setServerResturantName(String serverResturantName) {
        this.serverResturantName = serverResturantName;
    }

    public String getServerImage() {
        return serverImage;
    }

    public void setServerImage(String serverImage) {
        this.serverImage = serverImage;
    }

    public String getServerAvgRating() {
        return serverAvgRating;
    }

    public void setServerAvgRating(String serverAvgRating) {
        this.serverAvgRating = serverAvgRating;
    }

    public String getServerRating() {
        return serverRating;
    }

    public void setServerRating(String serverRating) {
        this.serverRating = serverRating;
    }

    public String getServerTip() {
        return serverTip;
    }

    public void setServerTip(String serverTip) {
        this.serverTip = serverTip;
    }
}
