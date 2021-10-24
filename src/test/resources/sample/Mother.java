package sample;

import java.util.List;

public abstract class Mother implements Human{
    public String motherStuff;
    private String privateMotherStuff;
    public List<Friend> friends;

    public String getPrivateMotherStuff() {
        return motherStuff;
    }
}
