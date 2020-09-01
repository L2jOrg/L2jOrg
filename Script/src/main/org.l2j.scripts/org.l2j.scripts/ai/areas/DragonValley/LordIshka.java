package org.l2j.scripts.ai.areas.DragonValley;

import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.scripts.ai.AbstractNpcAI;

public class LordIshka extends AbstractNpcAI {
    private static final int LORDISHKA = 22100;
    private LordIshka()
    {
        addKillId(LORDISHKA);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon)
    {
        npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.GLORY_TO_THE_HEROES_WHO_HAVE_DEFEATED_LORD_ISHKA, 2, 5000, true));
        if (killer.isInParty()){
            killer.getParty().getMembers().forEach(member -> {
                SkillCaster.triggerCast(npc, member,  SkillEngine.getInstance().getSkill(50124, 1));
            });
        }
        else {
            SkillCaster.triggerCast(npc, killer,  SkillEngine.getInstance().getSkill(50124, 1));
        }
        return super.onKill(npc, killer, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new LordIshka();
    }
}
