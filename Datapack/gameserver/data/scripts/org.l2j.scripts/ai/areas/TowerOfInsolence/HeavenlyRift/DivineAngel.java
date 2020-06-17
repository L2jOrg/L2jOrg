package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.gameserver.ai.AttackableAI;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.util.GameUtils;

public class DivineAngel extends AbstractNpcAI {
    public DivineAngel() {
        addSpawnId(20139);
        addKillId(20139);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0) > 1) {
            if (HeavenlyRift.getAliveNpcCount(npc.getId()) == 0)//Last
            {
                GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
                GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
                GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 1);
                HeavenlyRift.getZone().forEachCreature(riftNpc -> {
                   ((Npc) riftNpc).broadcastSay(ChatType.NPC_SHOUT, NpcStringId.DIVINE_ANGELS_ARE_NOWHERE_TO_BE_SEEN_I_WANT_TO_TALK_TO_THE_PARTY_LEADER);
                }, riftNpc -> GameUtils.isNpc(riftNpc) && riftNpc.getId() == 18004);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }

    @Override
    public String onSpawn(Npc npc) {
        if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0) == 2) // For Tower Rift Instance
            ((AttackableAI) npc.getAI()).setGlobalAggro(0); // We want angel to be aggressive as fast as possible

        return super.onSpawn(npc);
    }

    public static AbstractNpcAI provider() {
        return new DivineAngel();
    }
}
