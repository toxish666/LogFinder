package com.aaa.finder;


import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

//there might be 2 strategies of finding:
//1) in memory(local fs)
//2) ftp(network fs)
public interface FinderStrategy {
    Map<Path, List<Integer>> listAllMatchedFiles(Path initpath, Pattern format, String textToFind);
}
