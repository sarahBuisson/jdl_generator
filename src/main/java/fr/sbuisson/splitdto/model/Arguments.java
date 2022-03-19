package fr.sbuisson.splitdto.model;

import com.github.jankroken.commandline.annotations.*;

import java.util.List;

public class Arguments {
    private List<String> directoryInput;
    private List<String> directoryOutput;

    @Option
    @LongSwitch("inputdirectory")
    @ShortSwitch("i")
    @Required
    @AllAvailableArguments
    public void setDirectoryInput(List<String> directoryInput) {
        this.directoryInput = directoryInput;
    }

    @Option
    @LongSwitch("outputDirectory")
    @ShortSwitch("o")
    @Required
    @AllAvailableArguments
    public void setDirectoryOutput(List<String> directoryOutput) {
        this.directoryOutput = directoryOutput;
    }



    public List<String> getDirectoryInput() {
        return directoryInput;
    }

    public List<String> getDirectoryOutput() {
        return directoryOutput;
    }
}
