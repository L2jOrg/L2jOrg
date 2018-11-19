package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.c2s.RequestRefine;
import l2s.gameserver.network.l2.c2s.RequestRefineCancel;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowVariationCancelWindow;
import l2s.gameserver.network.l2.s2c.ExShowVariationMakeWindow;
import l2s.gameserver.templates.npc.NpcTemplate;

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
					player.sendPacket(SystemMsg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowVariationMakeWindow.STATIC);
				else if(cmdChoice == 2)
					player.sendPacket(SystemMsg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
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