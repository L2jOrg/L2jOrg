package npc.model;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.c2s.L2GameClientPacket;
import org.l2j.gameserver.network.l2.c2s.RequestRefine;
import org.l2j.gameserver.network.l2.c2s.RequestRefineCancel;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExShowVariationCancelWindow;
import org.l2j.gameserver.network.l2.s2c.ExShowVariationMakeWindow;
import org.l2j.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class AnvilInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public AnvilInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("Augment"))
		{
			if(Config.ALLOW_AUGMENTATION)
			{
				int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
				if(cmdChoice == 1)
					player.sendPacket(SystemMsg.SELECT_THE_ITEM_TO_BE_AUGMENTED, new ExShowVariationMakeWindow());
				else if(cmdChoice == 2)
					player.sendPacket(SystemMsg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, new ExShowVariationCancelWindow());
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		if(val == 0)
		{
			if(!Config.ALLOW_AUGMENTATION)
				showChatWindow(player, "default/" + getNpcId() + "-not_allowed.htm", firstTalk);
			else
				super.showChatWindow(player, val, firstTalk, replace);
		}
		else
			super.showChatWindow(player, val, firstTalk, replace);
	}

	@Override
	public boolean canPassPacket(Player player, Class<? extends L2GameClientPacket> packet, Object... arg)
	{
		return packet == RequestRefine.class || packet == RequestRefineCancel.class;
	}
}