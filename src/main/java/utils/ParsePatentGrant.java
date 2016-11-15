package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by wyq on 2016/10/17.
 */
public class ParsePatentGrant {
    String[] field = {
            "patent_appl_id",
            "patent_public_id",
            "title",
            "appl_date",
            "public_date",
            "locarno_edition",
            "locarno_class",
            "classification-national",
            "inventors"
    };

    String[] xmlElement = {
            "/us-patent-grant/us-bibliographic-data-grant/application-reference/document-id/doc-number",
            "/us-patent-grant/us-bibliographic-data-grant/publication-reference/document-id/doc-number",
            "/us-patent-grant/us-bibliographic-data-grant/invention-title",
            "/us-patent-grant/us-bibliographic-data-grant/application-reference/document-id/date",
            "/us-patent-grant/us-bibliographic-data-grant/publication-reference/document-id/date",
            "/us-patent-grant/us-bibliographic-data-grant/classification-locarno/edition",
            "/us-patent-grant/us-bibliographic-data-grant/classification-locarno/main-classification",
            "/us-patent-grant/us-bibliographic-data-grant/classification-national/main-classification",
            "/us-patent-grant/us-bibliographic-data-grant/us-parties/inventors/inventor/addressbook/first-name",
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
                while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("</us-patent-grant>")) {
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

    void mergeCsv(String csvFileDir, String saveFileName) throws IOException {
        File fileDir = new File(csvFileDir);
        if(!fileDir.isDirectory()){
            return ;
        }

        File[] files = fileDir.listFiles();
        if(files == null || files.length == 0){
            return;
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(saveFileName));
        boolean isFirst = true;
        for (File file : files){
            if(file.isDirectory())
                continue;
            System.out.println("start process \"" + file.getName() + "\" ...");

            BufferedReader br = new BufferedReader(new FileReader(file));
            if(!isFirst){
                br.readLine();
            }
            else {
                isFirst = false;
            }
            String line;
            while ((line = br.readLine()) != null){
                bw.write(line + "\n");
            }
            br.close();
        }
        bw.close();
    }

    public static void main(String[] args){
        ParsePatentGrant parsePatentGrant = new ParsePatentGrant();
//        parsePatentGrant.toCsv("D:\\IBM\\watson\\patents\\xml\\ipg", "D:\\IBM\\watson\\patents\\csv\\ipg");
        try {
            parsePatentGrant.mergeCsv("D:\\IBM\\watson\\patents\\csv\\ipg", "D:\\IBM\\watson\\patents\\csv\\ipg\\result\\ipg_all.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
