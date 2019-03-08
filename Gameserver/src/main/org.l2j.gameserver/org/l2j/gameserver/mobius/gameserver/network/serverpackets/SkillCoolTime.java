package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.model.TimeStamp;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Skill Cool Time server packet implementation.
 * @author KenM, Zoey76
 */
public class SkillCoolTime implements IClientOutgoingPacket
{
    private final List<TimeStamp> _skillReuseTimeStamps = new ArrayList<>();

    public SkillCoolTime(L2PcInstance player)
    {
        final Map<Long, TimeStamp> skillReuseTimeStamps = player.getSkillReuseTimeStamps();
        if (skillReuseTimeStamps != null)
        {
            for (TimeStamp ts : skillReuseTimeStamps.values())
            {
                final Skill skill = SkillData.getInstance().getSkill(ts.getSkillId(), ts.getSkillLvl(), ts.getSkillSubLvl());
                if (ts.hasNotPassed() && !skill.isNotBroadcastable())
                {
                    _skillReuseTimeStamps.add(ts);
                }
            }
        }
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.SKILL_COOL_TIME.writeId(packet);

        packet.writeD(_skillReuseTimeStamps.size());
        for (TimeStamp ts : _skillReuseTimeStamps)
        {
            packet.writeD(ts.getSkillId());
            packet.writeD(0x00); // ?
            packet.writeD((int) ts.getReuse() / 1000);
            packet.writeD((int) ts.getRemaining() / 1000);
        }
        return true;
    }
}
