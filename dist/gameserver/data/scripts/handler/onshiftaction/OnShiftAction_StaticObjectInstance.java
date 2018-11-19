package handler.onshiftaction;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.instances.StaticObjectInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;

/**
 * @author Bonux
**/
public class OnShiftAction_StaticObjectInstance extends ScriptOnShiftActionHandler<StaticObjectInstance>
{
	@Override
	public Class<StaticObjectInstance> getClazz()
	{
		return StaticObjectInstance.class;
	}

	@Override
	public boolean call(StaticObjectInstance object, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
			return false;

		HtmlMessage msg = new HtmlMessage(0);
		msg.setFile("scripts/actions/admin.L2StaticObjectInstance.onActionShift.htm");
		msg.replace("%ObjectId%", String.valueOf(object.getObjectId()));
		msg.replace("%uid%", String.valueOf(object.getUId()));
		msg.replace("%type%", String.valueOf(object.getType()));
		StringBuilder b = new StringBuilder("");
		for(Event e : object.getEvents())
			b.append(e.toString()).append(";");
		msg.replace("%event%", b.toString());

		player.sendPacket(msg);
		return true;
	}
}
