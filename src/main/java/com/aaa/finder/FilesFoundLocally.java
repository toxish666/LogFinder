package com.aaa.finder;


import org.apache.commons.lang3.tuple.Pair;
//import org.openjdk.jmh.annotations.Benchmark;
//import org.openjdk.jmh.annotations.Scope;
//import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//@State(Scope.Benchmark)
public class FilesFoundLocally extends FilesFoundAbstract{

    public FilesFoundLocally(){
        super(Executors.newWorkStealingPool(10));
    }
    public FilesFoundLocally(ExecutorService pool){
        super(pool);
    }


//    @Benchmark
//    public void benchmarking(){
//        Path p = Paths.get("/Users/aaa/MyProjects/rust_proj/tox/target/debug/build/libsodium-sys-7700862c8e74ab17/out/source/libsodium-1.0.16/");
//        FilesFoundLocally vvv =  new FilesFoundLocally();
//        Map map = vvv.listAllMatchedFiles(
//                p, Pattern.compile(".*?\\.log"), "afdadsf");
//    }

    @Override
    public Map<Path, List<Integer>> listAllMatchedFiles(Path initpath,
                                                                      Pattern format,
                                                                      String textToFind) {
        this.format = format;
        this.textToFind = textToFind;

        //run futures
        Map<Path, List<Integer>> m =
                processFolder(initpath)
                        .stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .collect(Collectors.toConcurrentMap((v) -> v.get().getKey(), v -> v.get().getValue()));
        return m;


        //////v2
//        FolderStructures folds = new FolderStructures();
//        folds.processFolder(initpath);
//        List<Path> filesToCheck = folds.getFilesToCheck();
//
//
//        Map<Path, List<Integer>> m = filesToCheck.stream()
//                .map(this::processFileInFuture)
//                .map(CompletableFuture::join)
//                .filter(Optional::isPresent)
//                .collect(Collectors.toConcurrentMap((v) -> v.get().getKey(), v -> v.get().getValue()));
//
//        return m;

    }



      ///for v2
//    private class FolderStructures{
//        List<Path> filesToCheck = new ArrayList<>();
//
//        List<Path> getFilesToCheck() {
//            return filesToCheck;
//        }
//
//        private void processFolder(Path inputPath){
//            try (Stream<Path> paths = Files.walk(inputPath)) {
////
//                Map<Boolean, List<Path>> groups = paths
//                        .filter(p -> !p.equals(inputPath))
//                        .collect(Collectors.partitioningBy(Files::isRegularFile));
//
//                groups.get(true).stream()
//                        .filter(filename -> format.matcher(filename.toString()).matches() && !Files.isSymbolicLink(filename))
//                        .forEach(filename -> filesToCheck.add(filename)
//                        );
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    }




    private List<
            CompletableFuture<
                    Optional<
                            Pair<
                                    Path,List<Integer>>>>> processFolder(Path inputPath) {

        try (Stream<Path> paths = Files.walk(inputPath)) {

            Map<Boolean, List<Path>> groups = paths
                    .filter(p -> !p.equals(inputPath))
                    .collect(Collectors.partitioningBy(Files::isRegularFile));


            return groups.get(true).stream()
                    .filter(filename -> format.matcher(filename.toString()).matches() && !Files.isSymbolicLink(filename))
                    .map(this::processFileInFuture)
                    .collect(Collectors.toList());
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private CompletableFuture
            <Optional
                    <Pair
                            <Path,List<Integer>>>> processFileInFuture(Path inputFileName){

        CompletableFuture<Optional<Pair<Path,List<Integer>>>> futureForSearching
                = new CompletableFuture<>();

       // LOGGER.log(Level.INFO, "ENTERING FILE: "+ inputFileName );
        pool.submit(() -> {
            try {
                List<Integer> info =
                        FileFoundInformation.findFileInfo(inputFileName.toAbsolutePath().toString(), textToFind);
                if (info.size() != 0) {
                    LOGGER.log(Level.INFO,
                            "Find appropriate file: " + inputFileName + " with " + info.size() + " matches");
                    futureForSearching.complete(Optional.of(Pair.of(inputFileName,info)));
                } else
                    futureForSearching.complete(Optional.empty());
            } catch (IOException ioexcp) {
                ioexcp.printStackTrace();
            }

        });
        return futureForSearching;
    }




}
