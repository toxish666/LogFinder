package com.aaa.finder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FinderUtils {

    final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static final class NumberedLine {
        final int number;
        final String line;

        final int nextmatched;
        final boolean isend;

        NumberedLine(int number, String line, int nextmatched, boolean isend) {
            this.number = number;
            this.line = line;

            this.nextmatched = nextmatched;
            this.isend = isend;
        }


        public boolean isIsend() {
            return isend;
        }

        public int getNumber() {
            return number;
        }
        public String getLine() {
            return line;
        }
        @Override
        public String toString() {
            return number+":\t"+line;
        }
    }

    public static Stream<NumberedLine> lines(Path p, List<String> textToFind) throws IOException {
        BufferedReader b= Files.newBufferedReader(p);
        Spliterator<NumberedLine> sp=new Spliterators.AbstractSpliterator<NumberedLine>(
                Long.MAX_VALUE, Spliterator.ORDERED|Spliterator.NONNULL) {
            int line;
            int nextmatched = 0;
            public boolean tryAdvance(Consumer<? super NumberedLine> action) {
                String s;
                try { s=(b.readLine()); }
                catch(IOException e){ throw new UncheckedIOException(e); }
                if(s==null) return false;

                s=s.trim();

                //no element here
                if(nextmatched == 0 && !s.contains(textToFind.get(0))) {
                    action.accept(new NumberedLine(++line, s, 0, false));
                //first case - there is only 1 element in list (only 1 line of searching)
                //so if the string that we pass contains our lookup string --> we find it
                } else if (textToFind.size() == 1 && s.contains(textToFind.get(0))) {
                    action.accept(new NumberedLine(++line, s, 0, true));
                    nextmatched = 0;
                //second case - there is particularly 2 elements in list (only 2 lines of searchings)
                //so, first element must be the suffix of the string that we pass
                //    second element mus be the prefix of the string that we pass
                } else if (textToFind.size() == 2
                        && nextmatched == 0
                        && s.endsWith(textToFind.get(0))){
                    action.accept(new NumberedLine(++line, s, ++nextmatched, true));
                } else if(textToFind.size() == 2
                        && nextmatched == 1
                        && s.startsWith(textToFind.get(1))){
                    action.accept(new NumberedLine(++line, s, ++nextmatched, false));
                    nextmatched=0;
                }
                //third case - there is more than 2 elements (more than 2 lines of searchings)
                //so, first element must be the suffix of the string that we pass
                //    n-1 element must be equal to the string that we pass
                //    n element mus be the prefix of the string that we pass
                else if(textToFind.size() > 2
                        && nextmatched == 0
                        && s.endsWith(textToFind.get(0))){
                    action.accept(new NumberedLine(++line, s, ++nextmatched, false));
                } else if(textToFind.size() > 2
                        && nextmatched==textToFind.size() - 1
                        && s.startsWith(textToFind.get(nextmatched))){
                    action.accept(new NumberedLine(++line, s, ++nextmatched, true));
                    nextmatched = 0;
                } else if(textToFind.size() > 2
                        && s.equals(textToFind.get(nextmatched))){
                    action.accept(new NumberedLine(++line, s, ++nextmatched, false));
                } else {
                    nextmatched = 0;
                }

                return true;
            }
        };
        return StreamSupport.stream(sp, false).onClose(()->{
            try { b.close(); } catch(IOException e){ throw new UncheckedIOException(e); }});
    }
}
