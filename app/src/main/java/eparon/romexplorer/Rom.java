package eparon.romexplorer;

import java.io.Serializable;

public class Rom implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String status;
    private String type;
    private String url;

    public Rom(String n, String s, String t, String u) {
        this.name = n;
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

    public static Rom[] sortRoms (String[] r) {

        Rom[] temp = new Rom[r.length / 4];

        for (int i = 0; i < r.length / 4; i++)
            temp[i] = new Rom(r[4*i], r[ 4*i + 1], r[4*i + 2], r[4*i + 3]);

        return temp;
    }

}
