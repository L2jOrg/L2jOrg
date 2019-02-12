package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.NpcHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.player.Cubic;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.s2c.updatetype.UserInfoType;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
		private_store =  player.getPrivateStoreType();
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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		if(!can_writeImpl)
			return;

		buffer.putInt(obj_id);
		buffer.putInt(_initSize);
		buffer.putShort((short) 23);
		buffer.put(_masks);

		if(containsMask(UserInfoType.RELATION))
			buffer.putInt(_relation);

		if(containsMask(UserInfoType.BASIC_INFO)) {
			buffer.putShort((short) (UserInfoType.BASIC_INFO.getBlockLength() + (_name.length() * 2)));
			writeSizedString(_name, buffer);
			buffer.put((byte)gm_commands);
			buffer.put((byte)_race);
			buffer.put((byte)sex);
			buffer.putInt(base_class);
			buffer.putInt(class_id);
			buffer.put((byte)level);
		}

		if(containsMask(UserInfoType.BASE_STATS)) {
			buffer.putShort((short) UserInfoType.BASE_STATS.getBlockLength());
			buffer.putShort((short) _str);
			buffer.putShort((short) _dex);
			buffer.putShort((short) _con);
			buffer.putShort((short) _int);
			buffer.putShort((short) _wit);
			buffer.putShort((short) _men);
			buffer.putShort((short) 0x00);
			buffer.putShort((short) 0x00);
		}

		if(containsMask(UserInfoType.MAX_HPCPMP)) {
			buffer.putShort((short) UserInfoType.MAX_HPCPMP.getBlockLength());
			buffer.putInt(maxHp);
			buffer.putInt(maxMp);
			buffer.putInt(maxCp);
		}

		if(containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
			buffer.putShort((short) UserInfoType.CURRENT_HPMPCP_EXP_SP.getBlockLength());
			buffer.putInt(curHp);
			buffer.putInt(curMp);
			buffer.putInt(curCp);
			buffer.putLong(_sp);
			buffer.putLong(_exp);
			buffer.putDouble(_expPercent);
		}

		if(containsMask(UserInfoType.ENCHANTLEVEL)) {
			buffer.putShort((short) UserInfoType.ENCHANTLEVEL.getBlockLength());
			buffer.put((byte)_weaponEnchant);
			buffer.put((byte)_armorSetEnchant);
		}

		if(containsMask(UserInfoType.APPAREANCE)) {
			buffer.putShort((short) UserInfoType.APPAREANCE.getBlockLength());
			buffer.putInt(hair_style);
			buffer.putInt(hair_color);
			buffer.putInt(face);
			buffer.put((byte) (!_hideHeadAccessories ? 0x01 : 0x00));  //переключения прически/головного убора
		}

		if(containsMask(UserInfoType.STATUS)) {
			buffer.putShort((short) UserInfoType.STATUS.getBlockLength());
			buffer.put((byte)mount_type);
			buffer.put((byte)private_store);
			buffer.put((byte)can_crystalize);
			buffer.put((byte)0x00);
		}

		if(containsMask(UserInfoType.STATS)) {
			buffer.putShort((short) UserInfoType.STATS.getBlockLength());
			buffer.putShort((short) _weaponFlag);
			buffer.putInt(_patk);
			buffer.putInt(_patkspd);
			buffer.putInt(_pdef);
			buffer.putInt(_pEvasion);
			buffer.putInt(_pAccuracy);
			buffer.putInt(_pCrit);
			buffer.putInt(_matk);
			buffer.putInt(_matkspd);
			buffer.putInt(_patkspd);
			buffer.putInt(_mEvasion);
			buffer.putInt(_mdef);
			buffer.putInt(_mAccuracy);
			buffer.putInt(_mCrit);
		}

		if(containsMask(UserInfoType.ELEMENTALS)) {
			buffer.putShort((short) UserInfoType.ELEMENTALS.getBlockLength());
			buffer.putShort((short) defenceFire);
			buffer.putShort((short) defenceWater);
			buffer.putShort((short) defenceWind);
			buffer.putShort((short) defenceEarth);
			buffer.putShort((short) defenceHoly);
			buffer.putShort((short) defenceUnholy);
		}

		if(containsMask(UserInfoType.POSITION)) {
			buffer.putShort((short) UserInfoType.POSITION.getBlockLength());
			buffer.putInt(_loc.x);
			buffer.putInt(_loc.y);
			buffer.putInt(_loc.z + Config.CLIENT_Z_SHIFT);
			buffer.putInt(vehicle_obj_id);
		}

		if(containsMask(UserInfoType.SPEED)) {
			buffer.putShort((short) UserInfoType.SPEED.getBlockLength());
			buffer.putShort((short) _runSpd);
			buffer.putShort((short) _walkSpd);
			buffer.putShort((short) _swimRunSpd);
			buffer.putShort((short) _swimWalkSpd);
			buffer.putShort((short) _flRunSpd);
			buffer.putShort((short) _flWalkSpd);
			buffer.putShort((short) _flyRunSpd);
			buffer.putShort((short) _flyWalkSpd);
		}

		if(containsMask(UserInfoType.MULTIPLIER)) {
			buffer.putShort((short) UserInfoType.MULTIPLIER.getBlockLength());
			buffer.putDouble(move_speed);
			buffer.putDouble(attack_speed);
		}

		if(containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
			buffer.putShort((short) UserInfoType.COL_RADIUS_HEIGHT.getBlockLength());
			buffer.putDouble(col_radius);
			buffer.putDouble(col_height);
		}

		if(containsMask(UserInfoType.ATK_ELEMENTAL)) {
			buffer.putShort((short) UserInfoType.ATK_ELEMENTAL.getBlockLength());
			buffer.put((byte)attackElement.getId());
			buffer.putShort((short) attackElementValue);
		}

		if(containsMask(UserInfoType.CLAN)) {
			buffer.putShort((short) (UserInfoType.CLAN.getBlockLength() + (_title.length() * 2)));
			writeSizedString(_title, buffer);
			buffer.putShort((short) pledge_type);
			buffer.putInt(clan_id);
			buffer.putInt(large_clan_crest_id);
			buffer.putInt(clan_crest_id);
			buffer.putInt(ClanPrivs);
			buffer.put((byte)_isClanLeader);
			buffer.putInt(ally_id);
			buffer.putInt(ally_crest_id);
			buffer.put((byte) ( partyRoom ? 0x01 : 0x00));
		}

		if(containsMask(UserInfoType.SOCIAL)) {
			buffer.putShort((short) UserInfoType.SOCIAL.getBlockLength());
			buffer.put((byte)pvp_flag);
			buffer.putInt(karma);
			buffer.put((byte)0x00);
			buffer.put((byte)hero);
			buffer.put((byte)pledge_class);
			buffer.putInt(pk_kills);
			buffer.putInt(pvp_kills);
			buffer.putShort((short) rec_left);
			buffer.putShort((short) rec_have);
		}

		if(containsMask(UserInfoType.VITA_FAME)) {
			buffer.putShort((short) UserInfoType.VITA_FAME.getBlockLength());
			buffer.putInt(0x00);
			buffer.put((byte)0x00); // Vita Bonus
			buffer.putInt(fame);
			buffer.putInt(0x00); // raid points
		}

		if(containsMask(UserInfoType.SLOTS)) {
			buffer.putShort((short) UserInfoType.SLOTS.getBlockLength());
			buffer.put((byte)talismans);
			buffer.put((byte)_jewelsLimit);
			buffer.put((byte)_team.ordinal());
			buffer.putInt(0x00); // Team mask ?
		}

		if(containsMask(UserInfoType.MOVEMENTS)) {
			buffer.putShort((short) UserInfoType.MOVEMENTS.getBlockLength());
			buffer.put((byte)_moveType);
			buffer.put((byte)running);
		}

		if(containsMask(UserInfoType.COLOR)) {
			buffer.putShort((short) UserInfoType.COLOR.getBlockLength());
			buffer.putInt(name_color);
			buffer.putInt(title_color);
		}

		if(containsMask(UserInfoType.INVENTORY_LIMIT)) {
			buffer.putShort((short) UserInfoType.INVENTORY_LIMIT.getBlockLength());
			buffer.putInt(0x00);
			buffer.putShort((short) InventoryLimit);
			buffer.put((byte)0); // hide title - 1, 0 - no
		}

		if(containsMask(UserInfoType.UNK_3)) {
			buffer.putShort((short) UserInfoType.UNK_3.getBlockLength());
			buffer.putInt(0x00);
			buffer.putShort((short) 0x00);
			buffer.put((byte)0x00);
		}
	}

	@Override
	protected int size(GameClient client) {
		return _initSize + 11;
	}
}