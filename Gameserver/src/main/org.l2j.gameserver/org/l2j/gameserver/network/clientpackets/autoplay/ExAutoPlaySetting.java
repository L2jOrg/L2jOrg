package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.engine.autoplay.AutoPlaySetting;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

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
    private boolean respectfulHunt;

    @Override
    protected void readImpl() throws Exception {
        options = readShort();
        active = readByteAsBoolean();
        pickUp = readByteAsBoolean();
        nextTargetMode = readShort();
        isNearTarget = readByteAsBoolean();
        usableHpPotionPercent = readInt();
        respectfulHunt = readByteAsBoolean();
    }

    @Override
    protected void runImpl() {
        var setting = new AutoPlaySetting(options, active, pickUp, nextTargetMode, isNearTarget, usableHpPotionPercent, respectfulHunt);
        if(active) {
            AutoPlayEngine.getInstance().startAutoPlay(client.getPlayer(), setting);
        } else {
            AutoPlayEngine.getInstance().stopAutoPlay(client.getPlayer());
        }
        client.sendPacket(new org.l2j.gameserver.network.serverpackets.autoplay.ExAutoPlaySetting(setting));
    }
}
