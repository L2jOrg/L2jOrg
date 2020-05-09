package org.l2j.gameserver.network.serverpackets.autoplay;

import org.l2j.gameserver.engine.autoplay.AutoPlaySettings;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExAutoPlaySettingResponse extends ServerPacket {

    private final AutoPlaySettings setting;

    public ExAutoPlaySettingResponse(AutoPlaySettings setting) {
        this.setting = setting;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_AUTOPLAY_SETTING);
        writeShort(setting.getSize());
        writeByte(setting.isActive());
        writeByte(setting.isAutoPickUpOn());
        writeShort(setting.getNextTargetMode());
        writeByte(setting.isNearTarget());
        writeInt(setting.getUsableHpPotionPercent());
        writeInt(setting.getUsableHpPetPotionPercent());
        writeByte(setting.isRespectfulMode());
    }
}
