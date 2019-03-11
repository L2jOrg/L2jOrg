/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.actor.L2Character;

import java.util.function.Function;

/**
 * @author UnAfraid
 */
public enum StatusUpdateType {
    LEVEL(0x01, L2Character::getLevel),
    EXP(0x02, creature -> (int) creature.getStat().getExp()),
    STR(0x03, L2Character::getSTR),
    DEX(0x04, L2Character::getDEX),
    CON(0x05, L2Character::getCON),
    INT(0x06, L2Character::getINT),
    WIT(0x07, L2Character::getWIT),
    MEN(0x08, L2Character::getMEN),

    CUR_HP(0x09, creature -> (int) creature.getCurrentHp()),
    MAX_HP(0x0A, L2Character::getMaxHp),
    CUR_MP(0x0B, creature -> (int) creature.getCurrentMp()),
    MAX_MP(0x0C, L2Character::getMaxMp),

    P_ATK(0x11, L2Character::getPAtk),
    ATK_SPD(0x12, L2Character::getPAtkSpd),
    P_DEF(0x13, L2Character::getPDef),
    EVASION(0x14, L2Character::getEvasionRate),
    ACCURACY(0x15, L2Character::getAccuracy),
    CRITICAL(0x16, creature -> (int) creature.getCriticalDmg(1)),
    M_ATK(0x17, L2Character::getMAtk),
    CAST_SPD(0x18, L2Character::getMAtkSpd),
    M_DEF(0x19, L2Character::getMDef),
    PVP_FLAG(0x1A, creature -> (int) creature.getPvpFlag()),
    REPUTATION(0x1B, creature -> creature.isPlayer() ? creature.getActingPlayer().getReputation() : 0),

    CUR_CP(0x21, creature -> (int) creature.getCurrentCp()),
    MAX_CP(0x22, L2Character::getMaxCp);

    private int _clientId;
    private Function<L2Character, Integer> _valueSupplier;

    StatusUpdateType(int clientId, Function<L2Character, Integer> valueSupplier) {
        _clientId = clientId;
        _valueSupplier = valueSupplier;
    }

    public int getClientId() {
        return _clientId;
    }

    public int getValue(L2Character creature) {
        return _valueSupplier.apply(creature);
    }
}
