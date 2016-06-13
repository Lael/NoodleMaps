package Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Feed it a path to a .xml file. It'll be able to read the thing off tag by tag.
 *
 * Created by laelcosta on 6/11/16.
 */
public class XMLReader {

    private File file;
    Scanner scanner;

    public XMLReader(String file) throws FileNotFoundException {
        this.file = new File(file);
        scanner = new Scanner(this.file);
    }

    public XMLTag getTagOfName(String name) {
        String pattern = "\\s*<" + name + "\\s";
        return getTagFromString(pattern);
    }

    public XMLTag getTag() {
        String pattern = "\\s*<";
        return getTagFromString(pattern);
    }

    private int getNextDatum(XMLTag tag, String line, int index, int len) {
        int i = index;
        while (Character.isWhitespace(line.charAt(index)))
            i ++;
        StringBuilder datumsb = new StringBuilder();
        char bchar = ' ';
        char c = line.charAt(i);
        while (i < len && c != bchar) {
            if (bchar == ' ' && (c == '/' || c == '>'))
                break;
            if (c == '\"') {
                bchar = '\"';
            }

            datumsb.append(c);

            i ++;
            c = line.charAt(i);
            if (c == '\"') {
                datumsb.append(c);
                break;
            }
        }
        String datum = datumsb.toString();

        /* now we should have a string like 'id="1234"' */
        String[] split = datum.split("=", 2);
        if (split.length != 2) {
            return -1;
        }
        tag.addDatum(split[0], split[1]);

        return i - index;
    }

    private XMLTag getTagFromString(String pattern) {
        Pattern p = Pattern.compile(pattern);
        String line = null;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            Matcher m = p.matcher(line);
            if (m.find()) {
                break;
            }
            line = null;
        }

        if (line == null)
            return null;

        int index = 0;
        while (Character.isWhitespace(line.charAt(0)))
            index ++;

        index += 1;

        StringBuilder typesb = new StringBuilder();
        char c = line.charAt(index);
        while (!Character.isWhitespace(c)) {
            typesb.append(c);
            index ++;
        }

        String name = typesb.toString();

        XMLTag tag = new XMLTag(name);

        /* now we have something like <node id="123"/> or <node id="123">. Let's figure out which. It'll be messy. */
        while (Character.isWhitespace(line.charAt(index)))
            index ++;

        int len = name.length();

        index += (1 + len);
        int l = 0;
        while ((l = getNextDatum(tag, line, index, len)) > 0)
            index += l;

        assert(index < len);
        if (line.charAt(index) == '/') {
            /* that was the whole tag */
            assert(index < len - 1);
            assert(line.charAt(index + 1) == '>');

            return tag;
        } else if (line.charAt(index) == '>') {
            /* that was not the whole tag */
            XMLTag child;

            while (!(child = getTag()).getType().equals("/" + name)) {
                tag.addChild(child);
            }
            assert(tag.getType().equals(name));
        } else {
            System.out.println("Error: XML file did a bad. Or was it the reader?");
            return null;
        }

        return tag;
    }
}
