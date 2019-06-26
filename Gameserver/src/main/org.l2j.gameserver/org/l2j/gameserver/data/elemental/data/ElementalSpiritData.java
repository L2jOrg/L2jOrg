package org.l2j.gameserver.data.elemental.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("character_spirits")
public class ElementalSpiritData {

    private int charId;
    private byte type;
    private byte level = 1;
    private byte stage;
    private long experience;

    @Column("attack_points")
    private byte attackPoints;

    @Column("defense_points")
    private byte defensePoints;

    @Column("crit_rate_points")
    private byte critRatePoints;

    @Column("crit_damage_points")
    private byte critDamagePoints;


    public ElementalSpiritData() {
        // default
    }

    public ElementalSpiritData(byte type, int objectId) {
        this.charId = objectId;
        this.type = type;
    }
}
