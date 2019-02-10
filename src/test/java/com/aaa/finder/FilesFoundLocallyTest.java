package com.aaa.finder;

import com.aaa.ftp.FtpClient;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FilesFoundLocallyTest {

    @Test
    public void givenFolder_whenTwoLinesAreGiven_thenInMap() throws IOException {
        Path p = Paths.get("src/main/resources/finder/");

        Map map = new FilesFoundLocally().listAllMatchedFiles(
                p, Pattern.compile(".*?\\.log"), "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "\t\t\t<request protocol=\"3.0\" version=\"KeystoneAgent-1.");

        assertThat(map.size()).isEqualTo(2);
    }


    @Test
    public void givenFolder_whenMoreThanTwoLinesAreGiven_thenInMap() throws IOException {
        Path p = Paths.get("src/main/resources/finder/");

        Map map = new FilesFoundLocally().listAllMatchedFiles(
                p, Pattern.compile(".*?\\.log"), "ion=\"10.13\" arch=\"x86_64h\" sp=\"10.13.6_x86_64h\"></os>\n" +
                        "\t\t\t    <app appid=\"com.google.Keystone\" version=\"1.2.11.124\" cohort=\"1:0:ql9@0.0\" cohortname=\"Everyone\" lang=\"en-us\" installage=\"217\" installdate=\"4200\" brand=\"GGLG\" signed=\"1\">\n" +
                        "\t\t\t        <ping rd=\"4420\" ad=\"4420\" ping_freshness=\"{A134ED8B-046B-4BD3-86C0-87137EB350C3}\"></ping>\n" +
                        "        <updatecheck></updatecheck>\n" +
                        "\t    </app>\n" +
                        "\t\t\t</request>\n" +
                        "\t\theaders={\n" +
                        "\t\t\t\"X-Goog-Update-Interactivity\" = bg;\n" +
                        "\t\t}\n" +
                        "\t>\n" +
                        "\n" +
                        "Feb  8 07:42:15 Antons-MacBook-Pro GoogleSoftwareUpdateAgent[17021]: 2019-02-08 07:42:15.590 GoogleSoftwareUpdateAgent[17021/0x70000fc9d000] [lvl=2] -[KSEngineInvocation(KeystoneThread) performSelfUpdateWithEngine:error:] This process may be killed if self-update is necessary. In such cases, sub-processes may still be running to complete the self-update.\n" +
                        "Feb  8 07:42:15 Antons-MacBook-Pro GoogleSoftwareUpdateAgent[17021]: 2019-02-08 07:42:15.591 GoogleSoftwareUpdateAgent[17021/0x70000fc9d000] [lvl=2] -[KSEngineInvocation(KeystoneThread) updateProductWithProductID:usingEngine:error:] Checking for updates for \"com.google.Keystone\" using engine <KSUpdateEngine:0x7f8886469240\n" +
                        "\t\tticketStore=<KSPersistentTicketSto");

        assertThat(map.size()).isEqualTo(2);
    }
}
