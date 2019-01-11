import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadSentences {
    public static ArrayList<String> getSentenceArray(File file) {
        var sentences = new ArrayList<String>();
        try {
            Scanner scanner = new Scanner(file,"utf-8");
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                if (!nextLine.isEmpty()) sentences.add(nextLine);
            }
            return sentences;
        }
        catch (FileNotFoundException e){ // sloppy
            return sentences;
        }
    }
}
