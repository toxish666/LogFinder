package com.aaa.finder;

//import org.openjdk.jmh.annotations.Benchmark;
//import org.openjdk.jmh.annotations.Scope;
//import org.openjdk.jmh.annotations.State;
//import org.openjdk.jmh.infra.Blackhole;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


import static java.util.stream.Collectors.toList;

//helper class that contains information about lines where match happens

//at the very same time this class contains all work related to finding
//and analyzing file whether it contains any matches or not
//(if not - lineMatches is empty, so we don't count this analyzed file in the 'userclass' of this class)
//@State(Scope.Benchmark)
public class FileFoundInformation {

    //for bench
    public FileFoundInformation(){

    }

//    @Benchmark
//    public void benchmarking(){
//        try {
//            findFileInfo("src/main/resources/finder/sys.log", "Antons-MacBook-Pro syslogd[47]: ASL Sender Statistics\n" +
//                    "Feb  8 02:55:17 Antons-MacBook-Pro com.apple.xpc.launchd[1]");
//        }catch (Exception e){e.printStackTrace();}
//    }


    //since line by line method is not appropriate
    //trying to read file by chunks and find there information
    public static List<Integer> findFileInfo(String path, String textToFind) throws IOException{
//        v1
//        RandomAccessFile aFile = new RandomAccessFile(path, "r");
//        FileChannel inChannel = aFile.getChannel();
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        while(inChannel.read(buffer) > 0) {
//            buffer.flip();
//
//            new String(buffer.array(), StandardCharsets.UTF_8);
//
//
//            buffer.clear(); // do something with the data and clear/compact it.
//        }
//        inChannel.close();
//        aFile.close();

        //v2
//        Files.lines(Paths.get(path))
//                .forEach(l -> {
//                    if (l.contains(textToFind)){
//                          System.out.print(linecounter);
//                    }
//                });


        //v3
//        LineNumberReader numberRdr = new LineNumberReader(Files.newBufferedReader(Paths.get(path)));
//        List<Integer> linesNumbers = numberRdr.lines()
//                .filter(w -> w.contains(textToFind))
//                .map(w -> numberRdr.getLineNumber())
//                .collect(toList());
//        System.out.println(linesNumbers.size());


        String[] lines = textToFind.split("\\r?\\n");
        List<String> listOfFind = new ArrayList<>(Arrays.asList(lines));
        listOfFind = listOfFind
                .stream()
                .map(String::trim)
                .collect(toList());

        return FinderUtils.lines(Paths.get(path), listOfFind)
                .filter(FinderUtils.NumberedLine::isIsend)
                .map(FinderUtils.NumberedLine::getNumber)
                .collect(Collectors.toList());

    }
}


