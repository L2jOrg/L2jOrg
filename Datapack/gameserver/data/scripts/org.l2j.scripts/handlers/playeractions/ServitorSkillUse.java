package handlers.playeractions;

import org.l2j.gameserver.data.xml.impl.PetSkillData;
import org.l2j.gameserver.data.xml.impl.SkillData;
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
	public void useAction(Player activeChar, ActionData data, boolean ctrlPressed, boolean shiftPressed)
	{
		final Summon summon = activeChar.getAnyServitor();
		if ((summon == null) || !summon.isServitor())
		{
			activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
			return;
		}
		
		activeChar.getServitors().values().forEach(servitor ->
		{
			if (summon.isBetrayed())
			{
				activeChar.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return;
			}
			
			final int skillLevel = PetSkillData.getInstance().getAvailableLevel(servitor, data.getOptionId());
			if (skillLevel > 0)
			{
				servitor.setTarget(activeChar.getTarget());
				servitor.useMagic(SkillData.getInstance().getSkill(data.getOptionId(), skillLevel), null, ctrlPressed, shiftPressed);
			}
		});
	}
}
