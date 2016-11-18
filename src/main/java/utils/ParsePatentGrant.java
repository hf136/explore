package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import pojo.pg.Inventor;
import pojo.pg.Organization;
import pojo.pg.PatentGrant;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by wyq on 2016/10/17.
 */
public class ParsePatentGrant {

    /**
     * 解析 USPTO 数据中的 ipgxxxxxx.xml 文件
     * @param inputXml
     * @param saveCsvDir
     * @throws IOException
     */
    public void parserXml(File inputXml, String saveCsvDir) throws IOException {
        String name = inputXml.getName();
        String csvNamePG = saveCsvDir + "/" + name.substring(0, name.lastIndexOf(".")) + "_patent_grant.csv";
        String csvNameInventors = saveCsvDir + "/" + name.substring(0, name.lastIndexOf(".")) + "_inventors.csv";
        String csvNameOrg = saveCsvDir + "/" + name.substring(0, name.lastIndexOf(".")) + "_organizations.csv";

        BufferedWriter bwPG = new BufferedWriter(new FileWriter(new File(csvNamePG)));
        BufferedWriter bwInventors = new BufferedWriter(new FileWriter(new File(csvNameInventors)));
        BufferedWriter bwOrganization = new BufferedWriter(new FileWriter(new File(csvNameOrg)));

        // 写入列名
        bwPG.write(new PatentGrant().toCSVHead());
        bwPG.newLine();
        bwInventors.write(new Inventor().toCSVHead());
        bwInventors.newLine();
        bwOrganization.write(new Organization().toCSVHead());
        bwOrganization.newLine();

        Scanner scanner = new Scanner(new FileInputStream(inputXml));

        boolean documentException = false;
        while (scanner.hasNextLine()){
            StringBuilder inBuffer = new StringBuilder();
            String line = scanner.nextLine().trim();
            if(line.startsWith("<us-patent-grant")) {
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
                // 对每一篇授权专利进行解析
                Document document = DocumentHelper.parseText(inBuffer.toString());

                PGVisitor pgVisitor = new PGVisitor();
                document.accept(pgVisitor);
//                System.out.println(pgVisitor.getPatentGrant());
//                System.out.println(pgVisitor.getInventors());
//                System.out.println(pgVisitor.getOrganizations());
                // 授权专利
                bwPG.write(pgVisitor.getPatentGrant().toCSV());
                bwPG.newLine();
                // 发明家
                List<Inventor> inventors = pgVisitor.getInventors();
                for (int i = 0; i < inventors.size(); i++) {
                    bwInventors.write(inventors.get(i).toCSV());
                    bwInventors.newLine();
                }
                // 相关组织
                List<Organization> organizations = pgVisitor.getOrganizations();
                for (int i = 0; i < organizations.size(); i++) {
                    bwOrganization.write(organizations.get(i).toCSV());
                    bwOrganization.newLine();
                }

            } catch (DocumentException e) {
                if(!documentException) {
                    System.err.println(inBuffer.toString());
                    documentException = true;
                }
                else {
                    System.err.println(inBuffer.substring(0, 20));
                }
            }
        }
        scanner.close();
        bwPG.close();
        bwInventors.close();
        bwOrganization.close();
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
            try {
                parserXml(file, savePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把一个目录下的 csv 文件合成一个 csv 文件
     * @param csvFileDir
     * @param saveFileName
     */
    void mergeCsv(String csvFileDir, String saveFileName){
        try {
            File fileDir = new File(csvFileDir);
            if (!fileDir.isDirectory()) {
                return;
            }

            File[] files = fileDir.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveFileName));
            boolean isFirst = true;
            for (File file : files) {
                if (file.isDirectory())
                    continue;
                System.out.println("start process \"" + file.getName() + "\" ...");

                BufferedReader br = new BufferedReader(new FileReader(file));
                if (!isFirst) {
                    br.readLine();
                } else {
                    isFirst = false;
                }
                String line;
                while ((line = br.readLine()) != null) {
                    bw.write(line + "\n");
                }
                br.close();
            }
            bw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        ParsePatentGrant parsePatentGrant = new ParsePatentGrant();
//        parsePatentGrant.toCsv("data/xml/ipg", "data/res/ipg");
        parsePatentGrant.toCsv("D:\\IBM\\watson\\patents\\xml\\ipg", "D:\\IBM\\watson\\patents\\csv\\ipg");
//        parsePatentGrant.mergeCsv("data/xml/ipg/", "data/res/ipg/ipg.csv");
    }
}
