package autocorrect;

import com.google.common.collect.Lists;

import java.util.*;

public class Trie {
    private boolean end = false;
    private Character c = null;
    private Map<Character, Trie> children = new HashMap<Character, Trie>();

    public Trie() {

    }

    public Trie(Character c) {
        this.c = c;
    }

     public void add(String s) {
         if (s.isEmpty()) {
             end = true;
             return;
         }

         char first = s.charAt(0);
         Trie child = children.get(first);
         if (child == null) {
             child = new Trie(first);
         }

         child.add(s.substring(1));
         children.put(first, child);
     }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public Character getC() {
        return c;
    }

    public void setC(Character c) {
        this.c = c;
    }

    public Map<Character, Trie> getChildren() {
        return children;
    }

    public void setChildren(Map<Character, Trie> children) {
        this.children = children;
    }

    public void printTrie() {
        System.out.println("Trie:");
        printHelper(" ");
    }

    private void printHelper(String spaces) {
        if (c != null) {
            System.out.println(spaces + c + (end ? "." : ""));
        }
        Collection<Trie> tries = children.values();
        for (Trie trie : tries) {
            trie.printHelper(spaces + " ");
        }
    }

    public List<String> words() {
        List<String> words = Lists.newArrayList();

        if (end) {
            words.add(String.valueOf(c));
        }

        Collection<Trie> tries = children.values();
        for (Trie trie : tries) {
            List<String> suffixes = trie.words();
            for (String s : suffixes) {
                words.add((c == null ? "" : c) + s);
            }
        }

        return words;
    }

    public List<String> wordsWithPrefix(String prefix) {
        if (prefix.isEmpty()) {
            return words();
        }

        List<String> words = Lists.newArrayList();

        Trie trie = children.get(prefix.charAt(0));
        if (trie == null) {
            return words;
        }

        List<String> suffixes = trie.wordsWithPrefix(prefix.substring(1));
        for (String suffix : suffixes) {
            words.add((c == null ? "" : c)  + suffix);
        }

        return words;
    }

    private void levenshteinHelper(Trie trie, String sofar, String prefix, Integer[] row, int led, List<String> words) {
        int len = row.length;
        Integer[] newRow = new Integer[len];
        newRow[0] = row[0] + 1;

        // this is a good prefix, done here
        if (sofar.length() == len - 1) {
            List<String> theseWords = trie.words();
            for (String s : theseWords) {
                words.add(sofar + s);
            }
            return;
        }

        int insert, delete, replace;
        for (int i = 1; i < len; i++) {
            insert = newRow[i - 1] + 1;
            delete = row[i - 1] + 1;
            if (trie.c == prefix.charAt(sofar.length())) {
                replace = row[i - 1];
            } else {
                replace = row[i - 1] + 1;
            }
            newRow[i] = Math.min(replace, Math.min(insert, delete));
        }

        if (trie.end && newRow[len - 1] <= led) {
            words.add(sofar + trie.c);
        }



        // it's worth continuing down this path
        if (Collections.min(Arrays.asList(newRow)) <= led) {
            for (Trie child : trie.children.values()) {
                levenshteinHelper(child, sofar + trie.c, prefix, newRow, led, words);
            }
        }
    }

    public List<String> wordsWithinDistance(String prefix, int led) {
        int len = prefix.length();
        if (len == 0) {
            return Lists.newArrayList();
        }

        Integer[] row = new Integer[len + 1];
        for (int i = 0; i < len + 1; i++) {
            row[i] = i;
        }

        List<String> words = Lists.newArrayList();
        for (Trie trie : children.values()) {
            levenshteinHelper(trie, "", prefix, row, Math.min(led, len - 1), words);
        }

        return words;
    }
}
