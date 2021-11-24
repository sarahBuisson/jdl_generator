package fr.sbuisson.packagemover.model;

import com.github.jankroken.commandline.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class Arguments {
    private String filename;
    private String directoryInput;
    private String directoryOutput;

    @Option
    @LongSwitch("file")
    @ShortSwitch("f")
    @SingleArgument
    @Required
    public void setFilename(String filename) {
        this.filename = filename;
    }

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

    public String getFilename() {
        return filename;
    }



    public String getDirectoryInput() {
        return directoryInput;
    }

    public String getDirectoryOutput() {
        return directoryOutput;
    }
}
