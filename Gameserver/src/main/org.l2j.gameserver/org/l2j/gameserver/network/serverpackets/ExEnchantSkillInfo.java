package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collections;
import java.util.Set;

public final class ExEnchantSkillInfo extends ServerPacket {
    private final Set<Integer> _routes;

    private final int _skillId;
    private final int _skillLevel;
    private final int _skillSubLevel;
    private final int _currentSubLevel;

    public ExEnchantSkillInfo(int skillId, int skillLevel, int skillSubLevel, int currentSubLevel) {
        _skillId = skillId;
        _skillLevel = skillLevel;
        _skillSubLevel = skillSubLevel;
        _currentSubLevel = currentSubLevel;
        _routes = Collections.emptySet();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ENCHANT_SKILL_INFO);
        writeInt(_skillId);
        writeShort((short) _skillLevel);
        writeShort((short) _skillSubLevel);
        writeInt((_skillSubLevel % 1000) == 0);
        writeInt(_skillSubLevel > 1000 ? 1 : 0);
        writeInt(_routes.size());
        _routes.forEach(route ->
        {
            final int routeId = route / 1000;
            final int currentRouteId = _skillSubLevel / 1000;
            final int subLevel = _currentSubLevel > 0 ? (route + (_currentSubLevel % 1000)) - 1 : route;
            writeShort((short) _skillLevel);
            writeShort((short)( currentRouteId != routeId ? subLevel : Math.min(subLevel + 1, route)));
        });
    }

}
