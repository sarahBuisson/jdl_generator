package fr.sbuisson.packagemover;

import fr.sbuisson.packagemover.model.Arguments;
import org.junit.jupiter.api.Test;

public class MainPackageMoverTest {

    @Test
    public void testMain() throws Exception {
        Arguments arg= new Arguments();
        arg.setDirectoryInput(".\\src\\test\\java");
        arg.setDirectoryOutput(".\\output");
        arg.setFilename(".\\src\\test\\resources\\sample\\params.json");
        MainPackageMover.run(arg);
    }
}
