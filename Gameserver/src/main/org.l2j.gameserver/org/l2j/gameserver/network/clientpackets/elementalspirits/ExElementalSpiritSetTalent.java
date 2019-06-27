package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritSetTalent;

import static java.util.Objects.nonNull;

public class ExElementalSpiritSetTalent extends ClientPacket {

    private byte type;
    private byte attackPoints;
    private byte defensePoints;
    private byte critRate;
    private byte critDamage;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
        readByte(); // Characteristics for now always 4

        readByte(); // attack id
        attackPoints = readByte();

        readByte(); // defense id
        defensePoints = readByte();

        readByte(); // crit rate id
        critRate = readByte();

        readByte(); // crit damage id
        critDamage = readByte();
    }

    @Override
    protected void runImpl() throws Exception {
        var spirit = client.getActiveChar().getElementalSpirit(ElementalType.of(type));

        var result = false;

        if(nonNull(spirit) ) {
            if(attackPoints > 0 && spirit.getAvailableCharacteristicsPoints() >= attackPoints) {
                spirit.addAttackPoints(attackPoints);
                result = true;
            }

            if(defensePoints > 0 && spirit.getAvailableCharacteristicsPoints() >= defensePoints) {
                spirit.addDefensePoints(defensePoints);
                result = true;
            }

            if(critRate > 0 && spirit.getAvailableCharacteristicsPoints() >= critRate) {
                spirit.addCritRatePoints(critRate);
                result = true;
            }

            if(critDamage > 0 && spirit.getAvailableCharacteristicsPoints() >= critDamage) {
                spirit.addCritDamage(critDamage);
                result = true;
            }
        }

        client.sendPacket(new ElementalSpiritSetTalent(type, result));

    }
}
