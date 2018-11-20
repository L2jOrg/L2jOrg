package npc.model;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.templates.npc.NpcTemplate;

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