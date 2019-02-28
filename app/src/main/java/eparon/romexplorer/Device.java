package eparon.romexplorer;

import java.io.Serializable;

public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String codename;
    private String manufacturer;
    private String year;
    private Rom[] roms;

    public Device(String n, String cn, String m, String y, Rom[] r) {
        this.name = n;
        this.codename = cn;
        this.manufacturer = m;
        this.year = y;
        this.roms = r;
    }

    public String getName() {
        return this.name;
    }

    public String getCodename() {
        return this.codename;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public String getYear() {
        return this.year;
    }

    public Rom[] getRoms() {
        return this.roms;
    }

    public static Device findDeviceByName(Device[] dev, String n) {

        for (Device aDev : dev)
            if (aDev.getName().equals(n))
                return aDev;

        return null;
    }

    public static Device findDeviceByCodename(Device[] dev, String cn) {

        for (Device aDev : dev)
            if (aDev.getCodename().equals(cn))
                return aDev;

        return null;
    }

    public static Device[] findDevicesByManufacturer(Device[] dev, String m) {

        int counter = 0;
        int[] arr = new int[dev.length];

        for (int i = 0; i < dev.length; i++) {
            if (dev[i].getManufacturer().equals(m)) {
                arr[counter] = i;
                counter++;
            }
        }

        if (counter == 0)
            return null;
        else {
            Device[] temp = new Device[counter];
            for (int i = 0; i < counter; i++)
                temp[i] = dev[arr[i]];
            return temp;
        }

    }

}
