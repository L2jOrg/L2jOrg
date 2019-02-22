package org.l2j.scripts.services;

import org.l2j.gameserver.handler.bypass.Bypass;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.PlaySoundPacket;
import org.l2j.gameserver.utils.Functions;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.NpcUtils;
import org.l2j.gameserver.utils.PositionUtils;

import java.util.Calendar;

/**
 * @author
 *
 * High Five: Exchanges Explorer Hat for Birthday Hat
 */
public class Birthday
{
	private static final int EXPLORERHAT = 10250;
	private static final int HAT = 13488; // Birthday Hat
	private static final int NPC_ALEGRIA = 32600; // Alegria

	private static final String msgNoBirthday = "scripts/org.l2j.scripts.services/Birthday-no.htm";
	private static final String msgSpawned = "scripts/org.l2j.scripts.services/Birthday-spawned.htm";

	/**
	 * Вызывается у гейткиперов
	 */
	@Bypass("org.l2j.scripts.services.Birthday:summonAlegria")
	public void summonAlegria(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null || !npc.canBypassCheck(player))
			return;

		if(!isBirthdayToday(player))
		{
			Functions.show(msgNoBirthday, player, npc);
			return;
		}

		//TODO: На оффе можно вызвать до 3х нпсов. Но зачем? о.0
		for(NpcInstance n : World.getAroundNpc(npc))
			if(n.getNpcId() == NPC_ALEGRIA)
			{
				Functions.show(msgSpawned, player, npc);
				return;
			}

		player.sendPacket(PlaySoundPacket.HB01);

		try
		{
			//Спаним Аллегрию где-то спереди от ГК
			int x = (int) (npc.getX() + 40 * Math.cos(npc.headingToRadians(npc.getHeading() - 32768 + 8000)));
			int y = (int) (npc.getY() + 40 * Math.sin(npc.headingToRadians(npc.getHeading() - 32768 + 8000)));

			NpcInstance alegria = NpcUtils.spawnSingle(NPC_ALEGRIA, x, y, npc.getZ(), 180000);
			alegria.setHeading(PositionUtils.calculateHeadingFrom(alegria, player));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Вызывается у NPC Alegria
	 */
	@Bypass("org.l2j.scripts.services.Birthday:exchangeHat")
	public void exchangeHat(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null || !npc.canBypassCheck(player) || npc.isBusy())
			return;

		if(ItemFunctions.getItemCount(player, EXPLORERHAT) < 1)
		{
			Functions.show("default/32600-nohat.htm", player, npc);
			return;
		}
		ItemFunctions.deleteItem(player, EXPLORERHAT, 1,true);
		ItemFunctions.addItem(player, HAT, 1, true);
		Functions.show("default/32600-successful.htm", player, npc);

		long now = System.currentTimeMillis() / 1000;
		player.setVar("Birthday", String.valueOf(now), -1);

		npc.setBusy(true);
		npc.deleteMe();
	}

	/**
	 * Вернет true если у чара сегодня день рождения
	 */
	private boolean isBirthdayToday(Player player)
	{
		if(player.getCreateTime() == 0)
			return false;

		Calendar create = Calendar.getInstance();
		create.setTimeInMillis(player.getCreateTime());
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());

		return create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) && create.get(Calendar.YEAR) != now.get(Calendar.YEAR);
	}
}