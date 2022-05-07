package com.dextreem.tj;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tags {

    private final String tagFile;
    private final List<String> lines;
    private final Map<Integer, String> ir;

    public static final String SEPARATOR = ";";

    private int fileIndex;
    private String[] currentLine;
    private final boolean inDiscussion;

    public Tags(String tagFile, Map<Integer, String> ir, boolean inDiscussion) {
        this.tagFile = tagFile;
        this.lines = readFile();
        this.ir = ir;

        this.inDiscussion = inDiscussion;

        fileIndex = -1;
    }

    private List<String> readFile() {
        try {
            FileInputStream fr = new FileInputStream(this.tagFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fr, "UTF-8"));
            List<String> lines = new ArrayList<>();
            String line;
            while (null != (line = br.readLine())) {
                lines.add(line);
            }
            br.close();
            return lines;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean next() {
        ++fileIndex;
        if (fileIndex < lines.size()) {
            currentLine = lines.get(fileIndex).split(SEPARATOR);

            return !isJudged() || next();
        }
        return false;
    }

    private boolean isJudged() {
        return (!inDiscussion && currentLine.length == 4) || (inDiscussion && currentLine[3].equals(currentLine[4]));
    }

    public String getFileName() {
        return ir.get(Integer.parseInt(currentLine[1]));
    }

    public String getFrequency(){
        return currentLine[1];
    }

    public String getTag() {
        return currentLine[2];
    }

    public String getRates(){
        return String.format("[R1: %s, R2: %s]", currentLine[3], currentLine[4]);
    }

    public void storeTags(){
        try {
            File fout = new File(tagFile);
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));

            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }

            bw.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void judge(int value) {
        String[] newRow = new String[inDiscussion ? 5 : 4];
        newRow[0] = currentLine[0];
        newRow[1] = currentLine[1];
        newRow[2] = currentLine[2];
        newRow[3] = Integer.toString(value);
        if(inDiscussion){
            newRow[4] = Integer.toString(value);
        }
        lines.set(fileIndex, getSeparatorSeparatedStringFromArray(newRow));
    }

    private String getSeparatorSeparatedStringFromArray(String[] arr){
        if (arr.length > 0) {
            StringBuilder nameBuilder = new StringBuilder();

            for (String n : arr) {
                nameBuilder.append(n).append(SEPARATOR);
            }

            nameBuilder.deleteCharAt(nameBuilder.length() - 1);

            return nameBuilder.toString();
        } else {
            return "";
        }
    }

    public Map<Integer,Map<String, Integer>> getRatingPerImageAndTag(){
        Map<Integer,Map<String, Integer>> ratingPerImageAndTag = new HashMap<>();
        for (String line : lines){
            String[] elts = line.split(SEPARATOR);
            if(elts.length != 4){
                System.out.println("WARNUNG: Unbewertete Zeile gefunden. Diese wird übersprungen: " + line);
                continue;
            }

            int iid = Integer.parseInt(elts[0]);
            String tag = elts[2];
            int rating = Integer.parseInt(elts[3]);

            if(!ratingPerImageAndTag.containsKey(iid)){
                Map<String, Integer> tmp = new HashMap<>();
                tmp.put(tag, rating);
                ratingPerImageAndTag.put(iid, tmp);
            }else{
                ratingPerImageAndTag.get(iid).put(tag, rating);
            }

        }
        return ratingPerImageAndTag;
    }
}
