package handlers.admincommandhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GameUtils;

import java.util.StringTokenizer;

/**
 * Admin Coins manage admin commands.
 */
public final class AdminCoins implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = { "admin_coins" };
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		if (actualCommand.equals("admin_coins"))
		{
			if (st.hasMoreTokens())
			{
				final String action = st.nextToken();
				
				final Player target = getTarget(activeChar);
				if ((target == null) || !st.hasMoreTokens()) {
					return false;
				}
				
				int value;
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

						updateCoin(target, value);
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

						final var coinCount = value;
						if (range <= 0) {
							World.getInstance().getPlayers().forEach(player -> updateCoin(player, coinCount));
							BuilderUtil.sendSysMessage(activeChar, "You increased Coins of all online players by " + value + ".");
						}
						else {
							World.getInstance().forEachPlayerInRange(activeChar, range, player -> updateCoin(player, coinCount), this::canReceiveCoin);
							BuilderUtil.sendSysMessage(activeChar, "You increased Coins of all players in range " + range + " by " + value + ".");
						}
						break;
					}
				}
			}
			showMenuHtml(activeChar);
		}
		return true;
	}

	private boolean canReceiveCoin(Player player) {
		return player.isOnlineInt() == 1 && player.getL2Coins() < Integer.MAX_VALUE;
	}

	private void updateCoin(Player player, int coinCount) {
		player.updateL2Coins(coinCount);
		player.sendMessage("Admin increase your Coins by " + coinCount + "!");
	}

	private Player getTarget(Player activeChar) {
		return ((activeChar.getTarget() != null) && (activeChar.getTarget().getActingPlayer() != null)) ? activeChar.getTarget().getActingPlayer() : activeChar;
	}
	
	private void showMenuHtml(Player activeChar) {
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		final Player target = getTarget(activeChar);
		html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/coins.htm"));
		html.replace("%coins%", GameUtils.formatAdena(target.getL2Coins()));
		html.replace("%targetName%", target.getName());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}