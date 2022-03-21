package fr.sbuisson.itgenerator.model;

import com.github.jankroken.commandline.annotations.*;

import java.util.List;

import static java.util.Arrays.asList;

public class Arguments {
    private List<String> inputDirectory;
    private List<String> outputDirectory;
    private List<String> srcDirectory;

    @Option
    @LongSwitch("inputdirectory")
    @ShortSwitch("i")
    @Required
    @AllAvailableArguments
    public void setInputDirectory(List<String> inputDirectory) {

        this.inputDirectory = inputDirectory;
        if (this.outputDirectory == null || this.outputDirectory.isEmpty()) {
            this.outputDirectory = asList(this.inputDirectory.get(0).replace("main", "test"));
        }
    }

    @Option
    @LongSwitch("outputDirectory")
    @ShortSwitch("o")
    @AllAvailableArguments
    public void setOutputDirectory(List<String> outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


    @Option
    @LongSwitch("srcDirectory")
    @ShortSwitch("s")
    @AllAvailableArguments
    public void setSrcDirectory(List<String> srcDirectory) {
        this.srcDirectory = srcDirectory;
    }


    public String getInputDirectory() {
        return inputDirectory.get(0);
    }

    public String getOutputDirectory() {
        return outputDirectory.get(0);
    }

    public String getSrcDirectory() {
        return srcDirectory.get(0);
    }
}
