package pojo.pg;

/**
 * Created by wyq on 2016/11/16.
 */
public class patent_citation {
    public String patent_id;
    public String citation_id;

    @Override
    public String toString() {
        return "patent_citation{" +
                "patent_id='" + patent_id + '\'' +
                ", citation_id='" + citation_id + '\'' +
                '}';
    }

    public String toCSVHead(){
        return "patent_id,citation_id";
    }

    public String toCSV() {
        return patent_id + ',' +
                citation_id;
    }
}
