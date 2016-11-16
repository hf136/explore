package utils;

import org.dom4j.Element;
import org.dom4j.VisitorSupport;

/**
 * Created by wyq on 2016/11/16.
 */
public class PAVisitor extends VisitorSupport{
    @Override
    public void visit(Element node) {
        System.out.println(node.getName());
    }
}
