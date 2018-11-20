package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.ShortCut;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.skills.TimeStamp;

/**
 * @author VISTALL
 * @date 7:48/29.03.2011
 */
public abstract class ShortCutPacket extends L2GameServerPacket
{
	public static ShortcutInfo convert(Player player, ShortCut shortCut)
	{
		ShortcutInfo shortcutInfo = null;
		int page = shortCut.getSlot() + shortCut.getPage() * 12;
		switch(shortCut.getType())
		{
			case ShortCut.TYPE_ITEM:
				int reuseGroup = -1,
				currentReuse = 0,
				reuse = 0,
				variation1Id = 0,
				variation2Id = 0;
				ItemInstance item = player.getInventory().getItemByObjectId(shortCut.getId());
				if(item != null)
				{
					variation1Id = item.getVariation1Id();
					variation2Id = item.getVariation2Id();
					reuseGroup = item.getTemplate().getDisplayReuseGroup();
					if(item.getTemplate().getReuseDelay() > 0)
					{
						TimeStamp timeStamp = player.getSharedGroupReuse(item.getTemplate().getReuseGroup());
						if(timeStamp != null)
						{
							currentReuse = (int) (timeStamp.getReuseCurrent() / 1000L);
							reuse = (int) (timeStamp.getReuseBasic() / 1000L);
						}
					}
				}
				shortcutInfo = new ItemShortcutInfo(shortCut.getType(), page, shortCut.getId(), reuseGroup, currentReuse, reuse, variation1Id, variation2Id, shortCut.getCharacterType());
				break;
			case ShortCut.TYPE_SKILL:
				shortcutInfo = new SkillShortcutInfo(shortCut.getType(), page, shortCut.getId(), shortCut.getLevel(), shortCut.getCharacterType());
				break;
			default:
				shortcutInfo = new ShortcutInfo(shortCut.getType(), page, shortCut.getId(), shortCut.getCharacterType());
				break;
		}
		return shortcutInfo;
	}

	protected static class ItemShortcutInfo extends ShortcutInfo
	{
		private int _reuseGroup;
		private int _currentReuse;
		private int _basicReuse;
		private int _variation1Id;
		private int _variation2Id;

		public ItemShortcutInfo(int type, int page, int id, int reuseGroup, int currentReuse, int basicReuse, int variation1Id, int variation2Id, int characterType)
		{
			super(type, page, id, characterType);
			_reuseGroup = reuseGroup;
			_currentReuse = currentReuse;
			_basicReuse = basicReuse;
			_variation1Id = variation1Id;
			_variation2Id = variation2Id;
		}

		@Override
		protected void write0(ShortCutPacket p)
		{
			p.writeInt(_id);
			p.writeInt(_characterType);
			p.writeInt(_reuseGroup);
			p.writeInt(_currentReuse);
			p.writeInt(_basicReuse);
			p.writeInt(_variation1Id);
			p.writeInt(_variation2Id);
			p.writeInt(0x00); //TODO: [Bonux] ??HARMONY??
		}
	}

	protected static class SkillShortcutInfo extends ShortcutInfo
	{
		private final int _level;

		public SkillShortcutInfo(int type, int page, int id, int level, int characterType)
		{
			super(type, page, id, characterType);
			_level = level;
		}

		public int getLevel()
		{
			return _level;
		}

		@Override
		protected void write0(ShortCutPacket p)
		{
			p.writeInt(_id);
			p.writeInt(_level);
			p.writeInt(_id); //TODO [VISTALL] skill reuse group
			p.writeByte(0x00);
			p.writeInt(_characterType);
		}
	}

	protected static class ShortcutInfo
	{
		protected final int _type;
		protected final int _page;
		protected final int _id;
		protected final int _characterType;

		public ShortcutInfo(int type, int page, int id, int characterType)
		{
			_type = type;
			_page = page;
			_id = id;
			_characterType = characterType;
		}

		protected void write(ShortCutPacket p)
		{
			p.writeInt(_type);
			p.writeInt(_page);
			write0(p);
		}

		protected void write0(ShortCutPacket p)
		{
			p.writeInt(_id);
			p.writeInt(_characterType);
		}
	}
}
