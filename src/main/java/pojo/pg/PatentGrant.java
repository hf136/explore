package pojo.pg;

/**
 * Created by wyq on 2016/11/16.
 */
public class PatentGrant {
    public String grant_id;
    public String kind;
    public String date;
    public String country;
    public String appl_id;
    public String appl_type;
    public String appl_date;
    public String invention_title_id;
    public String invention_title;
    public String locarno_classification;
    public String locarno_edition;
    public String national_classification;
    public String national_country;
    public String number_of_claims;
    public String exemplary_claim;

    @Override
    public String toString() {
        return "PatentGrant{" +
                "grant_id='" + grant_id + '\'' +
                ", kind='" + kind + '\'' +
                ", date='" + date + '\'' +
                ", country='" + country + '\'' +
                ", appl_id='" + appl_id + '\'' +
                ", appl_type='" + appl_type + '\'' +
                ", appl_date='" + appl_date + '\'' +
                ", invention_title_id='" + invention_title_id + '\'' +
                ", invention_title='" + invention_title + '\'' +
                ", locarno_classification='" + locarno_classification + '\'' +
                ", locarno_edition='" + locarno_edition + '\'' +
                ", national_classification='" + national_classification + '\'' +
                ", national_country='" + national_country + '\'' +
                ", number_of_claims='" + number_of_claims + '\'' +
                ", exemplary_claim='" + exemplary_claim + '\'' +
                '}';
    }

    public String toCSVHead(){
        return "grant_id,kind,date,country,appl_id,appl_type,appl_date,invention_title_id,invention_title,locarno_classification,locarno_edition,national_classification,national_country,number_of_claims,exemplary_claim";
    }

    public String toCSV() {
        return grant_id + ',' +
                kind + ',' +
                date + ',' +
                country + ',' +
                appl_id + ',' +
                appl_type + ',' +
                appl_date + ',' +
                invention_title_id + ',' +
                invention_title + ',' +
                locarno_classification + ',' +
                locarno_edition + ',' +
                national_classification + ',' +
                national_country + ',' +
                number_of_claims + ',' +
                exemplary_claim;
    }
}
