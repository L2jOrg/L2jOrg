package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class StaticWorldObjectAction implements IActionHandler
{
	@Override
	public boolean action(Player activeChar, WorldObject target, boolean interact)
	{
		final StaticWorldObject staticObject = (StaticWorldObject) target;
		if (staticObject.getType() < 0)
		{
			LOGGER.info("StaticWorldObject: StaticObject with invalid type! StaticObjectId: " + staticObject.getId());
		}
		
		// Check if the Player already target the Folk
		if (activeChar.getTarget() != staticObject)
		{
			// Set the target of the Player activeChar
			activeChar.setTarget(staticObject);
		}
		else if (interact)
		{
			// Calculate the distance between the Player and the Folk
			if (!activeChar.isInsideRadius2D(staticObject, Npc.INTERACTION_DISTANCE))
			{
				// Notify the Player AI with AI_INTENTION_INTERACT
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, staticObject);
			}
			else if (staticObject.getType() == 2)
			{
				final String filename = (staticObject.getId() == 24230101) ? "data/html/signboards/tomb_of_crystalgolem.htm" : "data/html/signboards/pvp_signboard.htm";
				final String content = HtmCache.getInstance().getHtm(activeChar, filename);
				final NpcHtmlMessage html = new NpcHtmlMessage(staticObject.getObjectId());
				
				if (content == null)
				{
					html.setHtml("<html><body>Signboard is missing:<br>" + filename + "</body></html>");
				}
				else
				{
					html.setHtml(content);
				}
				
				activeChar.sendPacket(html);
			}
			else if (staticObject.getType() == 0)
			{
				activeChar.sendPacket(staticObject.getMap());
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2StaticObjectInstance;
	}
}
