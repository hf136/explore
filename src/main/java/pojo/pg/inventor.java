package pojo.pg;

/**
 * Created by wyq on 2016/11/16.
 */
public class Inventor {
    public String id = "";
    public String firstname;
    public String lastname;
    public String city;
    public String state;
    public String country;

    @Override
    public String toString() {
        return "Inventor{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public String toCSV() {
        return id + ',' +
                firstname + ',' +
                lastname + ',' +
                city + ',' +
                state + ',' +
                country;
    }
}
