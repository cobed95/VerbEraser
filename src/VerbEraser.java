import java.io.*;
import java.util.*;
import java.io.IOException;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class VerbEraser {
    private static String readFromFile(String fileName) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File(fileName));
        String result = "";
        while (fileScanner.hasNextLine()) {
            result += fileScanner.nextLine();
            result += "\n";
        }
        return result;
    }

    private static String substituteVerbs(String tagged) {
        Scanner stringScanner = new Scanner(tagged);
        String result = "";
        while (stringScanner.hasNext()) {
            String word = stringScanner.next();
            if (word.equals("\n")) result += "\n";
            else if (word.charAt(word.indexOf('_') + 1) == 'V') result += "____";
            else result += word.substring(0, word.indexOf('_'));
            result += " ";
        }
        return result;
    }

    private static void writeToFile(String fileName, String text) throws FileNotFoundException {
        PrintStream out = new PrintStream(new File(fileName));
        out.println(text);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MaxentTagger tagger = new MaxentTagger("taggers/wsj-0-18-bidirectional-distsim.tagger");

        String raw = readFromFile("txt/input.txt");
        String tagged = tagger.tagString(raw);
        String verbErased = substituteVerbs(tagged);
        writeToFile("txt/output.txt", verbErased);
    }
}
