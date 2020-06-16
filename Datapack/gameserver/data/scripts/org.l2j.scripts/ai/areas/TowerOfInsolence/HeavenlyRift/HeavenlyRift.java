package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;

import java.util.concurrent.atomic.AtomicInteger;

public class HeavenlyRift extends AbstractNpcAI {
    public static class ClearZoneTask implements Runnable
    {
        private Npc _npc;

        public ClearZoneTask(Npc npc)
        {
            _npc = npc;
        }

        @Override
        public void run()
        {
            HeavenlyRift.getZone().forEachCreature(creature -> {
                if(GameUtils.isPlayer(creature))
                    ((Player) creature).teleToLocation(114298, 13343, -5104);
                else if(GameUtils.isNpc(creature) && creature.getId() != 30401)
                    creature.decayMe();
            });

            _npc.setBusy(false);
        }
    }

    private static Zone _zone = null;

    public static Zone getZone()
    {
        if(_zone == null)
            _zone = ZoneManager.getInstance().getZoneByName("[heavenly_rift]");
        return _zone;
    }

    public static int getAliveNpcCount(int npcId)
    {
        AtomicInteger res = new AtomicInteger();

        getZone().forEachCreature(creature -> {
            res.getAndIncrement();
        }, npc -> npc.getId() == npcId && !npc.isDead());

        return res.get();
    }

    public static void startEvent20Bomb(Player player)
    {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.SET_OFF_BOMBS_AND_GET_TREASURES, 10000, 5000));

        addSpawn(18003, 113352, 12936, 10976, 0, false, 1800000);
        addSpawn(18003, 113592, 13272, 10976, 0, false, 1800000);
        addSpawn(18003, 113816, 13592, 10976, 0, false, 1800000);
        addSpawn(18003, 113080, 13192, 10976, 0, false, 1800000);
        addSpawn(18003, 113336, 13528, 10976, 0, false, 1800000);
        addSpawn(18003, 113560, 13832, 10976, 0, false, 1800000);
        addSpawn(18003, 112776, 13512, 10976, 0, false, 1800000);
        addSpawn(18003, 113064, 13784, 10976, 0, false, 1800000);
        addSpawn(18003, 112440, 13848, 10976, 0, false, 1800000);
        addSpawn(18003, 112728, 14104, 10976, 0, false, 1800000);
        addSpawn(18003, 112760, 14600, 10976, 0, false, 1800000);
        addSpawn(18003, 112392, 14456, 10976, 0, false, 1800000);
        addSpawn(18003, 112104, 14184, 10976, 0, false, 1800000);
        addSpawn(18003, 111816, 14488, 10976, 0, false, 1800000);
        addSpawn(18003, 112104, 14760, 10976, 0, false, 1800000);
        addSpawn(18003, 112392, 15032, 10976, 0, false, 1800000);
        addSpawn(18003, 112120, 15288, 10976, 0, false, 1800000);
        addSpawn(18003, 111784, 15064, 10976, 0, false, 1800000);
        addSpawn(18003, 111480, 14824, 10976, 0, false, 1800000);
        addSpawn(18003, 113144, 14216, 10976, 0, false, 1800000);
    }

    public static void startEventTower(Player player)
    {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.PROTECT_THE_CENTRAL_TOWER_FROM_DIVINE_ANGELS, 10000, 5000));
        addSpawn(18004, 112648, 14072, 10976, 0, false, 1800000);
        ThreadPool.schedule(() ->
        {
            for(int i = 0 ; i < 40 ; i++)
            {
                addSpawn(20139, new Location(112696, 13960, 10958), true, 1800000);
            }
        }, 10000);
    }

    public static void startEvent40Angels(Player player)
    {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.DESTROY_WEAKENED_DIVINE_ANGELS, 10000, 5000));
        for(int i = 0 ; i < 40 ; i++)
            addSpawn(20139, new Location(112696, 13960, 10958), true, 1800000);
    }
}
