package Model;

/**
 * Created by ravi on 31-03-2017.
 */

public class RatingItem extends BaseObject{
    String rating4,rating3,rating2,rating1;

    public String getRating4() {
        return rating4;
    }

    public void setRating4(String rating4) {
        if(rating4!=null&&!rating4.equalsIgnoreCase(""))
        this.rating4 = rating4;
        else
            this.rating4 = "0";
    }

    public String getRating3() {
        return rating3;
    }

    public void setRating3(String rating3) {
        if(rating3!=null&&!rating3.equalsIgnoreCase(""))
        this.rating3 = rating3;
        else
            this.rating3 = "0";
    }

    public String getRating2() {
        return rating2;
    }

    public void setRating2(String rating2) {
        if(rating2!=null&&!rating2.equalsIgnoreCase(""))
        this.rating2 = rating2;
        else
            this.rating2="0";
    }

    public String getRating1() {
        return rating1;
    }

    public void setRating1(String rating1) {
        if(rating1!=null&&!rating1.equalsIgnoreCase(""))
            this.rating1 = rating1;
        else{
            this.rating1 = "0";
        }
    }
}
