package com.dextreem.tj;

import com.dextreem.tj.gui.TagJudgeGui;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Zu wenig Argumente.");
            System.out.println("Folgende brauche ich:");
            System.out.println("\t1. Tags als CSV");
            System.out.println("\t2. ID zu Bild Relation als CSV oder User Tags");
            System.out.println("\t3. Pfad zu den Bildern oder \"MERGE\"");
            System.out.println("\t4. \"DISCUSS\", wenn bestehende Ratings diskutiert werden sollen");
            System.out.println("Beispiel 1: tagJudge \"tags.csv\" \"relations.csv\" \"./images\"");
            System.out.println("\tBewerten der Tags mit der GUI.");
            System.out.println("Beispiel 2: tagJudge \"tags.csv\" \"original_tags.csv\" \"MERGE\"");
            System.out.println("\tMerged die bewerteten tags aus tags.csv in original_tags.csv. Fügt die Bewertung als neue Spalte hinzu.");
            System.out.println("Beispiel 3: tagJudge \"tags.csv\" \"relations.csv\" \"./images\" \"DISCUSS\"");
            System.out.println("\tBewerten der Tags mit der GUI und Anzeige der beiden gegebenen Ratings. Kann zum Diskutieren der Ratings genutzt werden.");
            System.out.println("Die Reihenfolge dabei ist wichtig!");
            System.exit(-1);
        }

        if (args[2].equals("MERGE")) {
            Tags tags = new Tags(args[0], null, false);
            mergeFiles(tags, args[1]);
        }else {
            Tags tags = new Tags(args[0], readRelationMap(args[1]), args[3].equals("DISCUSS"));
            File images = new File(args[2]);

            TagJudgeGui gui;
            if(args[3].equals("DISCUSS")){
                gui = new TagJudgeGui(tags, images, true);
            }else{
                gui = new TagJudgeGui(tags, images, false);
            }
            gui.view();
        }
    }

    private static Map<Integer, String> readRelationMap(String path) {
        System.out.println(path);
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            Map<Integer, String> relations = new HashMap<>();
            String line;
            while (null != (line = br.readLine())) {
                String[] elts = line.split(",");
                relations.put(Integer.parseInt(elts[0]), elts[1]);
            }
            br.close();
            return relations;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static void mergeFiles(Tags tags, String originalFile) {
        Map<Integer, Map<String, Integer>> rating = tags.getRatingPerImageAndTag();

        long missingLines = 0;

        List<String> lines = new ArrayList<>();
        try {
            FileInputStream fr = new FileInputStream(new File(originalFile));
            BufferedReader br = new BufferedReader(new InputStreamReader(fr, "UTF-8"));
            String line;
            while (null != (line = br.readLine())) {
                // uid, iid, tag
                String[] tmp = line.split(Tags.SEPARATOR);
                if (tmp.length == 0) {
                    continue;
                }
                try {
                    lines.add(line + Tags.SEPARATOR + rating.get(Integer.parseInt(tmp[0])).get(tmp[2]));
                } catch (NullPointerException ex) {
                    System.out.println("Fehler beim Bewerten der folgenden Line: " + line);
                    System.out.println("Diese Line wird dann ohne Bewertung erscheinen!!!");
                    lines.add(line);
                    ++missingLines;
                }
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Fehlende Lines: " + missingLines);
        System.out.println("Read and adapted. Storing to new file " + originalFile + "merged.csv");

        try {
            File fout = new File(originalFile + "merged.csv");
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }

            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
