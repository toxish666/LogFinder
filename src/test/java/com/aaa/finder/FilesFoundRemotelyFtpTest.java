package com.aaa.finder;

import com.aaa.ftp.FtpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FilesFoundRemotelyFtpTest {

    private FakeFtpServer fakeFtpServer;

    private FtpClient ftpClient;

    @Before
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/foo.txt", "sadfokasdfokpjasdfjpko\\n\" +\n" +
                "                \"fsdpkadsfkdfask\\n\" +\n" +
                "                \"kfkfkffk\\n\" +\n" +
                "                \"            okookkjokp  asdsad ___ -@Q$2 qpkkf/b/b/b/b"));
        fileSystem.add(new DirectoryEntry("/data/testfolder"));
        fileSystem.add(new FileEntry("/data/testfolder/fee.txt", "sadfokasdfokpjasdfjpko\\n\" +\n" +
                "                \"fsdpkadsfkdfask\\n\" +\n" +
                "                \"kfkfkffk\\n\" +\n" +
                "                \"            okookkjokp  asdsad ___ -@Q$2 qpkkf/b/b/b/b"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
    }


    @Test
    public void givenRemoteFileSystem_whenListingMatchingFiles_thenItIsContainedInList(){
        FilesFoundRemotelyFtp ftpworker = new FilesFoundRemotelyFtp(ftpClient);
        ftpworker.setFormat(Pattern.compile(".*?\\.txt"));
        List<Path> paths = ftpworker.getMatchedFiles(Paths.get("/"));
        paths.forEach(System.out::println);
        assertThat(paths.size()).isEqualTo(2);
    }


    @Test
    public void givenRemoteFileSystem_whenTextFindInFile_thenItIsContainedInMap() throws Exception{
        FilesFoundRemotelyFtp ftpworker = new FilesFoundRemotelyFtp(ftpClient);

        Map map = ftpworker.listAllMatchedFiles(Paths.get("/"), Pattern.compile(".*?\\.txt"),"@Q$2 qpkkf/b/b");
        assertThat(map.size()).isEqualTo(2);
    }




}
