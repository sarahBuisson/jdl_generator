package fr.sbuisson.itgenerator;

import fr.sbuisson.itgenerator.model.Arguments;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

class MainTestGeneratorTest {


    @Test
    public void testRun() throws Exception {
        var arguments = new Arguments();
        arguments.setInputDirectory(asList(".\\src\\test\\java\\sample\\rest\\controller"));
        arguments.setOutputDirectory(asList(".\\outputGenerator"));
        arguments.setSrcDirectory(asList(".\\src\\test\\java"));
        MainITGenerator.run(arguments);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
