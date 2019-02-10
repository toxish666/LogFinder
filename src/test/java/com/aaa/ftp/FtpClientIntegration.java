package com.aaa.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FtpClientIntegration {

    private FakeFtpServer fakeFtpServer;

    private FtpClient ftpClient;

    @Before
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/foo.txt", "sadfokasdfokpjasdfjpko\n" +
                "fsdpkadsfkdfask\n" +
                "kfkfkffk\n" +
                "            okookkjokp  asdsad ___ -@Q$2 qpkkf/b/b/b/b"));
        fileSystem.add(new DirectoryEntry("/data/testfolder"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
    }

    @After
    public void teardown() throws IOException {
        ftpClient.close();
        fakeFtpServer.stop();
    }

    @Test
    public void givenRemoteFile_whenListingRemoteFiles_thenItIsContainedInList() throws IOException {
        Collection<String> files = ftpClient.listFiles("");

        Path p = Paths.get("/data");

        ftpClient.listFoldersAndFiles(p).getKey().forEach(folder -> System.out.println(p.resolve(Paths.get(folder.getName()))));

        assertThat(files).contains("foo.txt");
        assertThat(files).contains("testfolder");
    }

    @Test
    public void givenRemoteFile_whenDownloading_thenItIsOnTheLocalFilesystem() throws IOException {
        ftpClient.downloadFile("/foo.txt", "downloaded.txt");

        assertThat(new File("downloaded.txt")).exists();
        new File("downloaded.txt").delete(); // cleanup
    }

    @Test
    public void givenLocalFile_whenUploadingIt_thenItExistsOnRemoteLocation() throws URISyntaxException, IOException {
        File file = new File(getClass().getClassLoader().getResource("ftp/test.txt").toURI());

        ftpClient.putFileToPath(file, "/test.txt");

        assertThat(fakeFtpServer.getFileSystem().exists("/test.txt")).isTrue();
    }

    @Test
    public void givenRemoteFile_whenOneLineOfSearchGiven_thenFindItCorrectly() throws IOException{

        List<Integer> ll = ftpClient.findString(Paths.get("/data/foo.txt"), "dfokasdf");
        assertThat(ll.size()).isEqualTo(1);

    }

    @Test
    public void givenRemoteFile_whenTwoLinesOfSearchGiven_thenFindItCorrectly() throws IOException{

        List<Integer> ll = ftpClient.findString(Paths.get("/data/foo.txt"), "asdfokpjasdfjpko\n" +
                "fsdpka");
        assertThat(ll.size()).isEqualTo(1);

    }

    @Test
    public void givenRemoteFile_whenMoreThanTwoLinesOfSearchGiven_thenFindItCorrectly() throws IOException{

        List<Integer> ll = ftpClient.findString(Paths.get("/data/foo.txt"), "dfokasdfokpjasdfjpko\n" +
                "fsdpkadsfkdfask\n" +
                "kfkfkffk\n" +
                "    okookkjokp  asdsad ___ -@Q$2 qpkkf/b/b/b");
        assertThat(ll.size()).isEqualTo(1);

    }


}