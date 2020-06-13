/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritSetTalent;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.CHARACTERISTICS_WERE_APPLIED_SUCCESSFULLY;

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
    protected void runImpl()  {
        var spirit = client.getPlayer().getElementalSpirit(ElementalType.of(type));

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

        if(result) {
            var userInfo = new UserInfo(client.getPlayer());
            userInfo.addComponentType(UserInfoType.SPIRITS);
            client.sendPacket(userInfo);
            client.sendPacket(SystemMessage.getSystemMessage(CHARACTERISTICS_WERE_APPLIED_SUCCESSFULLY));
        }
        client.sendPacket(new ElementalSpiritSetTalent(type, result));

    }
}
