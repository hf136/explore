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
     * iterateWholeXML:(ѭ������2��XML)
     * TODO(����������������������� �C ��ѡ)
     * TODO(�����������������ִ������ �C ��ѡ)
     * TODO(�����������������ʹ�÷��� �C ��ѡ)
     * TODO(�����������������ע������ �C ��ѡ)
     *
     * @param @param filename
     * @param @return �趨�ļ�
     * @return List<Map<String,String>> DOM����
     * @throws
     * @since CodingExample��Ver 1.1
     */
    public static List<Map<String, String>> iterateWholeXML(String filename) {
        // �����õ����� ֵ ����װ ������
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        // �õ�������
        SAXReader reader = new SAXReader();
        // �ڶ��� �õ���ǰXML �ĵ���Document����

        try {
            reader.setEntityResolver(new MyEntityResolver());
            Document document = reader.read(new File(filename));

            // ��ȡdocument ��Ŀ¼��
            Element root = document.getRootElement();
            // �������ڵ�������ӽڵ�

            for (Iterator iter = root.elementIterator(); iter.hasNext();) {
                // ��װ����ֵ��HashMap ������
                HashMap<String, String> map = new HashMap<String, String>();
                // �������нڵ�
                Element element = (Element) iter.next();
                // �ж� element ������null
                if (element == null)
                    continue;
                // ��ȡ���Ժ�����ֵ
                for (Iterator attrs = element.attributeIterator(); attrs
                        .hasNext();) {
                    // ��ȡ����
                    Attribute attr = (Attribute) attrs.next();
                    // �ж����� null
                    if (attr == null)
                        continue;
                    // ��ȡ����
                    String attrName = attr.getName();
                    // ��ȡֵ
                    String attrValue = attr.getValue();
                    // ��װmap������ �����Ժ�ֵ
                    map.put(attrName, attrValue);
                }
                // �ж� ֻ��
                if (element.isReadOnly()) {
                    String elementName = element.getName();
                    String elementValue = element.getText();
                    map.put(elementName, elementValue);

                }
                else {
                    // �����ڵ�����к��ӽڵ㣬�����д���

                    for (Iterator iterInner = element.elementIterator(); iterInner
                            .hasNext();) {

                        Element elementInner = (Element) iterInner.next();

                        // ���û�к��ӽڵ㣬��ֱ��ȡֵ
                        if (elementInner == null) {
                            String elementName = element.getName();
                            String elementValue = element.getText();

                            map.put(elementName, elementValue);

                        }
                        // ���ӽڵ������

                        for (Iterator innerAttrs = elementInner
                                .attributeIterator(); innerAttrs.hasNext();) {
                            Attribute innerAttr = (Attribute) innerAttrs.next();
                            if (innerAttr == null)
                                continue;
                            String innerAttrName = innerAttr.getName();
                            String innerAttrValue = innerAttr.getValue();
                            map.put(innerAttrName, innerAttrValue);
                        }
                        // ����û�е�����Ƕ�ף���õڶ����ֵ
                        String innerName = elementInner.getName();
                        String innerValue = elementInner.getText();
                        map.put(innerName, innerValue);

                    }
                }

                // ��װlist������
                list.add(map);
            }
            return list; // ����list ������
        }
        catch (DocumentException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        return null;

    }

    // д�ط���
    public static void writeDocumentToFile(Document document , File filename)
            throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(filename), format);
        writer.write(document);
        writer.close();
    }

    // ����
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

    // ����
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