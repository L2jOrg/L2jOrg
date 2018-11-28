package manager;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.NpcUtils;
import org.l2j.gameserver.utils.ReflectionUtils;

/**
 * @reworked by Bonux
 */
public class HeavenlyRift
{
	public static class ClearZoneTask extends RunnableImpl
	{
		private NpcInstance _npc;

		public ClearZoneTask(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl() throws Exception
		{
			for(Creature cha : getZone().getObjects())
			{
				if(cha.isPlayer())
					cha.teleToLocation(114298, 13343, -5104);
				else if(cha.isNpc() && cha.getNpcId() != 30401)
					cha.decayMe();
			}
			_npc.setBusy(false);
		}
	}

	private static Zone _zone = null;

	public static Zone getZone()
	{
		if(_zone == null)
			_zone = ReflectionUtils.getZone("[heavenly_rift]");	
		return _zone;
	}

	public static int getAliveNpcCount(int npcId)
	{
		int res = 0;
		for(NpcInstance npc : getZone().getInsideNpcs())
		{
			if(npc.getNpcId() == npcId && !npc.isDead())
				res++;
		}
		return res;
	}	

	public static void startEvent20Bomb(Player player)
	{
		getZone().broadcastPacket(new ExShowScreenMessage(NpcString.SET_OFF_BOMBS_AND_GET_TREASURE, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER), false);

		NpcUtils.spawnSingle(18003, 113352, 12936, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 113592, 13272, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 113816, 13592, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 113080, 13192, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 113336, 13528, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 113560, 13832, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112776, 13512, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 113064, 13784, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112440, 13848, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112728, 14104, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112760, 14600, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112392, 14456, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112104, 14184, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 111816, 14488, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112104, 14760, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112392, 15032, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 112120, 15288, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 111784, 15064, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 111480, 14824, 10976, 1800000L); //despawn
		NpcUtils.spawnSingle(18003, 113144, 14216, 10976, 1800000L); //despawn
	}

	public static void startEventTower(Player player)
	{
		getZone().broadcastPacket(new ExShowScreenMessage(NpcString.PROTECT_THE_CENTRAL_TOWER_FROM_DIVINE_ANGELS, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER), false);

		NpcUtils.spawnSingle(18004, 112648, 14072, 10976, 1800000L); //despawn 30min

		ThreadPoolManager.getInstance().schedule(() ->
		{
			for(int i = 0 ; i < 40 ; i++)
			{
				NpcUtils.spawnSingle(20139, Location.findPointToStay(new Location(112696, 13960, 10958), 200, 500, ReflectionManager.MAIN.getGeoIndex()), 1800000L); //despawn 30min CORD RND GET CENTRAL POINT
			}
		}, 10000L);
	}

	public static void startEvent40Angels(Player player)
	{
		getZone().broadcastPacket(new ExShowScreenMessage(NpcString.DESTROY_WEAKED_DIVINE_ANGELS, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER), false);

		for(int i = 0 ; i < 40 ; i++)
		{
			Location loc = Location.findPointToStay(new Location(112696, 13960, 10958), 200, 500, player.getGeoIndex());
			NpcUtils.spawnSingle(20139, loc, 1800000L); //despawn 30min CORD RND GET CENTRAL POINT
		}		
	}
}
