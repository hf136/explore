package utils;

import org.dom4j.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ParsePatentApplication {

    String[] field = {
            "patent_appl_id",
            "title",
            "appl_date",
            "public_date",
            "classification-national",
//            "classification-ipcr",
            "inventors"
    };

    String[] xmlElement = {
            "/us-patent-application/us-bibliographic-data-application/application-reference/document-id/doc-number",
            "/us-patent-application/us-bibliographic-data-application/invention-title",
            "/us-patent-application/us-bibliographic-data-application/application-reference/document-id/date",
            "/us-patent-application/us-bibliographic-data-application/publication-reference/document-id/date",
            "/us-patent-application/us-bibliographic-data-application/classification-national/main-classification",
//            "/us-patent-application/us-bibliographic-data-application/classifications-ipcr/classification-ipcr",
            "/us-patent-application/us-bibliographic-data-application/us-parties/inventors/inventor/addressbook/first-name",
    };

    public void setField(String[] field) {
        this.field = field;
    }

    public void setXmlElement(String[] xmlElement) {
        this.xmlElement = xmlElement;
    }

    public void parserXml(File inputXml, String saveCsvDir) {
        String name = inputXml.getName();
        String csvName = saveCsvDir + "/" + name.substring(0, name.lastIndexOf(".")) + ".csv";
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(csvName)));

            // 写入列名
            StringBuilder sbFields = new StringBuilder();
            if(field.length > 0) {
                sbFields.append(field[0]);
                for (int i = 1; i < field.length; i++) {
                    sbFields.append(",").append(field[i]);
                }
                bw.write(sbFields.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(inputXml));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        boolean documentException = false;
        while (scanner.hasNextLine()){
            StringBuilder inBuffer = new StringBuilder();
            String line = scanner.nextLine().trim();
            if(line.startsWith("<?xml")) {
                inBuffer.append(line);
                while (!(line = scanner.nextLine()).equals("</us-patent-application>")) {
                    if (line.startsWith("<!DOCTYPE"))
                        continue;
                    inBuffer.append(line);
                }
                inBuffer.append(line);
            }
            else {
                continue;
            }

            try {
                Document document = DocumentHelper.parseText(inBuffer.toString());
                XMLUtil.treeWalk(document);

                // 对于每篇专利，写入数据
                StringBuffer text = new StringBuffer();
                for (int i = 0; i < xmlElement.length; i++) {
                    Node node = document.selectSingleNode(xmlElement[i]);
                    if(node == null){
                        System.out.println("found null in : " + xmlElement[i]);
                        text.append(",");
                        continue;
                    }
                    if(i == 0) {
                        text.append(node.getText());
                    }
                    else {
                        String str = node.getText().replaceAll(",", ";").trim();
                        text.append(",").append(str);
                    }
                }
                bw.write(text.toString());
                bw.newLine();

            } catch (DocumentException e) {
                if(!documentException) {
                    System.err.println(inBuffer.toString());
                    documentException = true;
                }
                else {
                    System.err.println(inBuffer.substring(0, 100));
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        scanner.close();
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void bfsParseXML(File inputXml, String saveCsvDir){
        String name = inputXml.getName();
        String csvName = saveCsvDir + "/" + name.substring(0, name.lastIndexOf(".")) + ".csv";
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(csvName)));

            // 写入列名
            StringBuilder sbFields = new StringBuilder();
            if(field.length > 0) {
                sbFields.append(field[0]);
                for (int i = 1; i < field.length; i++) {
                    sbFields.append(",").append(field[i]);
                }
                bw.write(sbFields.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(inputXml));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        boolean documentException = false;
        while (scanner.hasNextLine()){
            StringBuilder inBuffer = new StringBuilder();
            String line = scanner.nextLine().trim();
            if(line.startsWith("<?xml")) {
                inBuffer.append(line);
                while (!(line = scanner.nextLine()).equals("</us-patent-application>")) {
                    if (line.startsWith("<!DOCTYPE"))
                        continue;
                    inBuffer.append(line);
                }
                inBuffer.append(line);
            }
            else {
                continue;
            }

            try {
                Document document = DocumentHelper.parseText(inBuffer.toString());

                // 对于每篇专利，写入数据
                StringBuffer text = new StringBuffer();
                for (int i = 0; i < xmlElement.length; i++) {
                    Node node = document.selectSingleNode(xmlElement[i]);
                    if(node == null){
                        System.out.println("found null in : " + xmlElement[i]);
                        text.append(",");
                        continue;
                    }
                    if(i == 0) {
                        text.append(node.getText());
                    }
                    else {
                        String str = node.getText().replaceAll(",", ";").trim();
                        text.append(",").append(str);
                    }
                }
                bw.write(text.toString());
                bw.newLine();

            } catch (DocumentException e) {
                if(!documentException) {
                    System.err.println(inBuffer.toString());
                    documentException = true;
                }
                else {
                    System.err.println(inBuffer.substring(0, 100));
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        scanner.close();
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toCsv(String xmlFileDir, String savePath){
        File xmlDir = new File(xmlFileDir);
        if(!xmlDir.isDirectory()){
            return;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
            PrintStream psOut =new PrintStream(new FileOutputStream(savePath + "/" + df.format(new Date()) + ".out.log"));
            PrintStream psErr =new PrintStream(new FileOutputStream(savePath + "/" + df.format(new Date()) + ".err.log"));
            System.setOut(psOut);
            System.setErr(psErr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File[] files = xmlDir.listFiles();
        for (File file : files){
            if(file.isDirectory())
                continue;
            System.out.println("start process \"" + file.getName() + "\" ...");
            parserXml(file, savePath);
        }
    }

    public static void main(String[] args){
        ParsePatentApplication xml2csv = new ParsePatentApplication();
        xml2csv.toCsv("data\\xml\\ipa", "data\\res\\ipa");
    }

    class MyEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new StringBufferInputStream(""));
        }
    }
}
