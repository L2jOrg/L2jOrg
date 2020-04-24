package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A mother-trees zone Basic type zone for Hp, MP regen
 *
 * @author durgus
 */
public class MotherTreeZone extends Zone {
    private int enterMsg;
    private int leaveMsg;
    private int mpRegen;
    private int hpRegen;

    public MotherTreeZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        switch (name) {
            case "enterMsgId" -> enterMsg = Integer.parseInt(value);
            case "leaveMsgId" -> leaveMsg = Integer.parseInt(value);
            case "MpRegenBonus" -> mpRegen = Integer.parseInt(value);
            case "HpRegenBonus" -> hpRegen = Integer.parseInt(value);
            default -> super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            creature.setInsideZone(ZoneType.MOTHER_TREE, true);
            if (enterMsg != 0) {
                player.sendPacket(SystemMessage.getSystemMessage(enterMsg));
            }
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (isPlayer(character)) {
            final Player player = character.getActingPlayer();
            player.setInsideZone(ZoneType.MOTHER_TREE, false);
            if (leaveMsg != 0) {
                player.sendPacket(SystemMessage.getSystemMessage(leaveMsg));
            }
        }
    }

    /**
     * @return the _mpRegen
     */
    public int getMpRegenBonus() {
        return mpRegen;
    }

    /**
     * @return the _hpRegen
     */
    public int getHpRegenBonus() {
        return hpRegen;
    }
}
