package pojo.pg;

/**
 * Created by wyq on 2016/11/16.
 */
public class patent_inventor {
    public String patent_id;
    public String inventor_id;

    @Override
    public String toString() {
        return "patent_inventor{" +
                "patent_id='" + patent_id + '\'' +
                ", inventor_id='" + inventor_id + '\'' +
                '}';
    }

    public String toCSVHead(){
        return "patent_id,inventor_id";
    }

    public String toCSV() {
        return patent_id + ',' +
                inventor_id;
    }
}
