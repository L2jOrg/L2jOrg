package org.l2j.gameserver.model;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.collections.CollectionUtils;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.entity.Reflection;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.IBroadcastPacket;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.*;
import org.l2j.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import org.l2j.gameserver.taskmanager.LazyPrecisionTaskManager;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public class Party implements PlayerGroup
{
	public static final int MAX_SIZE = Config.MAXIMUM_MEMBERS_IN_PARTY;

	public static final int ITEM_LOOTER = 0;
	public static final int ITEM_RANDOM = 1;
	public static final int ITEM_RANDOM_SPOIL = 2;
	public static final int ITEM_ORDER = 3;
	public static final int ITEM_ORDER_SPOIL = 4;

	private final List<Player> _members = new CopyOnWriteArrayList<Player>();

	private int _partyLvl = 0;
	private int _itemDistribution = 0;
	private int _itemOrder = 0;
	private int _dimentionalRift;

	private Reflection _reflection;
	private CommandChannel _commandChannel;

	public double _rateExp;
	public double _rateSp;
	public double _rateDrop;
	public double _rateAdena;
	public double _rateSpoil;
	public double _dropChanceMod;
	public double _spoilChanceMod;

	private ScheduledFuture<?> positionTask;

	private int _requestChangeLoot = -1;
	private long _requestChangeLootTimer = 0;
	private Set<Integer> _changeLootAnswers = null;
	private static final int[] LOOT_SYSSTRINGS = { 487, 488, 798, 799, 800 };
	private static final int[] TACTICAL_SYSSTRINGS = { 0, 2664, 2665, 2666, 2667 };
	private Future<?> _checkTask = null;

	private TIntObjectHashMap<Creature> _tacticalTargets = new TIntObjectHashMap<Creature>(4);

	/**
	 * constructor ensures party has always one member - leader
	 * @param leader создатель парти
	 * @param itemDistribution режим распределения лута
	 */
	public Party(Player leader, int itemDistribution)
	{
		_itemDistribution = itemDistribution;
		_members.add(leader);
		_partyLvl = leader.getLevel();
		_rateExp = leader.getPremiumAccount().getRates().getExp();
		_rateSp = leader.getPremiumAccount().getRates().getSp();
		_rateAdena = leader.getPremiumAccount().getRates().getAdena();
		_rateDrop = leader.getPremiumAccount().getRates().getDrop();
		_rateSpoil = leader.getPremiumAccount().getRates().getSpoil();

		_dropChanceMod = leader.getPremiumAccount().getModifiers().getDropChance();
		_spoilChanceMod = leader.getPremiumAccount().getModifiers().getSpoilChance();
	}

	/**
	 * @return number of party members
	 */
	public int getMemberCount()
	{
		return _members.size();
	}

	public int getMemberCountInRange(Player player, int range)
	{
		int count = 0;
		for(Player member : _members)
			if(member == player || member.isInRangeZ(player, range))
				count++;
		return count;
	}

	/**
	 * @return all party members
	 */
	public List<Player> getPartyMembers()
	{
		return _members;
	}

	public List<Integer> getPartyMembersObjIds()
	{
		List<Integer> result = new ArrayList<Integer>(_members.size());
		for(Player member : _members)
			result.add(member.getObjectId());
		return result;
	}

	public List<Playable> getPartyMembersWithPets()
	{
		List<Playable> result = new ArrayList<Playable>();
		for(Player member : _members)
		{
			result.add(member);
			for(Servitor servitor : member.getServitors())
				result.add(servitor);
		}
		return result;
	}

	/**
	 * @return next item looter
	 */
	private Player getNextLooterInRange(Player player, ItemInstance item, int range)
	{
		synchronized (_members)
		{
			int antiloop = _members.size();
			while(--antiloop > 0)
			{
				int looter = _itemOrder;
				_itemOrder++;
				if(_itemOrder > _members.size() - 1)
					_itemOrder = 0;

				Player ret = looter < _members.size() ? _members.get(looter) : player;

				if(ret != null && !ret.isDead() && ret.isInRangeZ(player, range) && ret.getInventory().validateCapacity(item) && ret.getInventory().validateWeight(item))
					return ret;
			}
		}
		return player;
	}

	/**
	 * true if player is party leader
	 */
	public boolean isLeader(Player player)
	{
		return getPartyLeader() == player;
	}

	/**
	 * Возвращает лидера партии
	 * @return L2Player Лидер партии
	 */
	public Player getPartyLeader()
	{
		synchronized (_members)
		{
			if(_members.size() == 0)
				return null;
			return _members.get(0);
		}
	}

	/**
	 * Broadcasts packet to every party member
	 * @param msg packet to broadcast
	 */
	@Override
	public void broadCast(IBroadcastPacket... msg)
	{
		for(Player member : _members)
			member.sendPacket(msg);
	}

	/**
	 * Рассылает текстовое сообщение всем членам группы
	 * @param msg сообщение
	 */
	public void broadcastMessageToPartyMembers(String msg)
	{
		this.broadCast(new SystemMessage(msg));
	}

	public void broadcastCustomMessageToPartyMembers(String address, String... replacements)
	{
		for(Player member : _members)
		{
			CustomMessage cm = new CustomMessage(address);
			for(String s : replacements)
				cm.addString(s);
			member.sendMessage(cm);
		}
	}

	/**
	 * Рассылает пакет всем членам группы исключая указанного персонажа<BR><BR>
	 */
	public void broadcastToPartyMembers(Player exclude, IBroadcastPacket msg)
	{
		for(Player member : _members)
			if(exclude != member)
				member.sendPacket(msg);
	}

	public void broadcastToPartyMembersInRange(Player player, IBroadcastPacket msg, int range)
	{
		for(Player member : _members)
			if(player.isInRangeZ(member, range))
				member.sendPacket(msg);
	}

	public boolean containsMember(Player player)
	{
		return _members.contains(player);
	}

	public int indexOf(Player player)
	{
		return _members.indexOf(player);
	}

	/**
	 * adds new member to party
	 * @param player L2Player to add
	 */
	public boolean addPartyMember(Player player)
	{
		Player leader = getPartyLeader();
		if(leader == null)
			return false;

		synchronized (_members)
		{
			if(_members.isEmpty())
				return false;
			if(_members.contains(player))
				return false;
			if(_members.size() == MAX_SIZE)
				return false;
			_members.add(player);
		}

		if(_requestChangeLoot != -1)
			finishLootRequest(false); // cancel on invite

		player.setParty(this);
		player.getListeners().onPartyInvite();

		List<IBroadcastPacket> addInfo = new ArrayList<IBroadcastPacket>(4 + _members.size() * 4);
		List<IBroadcastPacket> pplayer = new ArrayList<IBroadcastPacket>(20);

		//sends new member party window for all members
		//we do all actions before adding member to a list, this speeds things up a little
		pplayer.add(new PartySmallWindowAllPacket(this, player));
		pplayer.add(new SystemMessage(SystemMessage.YOU_HAVE_JOINED_S1S_PARTY).addName(leader));

		addInfo.add(new SystemMessage(SystemMessage.S1_HAS_JOINED_THE_PARTY).addName(player));
		addInfo.add(new PartySpelledPacket(player, true));

		for(Servitor servitor : player.getServitors())
		{
			addInfo.add(new ExPartyPetWindowAdd(servitor));
			addInfo.add(new PartySpelledPacket(servitor, true));
		}

		RelationChangedPacket rcp = new RelationChangedPacket();
		PartyMemberPositionPacket pmp = new PartyMemberPositionPacket();
		List<IBroadcastPacket> pmember;
		for(Player member : _members)
			if(member != player)
			{
				pmember = new ArrayList<IBroadcastPacket>(addInfo.size() + 4);
				pmember.addAll(addInfo);
				pmember.add(new PartySmallWindowAddPacket(member, player));
				pmember.add(new PartyMemberPositionPacket().add(player));
				RelationChangedPacket memberrcp = new RelationChangedPacket(player, member);
				for(Servitor servitor : player.getServitors())
					memberrcp.add(servitor, member);

				pmember.add(memberrcp);
				member.sendPacket(pmember);

				pplayer.add(new PartySpelledPacket(member, true));

				for(Servitor servitor : player.getServitors())
				{
					pplayer.add(new PartySpelledPacket(servitor, true));
					servitor.broadcastCharInfoImpl(member, NpcInfoType.VALUES);
				}

				rcp.add(member, player);
				for(Servitor servitor : member.getServitors())
					rcp.add(servitor, player);

				pmp.add(member);
			}

		pplayer.add(rcp);
		pplayer.add(pmp);
		// Если партия уже в СС, то вновь прибывшем посылаем пакет открытия окна СС
		if(isInCommandChannel())
			pplayer.add(ExOpenMPCCPacket.STATIC);

		player.sendPacket(pplayer);

		startUpdatePositionTask();
		recalculatePartyData();

		sendTacticalSign(player);

		final MatchingRoom currentRoom = player.getMatchingRoom();
		final MatchingRoom room = leader.getMatchingRoom();
		if (currentRoom != null && currentRoom != room)
			currentRoom.removeMember(player, false);
		if(room != null && room.getType() == MatchingRoom.PARTY_MATCHING)
			room.addMemberForce(player);
		else
			MatchingRoomManager.getInstance().removeFromWaitingList(player);

		return true;
	}

	/**
	 * Удаляет все связи
	 */
	public void dissolveParty()
	{
		for(Player p : _members)
		{
			p.sendPacket(PartySmallWindowDeleteAllPacket.STATIC);
			p.setParty(null);
		}

		synchronized (_members)
		{
			_members.clear();
		}

		setCommandChannel(null);
		stopUpdatePositionTask();
	}

	/**
	 * removes player from party
	 * @param player L2Player to remove
	 */
	public boolean removePartyMember(Player player, boolean kick)
	{
		boolean isLeader = isLeader(player);
		boolean dissolve = false;

		synchronized (_members)
		{
			if(!_members.remove(player))
				return false;
			dissolve = _members.size() == 1;
		}

		player.stopSubstituteTask();
		player.getListeners().onPartyLeave();

		player.setParty(null);

		recalculatePartyData();

		List<IBroadcastPacket> pplayer = new ArrayList<IBroadcastPacket>(4 + _members.size() * 2);

		// Отсылаемы вышедшему пакет закрытия СС
		if(isInCommandChannel())
			pplayer.add(ExCloseMPCCPacket.STATIC);
		if(kick)
			pplayer.add(SystemMsg.YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY);
		else
			pplayer.add(SystemMsg.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY);
		pplayer.add(PartySmallWindowDeleteAllPacket.STATIC);

		List<IBroadcastPacket> outsInfo = new ArrayList<IBroadcastPacket>(3);
		for(Servitor servitor : player.getServitors())
			outsInfo.add(new ExPartyPetWindowDelete(servitor));

		outsInfo.add(new PartySmallWindowDeletePacket(player));
		if(kick)
			outsInfo.add(new SystemMessage(SystemMessage.S1_WAS_EXPELLED_FROM_THE_PARTY).addName(player));
		else
			outsInfo.add(new SystemMessage(SystemMessage.S1_HAS_LEFT_THE_PARTY).addName(player));

		RelationChangedPacket rcp = new RelationChangedPacket();

		List<IBroadcastPacket> pmember;
		for(Player member : _members)
		{
			pmember = new ArrayList<IBroadcastPacket>(2 + outsInfo.size());
			pmember.addAll(outsInfo);

			RelationChangedPacket memberrcp = new RelationChangedPacket(player, member);
			for(Servitor servitor : player.getServitors())
				memberrcp.add(servitor, member);

			pmember.add(memberrcp);
			member.sendPacket(pmember);

			rcp.add(member, player);

			for(Servitor servitor : member.getServitors())
				rcp.add(servitor, player);
		}
		pplayer.add(rcp);

		player.sendPacket(pplayer);
		clearTacticalTargets(player);

		Reflection reflection = getReflection();

		if(reflection != null && player.getReflection() == reflection && reflection.getReturnLoc() != null)
			player.teleToLocation(reflection.getReturnLoc(), ReflectionManager.MAIN);

		Player leader = getPartyLeader();
		final MatchingRoom room = leader != null ? leader.getMatchingRoom() : null;

		if(dissolve)
		{
			// Если в партии остался 1 человек, то удаляем ее из СС
			if(isInCommandChannel())
				_commandChannel.removeParty(this);
			else if(reflection != null)
			{
				//lastMember.teleToLocation(getReflection().getReturnLoc(), 0);
				//getReflection().stopCollapseTimer();
				//getReflection().collapse();
				if(reflection.getInstancedZone() != null && reflection.getInstancedZone().isCollapseOnPartyDismiss())
				{
					if(reflection.getParty() == this)
					{
						reflection.startCollapseTimer(reflection.getInstancedZone().getTimerOnCollapse() * 1000);
						if(leader != null && leader.getReflection() == reflection)
							leader.broadcastPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
					}
				}
			}

			if(room != null && room.getType() == MatchingRoom.PARTY_MATCHING)
			{
				if(isLeader) // Вышел/отвалился лидер, остался один партиец, пати и комната распускаются
					room.disband();
				else // Вышел/кикнули единственного партийца, комната переходит к лидеру, пати распускается
					room.removeMember(player, kick);
			}

			dissolveParty();
		}
		else
		{
			if(isInCommandChannel() && _commandChannel.getChannelLeader() == player)
				_commandChannel.setChannelLeader(leader);

			if(room != null && room.getType() == MatchingRoom.PARTY_MATCHING)
				room.removeMember(player, kick);

			if(isLeader)
				updateLeaderInfo();
		}

		if(_checkTask != null)
		{
			_checkTask.cancel(true);
			_checkTask = null;
		}

		return true;
	}

	public boolean changePartyLeader(Player player)
	{
		Player leader = getPartyLeader();

		// Меняем местами нового и текущего лидера
		synchronized (_members)
		{
			int index = _members.indexOf(player);
			if(index == -1)
				return false;
			_members.set(0, player);
			_members.set(index, leader);
		}

		leader.sendPacket(ExReplyHandOverPartyMaster.FALSE);
		player.sendPacket(ExReplyHandOverPartyMaster.TRUE);

		updateLeaderInfo();

		if(isInCommandChannel() && _commandChannel.getChannelLeader() == leader)
			_commandChannel.setChannelLeader(player);

		return true;
	}

	private void updateLeaderInfo()
	{
		Player leader = getPartyLeader();
		if(leader == null) // некрасиво, но иначе NPE.
			return;

		SystemMessage msg = new SystemMessage(SystemMessage.S1_HAS_BECOME_A_PARTY_LEADER).addName(leader);

		for(Player member : _members)
		{
			// индивидуальные пакеты - удаления и инициализация пати
			member.sendPacket(PartySmallWindowDeleteAllPacket.STATIC, // Удаляем все окошки
					new PartySmallWindowAllPacket(this, member), // Показываем окошки
					msg); // Сообщаем о смене лидера
		}

		// броадкасты состояний
		for(Player member : _members)
		{
			broadcastToPartyMembers(member, new PartySpelledPacket(member, true)); // Показываем иконки

			for(Servitor servitor : member.getServitors())
				this.broadCast(new ExPartyPetWindowAdd(servitor)); // Показываем окошки петов

			// broadcastToPartyMembers(member, new PartyMemberPositionPacket(member)); // Обновляем позицию на карте
		}

		MatchingRoom room = leader.getMatchingRoom();
		if(room != null && room.getType() == MatchingRoom.PARTY_MATCHING)
			room.setLeader(leader);
	}

	/**
	 * finds a player in the party by name
	 * @param name имя для поиска
	 * @return найденый L2Player или null если не найдено
	 */
	public Player getPlayerByName(String name)
	{
		for(Player member : _members)
			if(name.equalsIgnoreCase(member.getName()))
				return member;
		return null;
	}

	/**
	 * distribute item(s) to party members
	 * @param player
	 * @param item
	 */
	public void distributeItem(Player player, ItemInstance item, NpcInstance fromNpc)
	{
		switch(item.getItemId())
		{
			case ItemTemplate.ITEM_ID_ADENA:
				distributeAdena(player, item, fromNpc);
				break;
			default:
				distributeItem0(player, item, fromNpc);
				break;
		}

	}

	private void distributeItem0(Player player, ItemInstance item, NpcInstance fromNpc)
	{
		Player target = null;

		List<Player> ret = null;
		switch(_itemDistribution)
		{
			case ITEM_RANDOM:
			case ITEM_RANDOM_SPOIL:
				ret = new ArrayList<Player>(_members.size());
				for(Player member : _members)
					if(member.isInRangeZ(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) && !member.isDead() && member.getInventory().validateCapacity(item) && member.getInventory().validateWeight(item))
						ret.add(member);

				target = ret.isEmpty() ? null : ret.get(Rnd.get(ret.size()));
				break;
			case ITEM_ORDER:
			case ITEM_ORDER_SPOIL:
				synchronized (_members)
				{
					ret = new CopyOnWriteArrayList<Player>(_members);
					while(target == null && !ret.isEmpty())
					{
						int looter = _itemOrder;
						_itemOrder++;
						if(_itemOrder > ret.size() - 1)
							_itemOrder = 0;

						Player looterPlayer = looter < ret.size() ? ret.get(looter) : null;

						if(looterPlayer != null)
						{
							if(!looterPlayer.isDead() && looterPlayer.isInRangeZ(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) && ItemFunctions.canAddItem(looterPlayer, item))
								target = looterPlayer;
							else
								ret.remove(looterPlayer);
						}
					}
				}

				if(target == null)
					return;
				break;
			case ITEM_LOOTER:
			default:
				target = player;
				break;
		}

		if(target == null)
			target = player;

		if(target.pickupItem(item, Log.PartyPickup))
		{
			if(fromNpc == null)
				player.broadcastPacket(new GetItemPacket(item, player.getObjectId()));

			player.broadcastPickUpMsg(item);
			item.pickupMe();

			broadcastToPartyMembers(target, SystemMessagePacket.obtainItemsBy(item, target));
		}
		else
			item.dropToTheGround(player, fromNpc);
	}

	private void distributeAdena(Player player, ItemInstance item, NpcInstance fromNpc)
	{
		if(player == null)
			return;

		List<Player> membersInRange = new ArrayList<Player>();

		if(item.getCount() < _members.size())
			membersInRange.add(player);
		else
		{
			for(Player member : _members)
				if(!member.isDead() && (member == player || player.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE)) && ItemFunctions.canAddItem(player, item))
					membersInRange.add(member);
		}

		if(membersInRange.isEmpty())
			membersInRange.add(player);

		long totalAdena = item.getCount();
		long amount = totalAdena / membersInRange.size();
		long ost = totalAdena % membersInRange.size();

		for(Player member : membersInRange)
		{
			long count = member.equals(player) ? amount + ost : amount;
			member.getInventory().addAdena(count);
			member.sendPacket(SystemMessagePacket.obtainItems(ItemTemplate.ITEM_ID_ADENA, count, 0));
		}

		if(fromNpc == null)
			player.broadcastPacket(new GetItemPacket(item, player.getObjectId()));

		item.pickupMe();
	}

	public void distributeXpAndSp(double xpReward, double spReward, List<Player> rewardedMembers, Creature lastAttacker, MonsterInstance monster)
	{
		recalculatePartyData();

		List<Player> mtr = new ArrayList<Player>();
		int partyLevel = lastAttacker.getLevel();
		int partyLvlSum = 0;

		// считаем минимальный/максимальный уровень
		for(Player member : rewardedMembers)
		{
			if(!monster.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE))
				continue;
			partyLevel = Math.max(partyLevel, member.getLevel());
		}

		int maxDistributionLevelDIff = Config.ALT_PARTY_LVL_DIFF_PENALTY.length - 1;

		// составляем список игроков, удовлетворяющих требованиям
		for(Player member : rewardedMembers)
		{
			if(!monster.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE))
				continue;
			if(member.getLevel() <= partyLevel - maxDistributionLevelDIff)
				continue;
			partyLvlSum += member.getLevel();
			mtr.add(member);
		}

		if(mtr.isEmpty())
			return;

		TIntIntMap clansInParty = new TIntIntHashMap();
		for(Player member : mtr)
		{
			Clan clan = member.getClan();
			if(clan == null)
				continue;
			clansInParty.put(clan.getClanId(), clansInParty.get(clan.getClanId()) + 1);
		}

		// бонус за пати
		double bonus = Config.ALT_PARTY_BONUS[Math.min(Config.ALT_PARTY_BONUS.length, mtr.size()) - 1];

		// количество эксп и сп для раздачи на всех
		double XP = xpReward * bonus;
		double SP = spReward * bonus;

		for(Player member : mtr)
		{
			double lvlPenalty = Experience.penaltyModifier(monster.calculateLevelDiffForDrop(member.getLevel()), 9);
			int lvlDiff = partyLevel - member.getLevel();
			lvlDiff = Math.max(0, Math.min(lvlDiff, maxDistributionLevelDIff));
			lvlPenalty *= Config.ALT_PARTY_LVL_DIFF_PENALTY[lvlDiff] / 100.;

			double clanBonus = Config.ALT_PARTY_CLAN_BONUS[Math.min(Config.ALT_PARTY_CLAN_BONUS.length - 1, clansInParty.get(member.getClanId()))];

			// отдаем его часть с учетом пенальти
			double memberXp = XP * clanBonus * lvlPenalty * member.getLevel() / partyLvlSum;
			double memberSp = SP * clanBonus * lvlPenalty * member.getLevel() / partyLvlSum;

			// больше чем соло не дадут
			memberXp = Math.min(memberXp, xpReward);
			memberSp = Math.min(memberSp, spReward);

			member.addExpAndCheckBonus(monster, (long) memberXp, (long) memberSp);
		}

		recalculatePartyData();
	}

	public void recalculatePartyData()
	{
		_partyLvl = 0;
		double rateExp = 0.;
		double rateSp = 0.;
		double rateDrop = 0.;
		double rateAdena = 0.;
		double rateSpoil = 0.;
		double dropChanceMod = 0.;
		double spoilChanceMod = 0.;
		double minRateExp = Double.MAX_VALUE;
		double minRateSp = Double.MAX_VALUE;
		double minRateDrop = Double.MAX_VALUE;
		double minRateAdena = Double.MAX_VALUE;
		double minRateSpoil = Double.MAX_VALUE;
		double minDropChanceMod = Double.MAX_VALUE;
		double minSpoilChanceMod = Double.MAX_VALUE;
		int count = 0;

		for(Player member : _members)
		{
			int level = member.getLevel();
			_partyLvl = Math.max(_partyLvl, level);
			count++;

			rateExp += member.getPremiumAccount().getRates().getExp();
			rateSp += member.getPremiumAccount().getRates().getSp();
			rateDrop += member.getPremiumAccount().getRates().getDrop();
			rateAdena += member.getPremiumAccount().getRates().getAdena();
			rateSpoil += member.getPremiumAccount().getRates().getSpoil();
			dropChanceMod += member.getPremiumAccount().getModifiers().getDropChance();
			spoilChanceMod += member.getPremiumAccount().getModifiers().getSpoilChance();

			minRateExp = Math.min(minRateExp, member.getPremiumAccount().getRates().getExp());
			minRateSp = Math.min(minRateSp, member.getPremiumAccount().getRates().getSp());
			minRateDrop = Math.min(minRateDrop, member.getPremiumAccount().getRates().getDrop());
			minRateAdena = Math.min(minRateAdena, member.getPremiumAccount().getRates().getAdena());
			minRateSpoil = Math.min(minRateSpoil, member.getPremiumAccount().getRates().getSpoil());
			minDropChanceMod = Math.min(minDropChanceMod, member.getPremiumAccount().getModifiers().getDropChance());
			minSpoilChanceMod = Math.min(minSpoilChanceMod, member.getPremiumAccount().getModifiers().getSpoilChance());
		}

		_rateExp = Config.RATE_PARTY_MIN ? minRateExp : rateExp / count;
		_rateSp = Config.RATE_PARTY_MIN ? minRateSp : rateSp / count;
		_rateDrop = Config.RATE_PARTY_MIN ? minRateDrop : rateDrop / count;
		_rateAdena = Config.RATE_PARTY_MIN ? minRateAdena : rateAdena / count;
		_rateSpoil = Config.RATE_PARTY_MIN ? minRateSpoil : rateSpoil / count;
		_dropChanceMod = Config.RATE_PARTY_MIN ? minDropChanceMod : dropChanceMod / count;
		_spoilChanceMod = Config.RATE_PARTY_MIN ? minSpoilChanceMod : spoilChanceMod / count;
	}

	public int getLevel()
	{
		return _partyLvl;
	}

	public int getLootDistribution()
	{
		return _itemDistribution;
	}

	public boolean isDistributeSpoilLoot()
	{
		boolean rv = false;

		if(_itemDistribution == ITEM_RANDOM_SPOIL || _itemDistribution == ITEM_ORDER_SPOIL)
			rv = true;

		return rv;
	}

	public boolean isInReflection()
	{
		if(_reflection != null)
			return true;
		return false;
	}

	public void setReflection(Reflection reflection)
	{
		_reflection = reflection;
	}

	public Reflection getReflection()
	{
		if(_reflection != null)
			return _reflection;
		return null;
	}

	public boolean isInCommandChannel()
	{
		return _commandChannel != null;
	}

	public CommandChannel getCommandChannel()
	{
		return _commandChannel;
	}

	public void setCommandChannel(CommandChannel channel)
	{
		_commandChannel = channel;
	}

	/**
	 * Телепорт всей пати в одну точку (x,y,z)
	 */
	public void Teleport(int x, int y, int z)
	{
		TeleportParty(getPartyMembers(), new Location(x, y, z));
	}

	/**
	 * Телепорт всей пати в одну точку dest
	 */
	public void Teleport(Location dest)
	{
		TeleportParty(getPartyMembers(), dest);
	}

	/**
	 * Телепорт всей пати на территорию, игроки расставляются рандомно по территории
	 */
	public void Teleport(Territory territory)
	{
		RandomTeleportParty(getPartyMembers(), territory);
	}

	/**
	 * Телепорт всей пати на территорию, лидер попадает в точку dest, а все остальные относительно лидера
	 */
	public void Teleport(Territory territory, Location dest)
	{
		TeleportParty(getPartyMembers(), territory, dest);
	}

	public static void TeleportParty(List<Player> members, Location dest)
	{
		for(Player _member : members)
		{
			if(_member == null)
				continue;
			_member.teleToLocation(dest);
		}
	}

	public static void TeleportParty(List<Player> members, Territory territory, Location dest)
	{
		if(!territory.isInside(dest.x, dest.y))
		{
			Log.add("TeleportParty: dest is out of territory", "errors");
			Thread.dumpStack();
			return;
		}
		int base_x = members.get(0).getX();
		int base_y = members.get(0).getY();

		for(Player _member : members)
		{
			if(_member == null)
				continue;
			int diff_x = _member.getX() - base_x;
			int diff_y = _member.getY() - base_y;
			Location loc = new Location(dest.x + diff_x, dest.y + diff_y, dest.z);
			while(!territory.isInside(loc.x, loc.y))
			{
				diff_x = loc.x - dest.x;
				diff_y = loc.y - dest.y;
				if(diff_x != 0)
					loc.x -= diff_x / Math.abs(diff_x);
				if(diff_y != 0)
					loc.y -= diff_y / Math.abs(diff_y);
			}
			_member.teleToLocation(loc);
		}
	}

	public static void RandomTeleportParty(List<Player> members, Territory territory)
	{
		for(Player member : members)
			member.teleToLocation(Territory.getRandomLoc(territory, member.getGeoIndex()));
	}

	private void startUpdatePositionTask()
	{
		if(positionTask == null)
			positionTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(new UpdatePositionTask(), 1000, 1000);
	}

	private void stopUpdatePositionTask()
	{
		if(positionTask != null)
			positionTask.cancel(false);
	}

	private class UpdatePositionTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			List<Player> update = CollectionUtils.pooledList();

			for(Player member : _members)
			{
				Location loc = member.getLastPartyPosition();
				if(loc == null || member.getDistance(loc) > 256) //TODO подкорректировать
				{
					member.setLastPartyPosition(member.getLoc());
					update.add(member);
				}
			}

			if(!update.isEmpty())
				for(Player member : _members)
				{
					PartyMemberPositionPacket pmp = new PartyMemberPositionPacket();
					for(Player m : update)
						if(m != member)
							pmp.add(m);
					if(pmp.size() > 0)
						member.sendPacket(pmp);
				}

			CollectionUtils.recycle(update);
		}
	}

	public void requestLootChange(byte type)
	{
		if(_requestChangeLoot != -1)
		{
			if(System.currentTimeMillis() > _requestChangeLootTimer)
				finishLootRequest(false);
			else
				return;
		}
		_requestChangeLoot = type;
		int additionalTime = 45000; // timeout 45sec, guess
		_requestChangeLootTimer = System.currentTimeMillis() + additionalTime;
		_changeLootAnswers = new CopyOnWriteArraySet<Integer>();
		_checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ChangeLootCheck(), additionalTime + 1000, 5000);
		broadcastToPartyMembers(getPartyLeader(), new ExAskModifyPartyLooting(getPartyLeader().getName(), type));
		SystemMessage sm = new SystemMessage(SystemMessage.REQUESTING_APPROVAL_CHANGE_PARTY_LOOT_S1);
		sm.addSystemString(LOOT_SYSSTRINGS[type]);
		getPartyLeader().sendPacket(sm);
	}

	public synchronized void answerLootChangeRequest(Player member, boolean answer)
	{
		if(_requestChangeLoot == -1)
			return;
		if(_changeLootAnswers.contains(member.getObjectId()))
			return;
		if(!answer)
		{
			finishLootRequest(false);
			return;
		}
		_changeLootAnswers.add(member.getObjectId());
		if(_changeLootAnswers.size() >= getMemberCount() - 1)
		{
			finishLootRequest(true);
		}
	}

	private synchronized void finishLootRequest(boolean success)
	{
		if(_requestChangeLoot == -1)
			return;
		if(_checkTask != null)
		{
			_checkTask.cancel(false);
			_checkTask = null;
		}
		if(success)
		{
			this.broadCast(new ExSetPartyLooting(1, _requestChangeLoot));
			_itemDistribution = _requestChangeLoot;
			SystemMessage sm = new SystemMessage(SystemMessage.PARTY_LOOT_CHANGED_S1);
			sm.addSystemString(LOOT_SYSSTRINGS[_requestChangeLoot]);
			this.broadCast(sm);
		}
		else
		{
			this.broadCast(new ExSetPartyLooting(0, (byte) 0));
			this.broadCast(new SystemMessage(SystemMessage.PARTY_LOOT_CHANGE_CANCELLED));
		}
		_changeLootAnswers = null;
		_requestChangeLoot = -1;
		_requestChangeLootTimer = 0;
	}

	private class ChangeLootCheck extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(System.currentTimeMillis() > Party.this._requestChangeLootTimer)
			{
				Party.this.finishLootRequest(false);
			}
		}
	}

	@Override
	public Player getGroupLeader()
	{
		return getPartyLeader();
	}

	@Override
	public Iterator<Player> iterator()
	{
		return _members.iterator();
	}

	public void changeTacticalSign(Player player, int sign, Creature target)
	{
		if(target == null)
			return;

		if(_tacticalTargets.containsKey(sign))
		{
			Creature oldTarget = _tacticalTargets.get(sign);
			if(oldTarget != null)
				broadCast(new ExTacticalSign(oldTarget.getObjectId(), 0));
		}
		_tacticalTargets.put(sign, target);
		broadCast(new ExTacticalSign(target.getObjectId(), sign));
		SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.C1_USED_S3_ON_C2);
		sm.addName(player);
		sm.addName(target);
		sm.addSysString(TACTICAL_SYSSTRINGS[sign]);
		broadCast(sm);
	}

	public Creature findTacticalTarget(Player player, int sign)
	{
		if(player == null)
			return null;

		if(!_tacticalTargets.containsKey(sign))
			return null;

		Creature target = _tacticalTargets.get(sign);
		if(player.getDistance3D(target) > 1000) // TODO: [Bonux] Check distance.
			return null;

		return target;
	}

	private void clearTacticalTargets(Player player)
	{
		for(Creature target : _tacticalTargets.valueCollection())
			player.sendPacket(new ExTacticalSign(target.getObjectId(), 0));
	}

	private void sendTacticalSign(Player member)
	{
		for(TIntObjectIterator<Creature> iterator = _tacticalTargets.iterator(); iterator.hasNext();)
		{
			iterator.advance();

			Creature target = iterator.value();
			if(target == null)
				continue;

			member.sendPacket(new ExTacticalSign(target.getObjectId(), iterator.key()));
		}
	}

	public void removeTacticalSign(Creature target)
	{
		for(TIntObjectIterator<Creature> iterator = _tacticalTargets.iterator(); iterator.hasNext();)
		{
			iterator.advance();

			if(iterator.value() == target)
			{
				broadCast(new ExTacticalSign(target.getObjectId(), 0));
				_tacticalTargets.remove(iterator.key());
				break;
			}
		}
	}

	public void substituteMember(Player member, Player member2)
	{
		Location defLoc = member.getLoc();
		Location defLoc2 = member2.getLoc();
		member.teleToLocation(defLoc2,member2.getReflection());
		member2.teleToLocation(defLoc,member.getReflection());
		removePartyMember(member, false);
		addPartyMember(member2);
	}
}