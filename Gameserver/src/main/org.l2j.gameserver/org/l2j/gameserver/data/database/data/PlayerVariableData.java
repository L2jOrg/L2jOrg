package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("player_variables")
public class PlayerVariableData {

    public static final int REVENGE_USABLE_FUNCTIONS = 5;

    @Column("player_id")
    private int playerId;

    @Column("revenge_teleports")
    private byte revengeTeleports;

    @Column("revenge_locations")
    private byte revengeLocations;

    public byte getRevengeTeleports() {
        return revengeTeleports;
    }

    public byte getRevengeLocations() {
        return revengeLocations;
    }

    public static PlayerVariableData init(int playerId) {
        var data = new PlayerVariableData();
        data.revengeTeleports = REVENGE_USABLE_FUNCTIONS;
        data.revengeLocations = REVENGE_USABLE_FUNCTIONS;
        data.playerId = playerId;
        return data;
    }

    public void useRevengeLocation() {
        revengeLocations--;
    }

    public void useRevengeTeleport() {
        revengeTeleports--;
    }

    public void resetRevengeData() {
        revengeTeleports = REVENGE_USABLE_FUNCTIONS;
        revengeLocations = REVENGE_USABLE_FUNCTIONS;
    }
}
