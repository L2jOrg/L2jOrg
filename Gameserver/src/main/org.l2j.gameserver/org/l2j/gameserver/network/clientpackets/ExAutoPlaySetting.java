package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.network.serverpackets.ExAutoPlayDoMacro;
import org.l2j.gameserver.world.World;

/**
 * @author JoeAlisson
 */
public class ExAutoPlaySetting extends ClientPacket {

    private short options;
    private boolean active;
    private boolean pickUp;
    private short nextTargetMode;
    private boolean isNearTarget;
    private int usableHpPotionPercent;
    private boolean mannerMode;

    @Override
    protected void readImpl() throws Exception {
        options = readShort();
        active = readByteAsBoolean();
        pickUp = readByteAsBoolean();
        nextTargetMode = readShort();
        isNearTarget = readByteAsBoolean();
        usableHpPotionPercent = readInt();
        mannerMode = readByteAsBoolean();
    }

    @Override
    protected void runImpl() {
        var range = isNearTarget ? 600 : 1400;
        var player = client.getPlayer();
        var monster = World.getInstance().findAnyVisibleObject(player, Monster.class, range, false, m -> m.isAutoAttackable(player));
        player.setTarget(monster);
        client.sendPacket(new org.l2j.gameserver.network.serverpackets.ExAutoPlaySetting(options, active, pickUp, nextTargetMode, isNearTarget, usableHpPotionPercent, mannerMode));
        client.sendPacket(new ExAutoPlayDoMacro());
    }
}
