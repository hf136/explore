package utils;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class IterateWholeXMLUtil {
    /**
     * iterateWholeXML:(循环解析2层XML)
     * TODO(这里描述这个方法适用条件 – 可选)
     * TODO(这里描述这个方法的执行流程 – 可选)
     * TODO(这里描述这个方法的使用方法 – 可选)
     * TODO(这里描述这个方法的注意事项 – 可选)
     *
     * @param @param filename
     * @param @return 设定文件
     * @return List<Map<String,String>> DOM对象
     * @throws
     * @since CodingExample　Ver 1.1
     */
    public static List<Map<String, String>> iterateWholeXML(String filename) {
        // 把所得的属性 值 都封装 集合里
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        // 得到解析器
        SAXReader reader = new SAXReader();
        // 第二步 得到当前XML 文档的Document对象

        try {
            reader.setEntityResolver(new MyEntityResolver());
            Document document = reader.read(new File(filename));

            // 获取document 根目录下
            Element root = document.getRootElement();
            // 遍历根节点的所有子节点

            for (Iterator iter = root.elementIterator(); iter.hasNext();) {
                // 封装属性值到HashMap 集合里
                HashMap<String, String> map = new HashMap<String, String>();
                // 遍历所有节点
                Element element = (Element) iter.next();
                // 判断 element 不等于null
                if (element == null)
                    continue;
                // 获取属性和它的值
                for (Iterator attrs = element.attributeIterator(); attrs
                        .hasNext();) {
                    // 获取属性
                    Attribute attr = (Attribute) attrs.next();
                    // 判断属性 null
                    if (attr == null)
                        continue;
                    // 获取属性
                    String attrName = attr.getName();
                    // 获取值
                    String attrValue = attr.getValue();
                    // 封装map集合里 把属性和值
                    map.put(attrName, attrValue);
                }
                // 判断 只读
                if (element.isReadOnly()) {
                    String elementName = element.getName();
                    String elementValue = element.getText();
                    map.put(elementName, elementValue);

                }
                else {
                    // 遍历节点的所有孩子节点，并进行处理

                    for (Iterator iterInner = element.elementIterator(); iterInner
                            .hasNext();) {

                        Element elementInner = (Element) iterInner.next();

                        // 如果没有孩子节点，则直接取值
                        if (elementInner == null) {
                            String elementName = element.getName();
                            String elementValue = element.getText();

                            map.put(elementName, elementValue);

                        }
                        // 孩子节点的属性

                        for (Iterator innerAttrs = elementInner
                                .attributeIterator(); innerAttrs.hasNext();) {
                            Attribute innerAttr = (Attribute) innerAttrs.next();
                            if (innerAttr == null)
                                continue;
                            String innerAttrName = innerAttr.getName();
                            String innerAttrValue = innerAttr.getValue();
                            map.put(innerAttrName, innerAttrValue);
                        }
                        // 假设没有第三层嵌套，获得第二层的值
                        String innerName = elementInner.getName();
                        String innerValue = elementInner.getText();
                        map.put(innerName, innerValue);

                    }
                }

                // 封装list集合里
                list.add(map);
            }
            return list; // 返回list 集合里
        }
        catch (DocumentException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        return null;

    }

    // 写回方法
    public static void writeDocumentToFile(Document document , File filename)
            throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(filename), format);
        writer.write(document);
        writer.close();
    }

    // 测试
    public void show() {

        String filename = "products.xml";
        List<Map<String, String>> list = IterateWholeXMLUtil
                .iterateWholeXML(filename);
        for (Map<String, String> map : list) {
            for (String ss : map.keySet()) {
                System.out.println(ss + ":" + map.get(ss));
            }
        }

    }

    // 测试
    public static void main(String[] args){
        String filename = "D:\\IBM\\watson\\patents\\xml\\test\\us-patent-grant.xml";
        List<Map<String, String>> list = IterateWholeXMLUtil
                .iterateWholeXML(filename);
        for (Map<String, String> map : list) {
            for (String ss : map.keySet()) {
                System.out.println(ss + ":" + map.get(ss));
            }
        }
    }
}