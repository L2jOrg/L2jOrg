package org.l2j.scripts.handler.onshiftaction;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.instances.DoorInstance;
import org.l2j.gameserver.network.l2.components.HtmlMessage;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_DoorInstance extends ScriptOnShiftActionHandler<DoorInstance>
{
	@Override
	public Class<DoorInstance> getClazz()
	{
		return DoorInstance.class;
	}

	@Override
	public boolean call(DoorInstance door, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
			return false;

		HtmlMessage msg = new HtmlMessage(0);
		msg.setFile("scripts/actions/admin.L2DoorInstance.onActionShift.htm");
		msg.replace("%CurrentHp%", String.valueOf((int) door.getCurrentHp()));
		msg.replace("%MaxHp%", String.valueOf(door.getMaxHp()));
		msg.replace("%ObjectId%", String.valueOf(door.getObjectId()));
		msg.replace("%doorId%", String.valueOf(door.getDoorId()));
		msg.replace("%pdef%", String.valueOf(door.getPDef(null)));
		msg.replace("%mdef%", String.valueOf(door.getMDef(null, null)));
		msg.replace("%type%", door.getDoorType().name());
		msg.replace("%upgradeHP%", String.valueOf(door.getUpgradeHp()));
		StringBuilder b = new StringBuilder("");
		for(Event e : door.getEvents())
			b.append(e.toString()).append(";");
		msg.replace("%event%", b.toString());

		player.sendPacket(msg);
		return true;
	}
}
