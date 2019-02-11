package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class GMViewCharacterInfoPacket extends L2GameServerPacket
{
	private Location _loc;
	private int[][] _inv;
	private int obj_id, _race, _sex, class_id, pvp_flag, karma, level, mount_type;
	private int _str, _con, _dex, _int, _wit, _men;
	private int curHp, maxHp, curMp, maxMp, curCp, maxCp, curLoad, maxLoad, rec_left, rec_have;
	private int _patk, _patkspd, _pdef, evasion, accuracy, crit, _matk, _matkspd;
	private int _mdef,_mEvasion, _mAccuracy, _mCrit, hair_style, hair_color, face, gm_commands;
	private int clan_id, clan_crest_id, ally_id, title_color;
	private int hero, private_store, name_color, pk_kills, pvp_kills;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, DwarvenCraftLevel, running, pledge_class;
	private String _name, title;
	private long _exp, _sp;
	private double move_speed, attack_speed, col_radius, col_height;
	private Element attackElement;
	private int attackElementValue;
	private int defenceFire, defenceWater, defenceWind, defenceEarth, defenceHoly, defenceUnholy;
	private int fame;
	private int talismans;
	private int _jewelsLimit;
	private double _expPercent;

	public GMViewCharacterInfoPacket(final Player cha)
	{
		_loc = cha.getLoc();
		obj_id = cha.getObjectId();
		_name = cha.getName();
		_race = cha.getRace().ordinal();
		_sex = cha.getSex().ordinal();
		class_id = cha.getClassId().getId();
		level = cha.getLevel();
		_exp = cha.getExp();
		_str = cha.getSTR();
		_dex = cha.getDEX();
		_con = cha.getCON();
		_int = cha.getINT();
		_wit = cha.getWIT();
		_men = cha.getMEN();

		curHp = (int) cha.getCurrentHp();
		maxHp = cha.getMaxHp();
		curMp = (int) cha.getCurrentMp();
		maxMp = cha.getMaxMp();
		_sp = cha.getSp();
		curLoad = cha.getCurrentLoad();
		maxLoad = cha.getMaxLoad();
		_patk = cha.getPAtk(null);
		_patkspd = cha.getPAtkSpd();
		_pdef = cha.getPDef(null);
		evasion = cha.getPEvasionRate(null);
		accuracy = cha.getPAccuracy();
		crit = cha.getPCriticalHit(null);
		_matk = cha.getMAtk(null, null);
		_matkspd = cha.getMAtkSpd();
		_mdef = cha.getMDef(null, null);
		_mEvasion = cha.getMEvasionRate(null);
		_mAccuracy = cha.getMAccuracy();
		_mCrit = cha.getMCriticalHit(null, null);
		pvp_flag = cha.getPvpFlag();
		karma = cha.getKarma();
		_runSpd = cha.getRunSpeed();
		_walkSpd = cha.getWalkSpeed();
		_swimRunSpd = cha.getSwimRunSpeed();
		_swimWalkSpd = cha.getSwimWalkSpeed();
		move_speed = cha.getMovementSpeedMultiplier();
		attack_speed = cha.getAttackSpeedMultiplier();
		mount_type = cha.getMountType().ordinal();
		col_radius = cha.getCollisionRadius();
		col_height = cha.getCollisionHeight();
		hair_style = cha.getBeautyHairStyle() > 0 ? cha.getBeautyHairStyle() : cha.getHairStyle();
		hair_color = cha.getBeautyHairColor() > 0 ? cha.getBeautyHairColor() : cha.getHairColor();
		face = cha.getBeautyFace() > 0 ? cha.getBeautyFace() : cha.getFace();
		gm_commands = cha.isGM() ? 1 : 0;
		title = cha.getTitle();
		_expPercent = Experience.getExpPercent(cha.getLevel(), cha.getExp());
		//
		Clan clan = cha.getClan();
		Alliance alliance = clan == null ? null : clan.getAlliance();
		//
		clan_id = clan == null ? 0 : clan.getClanId();
		clan_crest_id = clan == null ? 0 : clan.getCrestId();
		//
		ally_id = alliance == null ? 0 : alliance.getAllyId();
		//ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

		private_store = cha.isInObserverMode() ? Player.STORE_OBSERVING_GAMES : cha.getPrivateStoreType();
		DwarvenCraftLevel = Math.max(cha.getSkillLevel(1320), 0);
		pk_kills = cha.getPkKills();
		pvp_kills = cha.getPvpKills();
		rec_left = cha.getRecomLeft(); //c2 recommendations remaining
		rec_have = cha.getRecomHave(); //c2 recommendations received
		curCp = (int) cha.getCurrentCp();
		maxCp = cha.getMaxCp();
		running = cha.isRunning() ? 0x01 : 0x00;
		pledge_class = cha.getPledgeRank().ordinal();
		hero = cha.isHero() ? 1 : 0; //0x01: Hero Aura and symbol
		name_color = cha.getNameColor();
		title_color = cha.getTitleColor();
		attackElement = cha.getAttackElement();
		attackElementValue = cha.getAttack(attackElement);
		defenceFire = cha.getDefence(Element.FIRE);
		defenceWater = cha.getDefence(Element.WATER);
		defenceWind = cha.getDefence(Element.WIND);
		defenceEarth = cha.getDefence(Element.EARTH);
		defenceHoly = cha.getDefence(Element.HOLY);
		defenceUnholy = cha.getDefence(Element.UNHOLY);
		fame = cha.getFame();
		talismans = cha.getTalismanCount();
		_jewelsLimit = cha.getJewelsLimit();
		_inv = new int[Inventory.PAPERDOLL_MAX][4];
		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			_inv[PAPERDOLL_ID][0] = cha.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][1] = cha.getInventory().getPaperdollItemId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][1] = cha.getInventory().getPaperdollItemId(PAPERDOLL_ID); // TODO зачем ещё одно
			_inv[PAPERDOLL_ID][2] = cha.getInventory().getPaperdollVariation1Id(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][3] = cha.getInventory().getPaperdollVariation2Id(PAPERDOLL_ID);
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(_loc.h);
		buffer.putInt(obj_id);
		writeString(_name, buffer);
		buffer.putInt(_race);
		buffer.putInt(_sex);
		buffer.putInt(class_id);
		buffer.putInt(level);
		buffer.putLong(_exp);
		buffer.putDouble(_expPercent);
		buffer.putInt(_str);
		buffer.putInt(_dex);
		buffer.putInt(_con);
		buffer.putInt(_int);
		buffer.putInt(_wit);
		buffer.putInt(_men);
		buffer.putInt(0);
		buffer.putInt(0);
		buffer.putInt(maxHp);
		buffer.putInt(curHp);
		buffer.putInt(maxMp);
		buffer.putInt(curMp);
		buffer.putLong(_sp);
		buffer.putInt(curLoad);
		buffer.putInt(maxLoad);
		buffer.putInt(pk_kills);

		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
			buffer.putInt(_inv[PAPERDOLL_ID][0]);

		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
			buffer.putInt(_inv[PAPERDOLL_ID][1]);

		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			buffer.putInt(_inv[PAPERDOLL_ID][2]);
			buffer.putInt(_inv[PAPERDOLL_ID][3]);
		}

		buffer.put((byte)talismans);
		buffer.put((byte)_jewelsLimit);
		buffer.putInt(0x00);
		buffer.putShort((short) 0x00);
		buffer.putInt(_patk);
		buffer.putInt(_patkspd);
		buffer.putInt(_pdef);
		buffer.putInt(evasion);
		buffer.putInt(accuracy);
		buffer.putInt(crit);
		buffer.putInt(_matk);

		buffer.putInt(_matkspd);
		buffer.putInt(_patkspd);

		buffer.putInt(_mdef);
		buffer.putInt(_mEvasion);
		buffer.putInt(_mAccuracy);
		buffer.putInt(_mCrit);

		buffer.putInt(pvp_flag);
		buffer.putInt(karma);

		buffer.putInt(_runSpd);
		buffer.putInt(_walkSpd);
		buffer.putInt(_swimRunSpd); // swimspeed
		buffer.putInt(_swimWalkSpd); // swimspeed
		buffer.putInt(_runSpd);
		buffer.putInt(_walkSpd);
		buffer.putInt(_runSpd);
		buffer.putInt(_walkSpd);
		buffer.putDouble(move_speed);
		buffer.putDouble(attack_speed);
		buffer.putDouble(col_radius);
		buffer.putDouble(col_height);
		buffer.putInt(hair_style);
		buffer.putInt(hair_color);
		buffer.putInt(face);
		buffer.putInt(gm_commands);

		writeString(title, buffer);
		buffer.putInt(clan_id);
		buffer.putInt(clan_crest_id);
		buffer.putInt(ally_id);
		buffer.put((byte)mount_type);
		buffer.put((byte)private_store);
		buffer.put((byte)DwarvenCraftLevel); //_cha.getDwarvenCraftLevel() > 0 ? 1 : 0
		buffer.putInt(pk_kills);
		buffer.putInt(pvp_kills);

		buffer.putShort((short) rec_left);
		buffer.putShort((short) rec_have); //Blue value for name (0 = white, 255 = pure blue)
		buffer.putInt(class_id);
		buffer.putInt(0x00); // special effects? circles around player...
		buffer.putInt(maxCp);
		buffer.putInt(curCp);

		buffer.put((byte)running); //changes the Speed display on Status Window

		buffer.put((byte)321);

		buffer.putInt(pledge_class); //changes the text above CP on Status Window

		buffer.put((byte)0);
		buffer.put((byte)hero);

		buffer.putInt(name_color);
		buffer.putInt(title_color);

		buffer.putShort((short) attackElement.getId());
		buffer.putShort((short) attackElementValue);
		buffer.putShort((short) defenceFire);
		buffer.putShort((short) defenceWater);
		buffer.putShort((short) defenceWind);
		buffer.putShort((short) defenceEarth);
		buffer.putShort((short) defenceHoly);
		buffer.putShort((short) defenceUnholy);

		buffer.putInt(fame);
		buffer.putInt(0);

		buffer.putInt(0);
		buffer.putInt(0);
	}
}