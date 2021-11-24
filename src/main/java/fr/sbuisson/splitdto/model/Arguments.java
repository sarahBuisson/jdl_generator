package fr.sbuisson.splitdto.model;

import com.github.jankroken.commandline.annotations.*;

public class Arguments {
    private String directoryInput;
    private String directoryOutput;

    @Option
    @LongSwitch("inputdirectory")
    @ShortSwitch("i")
    @Required
    @AllAvailableArguments
    public void setDirectoryInput(String directoryInput) {
        this.directoryInput = directoryInput;
    }

    @Option
    @LongSwitch("outputDirectory")
    @ShortSwitch("o")
    @Required
    @AllAvailableArguments
    public void setDirectoryOutput(String directoryOutput) {
        this.directoryOutput = directoryOutput;
    }



    public String getDirectoryInput() {
        return directoryInput;
    }

    public String getDirectoryOutput() {
        return directoryOutput;
    }
}
