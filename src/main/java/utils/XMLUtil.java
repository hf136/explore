package utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Created by wyq on 2016/11/14.
 */
public class XMLUtil {

    /**
     * 遍历整个 XML 文档
     * @param document
     */
    public static void treeWalk(Document document) {
        treeWalk( document.getRootElement() , 0);
    }

    public static void treeWalk(Element element, int depth) {
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node node = element.node(i);
            if(node != null) {
                for (int j = 0; j < depth; j++) {
                    System.out.print("\t");
                }
                System.out.println(node.getName() + ":" + node.getPath());
            }

            if ( node instanceof Element ) {
                treeWalk( (Element) node , depth + 1);
            }
            else {
                // do something....
            }
        }
    }

}
