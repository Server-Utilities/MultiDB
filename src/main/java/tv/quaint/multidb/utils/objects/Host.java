package tv.quaint.multidb.utils.objects;

public class Host {
    public String identifier;
    public String link;
    public String user;
    public String pass;

    public Host(String identifier, String link, String user, String pass) {
        this.identifier = identifier;
        this.link = link;
        this.user = user;
        this.pass = pass;
    }
}
