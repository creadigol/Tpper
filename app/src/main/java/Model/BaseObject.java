package Model;

import java.io.Serializable;

/**
 * Created by Ashfaq on 7/16/2016.
 */

public class BaseObject implements IObjaec, Serializable {

    private int status_code;
    private String message;

    public BaseObject(){

    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
