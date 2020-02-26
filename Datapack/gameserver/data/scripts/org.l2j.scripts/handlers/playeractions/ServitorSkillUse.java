package handlers.playeractions;

import org.l2j.gameserver.data.xml.impl.PetSkillData;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Summon skill use player action handler.
 * @author Nik
 */
public final class ServitorSkillUse implements IPlayerActionHandler
{
	@Override
	public void useAction(Player player, ActionData action, boolean ctrlPressed, boolean shiftPressed)
	{
		final Summon summon = player.getAnyServitor();
		if ((summon == null) || !summon.isServitor())
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
			return;
		}
		
		player.getServitors().values().forEach(servitor ->
		{
			if (summon.isBetrayed())
			{
				player.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return;
			}
			
			final int skillLevel = PetSkillData.getInstance().getAvailableLevel(servitor, action.getOptionId());
			if (skillLevel > 0)
			{
				servitor.setTarget(player.getTarget());
				servitor.useMagic(SkillEngine.getInstance().getSkill(action.getOptionId(), skillLevel), null, ctrlPressed, shiftPressed);
			}
		});
	}
}
