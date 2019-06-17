package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.EnumMap;
import java.util.Map;

public final class ActionFailed extends ServerPacket {
    public static final ActionFailed STATIC_PACKET = new ActionFailed();
    private static final Map<SkillCastingType, ActionFailed> STATIC_PACKET_BY_CASTING_TYPE = new EnumMap<>(SkillCastingType.class);

    static {
        for (SkillCastingType castingType : SkillCastingType.values()) {
            STATIC_PACKET_BY_CASTING_TYPE.put(castingType, new ActionFailed(castingType.getClientBarId()));
        }
    }

    private final int _castingType;

    private ActionFailed() {
        _castingType = 0;
    }

    private ActionFailed(int castingType) {
        _castingType = castingType;
    }

    public static ActionFailed get(SkillCastingType castingType) {
        return STATIC_PACKET_BY_CASTING_TYPE.getOrDefault(castingType, STATIC_PACKET);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.ACTION_FAIL);

        writeInt(_castingType); // MagicSkillUse castingType
    }

}
