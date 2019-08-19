package org.l2j.gameserver.data.xml.model;

import org.l2j.gameserver.model.Location;

public class TeleportData {

    private long price;
    private Location location;

    public TeleportData(long price, Location location) {
        this.price = price;
        this.location = location;
    }

    public long getPrice() {
        return price;
    }

    public Location getLocation() {
        return location;
    }
}