package utils;

import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import pojo.pg.Organization;
import pojo.pg.PatentGrant;
import pojo.pg.Inventor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wyq on 2016/11/16.
 */
public class PGVisitor extends VisitorSupport{

    PatentGrant pg = new PatentGrant();
    List<Inventor> inventors = new ArrayList<Inventor>();
    List<Organization> organizations = new ArrayList<Organization>();

    @Override
    public void visit(Element node) {
        try {
            // 专利授权
            if (node.getName().equals("publication-reference")) {
                Element doc = node.element("document-id");
                pg.country = doc.elementText("country");
                pg.grant_id = doc.elementText("doc-number");
                pg.kind = doc.elementText("kind");
                pg.date = doc.elementText("date");
            } else if (node.getName().equals("application-reference")) {
                Element doc = node.element("document-id");
                pg.appl_id = doc.elementText("doc-number");
                pg.appl_date = doc.elementText("date");
                pg.appl_type = node.attributeValue("appl-type");
            } else if (node.getName().equals("invention-title")) {
                pg.invention_title_id = node.attributeValue("id");
                pg.invention_title = node.getText().replaceAll(",", ";");
            } else if (node.getName().equals("classification-locarno")) {
                pg.locarno_classification = node.elementText("main-classification");
                pg.locarno_edition = node.elementText("edition");
            } else if (node.getPath().equals("/us-patent-grant/us-bibliographic-data-grant/classification-national")) {
                pg.national_classification = node.elementText("main-classification");
                pg.national_country = node.elementText("country");
            }

            //发明家
            if (node.getName().equals("inventors")) {
                for (Iterator i = node.elementIterator("inventor"); i.hasNext(); ) {
                    Inventor inven = new Inventor();
                    Element e = (Element) i.next();
                    e = e.element("addressbook");
                    inven.firstname = e.elementText("first-name") == null? "" : e.elementText("first-name").replaceAll(",", ";");
                    inven.lastname = e.elementText("last-name") == null? "" : e.elementText("last-name").replaceAll(",", ";");

                    Element address = e.element("address");
                    if (address != null) {
                        inven.city = address.elementText("city") == null? "" : address.elementText("city").replaceAll(",", ";");
                        inven.state = address.elementText("state");
                        inven.country = address.elementText("country");
                    }
                    //System.out.println(inven);
                    inventors.add(inven);
                }
            }

            //相关组织
            if (node.getName().equals("addressbook")) {
                if (node.element("orgname") != null) {
                    Organization organization = new Organization();
                    organization.orgname = node.elementText("orgname").replaceAll(",", ";");
                    Element address = node.element("address");
                    if (address != null) {
                        organization.city = address.elementText("city") == null? "": address.elementText("city").replaceAll(",", ";");
                        organization.state = address.elementText("state");
                        organization.country = address.elementText("country");
                    }
                    organization.parties = node.getParent().getName();
                    //System.out.println(organization);
                    organizations.add(organization);
                }
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public PatentGrant getPatentGrant(){
        return pg;
    }

    public List<Inventor> getInventors(){
        return inventors;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }
}
