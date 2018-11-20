package org.l2j.gameserver.model.instances;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.HennaEquipListPacket;
import org.l2j.gameserver.network.l2.s2c.HennaUnequipListPacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;

/**
 * This class ...
 *
 * @version $Revision$ $Date$
 */
public class SymbolMakerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public SymbolMakerInstance(int objectID, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectID, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equals("Draw"))
			player.sendPacket(new HennaEquipListPacket(player));
		else if(command.equals("RemoveList"))
			player.sendPacket(new HennaUnequipListPacket(player));
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "symbolmaker/";
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		if (val == 0)
			return "SymbolMaker.htm";
		else
			return "SymbolMaker-" + val + ".htm";
	}
}