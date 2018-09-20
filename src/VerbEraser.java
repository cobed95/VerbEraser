import java.io.*;
import java.util.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class VerbEraser {
    private static final String INPUTPATH = "txt/input/";
    private static final String OUTPUTPATH = "txt/output/";
    private static final String OUTSUFFIX = "-out";
    private static final String SUFFIX = ".txt";
    private static final String FILELISTPATH = INPUTPATH + "file-list" + SUFFIX;
    private static final String MODELPATH = "taggers/wsj-0-18-bidirectional-distsim.tagger";

    private static String readFromFile(String filePath) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File(filePath));
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
            String raw = word.substring(0, word.indexOf('_'));
            String tag = word.substring(word.indexOf('_') + 1);
            if (tag.charAt(0) >= 33 && tag.charAt(0) <= 47) result += raw;
            else if (tag.charAt(0) == 'V') result += " ________";
            else if (tag.equals("MD")) result += "________";
            else if (raw.length() > 3
                    && (raw.substring(raw.length() - 3).equals("ing")
                        || raw.substring(raw.length() - 2).equals("ed")))
                result += " ________";
            else result += " " + raw;
        }
        return result;
    }

    private static void writeToFile(String filePath, String text) throws FileNotFoundException {
        PrintStream out = new PrintStream(new File(filePath));
        out.println(text);
    }

    private static void workOneFile(MaxentTagger tagger, String fileName) throws FileNotFoundException {
        String raw = readFromFile(INPUTPATH + fileName + SUFFIX);
        String tagged = tagger.tagString(raw);
        String verbErased = substituteVerbs(tagged);
        writeToFile(OUTPUTPATH + fileName + OUTSUFFIX + SUFFIX, verbErased);
    }

    private static void workAllFiles(MaxentTagger tagger, String fileList) throws FileNotFoundException {
        Scanner listScanner = new Scanner(fileList);
        while (listScanner.hasNextLine()) {
            String fileName = listScanner.nextLine();
            workOneFile(tagger, fileName);
        }
    }

    private static void makeFileList() throws FileNotFoundException {
        PrintStream out = new PrintStream(new File(FILELISTPATH));
        String prefix = "IMG_0";
        for (int i = 475; i <= 644; i++) {
            if (i > 479 && i < 573) continue;
            else if (i > 590 && i < 628) continue;
            else if (i == 630) continue;
            else out.println(prefix + i);
        }
    }

    private static void mergeTexts(String fileList) throws FileNotFoundException {
        String result = "";
        Scanner listScanner = new Scanner(fileList);
        while (listScanner.hasNextLine()) {
            String fileName = listScanner.nextLine();
            result += readFromFile(OUTPUTPATH + fileName + OUTSUFFIX + SUFFIX);
        }
        writeToFile(OUTPUTPATH + "merged" + SUFFIX, result);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MaxentTagger tagger = new MaxentTagger(MODELPATH);
        makeFileList();
        String fileList = readFromFile(FILELISTPATH);
        workAllFiles(tagger, fileList);
        mergeTexts(fileList);
    }
}
