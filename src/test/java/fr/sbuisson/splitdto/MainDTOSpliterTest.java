package fr.sbuisson.splitdto;

import fr.sbuisson.splitdto.model.Arguments;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static java.util.Arrays.asList;

public class MainDTOSpliterTest {

    @Test
    public void testMain() throws Exception {
        MainDTOSpliter.main(new String[]{"-inputdirectory",".\\src\\test\\java", "-outputDirectory",".\\outputSplit"});
    }

    @Test
    public void testRun() throws Exception {
        Arguments arguments = new Arguments();
        arguments.setDirectoryInput(asList(".\\src\\test\\java\\sample\\rest"));
        arguments.setDirectoryOutput(asList(".\\outputSplit"));
        MainDTOSpliter.run(arguments);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
