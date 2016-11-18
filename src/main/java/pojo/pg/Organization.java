package pojo.pg;

/**
 * Created by wyq on 2016/11/16.
 */
public class Organization {
    public String id = "";
    public String orgname;
    public String city;
    public String state;
    public String country;
    public String parties;

    @Override
    public String toString() {
        return "Organization{" +
                "id='" + id + '\'' +
                ", orgname='" + orgname + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", parties='" + parties + '\'' +
                '}';
    }

    public String toCSVHead(){
        return "id,orgname,city,state,country,parties";
    }

    public String toCSV() {
        return id + ',' +
                orgname + ',' +
                city + ',' +
                state + ',' +
                country + ',' +
                parties;
    }
}
