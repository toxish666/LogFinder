package com.aaa.finder;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class FileFoundInformationIntegration {

    @Test
    public void givenLogFile_whenOnlyOneLineToFind_thenItIsContainedInList() throws IOException {
        String path = "src/main/resources/finder/sys.log";
        String textToFind = "b  8 07:42:07 Antons-MacBook-Pro com.apple.xpc.launchd[1] (org.jenkins-ci): Service only ran for 0 secon";
        String[] lines = textToFind.split("\\r?\\n");
        List<String> listOfFind = new ArrayList<>(Arrays.asList(lines));
        listOfFind = listOfFind.stream().map(String::trim).collect(toList());

        assertThat(listOfFind.size()).isEqualTo(1);

        List<Integer> all=FinderUtils.lines(Paths.get(path), listOfFind)
                .filter(FinderUtils.NumberedLine::isIsend)
                .map(FinderUtils.NumberedLine::getNumber)
                .collect(Collectors.toList());

        assertThat(all.size()).isEqualTo(1);
    }


    @Test
    public void givenLogFile_whenOnlyTwoLineToFind_thenItIsContainedInList() throws IOException {
        String path = "src/main/resources/finder/sys.log";
        String textToFind = "ding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<request protocol=\"3.0\" version=\"Keyston";
        String[] lines = textToFind.split("\\r?\\n");
        List<String> listOfFind = new ArrayList<>(Arrays.asList(lines));
        listOfFind = listOfFind.stream().map(String::trim).collect(toList());

        assertThat(listOfFind.size()).isEqualTo(2);

        List<Integer> all=FinderUtils.lines(Paths.get(path), listOfFind)
                .filter(FinderUtils.NumberedLine::isIsend)
                .map(FinderUtils.NumberedLine::getNumber)
                .collect(Collectors.toList());

        assertThat(all.size()).isEqualTo(1);
    }


    @Test
    public void givenLogFile_whenMoreThanTwoLinesAreGiven_thenItIsContainedInList() throws IOException {
        String path = "src/main/resources/finder/sys.log";
        String textToFind = "<app appid=\"com.google.Keystone\" version=\"1.2.11.124\" cohort=\"1:0:ql9@0.0\" cohortname=\"Everyone\" lang=\"en-us\" installage=\"217\" installdate=\"4200\" brand=\"GGLG\" signed=\"1\">\n" +
                "\t       <ping rd=\"4420\" ad=\"4420\" ping_freshness=\"{A134ED8B-046B-4BD3-86C0-87137EB350C3}\"></ping>\n" +
                "      <updatecheck></updatecheck>\n" +
                "\t\t\t    </app>";
        String[] lines = textToFind.split("\\r?\\n");
        List<String> listOfFind = new ArrayList<>(Arrays.asList(lines));
        listOfFind = listOfFind.stream().map(String::trim).collect(toList());

        assertThat(listOfFind.size()).isEqualTo(4);

        List<Integer> all=FinderUtils.lines(Paths.get(path), listOfFind)
                .filter(FinderUtils.NumberedLine::isIsend)
                .map(FinderUtils.NumberedLine::getNumber)
                .collect(Collectors.toList());

        assertThat(all.size()).isEqualTo(1);
    }






}
