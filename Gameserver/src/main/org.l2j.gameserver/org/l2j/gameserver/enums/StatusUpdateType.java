package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.actor.Creature;

import java.util.function.Function;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public enum StatusUpdateType {
    LEVEL(0x01, Creature::getLevel),
    EXP(0x02, creature -> (int) creature.getStats().getExp()),
    STR(0x03, Creature::getSTR),
    DEX(0x04, Creature::getDEX),
    CON(0x05, Creature::getCON),
    INT(0x06, Creature::getINT),
    WIT(0x07, Creature::getWIT),
    MEN(0x08, Creature::getMEN),

    CUR_HP(0x09, creature -> (int) creature.getCurrentHp()),
    MAX_HP(0x0A, Creature::getMaxHp),
    CUR_MP(0x0B, creature -> (int) creature.getCurrentMp()),
    MAX_MP(0x0C, Creature::getMaxMp),

    P_ATK(0x11, Creature::getPAtk),
    ATK_SPD(0x12, Creature::getPAtkSpd),
    P_DEF(0x13, Creature::getPDef),
    EVASION(0x14, Creature::getEvasionRate),
    ACCURACY(0x15, Creature::getAccuracy),
    CRITICAL(0x16, creature -> (int) creature.getCriticalDmg(1)),
    M_ATK(0x17, Creature::getMAtk),
    CAST_SPD(0x18, Creature::getMAtkSpd),
    M_DEF(0x19, Creature::getMDef),
    PVP_FLAG(0x1A, creature -> (int) creature.getPvpFlag()),
    REPUTATION(0x1B, creature -> isPlayer(creature) ? creature.getActingPlayer().getReputation() : 0),

    CUR_CP(0x21, creature -> (int) creature.getCurrentCp()),
    MAX_CP(0x22, Creature::getMaxCp);

    private int _clientId;
    private Function<Creature, Integer> _valueSupplier;

    StatusUpdateType(int clientId, Function<Creature, Integer> valueSupplier) {
        _clientId = clientId;
        _valueSupplier = valueSupplier;
    }

    public int getClientId() {
        return _clientId;
    }

    public int getValue(Creature creature) {
        return _valueSupplier.apply(creature);
    }
}
