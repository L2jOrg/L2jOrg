package handler.items;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;

//TODO: [Bonux] Переделать через скиллы.
public class CharChangePotions extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		int itemId = item.getItemId();

		if(!player.getInventory().destroyItem(item, 1))
		{
			player.sendActionFailed();
			return false;
		}

		int face = player.getFace();
		int hairColor = player.getHairColor();
		int hairStyle = player.getHairStyle();
		switch(itemId)
		{
			case 5235:
				player.setFace(0);
				break;
			case 5236:
				player.setFace(1);
				break;
			case 5237:
				player.setFace(2);
				break;
			case 5238:
				player.setHairColor(0);
				break;
			case 5239:
				player.setHairColor(1);
				break;
			case 5240:
				player.setHairColor(2);
				break;
			case 5241:
				player.setHairColor(3);
				break;
			case 5242:
				player.setHairStyle(0);
				break;
			case 5243:
				player.setHairStyle(1);
				break;
			case 5244:
				player.setHairStyle(2);
				break;
			case 5245:
				player.setHairStyle(3);
				break;
			case 5246:
				player.setHairStyle(4);
				break;
			case 5247:
				player.setHairStyle(5);
				break;
			case 5248:
				player.setHairStyle(6);
				break;
		}

		player.broadcastPacket(new MagicSkillUse(player, player, 2003, 1, 1, 0));
		if(face != player.getFace() || hairColor != player.getHairColor() || hairStyle != player.getHairStyle())
			player.broadcastUserInfo(true);
		return true;
	}
}