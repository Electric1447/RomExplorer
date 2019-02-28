package eparon.romexplorer;

import java.io.Serializable;

public class Rom implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String version;
    private String status;
    private String type;
    private String url;

    public Rom(String n, String v, String s, String t, String u) {
        this.name = n;
        this.version = v;
        this.status = s;
        this.type = t;
        this.url = u;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;
    }

    public String getType() {
        return this.type;
    }

    public String getUrl() {
        return this.url;
    }

    public String getVersion() {
        return this.version;
    }

    public static Rom[] sortRoms (String[] r) {

        Rom[] temp = new Rom[r.length / 5];

        for (int i = 0; i < r.length / 5; i++)
            temp[i] = new Rom(r[5*i], r[ 5*i + 1], r[5*i + 2], r[5*i + 3], r[5*i + 4]);

        return temp;
    }

}
