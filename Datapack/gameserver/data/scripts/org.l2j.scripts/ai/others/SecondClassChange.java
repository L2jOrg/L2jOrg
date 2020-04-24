package ai.others;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;


import ai.AbstractNpcAI;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;

/**
 * Second class change has only level requirement.
 * @author Mobius
 */
public class SecondClassChange extends AbstractNpcAI
{
	private static final int LEVEL_REQUIREMENT = 40;
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		
		final Player player = event.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if ((player.getLevel() < LEVEL_REQUIREMENT) || !CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, player.getClassId().getId()))
		{
			return;
		}
		
		player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((player.getLevel() < LEVEL_REQUIREMENT) || !CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, player.getClassId().getId()))
		{
			return;
		}
		
		player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
	}
	
	public static void main(String[] args)
	{
		new SecondClassChange();
	}
}
