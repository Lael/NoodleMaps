package autocorrect;

import com.google.common.collect.Lists;

import java.util.*;

public class Trie {
    private int wordOccurrences = 0;
    private Character c = null;
    private Map<Character, Trie> children = new HashMap<Character, Trie>();

    private class Word implements Comparable<Word> {
        private String word;
        private int led;
        private int unigram;

        private Word(String word, int led, int unigram) {
            this.word = word;
            this.led = led;
            this.unigram = unigram;
        }

        public String toString() {
            return word;
        }

        private Word prepend(String s) {
            word = s + word;
            return this;
        }

        private void setLed(int led) {
            this.led = led;
        }

        public int compareTo(Word o) {
            if (this.led != o.led) {
                return o.led - this.led;
            }
            if (this.unigram != o.unigram) {
                return this.unigram - o.unigram;
            }
            return this.word.compareTo(o.word);
        }
    }

    public Trie() {

    }

    public Trie(Character c) {
        this.c = c;
    }

    public void add(String s) {
        if (s.isEmpty()) {
            wordOccurrences += 1;
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

    public void printTrie() {
        System.out.println("Trie:");
        printHelper(" ");
    }

    private void printHelper(String spaces) {
        if (c != null) {
            System.out.println(spaces + c + (wordOccurrences > 0 ? "." : ""));
        }
        Collection<Trie> tries = children.values();
        for (Trie trie : tries) {
            trie.printHelper(spaces + " ");
        }
    }

    public List<Word> words() {
        List<Word> words = Lists.newArrayList();
        if (wordOccurrences > 0) {
            words.add(new Word(String.valueOf(c), -1, wordOccurrences));
        }

        Collection<Trie> tries = children.values();
        String toPrepend = (c == null) ? "" : String.valueOf(c);
        for (Trie trie : tries) {
            List<Word> suffixes = trie.words();
            for (Word w : suffixes) {
                w.prepend(toPrepend);
            }
            words.addAll(suffixes);
        }
        return words;
    }

    public List<Word> wordsWithPrefix(String prefix) {
        if (prefix.isEmpty()) {
            return words();
        }

        List<Word> words = Lists.newArrayList();
        Trie trie = children.get(prefix.charAt(0));
        if (trie == null) {
            return words;
        }

        words = trie.wordsWithPrefix(prefix.substring(1));
        String toPrepend = (c == null) ? "" : String.valueOf(c);
        for (Word w : words) {
            w.prepend(toPrepend);
        }
        return words;
    }

    private void levenshteinHelper(Trie trie, String sofar, String prefix, Integer[] row, int led, List<Word> words) {
        int len = row.length;
        Integer[] newRow = new Integer[len];
        newRow[0] = row[0] + 1;

        if (sofar.length() == len - 1) {
            List<Word> theseWords = trie.words();
            for (Word w : theseWords) {
                w.setLed(row[len - 1]);
                words.add(w.prepend(sofar));
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

        if (trie.wordOccurrences > 0 && newRow[len - 1] <= led) {
            Word word = new Word(sofar + trie.c, newRow[len - 1], trie.wordOccurrences);
            words.add(word);
        }

        if (Collections.min(Arrays.asList(newRow)) <= led) {
            for (Trie child : trie.children.values()) {
                levenshteinHelper(child, sofar + trie.c, prefix, newRow, led, words);
            }
        }
    }

    public List<Word> wordsWithinDistance(String prefix, int led) {
        int len = prefix.length();
        if (len == 0) {
            return Lists.newArrayList();
        }
        Integer[] row = new Integer[len + 1];
        for (int i = 0; i < len + 1; i++) {
            row[i] = i;
        }
        List<Word> words = Lists.newArrayList();
        for (Trie trie : children.values()) {
            levenshteinHelper(trie, "", prefix, row, Math.min(led, len - 1), words);
        }
        return words;
    }

    public List<String> autocorrect(String word, int led, int limit) {
        List<String> strings = Lists.newArrayList();
        List<Word> words = wordsWithinDistance(word, led);
        Collections.sort(words);
        for (int i = 0; i < Math.min(words.size(), limit); i++) {
            strings.add(words.get(i).toString());
        }
        return strings;
    }
}
