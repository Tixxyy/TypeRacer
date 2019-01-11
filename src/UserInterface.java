import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class UserInterface extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private JPanel panel1;
    private JTextField textField1;
    private JLabel textInputLabel;
    private JButton startStopButton;
    private JRadioButton charBasedButton;
    private JButton getTxtFileButton;
    private JRadioButton randomRadioButton;

    private ArrayList<String> sentences;
    private boolean fileHasText;
    private volatile boolean startStop;

    private GameLogic logic;

    public UserInterface(){
        setSize(WIDTH,HEIGHT);
        setContentPane(panel1);
        updateFields();
        actionListeners();
        generalAppearance();
        logic = new GameLogic(textInputLabel,textField1,randomRadioButton.isSelected(),charBasedButton.isSelected(),sentences,startStop);
    }
    private void generalAppearance(){
        Font font = new Font("Courier", Font.BOLD,15);
        textField1.setFont(font);
        textInputLabel.setFont(font);

        textInputLabel.setMinimumSize(new Dimension(350,300));
        textInputLabel.setPreferredSize(new Dimension(400,300));
        textInputLabel.setMaximumSize(new Dimension(450,300));

        Border blackline = BorderFactory.createLineBorder(Color.black);
        textField1.setBorder(blackline);
        startStopButton.setFocusable(false);
    }

    private void startGame(){
        logic.updateGameLogicFields(textInputLabel,textField1,randomRadioButton.isSelected(),charBasedButton.isSelected(),sentences,startStop);
        updateFieldsWhenPlaying();
        logic.manageProcesses();
    }
    private void updateFieldsWhenPlaying(){
        randomRadioButton.setEnabled(!startStop);
        charBasedButton.setEnabled(!startStop);
        getTxtFileButton.setEnabled(!startStop);
    }
    private void updateFields(){
        textField1.setEnabled(fileHasText);
        startStopButton.setEnabled(fileHasText);
        charBasedButton.setEnabled(randomRadioButton.isSelected());
        randomRadioButton.setEnabled(fileHasText);
        textField1.setEnabled(false);
    }

    private void actionListeners(){
        getTxtFile();
        randomRadioButton.addActionListener(event ->{
            charBasedButton.setEnabled(randomRadioButton.isSelected());
            if (!randomRadioButton.isSelected()) charBasedButton.setSelected(false);
        } );
        startStopButton.addActionListener(event ->{
            if (!startStop) startStop=true;
            else startStop = false;
            startGame();
        });
    }
    private void getTxtFile(){
        getTxtFileButton.addActionListener(event -> {
            do{
                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
                fileChooser.setDialogTitle("Choose a .txt file");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT", "txt", "text");
                fileChooser.setFileFilter(filter);

                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    if (file != null) {
                        var tempList = ReadSentences.getSentenceArray(file);
                        if (tempList.isEmpty()) { // no text in txt file or txt file not found
                            this.fileHasText=false;
                            JOptionPane.showConfirmDialog(null, "File is empty/unsupported charset", "", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                        } else {
                            sentences = tempList;
                            this.fileHasText=true;
                        }
                    }
                }
                else break; // if user decides to cancel/exit
            } while (!fileHasText);
            updateFields();
        });
    }
}