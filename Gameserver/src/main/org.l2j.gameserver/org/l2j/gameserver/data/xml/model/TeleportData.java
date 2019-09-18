package org.l2j.gameserver.data.xml.model;

import org.l2j.gameserver.model.Location;

public class TeleportData {

    private final byte castleId;
    private final long price;
    private final Location location;

    public TeleportData(long price, Location location, byte castleId) {
        this.price = price;
        this.location = location;
        this.castleId = castleId;
    }

    public long getPrice() {
        return price;
    }

    public Location getLocation() {
        return location;
    }

    public byte getCastleId() {
        return castleId;
    }
}