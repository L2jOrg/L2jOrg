package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.model.TimeStamp;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Skill Cool Time server packet implementation.
 *
 * @author KenM, Zoey76
 */
public class SkillCoolTime extends IClientOutgoingPacket {
    private final List<TimeStamp> _skillReuseTimeStamps = new ArrayList<>();

    public SkillCoolTime(L2PcInstance player) {
        final Map<Long, TimeStamp> skillReuseTimeStamps = player.getSkillReuseTimeStamps();
        if (skillReuseTimeStamps != null) {
            for (TimeStamp ts : skillReuseTimeStamps.values()) {
                final Skill skill = SkillData.getInstance().getSkill(ts.getSkillId(), ts.getSkillLvl(), ts.getSkillSubLvl());
                if (ts.hasNotPassed() && !skill.isNotBroadcastable()) {
                    _skillReuseTimeStamps.add(ts);
                }
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SKILL_COOL_TIME.writeId(packet);

        packet.putInt(_skillReuseTimeStamps.size());
        for (TimeStamp ts : _skillReuseTimeStamps) {
            packet.putInt(ts.getSkillId());
            packet.putInt(0x00); // ?
            packet.putInt((int) ts.getReuse() / 1000);
            packet.putInt((int) ts.getRemaining() / 1000);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + _skillReuseTimeStamps.size() * 12;
    }
}
