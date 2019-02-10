package com.aaa.finder;

import com.aaa.ftp.FtpClient;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FilesFoundRemotelyFtp extends FilesFoundAbstract {

    private FtpClient ftpClient;

    public FilesFoundRemotelyFtp(String server, int port, String user, String password){
        super(Executors.newWorkStealingPool(10));
        ftpClient = new FtpClient(server, port, user, password);
    }
    public FilesFoundRemotelyFtp(ExecutorService pool, String server, int port, String user, String password){
        super(pool);
        ftpClient = new FtpClient(server, port, user, password);
    }
    public FilesFoundRemotelyFtp(ExecutorService pool, FtpClient ftpClient){
        super(pool);
        this.ftpClient = ftpClient;
    }
    public FilesFoundRemotelyFtp(FtpClient ftpClient){
        super(Executors.newWorkStealingPool(10));
        this.ftpClient = ftpClient;
    }

    public void setFormat(Pattern format){
        this.format = format;
    }

    @Override
    public Map<Path, List<Integer>> listAllMatchedFiles(Path initpath, Pattern format, String textToFind) {
        this.format = format;
        this.textToFind = textToFind;

        FolderStructures folds = new FolderStructures();
        folds.processFolder(initpath);
        List<Path> filesToCheck = folds.getFilesToCheck();

        Map<Path, List<Integer>> m = filesToCheck.stream()
                .map(this::processFileInFuture)
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .collect(Collectors.toConcurrentMap((v) -> v.get().getKey(), v -> v.get().getValue()));

        return m;
    }

    List<Path> getMatchedFiles (Path initpath){
        FolderStructures folders = new FolderStructures();
        folders.processFolder(initpath);
        return folders.getFilesToCheck();
    }


    private class FolderStructures{
        List<Path> filesToCheck = new ArrayList<>();

        List<Path> getFilesToCheck() {
            return filesToCheck;
        }

        private void processFolder(Path inputPath){
            try {
                Pair<Collection<FTPFile>, Collection<FTPFile>> foldersandfiles
                        = ftpClient.listFoldersAndFiles(inputPath);

                foldersandfiles.getKey().forEach(
                        folder ->
                                processFolder(
                                        inputPath.resolve(
                                                folder.getName()
                                        )
                                )
                );

                foldersandfiles.getValue().stream()
                        .filter(filename -> format.matcher(filename.getName()).matches())
                        .forEach(filename -> filesToCheck.add(inputPath.resolve(filename.getName())));

            }catch (IOException ioexception){
                ioexception.printStackTrace();
            }
        }
    }



    private CompletableFuture
            <Optional
                    <Pair
                            <Path,List<Integer>>>> processFileInFuture(Path inputFileName) {

        CompletableFuture<Optional<Pair<Path,List<Integer>>>> futureForSearching
                = new CompletableFuture<>();

        pool.submit(() -> {
            try {
                List<Integer> info =
                        ftpClient.findString(inputFileName, textToFind);
                if (info.size() != 0) {
                    LOGGER.log(Level.INFO,
                            "(FTP) Find appropriate file: " + inputFileName + " with " + info.size() + " matches");
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
