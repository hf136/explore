package utils;

import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import pojo.pg.PatentGrant;

/**
 * Created by wyq on 2016/11/16.
 */
public class PGVisitor extends VisitorSupport{

    StringBuffer line = new StringBuffer();
    PatentGrant pg = new PatentGrant();

    @Override
    public void visit(Element node) {
        if(node.getName().equals("publication-reference")){
            Element doc = node.element("document-id");
            pg.country = doc.element("country").getText();
            pg.grant_id = doc.element("doc-number").getText();
            pg.kind = doc.elementText("kind");
            pg.date = doc.elementText("date");
        }
        else if(node.getName().equals("application-reference")){
            Element doc = node.element("document-id");
            pg.appl_id = doc.elementText("doc-number");
            pg.appl_date = doc.elementText("date");
            pg.appl_type = node.attributeValue("appl-type");
        }
        else if(node.getName().equals("invention-title")){
            pg.invention_title_id = node.attributeValue("id");
            pg.invention_title = node.getText().replaceAll(",", ";");
        }
        else if(node.getName().equals("classification-locarno")){
            pg.locarno_classification = node.elementText("main-classification");
            pg.locarno_edition = node.elementText("edition");
        }
        else if(node.getPath().equals("/us-patent-grant/us-bibliographic-data-grant/classification-national")){
            pg.national_classification = node.elementText("main-classification");
            pg.national_country = node.elementText("country");
        }
    }

    public String getPatentGrant(){
        return pg.toString();
    }
}
