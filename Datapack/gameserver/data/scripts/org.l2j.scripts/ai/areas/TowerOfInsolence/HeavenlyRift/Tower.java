package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

public class Tower extends AbstractNpcAI {
    public Tower() {}

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {

        HeavenlyRift.getZone().forEachCreature(riftNpc -> {
            npc.decayMe();
        }, riftNpc -> GameUtils.isNpc(riftNpc) &&  riftNpc.getId() == 20139 && !npc.isDead());


        GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
        GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
        GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
        return super.onKill(npc, killer, isSummon);
    }

}
