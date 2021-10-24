package fr.sbuisson.jdlgenerator.model;

import com.github.jankroken.commandline.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class Arguments {
    private String filename;
    private boolean heritageMotherInsideDaughter;
    private List<String> directories = new ArrayList<>();

    @Option
    @LongSwitch("file")
    @ShortSwitch("f")
    @SingleArgument
    @Required
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Option
    @LongSwitch("directory")
    @ShortSwitch("d")
    @Required
    @AllAvailableArguments
    public void setDirectories(List<String> directories) {
        this.directories = directories;
    }

    @Option
    @LongSwitch("heritageMotherInsideDaughter")
    @ShortSwitch("hmid")
    @Toggle(true)
    public void setHeritageMotherInsideDaughter(boolean b) {
        this.heritageMotherInsideDaughter = b;
    }


    public String getFilename() {
        return filename;
    }

    public List<String> getDirectories() {
        return directories;
    }

    public boolean isHeritageMotherInsideDaughter() {
        return heritageMotherInsideDaughter;
    }
}
