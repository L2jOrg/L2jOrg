package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.ShortCut;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.CharacterCreateSuccessPacket;
import l2s.gameserver.network.l2.s2c.CharacterSelectionInfoPacket;
import l2s.gameserver.templates.item.StartItem;
import l2s.gameserver.templates.player.PlayerTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Util;

public class CharacterCreate extends L2GameClientPacket
{
	// cSdddddddddddd
	private String _name;
	private int _sex;
	private int _classId;
	private int _hairStyle;
	private int _hairColor;
	private int _face;

	@Override
	protected void readImpl()
	{
		_name = readS();
		readD(); // race
		_sex = readD();
		_classId = readD();
		readD(); // int
		readD(); // str
		readD(); // con
		readD(); // men
		readD(); // dex
		readD(); // wit
		_hairStyle = readD();
		_hairColor = readD();
		_face = readD();
	}

	@Override
	protected void runImpl()
	{
		ClassId cid = ClassId.valueOf(_classId);
		if(cid == null || !cid.isOfLevel(ClassLevel.NONE))
			return;

		if(CharacterDAO.getInstance().accountCharNumber(getClient().getLogin()) >= 8)
			return;

		if(!Util.isMatchingRegexp(_name, Config.CNAME_TEMPLATE))
			return;
		else if(CharacterDAO.getInstance().getObjectIdByName(_name) > 0)
			return;

		Player newChar = Player.create(_classId, _sex, getClient().getLogin(), _name, _hairStyle, _hairColor, _face);
		if(newChar == null)
			return;

		sendPacket(CharacterCreateSuccessPacket.STATIC);

		initNewChar(newChar);
		getClient().setCharSelection(CharacterSelectionInfoPacket.loadCharacterSelectInfo(getClient().getLogin()));
	}

	public static void initNewChar(Player newChar)
	{
		PlayerTemplate template = newChar.getTemplate();

		newChar.getSubClassList().restore();
		newChar.setLoc(template.getStartLocation());

		if(Config.CHAR_TITLE)
			newChar.setTitle(Config.ADD_CHAR_TITLE);
		else
			newChar.setTitle("");

		newChar.setCurrentHpMp(newChar.getMaxHp(), newChar.getMaxMp());
		newChar.setCurrentCp(0); // retail

		for(StartItem i : template.getStartItems())
		{
			ItemInstance item = ItemFunctions.createItem(i.getId());
			if(i.getEnchantLevel() > 0)
				item.setEnchantLevel(i.getEnchantLevel());

			long count = i.getCount();
			if(item.isStackable())
			{
				item.setCount(count);
				newChar.getInventory().addItem(item);
			}
			else
			{
				for(long n = 0; n < count; n++)
				{
					item = ItemFunctions.createItem(i.getId());
					if(i.getEnchantLevel() > 0)
						item.setEnchantLevel(i.getEnchantLevel());
					newChar.getInventory().addItem(item);
				}
				if(item.isEquipable() && i.isEquiped())
					newChar.getInventory().equipItem(item);
			}
		}

		for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_CREATE))
			hook.onPlayerCreate(newChar);

		newChar.rewardSkills(false, false, false, true);

		if(newChar.getSkillLevel(1001) > 0) // Soul Cry
			newChar.registerShortCut(new ShortCut(1, 0, ShortCut.TYPE_SKILL, 1001, 1, 1));
		if(newChar.getSkillLevel(1177) > 0) // Wind Strike
			newChar.registerShortCut(new ShortCut(1, 0, ShortCut.TYPE_SKILL, 1177, 1, 1));
		if(newChar.getSkillLevel(1216) > 0) // Self Heal
			newChar.registerShortCut(new ShortCut(9, 0, ShortCut.TYPE_SKILL, 1216, 1, 1));

		// add attack, take, sit shortcut
		newChar.registerShortCut(new ShortCut(0, 0, ShortCut.TYPE_ACTION, 2, -1, 1));
		newChar.registerShortCut(new ShortCut(3, 0, ShortCut.TYPE_ACTION, 5, -1, 1));
		newChar.registerShortCut(new ShortCut(4, 0, ShortCut.TYPE_ACTION, 4, -1, 1));
		newChar.registerShortCut(new ShortCut(10, 0, ShortCut.TYPE_ACTION, 0, -1, 1));
		newChar.registerShortCut(new ShortCut(11, 0, ShortCut.TYPE_ACTION, 65, -1, 1));

		newChar.checkLevelUpReward(true);

		newChar.setOnlineStatus(false);

		newChar.store(false);
		newChar.getInventory().store();
		newChar.deleteMe();
	}
}