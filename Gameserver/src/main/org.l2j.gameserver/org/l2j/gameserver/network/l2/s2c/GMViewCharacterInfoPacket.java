package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.utils.Location;

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
	protected final void writeImpl()
	{
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);
		writeInt(_loc.h);
		writeInt(obj_id);
		writeString(_name);
		writeInt(_race);
		writeInt(_sex);
		writeInt(class_id);
		writeInt(level);
		writeLong(_exp);
		writeDouble(_expPercent);
		writeInt(_str);
		writeInt(_dex);
		writeInt(_con);
		writeInt(_int);
		writeInt(_wit);
		writeInt(_men);
		writeInt(0);
		writeInt(0);
		writeInt(maxHp);
		writeInt(curHp);
		writeInt(maxMp);
		writeInt(curMp);
		writeLong(_sp);
		writeInt(curLoad);
		writeInt(maxLoad);
		writeInt(pk_kills);

		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
			writeInt(_inv[PAPERDOLL_ID][0]);

		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
			writeInt(_inv[PAPERDOLL_ID][1]);

		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			writeInt(_inv[PAPERDOLL_ID][2]);
			writeInt(_inv[PAPERDOLL_ID][3]);
		}

		writeByte(talismans);
		writeByte(_jewelsLimit);
		writeInt(0x00);
		writeShort(0x00);
		writeInt(_patk);
		writeInt(_patkspd);
		writeInt(_pdef);
		writeInt(evasion);
		writeInt(accuracy);
		writeInt(crit);
		writeInt(_matk);

		writeInt(_matkspd);
		writeInt(_patkspd);

		writeInt(_mdef);
		writeInt(_mEvasion);
		writeInt(_mAccuracy);
		writeInt(_mCrit);

		writeInt(pvp_flag);
		writeInt(karma);

		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_swimRunSpd); // swimspeed
		writeInt(_swimWalkSpd); // swimspeed
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeDouble(move_speed);
		writeDouble(attack_speed);
		writeDouble(col_radius);
		writeDouble(col_height);
		writeInt(hair_style);
		writeInt(hair_color);
		writeInt(face);
		writeInt(gm_commands);

		writeString(title);
		writeInt(clan_id);
		writeInt(clan_crest_id);
		writeInt(ally_id);
		writeByte(mount_type);
		writeByte(private_store);
		writeByte(DwarvenCraftLevel); //_cha.getDwarvenCraftLevel() > 0 ? 1 : 0
		writeInt(pk_kills);
		writeInt(pvp_kills);

		writeShort(rec_left);
		writeShort(rec_have); //Blue value for name (0 = white, 255 = pure blue)
		writeInt(class_id);
		writeInt(0x00); // special effects? circles around player...
		writeInt(maxCp);
		writeInt(curCp);

		writeByte(running); //changes the Speed display on Status Window

		writeByte(321);

		writeInt(pledge_class); //changes the text above CP on Status Window

		writeByte(0);
		writeByte(hero);

		writeInt(name_color);
		writeInt(title_color);

		writeShort(attackElement.getId());
		writeShort(attackElementValue);
		writeShort(defenceFire);
		writeShort(defenceWater);
		writeShort(defenceWind);
		writeShort(defenceEarth);
		writeShort(defenceHoly);
		writeShort(defenceUnholy);

		writeInt(fame);
		writeInt(0);

		writeInt(0);
		writeInt(0);
	}
}