package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class ToresInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public ToresInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == 933)
		{
			if(reply == 1)
			{
				if(player.getLevel() < 40)
				{
					showChatWindow(player, "default/" + getNpcId() + "-no_level.htm", false);
					return;
				}

				/*if(player.getLevel() > 46)
				{
					// На оффе нету верхнего порога.
					return;
				}*/
				player.teleToLocation(-120313, -179623, -6752, ReflectionManager.MAIN);
			}
			else if(reply == 2)
			{
				if(player.getLevel() < 45)
				{
					showChatWindow(player, "default/" + getNpcId() + "-no_level.htm", false);
					return;
				}

				/*if(player.getLevel() > 51)
				{
					// На оффе нету верхнего порога.
					return;
				}*/
				player.teleToLocation(-109334, -180564, -6752, ReflectionManager.MAIN);
			}
		}
		else
			super.onMenuSelect(player, ask, reply);
	}
}