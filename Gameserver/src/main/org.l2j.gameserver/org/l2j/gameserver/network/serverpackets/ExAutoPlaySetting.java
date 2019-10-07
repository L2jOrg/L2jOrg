package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public class ExAutoPlaySetting extends ServerPacket {

    private final short options;
    private final boolean active;
    private final boolean pickUp;
    private final short nextTargetMode;
    private final boolean isNearTarget;
    private final int usableHpPotionPercent;
    private final boolean mannerMode;

    public ExAutoPlaySetting(short options, boolean active, boolean pickUp, short nextTargetMode, boolean isNearTarget, int usableHpPotionPercent, boolean mannerMode) {
        this.options = options;
        this.active = active;
        this.pickUp = pickUp;
        this.nextTargetMode = nextTargetMode;
        this.isNearTarget = isNearTarget;
        this.usableHpPotionPercent = usableHpPotionPercent;
        this.mannerMode = mannerMode;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_AUTOPLAY_SETTING);
        writeShort(options);
        writeByte(active);
        writeByte(pickUp);
        writeShort(nextTargetMode);
        writeByte(isNearTarget);
        writeInt(usableHpPotionPercent);
        writeByte(mannerMode);
    }
}
