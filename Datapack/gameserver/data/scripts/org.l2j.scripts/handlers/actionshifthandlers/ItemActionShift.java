package handlers.actionshifthandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionShiftHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

public class ItemActionShift implements IActionShiftHandler
{
	@Override
	public boolean action(Player activeChar, WorldObject target, boolean interact)
	{
		if (activeChar.isGM())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0, 1, "<html><body><center><font color=\"LEVEL\">Item Info</font></center><br><table border=0><tr><td>Object ID: </td><td>" + target.getObjectId() + "</td></tr><tr><td>Item ID: </td><td>" + target.getId() + "</td></tr><tr><td>Owner ID: </td><td>" + ((Item) target).getOwnerId() + "</td></tr><tr><td>Location: </td><td>" + target.getLocation() + "</td></tr><tr><td><br></td></tr><tr><td>Class: </td><td>" + target.getClass().getSimpleName() + "</td></tr></table></body></html>");
			activeChar.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2ItemInstance;
	}
}
