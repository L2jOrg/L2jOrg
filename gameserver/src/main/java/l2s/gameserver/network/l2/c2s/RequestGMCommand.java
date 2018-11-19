package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExGMViewQuestItemListPacket;
import l2s.gameserver.network.l2.s2c.GMHennaInfoPacket;
import l2s.gameserver.network.l2.s2c.GMViewCharacterInfoPacket;
import l2s.gameserver.network.l2.s2c.GMViewItemListPacket;
import l2s.gameserver.network.l2.s2c.GMViewPledgeInfoPacket;
import l2s.gameserver.network.l2.s2c.GMViewQuestInfoPacket;
import l2s.gameserver.network.l2.s2c.GMViewSkillInfoPacket;
import l2s.gameserver.network.l2.s2c.GMViewWarehouseWithdrawListPacket;

public class RequestGMCommand extends L2GameClientPacket
{
	private String _targetName;
	private int _command;

	@Override
	protected void readImpl()
	{
		_targetName = readS();
		_command = readD();
		// readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		Player target = World.getPlayer(_targetName);
		if(player == null || target == null)
			return;
		if(!player.getPlayerAccess().CanViewChar)
			return;

		switch(_command)
		{
			case 1:
				player.sendPacket(new GMViewCharacterInfoPacket(target));
				player.sendPacket(new GMHennaInfoPacket(target));
				break;
			case 2:
				if(target.getClan() != null)
					player.sendPacket(new GMViewPledgeInfoPacket(target));
				break;
			case 3:
				player.sendPacket(new GMViewSkillInfoPacket(target));
				break;
			case 4:
				player.sendPacket(new GMViewQuestInfoPacket(target));
				break;
			case 5:
				ItemInstance[] items = target.getInventory().getItems();
				int questSize = 0;
				for(ItemInstance item : items)
					if(item.getTemplate().isQuest())
						questSize++;
				player.sendPacket(new GMViewItemListPacket(target, items, items.length - questSize));
				player.sendPacket(new ExGMViewQuestItemListPacket(target, items, questSize));

				player.sendPacket(new GMHennaInfoPacket(target));
				break;
			case 6:
				player.sendPacket(new GMViewWarehouseWithdrawListPacket(target));
				break;
		}
	}
}