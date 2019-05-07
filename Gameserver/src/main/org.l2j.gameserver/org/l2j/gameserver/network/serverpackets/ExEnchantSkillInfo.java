package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.EnchantSkillGroupsData;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

public final class ExEnchantSkillInfo extends IClientOutgoingPacket {
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
        _routes = EnchantSkillGroupsData.getInstance().getRouteForSkill(_skillId, _skillLevel);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENCHANT_SKILL_INFO.writeId(packet);
        packet.putInt(_skillId);
        packet.putShort((short) _skillLevel);
        packet.putShort((short) _skillSubLevel);
        packet.putInt((_skillSubLevel % 1000) == EnchantSkillGroupsData.MAX_ENCHANT_LEVEL ? 0 : 1);
        packet.putInt(_skillSubLevel > 1000 ? 1 : 0);
        packet.putInt(_routes.size());
        _routes.forEach(route ->
        {
            final int routeId = route / 1000;
            final int currentRouteId = _skillSubLevel / 1000;
            final int subLevel = _currentSubLevel > 0 ? (route + (_currentSubLevel % 1000)) - 1 : route;
            packet.putShort((short) _skillLevel);
            packet.putShort((short)( currentRouteId != routeId ? subLevel : Math.min(subLevel + 1, route + (EnchantSkillGroupsData.MAX_ENCHANT_LEVEL - 1))));
        });
    }

    @Override
    protected int size(L2GameClient client) {
        return 25 + _routes.size() * 4;
    }
}
