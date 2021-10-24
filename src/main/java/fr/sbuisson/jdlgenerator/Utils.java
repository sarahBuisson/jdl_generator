package fr.sbuisson.jdlgenerator;

public  class Utils {

    static public String packageToPath(String directImport) {
        String path = directImport.replace(".", "\\");
        return path;
    }

    static public String decapitalize(String asString) {
        return "" + Character.toLowerCase(asString.charAt(0)) + asString.substring(1);


    }

    static public String capitalize(String asString) {
        return "" + Character.toUpperCase(asString.charAt(0)) + asString.substring(1);
    }
}
