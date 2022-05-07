package com.dextreem.tj.gui;

import com.dextreem.tj.Tags;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class TagJudgeGui{
    private JPanel panelMain;
    private JPanel panelImage;
    private JPanel panelControl;
    private JPanel panelCurrentTag;
    private JLabel lblCurrentTag;
    private JLabel lblCurrentTagValue;
    private JButton btnGreat;
    private JButton btnOkay;
    private JButton btnBad;
    private JPanel panelButtons;
    private JLabel lblButtons;
    private JButton btnSave;
    private JPanel panelSave;

    private JFrame frame;

    private final Tags tags;
    private final File images;
    private final boolean inDiscussion;

    private String currentImage = "";

    public TagJudgeGui(Tags tags, File images, boolean inDiscussion){
        this.tags = tags;
        this.images = images;
        this.inDiscussion = inDiscussion;

        btnSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tags.storeTags();
            }
        });
        btnGreat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                judge(3);
            }
        });
        btnOkay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                judge(2);
            }
        });
        btnBad.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                judge(1);
            }
        });
    }

    private void judge(int val){
        tags.judge(val);
        tags.storeTags();
        getNextTag();
    }

    public void view() {
        frame = new JFrame("TagJudge");
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setVisible(true);

        getNextTag();
    }

    public void stop() {
        if (null != frame && frame.isVisible()) {
            frame.setVisible(false);
        }
    }

    private void getNextTag(){
        if(!tags.next()){
            System.out.println("Du hast es geschafft, es gibt kein weiteres Tag mehr!");
            tags.storeTags();
            JOptionPane.showMessageDialog(panelMain, "Du hast alle Tags bewertet. Erst mal ein Bier! ;-)\nDeine Antworten wurden gespeichert. Du kannst das Programm nun einfach schließen");
            return;
        }

        String tag = tags.getTag();
        String frequency = tags.getFrequency();
        String filename = tags.getFileName();

        System.out.println(filename + "Hallo");

        if (!filename.equals(currentImage)){
            currentImage = filename;
            JLabel imgLbl = new JLabel();
            imgLbl.setIcon(new ImageIcon(images + "/" + filename));
            panelImage.removeAll();
            panelImage.add(imgLbl);
        }

        String times = tag + " (" + frequency + " Mal)";
        if(inDiscussion){
            times += " " + tags.getRates();
        }

        lblCurrentTagValue.setText(times);
    }
}

