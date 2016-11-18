package pojo.pg;

/**
 * Created by wyq on 2016/11/16.
 */
public class patent_organization {
    public String patent_id;
    public String org_id;

    @Override
    public String toString() {
        return "patent_organization{" +
                "patent_id='" + patent_id + '\'' +
                ", org_id='" + org_id + '\'' +
                '}';
    }

    public String toCSVHead(){
        return "patent_id,org_id";
    }

    public String toCSV() {
        return patent_id + ',' +
                org_id;
    }
}
