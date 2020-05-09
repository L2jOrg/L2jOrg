package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.engine.autoplay.AutoPlaySettings;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.autoplay.ExAutoPlaySettingResponse;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class ExAutoPlaySetting extends ClientPacket {

    private short size;
    private boolean active;
    private boolean pickUp;
    private short nextTargetMode;
    private boolean isNearTarget;
    private int usableHpPotionPercent;
    private boolean respectfulHunt;
    private int usableHpPetPotionPercent;

    @Override
    protected void readImpl() throws Exception {
        size = readShort();
        active = readBoolean();
        pickUp = readBoolean();
        nextTargetMode = readShort();
        isNearTarget = readBoolean();
        usableHpPotionPercent = readInt();
        usableHpPetPotionPercent = readInt();
        respectfulHunt = readBoolean();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        var settings = player.getAutoPlaySettings();
        if(isNull(settings)) {
            settings = new AutoPlaySettings(size, active, pickUp, nextTargetMode, isNearTarget, usableHpPotionPercent, usableHpPetPotionPercent, respectfulHunt);
            player.setAutoPlaySettings(settings);
        } else {
            settings.setSize(size);
            settings.setActive(active);
            settings.setAutoPickUpOn(pickUp);
            settings.setNextTargetMode(nextTargetMode);
            settings.setNearTarget(isNearTarget);
            settings.setUsableHpPotionPercent(usableHpPotionPercent);
            settings.setUsableHpPetPotionPercent(usableHpPetPotionPercent);
            settings.setRespectfulHunt(respectfulHunt);
        }
        if(active) {
            AutoPlayEngine.getInstance().startAutoPlay(client.getPlayer());
        } else {
            AutoPlayEngine.getInstance().stopAutoPlay(client.getPlayer());
        }
        client.sendPacket(new ExAutoPlaySettingResponse(settings));
    }
}
