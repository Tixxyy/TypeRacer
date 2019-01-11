import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameLogic {
    private JLabel label;
    private JTextField field;
    private boolean random;
    private boolean charBased;
    private ArrayList<String> sentences;

    private String text;
    private int sentencePos;
    private boolean backspace;
    private int subPos;
    private int posOffset;
    private boolean killSwitch;
    private int fullLength;

    private long startime;
    private long endtime;
    private boolean firstRun;

    public GameLogic(JLabel label, JTextField field, boolean random, boolean charBased, ArrayList<String> sentences, boolean killSwitch) {
        this.label = label;
        this.field = field;
        this.random=random;
        this.charBased=charBased;
        this.sentences=sentences;
        this.sentencePos=0;
        this.killSwitch=killSwitch;
        firstRun=true;
    }
    public void manageProcesses(){
        if (firstRun){
            run();
            firstRun=false;
        }
        if (killSwitch) init();
        else deInit();
    }
    private void run() {
        field.addKeyListener(new KeyListener()  {
            @Override
            public void keyTyped(KeyEvent e) {
                if (killSwitch) {
                    String fullField = field.getText() + e.getKeyChar(); // +1 EVEN IF YOU PRESS BACKSPACE
                    if (backspace) fullLength = fullField.length() - 1 + subPos; // without counting backspace4
                    else fullLength = fullField.length() + subPos;

                    if (fullLength > text.length()) { // prevents from typing past original text length
                        e.consume();
                    } else {
                        String textSub = text.substring(subPos, fullLength);
                        boolean end = false;

                        if (!backspace) {
                            if (fullField.equals(textSub)) {
                                if (e.getKeyChar() == ' ') {
                                    subPos = fullLength;
                                    field.setText("");
                                    e.consume();
                                }
                                if (fullLength == text.length()) { // end
                                    changeColour(fullLength, posOffset);
                                    endtime = System.nanoTime();
                                    field.setEnabled(false);
                                    int time = (int) Math.round((text.length() / 5.0) * 60.0 / ((endtime - startime) / 1000000000.0));
                                    field.setText("WPM: " + time);
                                        java.util.Timer timer = new Timer();
                                        timer.schedule(new TimerTask() { // idk sloppy
                                            @Override
                                            public void run() {
                                                if (killSwitch)
                                                    init();
                                            }
                                        }, 3000);
                                    end = true;
                                }
                            } else posOffset++;
                        }
                        backspace = false;
                        if (!end) changeColour(fullLength, posOffset);
                    }
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 8) { // backspace
                    if (!field.getText().isEmpty()){
                        if (posOffset>0) posOffset--;
                    }
                    backspace=true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void init(){
        String buffer = sentences.get((int) (Math.random()* sentences.size()));
        if (random && charBased){
            String buffer2="";
            int backSpace = 1;
            for (int i = 0; i<buffer.length();i++){
                char k;
                do {
                    k = buffer.charAt((int)(Math.random()* buffer.length()));
                } while (k ==' ');

                if (i == backSpace*25) {
                    k= ' ';
                    backSpace++;
                }
                buffer2+=k;
            }
            text=buffer2;
        }
        else if (random && (!charBased)){
            text=buffer;
        }
        else if (!random){
            text=sentences.get(sentencePos);
            if (sentencePos+1 <sentences.size()) sentencePos++;
            else sentencePos=0;
        }
        label.setText("<html>"+text+"</html>");
        this.posOffset=0;
        this.backspace=false;
        this.subPos=0;
        countDown();
    }
    private void deInit(){
        text="";
        label.setText("<html></html>");
        field.setText("");
        field.setEnabled(false);
        this.posOffset=0;
        this.backspace=false;
        this.subPos=0;
        this.fullLength=0;
    }

    private void changeColour(int pos, int offPos){
        if (offPos!=0) label.setText("<html><font color='green'>"+text.substring(0,pos-offPos)+"</font><font color='red'>"+text.substring(pos-offPos,pos)+"</font><font color='black'>"+text.substring(pos)+""+"</html>");
        else label.setText("<html><font color='green'>"+text.substring(0,pos)+"</font><font color='black'>"+text.substring(pos)+""+"</html>");
    }

    private void countDown(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 3;
            @Override
            public void run() {
                if  (!killSwitch){
                    timer.cancel();
                    deInit();
                }
                else if (i==0){
                    timer.cancel();
                    field.setText("");
                    field.setEnabled(true);
                    field.grabFocus();
                    field.requestFocus();
                    startime = System.nanoTime();
                }
                else {
                    field.setText(Integer.toString(i));
                    i--;
                }
            }
        },0,1000);
    }

    public void updateGameLogicFields(JLabel label, JTextField field, boolean random, boolean charBased, ArrayList<String> sentences, boolean killSwitch){
        this.label = label;
        this.field = field;
        this.random=random;
        this.charBased=charBased;
        this.sentences=sentences;
        this.sentencePos=0;
        this.killSwitch=killSwitch;
    }
}
