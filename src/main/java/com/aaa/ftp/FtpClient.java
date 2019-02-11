package com.aaa.ftp;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class FtpClient {
    private String server;
    private int port;
    private String user;
    private String password;
    private FTPClient ftp;

    public FtpClient(String server, int port, String user, String password) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void open() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);

        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
        ftp.login(user, password);
        ftp.setControlKeepAliveTimeout(300);

    }

    public void close() throws IOException {
        ftp.disconnect();
    }

    public Collection<String> listFiles(String path) throws IOException {
        FTPFile[] files = ftp.listFiles(path);

        return Arrays.stream(files)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
    }

    void putFileToPath(File file, String path) throws IOException {
        ftp.storeFile(path, new FileInputStream(file));
    }

    void downloadFile(String source, String destination) throws IOException {
        FileOutputStream out = new FileOutputStream(destination);
        ftp.retrieveFile(source, out);
    }

    public Pair<Collection<FTPFile>, Collection<FTPFile>> listFoldersAndFiles(Path path) throws IOException {
         Map<Boolean, List<FTPFile>> folderfilesgroup =
                 Arrays
                         .stream(ftp.listFiles(path.toString()))
                         .collect(Collectors.partitioningBy(FTPFile::isDirectory));

        Collection<FTPFile> folders = folderfilesgroup.get(true);
        Collection<FTPFile> files = folderfilesgroup.get(false)
                .stream()
                .filter(f -> !f.isSymbolicLink() && f.hasPermission(FTPFile.USER_ACCESS , FTPFile.READ_PERMISSION))
                .collect(Collectors.toList());

        return Pair.of(folders,files);
    }

    public List<Integer> findString(Path path, String stringtofind) throws IOException{
        String[] lines = stringtofind.split("\\r?\\n");
        List<String> listOfFind = new ArrayList<>(Arrays.asList(lines));
        listOfFind = listOfFind.stream().map(String::trim).collect(toList());

        List<Integer> matchedStrings = new ArrayList<>();
        try (Scanner remotescanner = new Scanner(ftp.retrieveFileStream(path.toString()))) {

            int line = 0;
            while (remotescanner.hasNextLine()) {
                String nextline = remotescanner.nextLine().trim();
                //1 case
                if(listOfFind.size()==1 && nextline.contains(listOfFind.get(0))) {
                    matchedStrings.add(line);
                }
                //2 case
                if(listOfFind.size()==2 && nextline.endsWith(listOfFind.get(0))) {
                    if(remotescanner.hasNextLine()){
                        String nextline_2nd = remotescanner.nextLine().trim();
                        line++;
                        if(nextline_2nd.startsWith(listOfFind.get(1))){
                            matchedStrings.add(line);
                        }
                    }
                }
                //3 case
                if(listOfFind.size()>2 && nextline.endsWith(listOfFind.get(0))) {
                    int matchedlinesnumber = 1;
                    while (remotescanner.hasNextLine() && matchedlinesnumber !=listOfFind.size() - 1){
                        String nextline_nth = remotescanner.nextLine().trim();
                        line++;
                        if(!nextline_nth.equals(listOfFind.get(matchedlinesnumber)))
                            break;
                        matchedlinesnumber++;
                    }
                    if(remotescanner.hasNextLine()
                            && matchedlinesnumber == listOfFind.size() - 1){
                        String nextline_last = remotescanner.nextLine().trim();
                        line++;
                        if(nextline_last.startsWith(listOfFind.get(matchedlinesnumber)))
                            matchedStrings.add(line);
                    }
                }
                line++;
            }

        }

        ftp.completePendingCommand();
        return matchedStrings;
    }

    public InputStream retreiveInputStream(Path path){
        try {
            return ftp.retrieveFileStream(path.toString());
        }catch (IOException ex){
            ex.printStackTrace();
        }
        //unreachable
        return null;
    }
}
