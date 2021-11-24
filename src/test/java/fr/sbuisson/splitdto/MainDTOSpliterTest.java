package fr.sbuisson.splitdto;

import fr.sbuisson.splitdto.model.Arguments;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class MainDTOSpliterTest {

    @Test
    public void testMain() throws Exception {
        MainDTOSpliter.main(new String[]{"args"});
    }

    @Test
    public void testRun() throws Exception {
        Arguments arguments = new Arguments();
        arguments.setDirectoryInput(".\\src\\test\\java");
        arguments.setDirectoryOutput(".\\outputSplit");
        MainDTOSpliter.run(arguments);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
