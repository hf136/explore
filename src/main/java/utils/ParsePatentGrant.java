package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import pojo.pg.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

    HashMap<String, Integer> hashInventors = new HashMap<String, Integer>();
    HashMap<String, Integer> hashOrganizations = new HashMap<String, Integer>();

    /**
     * 解析 USPTO 数据中的 ipgxxxxxx.xml 文件, 转化成 6 个关系型 csv 文件
     * @param xmlFileDir
     * @param savePath
     */
    public void xml2csv(String xmlFileDir, String savePath){
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

            String csvNamePG                = savePath + "/" + "patent_grant.csv";
            String csvNameInventors         = savePath + "/" + "inventors.csv";
            String csvNameOrg               = savePath + "/" + "organizations.csv";
            String csvNamePatentCitation    = savePath + "/" + "patent_citation.csv";
            String csvNamePatentInventor    = savePath + "/" + "patent_inventor.csv";
            String csvNamePatentOrganization = savePath + "/" + "patent_organization.csv";

            BufferedWriter bwPG                 = new BufferedWriter(new FileWriter(new File(csvNamePG)));
            BufferedWriter bwInventors          = new BufferedWriter(new FileWriter(new File(csvNameInventors)));
            BufferedWriter bwOrganization       = new BufferedWriter(new FileWriter(new File(csvNameOrg)));
            BufferedWriter bwPatentCitation     = new BufferedWriter(new FileWriter(new File(csvNamePatentCitation)));
            BufferedWriter bwPatentInventor     = new BufferedWriter(new FileWriter(new File(csvNamePatentInventor)));
            BufferedWriter bwPatentOrganization = new BufferedWriter(new FileWriter(new File(csvNamePatentOrganization)));

            // 写入列名
            bwPG.write(new PatentGrant().toCSVHead());
            bwPG.newLine();
            bwInventors.write(new Inventor().toCSVHead());
            bwInventors.newLine();
            bwOrganization.write(new Organization().toCSVHead());
            bwOrganization.newLine();
            bwPatentCitation.write(new patent_citation().toCSVHead());
            bwPatentCitation.newLine();
            bwPatentInventor.write(new patent_inventor().toCSVHead());
            bwPatentInventor.newLine();
            bwPatentOrganization.write(new patent_organization().toCSVHead());
            bwPatentOrganization.newLine();

            File[] files = xmlDir.listFiles();
            // 对于每一个 xml 文件
            for (File file : files){
                if(file.isDirectory() || !file.getName().endsWith(".xml"))
                    continue;
                System.out.println("start process \"" + file.getName() + "\" ...");

                // 读取 XML 文件
                Scanner scanner = new Scanner(new FileInputStream(file));
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

                        // 授权专利
                        bwPG.write(pgVisitor.getPatentGrant().toCSV());
                        bwPG.newLine();
                        // 发明家
                        List<Inventor> inventors = pgVisitor.getInventors();
                        for (int i = 0; i < inventors.size(); i++) {
                            String key = inventors.get(i).toCSV();
                            if(!hashInventors.containsKey(key)){
                                Integer id = hashInventors.size();
                                hashInventors.put(key, id);
                                bwInventors.write(id + key);
                                bwInventors.newLine();
                            }
                            // patent 和 inventor 关系
                            patent_inventor pi = new patent_inventor();
                            pi.patent_id = pgVisitor.getPatentGrant().grant_id;
                            pi.inventor_id = String.valueOf(hashInventors.get(key));
                            bwPatentInventor.write(pi.toCSV());
                            bwPatentInventor.newLine();
                        }
                        // 相关组织
                        List<Organization> organizations = pgVisitor.getOrganizations();
                        for (int i = 0; i < organizations.size(); i++) {
                            String key = organizations.get(i).toCSV();
                            if(!hashOrganizations.containsKey(key)){
                                Integer id = hashOrganizations.size();
                                hashOrganizations.put(key, id);
                                bwOrganization.write(id + key);
                                bwOrganization.newLine();
                            }
                            // patent 和 organization 关系
                            patent_organization po = new patent_organization();
                            po.patent_id = pgVisitor.getPatentGrant().grant_id;
                            po.org_id = String.valueOf(hashOrganizations.get(key));
                            bwPatentOrganization.write(po.toCSV());
                            bwPatentOrganization.newLine();
                        }
                        // 专利引用关系
                        List<patent_citation> patent_citations = pgVisitor.getPatent_citations();
                        for (int i = 0; i < patent_citations.size(); i++) {
                            bwPatentCitation.write(patent_citations.get(i).toCSV());
                            bwPatentCitation.newLine();
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
            }


            bwPG.close();
            bwInventors.close();
            bwOrganization.close();
            bwPatentCitation.close();
            bwPatentInventor.close();
            bwPatentOrganization.close();
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

    /**
     * 把一个目录下的 csv 文件名中包含 patten 字符的文件合成一个 csv 文件
     * @param csvFileDir
     * @param saveFileName
     * @param patten
     */
    void mergeCsv(String csvFileDir, String saveFileName, String patten){
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
                if (file.isDirectory() || !file.getName().contains(patten))
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
        // XML 转化成 CSV
//        parsePatentGrant.toCsv("data/xml/ipg", "data/res/ipg");
//        parsePatentGrant.toCsv("D:\\IBM\\watson\\patents\\xml\\ipg", "D:\\IBM\\watson\\patents\\csv\\ipg");
        parsePatentGrant.xml2csv("data/xml/ipg", "data/res/ipg");
//        parsePatentGrant.xml2csv("D:\\IBM\\watson\\patents\\xml\\ipg", "D:\\IBM\\watson\\patents\\csv\\ipg\\table");

        // 合并 CSV 文件
//        parsePatentGrant.mergeCsv("data/xml/ipg/", "data/res/ipg/ipg.csv");
//        parsePatentGrant.mergeCsv("D:\\IBM\\watson\\patents\\csv\\ipg", "D:\\IBM\\watson\\patents\\csv\\ipg\\result\\ipg15_inventors.csv", "inventors");
//        parsePatentGrant.mergeCsv("D:\\IBM\\watson\\patents\\csv\\ipg", "D:\\IBM\\watson\\patents\\csv\\ipg\\result\\ipg15_organizations.csv", "_organizations");
//        parsePatentGrant.mergeCsv("D:\\IBM\\watson\\patents\\csv\\ipg", "D:\\IBM\\watson\\patents\\csv\\ipg\\result\\ipg15_patent_grant.csv", "_patent_grant");
    }
}
