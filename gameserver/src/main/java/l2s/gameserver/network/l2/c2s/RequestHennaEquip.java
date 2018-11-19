package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.HennaHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Henna;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.HennaTemplate;

public class RequestHennaEquip extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * packet type id 0x6F
	 * format:		cd
	 */
	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		HennaTemplate template = HennaHolder.getInstance().getHenna(_symbolId);
		if(template == null)
		{
			player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
			return;
		}

		Henna henna = new Henna(template, (int) (System.currentTimeMillis() / 1000), template.getPeriod() > 0);
		if(!player.getHennaList().canAdd(henna))
		{
			player.sendPacket(SystemMsg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
			return;
		}

		if(!template.isForThisClass(player))
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_SYMBOL_BECAUSE_YOU_DONT_MEET_THE_CLASS_REQUIREMENTS);
			return;
		}

		if(template.getDyeLvl() > player.getLevel())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_SYMBOL_BECAUSE_YOU_ARE_BELOW_THE_REQUIRED_LEVEL);
			return;
		}

		long countDye = player.getInventory().getCountOf(template.getDyeId());
		if(countDye < template.getDrawCount())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_SYMBOL_BECAUSE_YOU_DONT_HAVE_ENOUGH_DYE);
			return;
		}

		long drawPrice = template.getDrawPrice();
		if(drawPrice > 0 && drawPrice > player.getAdena())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_SYMBOL_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
			return;
		}

		if(player.consumeItem(template.getDyeId(), template.getDrawCount(), true) && (drawPrice == 0 || player.reduceAdena(template.getDrawPrice())))
		{
			if(player.getHennaList().add(henna))
				player.sendPacket(SystemMsg.THE_SYMBOL_HAS_BEEN_ADDED);
			else
				player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
		}
	}
}