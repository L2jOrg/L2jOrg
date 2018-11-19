package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.player.Cubic;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.matching.MatchingRoom;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.s2c.updatetype.UserInfoType;
import l2s.gameserver.utils.Location;

/**
 * @reworked by Bonux
 */
public class UIPacket extends AbstractMaskPacket<UserInfoType>
{
	// Params
	private boolean can_writeImpl = false, partyRoom;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd, _relation;
	private double move_speed, attack_speed, col_radius, col_height;
	private Location _loc;
	private int obj_id, vehicle_obj_id, _race, sex, base_class, level, curCp, maxCp, _weaponEnchant, _armorSetEnchant, _weaponFlag;
	private long _exp, _sp;
	private int curHp, maxHp, curMp, maxMp, curLoad, maxLoad, rec_left, rec_have;
	private int _str, _con, _dex, _int, _wit, _men, ClanPrivs, InventoryLimit;
	private int _patk, _patkspd, _pdef, _matk, _matkspd;
	private int _pEvasion, _pAccuracy, _pCrit, _mEvasion, _mAccuracy, _mCrit;
	private int _mdef, pvp_flag, karma, hair_style, hair_color, face, gm_commands, fame;
	private int clan_id, _isClanLeader, clan_crest_id, ally_id, ally_crest_id, large_clan_crest_id;
	private int private_store, can_crystalize, pk_kills, pvp_kills, class_id, agathion, _partySubstitute;
	private int hero, mount_id;
	private int name_color, running, pledge_class, pledge_type, title_color, transformation;
	private int defenceFire, defenceWater, defenceWind, defenceEarth, defenceHoly, defenceUnholy;
	private int mount_type;
	private String _name, _title;
	private Cubic[] cubics;
	private Element attackElement;
	private int attackElementValue;
	private int _moveType;
	private int talismans;
	private int _jewelsLimit;
	private double _expPercent;
	private TeamType _team;
	private final boolean _hideHeadAccessories;

	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00
	};

	private int _initSize = 5;

	public UIPacket(Player player)
	{
		this(player, true);
	}

	public UIPacket(Player player, boolean addAll)
	{
		_name = player.getVisibleName(player);
		name_color = player.getVisibleNameColor(player);
		_title = player.getVisibleTitle(player);
		title_color = player.getVisibleTitleColor(player);

		if(player.isPledgeVisible(player))
		{
			Clan clan = player.getClan();
			Alliance alliance = clan == null ? null : clan.getAlliance();
			//
			clan_id = clan == null ? 0 : clan.getClanId();
			_isClanLeader = player.isClanLeader() ? 1 : 0;
			clan_crest_id = clan == null ? 0 : clan.getCrestId();
			large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
			//
			ally_id = alliance == null ? 0 : alliance.getAllyId();
			ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();
		}

		if(player.isGMInvisible())
			_title += "[I]";
		if(player.isPolymorphed())
		{
			if(NpcHolder.getInstance().getTemplate(player.getPolyId()) != null)
				_title += " - " + NpcHolder.getInstance().getTemplate(player.getPolyId()).name;
			else
				_title += " - Polymorphed";
		}

		if(player.isMounted())
		{
			_weaponEnchant = 0;
			mount_id = player.getMountNpcId() + 1000000;
			mount_type = player.getMountType().ordinal();
		}
		else
		{
			_weaponEnchant = player.getEnchantEffect();
			mount_id = 0;
			mount_type = 0;
		}

		_weaponFlag = player.getActiveWeaponInstance() == null ? 0x14 : 0x28;

		move_speed = player.getMovementSpeedMultiplier();
		_runSpd = (int) (player.getRunSpeed() / move_speed);
		_walkSpd = (int) (player.getWalkSpeed() / move_speed);

		_flRunSpd = 0; // TODO
		_flWalkSpd = 0; // TODO

		if(player.isFlying())
		{
			_flyRunSpd = _runSpd;
			_flyWalkSpd = _walkSpd;
		}
		else
		{
			_flyRunSpd = 0;
			_flyWalkSpd = 0;
		}

		_swimRunSpd = (int) (player.getSwimRunSpeed() / move_speed);
		_swimWalkSpd = (int) (player.getSwimWalkSpeed() / move_speed);

		if(player.getClan() != null)
		{
			_relation |= RelationChangedPacket.USER_RELATION_CLAN_MEMBER;
			if(player.isClanLeader())
				_relation |= RelationChangedPacket.USER_RELATION_CLAN_LEADER;
		}

		for(Event e : player.getEvents())
			_relation = e.getUserRelation(player, _relation);

		_loc = player.getLoc();
		obj_id = player.getObjectId();
		vehicle_obj_id = player.isInBoat() ? player.getBoat().getBoatId() : 0x00;
		_race = player.getRace().ordinal();
		sex = player.getSex().ordinal();
		base_class = ClassId.VALUES[player.getBaseClassId()].getFirstParent(sex).getId();
		level = player.getLevel();
		_exp = player.getExp();
		_expPercent = Experience.getExpPercent(player.getLevel(), player.getExp());
		_str = player.getSTR();
		_dex = player.getDEX();
		_con = player.getCON();
		_int = player.getINT();
		_wit = player.getWIT();
		_men = player.getMEN();
		curHp = (int) player.getCurrentHp();
		maxHp = player.getMaxHp();
		curMp = (int) player.getCurrentMp();
		maxMp = player.getMaxMp();
		curLoad = player.getCurrentLoad();
		maxLoad = player.getMaxLoad();
		_sp = player.getSp();
		_patk = player.getPAtk(null);
		_patkspd = player.getPAtkSpd();
		_pdef = player.getPDef(null);
		_pEvasion = player.getPEvasionRate(null);
		_pAccuracy = player.getPAccuracy();
		_pCrit = player.getPCriticalHit(null);
		_mEvasion = player.getMEvasionRate(null);
		_mAccuracy = player.getMAccuracy();
		_mCrit = player.getMCriticalHit(null, null);
		_matk = player.getMAtk(null, null);
		_matkspd = player.getMAtkSpd();
		_mdef = player.getMDef(null, null);
		pvp_flag = player.getPvpFlag(); // 0=white, 1=purple, 2=purpleblink
		karma = player.getKarma();
		attack_speed = player.getAttackSpeedMultiplier();
		col_radius = player.getCollisionRadius();
		col_height = player.getCollisionHeight();
		hair_style = player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIR) > 0 ? sex : (player.getBeautyHairStyle() > 0 ? player.getBeautyHairStyle() : player.getHairStyle());
		hair_color = player.getBeautyHairColor() > 0 ? player.getBeautyHairColor() : player.getHairColor();
		face = player.getBeautyFace() > 0 ? player.getBeautyFace() : player.getFace();
		gm_commands = player.isGM() || player.getPlayerAccess().CanUseAltG ? 1 : 0;
		// builder level активирует в клиенте админские команды
		clan_id = player.getClanId();
		ally_id = player.getAllyId();
		private_store = player.isInBuffStore() ? Player.STORE_PRIVATE_NONE : player.getPrivateStoreType();
		can_crystalize = player.getSkillLevel(Skill.SKILL_CRYSTALLIZE) > 0 ? 1 : 0;
		pk_kills = player.getPkKills();
		pvp_kills = player.getPvpKills();
		cubics = player.getCubics().toArray(new Cubic[player.getCubics().size()]);
		ClanPrivs = player.getClanPrivileges();
		rec_left = player.getRecomLeft(); //c2 recommendations remaining
		rec_have = player.getRecomHave(); //c2 recommendations received
		InventoryLimit = player.getInventoryLimit();
		class_id = player.getClassId().getId();
		maxCp = player.getMaxCp();
		curCp = (int) player.getCurrentCp();
		_team = player.getTeam();
		hero = player.isHero() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0; //0x01: Hero Aura and symbol
		running = player.isRunning() ? 0x01 : 0x00; //changes the Speed display on Status Window
		pledge_class = player.getPledgeRank().ordinal();
		pledge_type = player.getPledgeType();
		transformation = player.getVisualTransformId();
		attackElement = player.getAttackElement();
		attackElementValue = player.getAttack(attackElement);
		defenceFire = player.getDefence(Element.FIRE);
		defenceWater = player.getDefence(Element.WATER);
		defenceWind = player.getDefence(Element.WIND);
		defenceEarth = player.getDefence(Element.EARTH);
		defenceHoly = player.getDefence(Element.HOLY);
		defenceUnholy = player.getDefence(Element.UNHOLY);
		agathion = player.getAgathionId();
		fame = player.getFame();
		partyRoom = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getLeader() == player;
		_moveType = player.isInFlyingTransform() ? 0x02 : (player.isInWater() ? 0x01 : 0x00);
		talismans = player.getTalismanCount();
		_jewelsLimit = player.getJewelsLimit();
		//_allowMap = player.isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP);
		_partySubstitute = player.isPartySubstituteStarted()  ? 1 : 0;
		_hideHeadAccessories = player.hideHeadAccessories();
		_armorSetEnchant = player.getArmorSetEnchant();

		can_writeImpl = true;

		if(addAll)
			addComponentType(UserInfoType.values());
	}

	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}

	@Override
	protected void onNewMaskAdded(UserInfoType component)
	{
		calcBlockSize(component);
	}

	private void calcBlockSize(UserInfoType type)
	{
		switch(type)
		{
			case BASIC_INFO:
			{
				_initSize += type.getBlockLength() + (_name.length() * 2);
				break;
			}
			case CLAN:
			{
				_initSize += type.getBlockLength() + (_title.length() * 2);
				break;
			}
			default:
			{
				_initSize += type.getBlockLength();
				break;
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;

		writeD(obj_id);

		writeD(_initSize);
		writeH(23);
		writeB(_masks);

		if(containsMask(UserInfoType.RELATION))
			writeD(_relation);

		if(containsMask(UserInfoType.BASIC_INFO))
		{
			writeH(UserInfoType.BASIC_INFO.getBlockLength() + (_name.length() * 2));
			writeString(_name);
			writeC(gm_commands);
			writeC(_race);
			writeC(sex);
			writeD(base_class);
			writeD(class_id);
			writeC(level);
		}

		if(containsMask(UserInfoType.BASE_STATS))
		{
			writeH(UserInfoType.BASE_STATS.getBlockLength());
			writeH(_str);
			writeH(_dex);
			writeH(_con);
			writeH(_int);
			writeH(_wit);
			writeH(_men);
			writeH(0x00);
			writeH(0x00);
		}

		if(containsMask(UserInfoType.MAX_HPCPMP))
		{
			writeH(UserInfoType.MAX_HPCPMP.getBlockLength());
			writeD(maxHp);
			writeD(maxMp);
			writeD(maxCp);
		}

		if(containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP))
		{
			writeH(UserInfoType.CURRENT_HPMPCP_EXP_SP.getBlockLength());
			writeD(curHp);
			writeD(curMp);
			writeD(curCp);
			writeQ(_sp);
			writeQ(_exp);
			writeF(_expPercent);
		}

		if(containsMask(UserInfoType.ENCHANTLEVEL))
		{
			writeH(UserInfoType.ENCHANTLEVEL.getBlockLength());
			writeC(_weaponEnchant);
			writeC(_armorSetEnchant);
		}

		if(containsMask(UserInfoType.APPAREANCE))
		{
			writeH(UserInfoType.APPAREANCE.getBlockLength());
			writeD(hair_style);
			writeD(hair_color);
			writeD(face);
			writeC(!_hideHeadAccessories);  //переключения прически/головного убора
		}

		if(containsMask(UserInfoType.STATUS))
		{
			writeH(UserInfoType.STATUS.getBlockLength());
			writeC(mount_type);
			writeC(private_store);
			writeC(can_crystalize);
			writeC(0x00);
		}

		if(containsMask(UserInfoType.STATS))
		{
			writeH(UserInfoType.STATS.getBlockLength());
			writeH(_weaponFlag);
			writeD(_patk);
			writeD(_patkspd);
			writeD(_pdef);
			writeD(_pEvasion);
			writeD(_pAccuracy);
			writeD(_pCrit);
			writeD(_matk);
			writeD(_matkspd);
			writeD(_patkspd);
			writeD(_mEvasion);
			writeD(_mdef);
			writeD(_mAccuracy);
			writeD(_mCrit);
		}

		if(containsMask(UserInfoType.ELEMENTALS))
		{
			writeH(UserInfoType.ELEMENTALS.getBlockLength());
			writeH(defenceFire);
			writeH(defenceWater);
			writeH(defenceWind);
			writeH(defenceEarth);
			writeH(defenceHoly);
			writeH(defenceUnholy);
		}

		if(containsMask(UserInfoType.POSITION))
		{
			writeH(UserInfoType.POSITION.getBlockLength());
			writeD(_loc.x);
			writeD(_loc.y);
			writeD(_loc.z + Config.CLIENT_Z_SHIFT);
			writeD(vehicle_obj_id);
		}

		if(containsMask(UserInfoType.SPEED))
		{
			writeH(UserInfoType.SPEED.getBlockLength());
			writeH(_runSpd);
			writeH(_walkSpd);
			writeH(_swimRunSpd);
			writeH(_swimWalkSpd);
			writeH(_flRunSpd);
			writeH(_flWalkSpd);
			writeH(_flyRunSpd);
			writeH(_flyWalkSpd);
		}

		if(containsMask(UserInfoType.MULTIPLIER))
		{
			writeH(UserInfoType.MULTIPLIER.getBlockLength());
			writeF(move_speed);
			writeF(attack_speed);
		}

		if(containsMask(UserInfoType.COL_RADIUS_HEIGHT))
		{
			writeH(UserInfoType.COL_RADIUS_HEIGHT.getBlockLength());
			writeF(col_radius);
			writeF(col_height);
		}

		if(containsMask(UserInfoType.ATK_ELEMENTAL))
		{
			writeH(UserInfoType.ATK_ELEMENTAL.getBlockLength());
			writeC(attackElement.getId());
			writeH(attackElementValue);
		}

		if(containsMask(UserInfoType.CLAN))
		{
			writeH(UserInfoType.CLAN.getBlockLength() + (_title.length() * 2));
			writeString(_title);
			writeH(pledge_type);
			writeD(clan_id);
			writeD(large_clan_crest_id);
			writeD(clan_crest_id);
			writeD(ClanPrivs);
			writeC(_isClanLeader);
			writeD(ally_id);
			writeD(ally_crest_id);
			writeC(partyRoom ? 0x01 : 0x00);
		}

		if(containsMask(UserInfoType.SOCIAL))
		{
			writeH(UserInfoType.SOCIAL.getBlockLength());
			writeC(pvp_flag);
			writeD(karma);
			writeC(0x00);
			writeC(hero);
			writeC(pledge_class);
			writeD(pk_kills);
			writeD(pvp_kills);
			writeH(rec_left);
			writeH(rec_have);
		}

		if(containsMask(UserInfoType.VITA_FAME))
		{
			writeH(UserInfoType.VITA_FAME.getBlockLength());
			writeD(0x00);
			writeC(0x00); // Vita Bonus
			writeD(fame);
			writeD(0x00); // raid points
		}

		if(containsMask(UserInfoType.SLOTS))
		{
			writeH(UserInfoType.SLOTS.getBlockLength());
			writeC(talismans);
			writeC(_jewelsLimit);
			writeC(_team.ordinal());
			writeC(0x00);
			writeC(0x00);
			writeC(0x00);
			writeC(0x00);
		}

		if(containsMask(UserInfoType.MOVEMENTS))
		{
			writeH(UserInfoType.MOVEMENTS.getBlockLength());
			writeC(_moveType);
			writeC(running);
		}

		if(containsMask(UserInfoType.COLOR))
		{
			writeH(UserInfoType.COLOR.getBlockLength());
			writeD(name_color);
			writeD(title_color);
		}

		if(containsMask(UserInfoType.INVENTORY_LIMIT))
		{
			writeH(UserInfoType.INVENTORY_LIMIT.getBlockLength());
			writeH(0x00);
			writeH(0x00);
			writeH(InventoryLimit);
			writeC(0); // hide title - 1, 0 - no
		}

		if(containsMask(UserInfoType.UNK_3))
		{
			writeH(UserInfoType.UNK_3.getBlockLength());
			writeD(0x00);
			writeH(0x00);
			writeC(0x00);
		}
	}
}