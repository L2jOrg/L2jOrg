package services;

import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SetupGaugePacket;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.SiegeUtils;

public class RideHire
{
	@Bypass("services.RideHire:ride")
	public void ride(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.SERVICES_RIDE_HIRE_ENABLED)
			return;

		if(player == null || npc == null)
			return;

		boolean ru = player.isLangRus();
		if(param.length != 3)
		{
			Functions.show(ru ? "Некорректные данные" : "Incorrect input", player, npc);
			return;
		}

		if(!npc.canBypassCheck(player))
			return;

		if(player.getActiveWeaponFlagAttachment() != null)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
			return;
		}

		if(player.isTransformed())
		{
			Functions.show(ru ? "Вы не можете взять пета в прокат, пока находитесь в режиме трансформации." : "Can't ride while in transformation mode.", player, npc);
			return;
		}

		if(player.hasServitor()|| player.isMounted())
		{
			player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
			return;
		}

		int npc_id;

		switch(Integer.parseInt(param[0]))
		{
			case 1:
				npc_id = PetDataHolder.WYVERN_ID;
				break;
			case 2:
				npc_id = PetDataHolder.STRIDER_WIND_ID;
				break;
			case 3:
				npc_id = PetDataHolder.WGREAT_WOLF_ID;
				break;
			case 4:
				npc_id = PetDataHolder.WFENRIR_WOLF_ID;
				break;
			default:
				Functions.show(ru ? "У меня нет таких питомцев!" : "Unknown pet.", player, npc);
				return;
		}

		if((npc_id == PetDataHolder.WYVERN_ID || npc_id == PetDataHolder.STRIDER_WIND_ID) && !SiegeUtils.getCanRide())
		{
			Functions.show(ru ? "Прокат виверн/страйдеров не работает во время осады." : "Can't ride wyvern/strider while Siege in progress.", player, npc);
			return;
		}

		Integer time = Integer.parseInt(param[1]);
		Long price = Long.parseLong(param[2]);

		if(time > 1800)
		{
			Functions.show(ru ? "Слишком большое время." : "Too long time to ride.", player, npc);
			return;
		}

		if(player.getAdena() < price)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(price, true);

		doLimitedRide(player, npc_id, time);
	}

	private static void doLimitedRide(Player player, Integer npc_id, Integer time)
	{
		if(!Functions.ride(player, npc_id))
			return;

		player.sendPacket(new SetupGaugePacket(player, SetupGaugePacket.Colors.GREEN, time * 1000));
		ThreadPoolManager.getInstance().schedule(() -> rideOver(player), time * 1000);
	}

	public static void rideOver(Player player)
	{
		Functions.unRide(player);
		Functions.show(player.isLangRus() ? "Время проката закончилось. Приходите еще!" : "Ride time is over. Welcome back again!", player);
	}
}