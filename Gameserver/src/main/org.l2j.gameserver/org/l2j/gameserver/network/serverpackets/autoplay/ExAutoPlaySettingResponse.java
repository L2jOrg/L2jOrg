package org.l2j.gameserver.network.serverpackets.autoplay;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExAutoPlaySettingResponse extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_AUTOPLAY_SETTING);

        final var settings = client.getPlayer().getAutoPlaySettings();
        writeShort(settings.getSize());
        writeByte(settings.isActive());
        writeByte(settings.isAutoPickUpOn());
        writeShort(settings.getNextTargetMode());
        writeByte(settings.isNearTarget());
        writeInt(settings.getUsableHpPotionPercent());
        writeInt(settings.getUsableHpPetPotionPercent());
        writeByte(settings.isRespectfulMode());
    }
}
