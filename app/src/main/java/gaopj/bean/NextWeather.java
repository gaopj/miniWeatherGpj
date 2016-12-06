package gaopj.bean;

/**
 * Created by gpj on 2016/10/11.
 */

public class NextWeather {


    private String fengxiang;
    private String date;
    private String high;
    private String low;
    private String type;

    public NextWeather() {
        this.date = "";
        this.fengxiang = "";
        this.high = "";
        this.low = "";
        this.type = "";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NextWeather{" +
                "date='" + date + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}


