package handlers.admincommandhandlers;

import java.util.Collection;
import java.util.StringTokenizer;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.Util;

/**
 * Admin Coins manage admin commands.
 */
public final class AdminCoins implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = { "admin_coins" };
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		if (actualCommand.equals("admin_coins"))
		{
			if (st.hasMoreTokens())
			{
				final String action = st.nextToken();
				
				final L2PcInstance target = getTarget(activeChar);
				if ((target == null) || !st.hasMoreTokens()) {
					return false;
				}
				
				int value = 0;
				try {
					value = Integer.parseInt(st.nextToken());
				}
				catch (Exception e) {
					showMenuHtml(activeChar);
					BuilderUtil.sendSysMessage(activeChar, "Invalid Value!");
					return false;
				}
				
				switch (action) {
					case "set": {
						target.setL2Coins(value);
						target.sendMessage("Admin set your Coins to " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You set " + value + " Coin to player " + target.getName());
						break;
					}
					case "increase": {
						if (target.getL2Coins() == Integer.MAX_VALUE) {
							showMenuHtml(activeChar);
							activeChar.sendMessage(target.getName() + " already have max count of Coins!");
							return false;
						}

						target.updateL2Coins(value);
						target.sendMessage("Admin increase your Coins by " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You increased Coins of " + target.getName() + " by " + value);
						break;
					}
					case "decrease": {
						if (target.getL2Coins() == 0) {
							showMenuHtml(activeChar);
							activeChar.sendMessage(target.getName() + " already have min count of Coins!");
							return false;
						}

						target.updateL2Coins(-value);
						target.sendMessage("Admin decreased your Coins by " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You decreased Coins of " + target.getName() + " by " + value);
						break;
					}
					case "rewardOnline": {
						int range = 0;
						try {
							range = Integer.parseInt(st.nextToken());
						}
						catch (Exception ignored) {
						}
						
						if (range <= 0) {
							final int count = increaseForAll(L2World.getInstance().getPlayers(), value);
							BuilderUtil.sendSysMessage(activeChar, "You increased Coins of all online players (" + count + ") by " + value + ".");
						}
						else {
							final int count = increaseForAll(L2World.getInstance().getVisibleObjectsInRange(activeChar, L2PcInstance.class, range), value);
							BuilderUtil.sendSysMessage(activeChar, "You increased Coins of all players (" + count + ") in range " + range + " by " + value + ".");
						}
						break;
					}
				}
			}
			showMenuHtml(activeChar);
		}
		return true;
	}
	
	private int increaseForAll(Collection<L2PcInstance> playerList, int value) {
		int counter = 0;
		for (L2PcInstance temp : playerList) {
			if ((temp != null) && (temp.isOnlineInt() == 1)) {
				if (temp.getL2Coins() == Integer.MAX_VALUE) {
					continue;
				}

				temp.updateL2Coins(value);
				temp.sendMessage("Admin increase your Coins by " + value + "!");
				counter++;
			}
		}
		return counter;
	}
	
	private L2PcInstance getTarget(L2PcInstance activeChar) {
		return ((activeChar.getTarget() != null) && (activeChar.getTarget().getActingPlayer() != null)) ? activeChar.getTarget().getActingPlayer() : activeChar;
	}
	
	private void showMenuHtml(L2PcInstance activeChar) {
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		final L2PcInstance target = getTarget(activeChar);
		html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/coins.htm"));
		html.replace("%coins%", Util.formatAdena(target.getL2Coins()));
		html.replace("%targetName%", target.getName());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}