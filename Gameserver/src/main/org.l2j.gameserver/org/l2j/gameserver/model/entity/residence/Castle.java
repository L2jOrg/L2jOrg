package org.l2j.gameserver.model.entity.residence;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.l2j.commons.dao.JdbcEntityState;
import org.l2j.commons.math.SafeMath;
import org.l2j.gameserver.Announcements;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.data.dao.CastleDAO;
import org.l2j.gameserver.data.dao.CastleHiredGuardDAO;
import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.Warehouse;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.s2c.ExCastleState;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.templates.item.support.MerchantGuard;
import org.l2j.gameserver.utils.Log;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Castle extends Residence
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(Castle.class);

	private final IntObjectMap<MerchantGuard> _merchantGuards = new HashIntObjectMap<MerchantGuard>();

	private long _treasury;
	private long _collectedShops;

	private final NpcString _npcStringName;

	private ResidenceSide _residenceSide = ResidenceSide.NEUTRAL;

	private static final Skill LIGHT_SIDE_SKILL = SkillHolder.getInstance().getSkill(19032, 1); // Способности Света
	private static final Skill DARK_SIDE_SKILL = SkillHolder.getInstance().getSkill(19033, 1); // Способности Тьмы

	private Set<ItemInstance> _spawnMerchantTickets = new CopyOnWriteArraySet<ItemInstance>();

	public Castle(StatsSet set)
	{
		super(set);
		_npcStringName = NpcString.valueOf(1001000 + getId());
	}

	@Override
	public ResidenceType getType()
	{
		return ResidenceType.CASTLE;
	}

	// This method sets the castle owner; null here means give it back to NPC
	@Override
	public void changeOwner(Clan newOwner)
	{
		// Если клан уже владел каким-либо замком/крепостью, отбираем его.
		if(newOwner != null)
		{
			if(newOwner.getCastle() != 0)
			{
				Castle oldCastle = ResidenceHolder.getInstance().getResidence(Castle.class, newOwner.getCastle());
				if(oldCastle != null)
					oldCastle.changeOwner(null);
			}
		}

		Clan oldOwner = null;
		// Если этим замком уже кто-то владел, отбираем у него замок
		if(getOwnerId() > 0 && (newOwner == null || newOwner.getClanId() != getOwnerId()))
		{
			// Удаляем замковые скилы у старого владельца
			removeSkills();

			cancelCycleTask();

			oldOwner = getOwner();
			if(oldOwner != null)
			{
				// Переносим сокровищницу в вархауз старого владельца
				long amount = getTreasury();
				if(amount > 0)
				{
					Warehouse warehouse = oldOwner.getWarehouse();
					if(warehouse != null)
					{
						warehouse.addItem(Items.ADENA, amount);
						addToTreasuryNoTax(-amount, false);
						Log.add(getName() + "|" + -amount + "|Castle:changeOwner", "treasury");
					}
				}

				// Проверяем членов старого клана владельца, снимаем короны замков и корону лорда с лидера
				for(Player clanMember : oldOwner.getOnlineMembers(0))
					if(clanMember != null && clanMember.getInventory() != null)
						clanMember.getInventory().validateItems();

				// Отнимаем замок у старого владельца
				oldOwner.setHasCastle(0);
			}
		}

		setOwner(newOwner);

		removeFunctions();

		// Выдаем замок новому владельцу
		if (newOwner != null)
		{
			newOwner.setHasCastle(getId());
			newOwner.broadcastClanStatus(true, false, false);
		}

		// Выдаем замковые скилы новому владельцу
		rewardSkills();

		update();
	}

	// This method loads castle
	@Override
	protected void loadData()
	{
		_treasury = 0;

		CastleDAO.getInstance().select(this);
		CastleHiredGuardDAO.getInstance().load(this);
	}

	public void setTreasury(long t)
	{
		_treasury = t;
	}

	public long getCollectedShops()
	{
		return _collectedShops;
	}

	public void setCollectedShops(long value)
	{
		_collectedShops = value;
	}

	// This method add to the treasury
	/** Add amount to castle instance's treasury (warehouse). */
	public void addToTreasury(long amount, boolean shop)
	{
		if(getOwnerId() <= 0)
			return;

		if(amount == 0)
			return;

		double deleteAmount = 0.4;
		if(getId() == 3)
			deleteAmount = 0.75;
		else if(getId() == 6)
			deleteAmount = 0;

		amount = (long) Math.max(0, amount - amount * deleteAmount);

		if(amount > 1 && getId() != 5 && getId() != 8) // If current castle instance is not Aden or Rune
		{
			Castle royal = ResidenceHolder.getInstance().getResidence(Castle.class, getId() >= 7 ? 8 : 5);
			if(royal != null)
			{
				double royalTaxRate = 0.25;
				if(getId() == 3)
					royalTaxRate = 0.5;

				long royalTax = (long) (amount * royalTaxRate); // Find out what royal castle gets from the current castle instance's income

				if(royal.getOwnerId() > 0)
				{
					royal.addToTreasury(royalTax, shop); // Only bother to really add the tax to the treasury if not npc owned
					if(getId() == 5)
						Log.add("Aden|" + royalTax + "|Castle:adenTax", "treasury");
					else if(getId() == 8)
						Log.add("Rune|" + royalTax + "|Castle:runeTax", "treasury");
				}

				amount -= royalTax; // Subtract royal castle income from current castle instance's income
			}
		}

		addToTreasuryNoTax(amount, shop);
	}

	/** Add amount to castle instance's treasury (warehouse), no tax paying. */
	public void addToTreasuryNoTax(long amount, boolean shop)
	{
		if(getOwnerId() <= 0)
			return;

		if(amount == 0)
			return;

		// Add to the current treasury total.  Use "-" to substract from treasury
		_treasury = SafeMath.addAndLimit(_treasury, amount);

		if(shop)
			_collectedShops += amount;

		setJdbcState(JdbcEntityState.UPDATED);
		update();
	}

	public int getSellTaxPercent()
	{
		if(getResidenceSide() == ResidenceSide.LIGHT)
			return Config.LIGHT_CASTLE_SELL_TAX_PERCENT;
		else if(getResidenceSide() == ResidenceSide.DARK)
			return Config.DARK_CASTLE_SELL_TAX_PERCENT;
		return 0;
	}

	public double getSellTaxRate()
	{
		return getSellTaxPercent() / 100.;
	}

	public int getBuyTaxPercent()
	{
		if(getResidenceSide() == ResidenceSide.LIGHT)
			return Config.LIGHT_CASTLE_BUY_TAX_PERCENT;
		else if(getResidenceSide() == ResidenceSide.DARK)
			return Config.DARK_CASTLE_BUY_TAX_PERCENT;
		return 0;
	}

	public double getBuyTaxRate()
	{
		return getBuyTaxPercent() / 100.;
	}

	public long getTreasury()
	{
		return _treasury;
	}

	@Override
	public void update()
	{
		CastleDAO.getInstance().update(this);
	}

	public NpcString getNpcStringName()
	{
		return _npcStringName;
	}

	public void addMerchantGuard(MerchantGuard merchantGuard)
	{
		_merchantGuards.put(merchantGuard.getItemId(), merchantGuard);
	}

	public MerchantGuard getMerchantGuard(int itemId)
	{
		return _merchantGuards.get(itemId);
	}

	public IntObjectMap<MerchantGuard> getMerchantGuards()
	{
		return _merchantGuards;
	}

	public Set<ItemInstance> getSpawnMerchantTickets()
	{
		return _spawnMerchantTickets;
	}

	@Override
	public void startCycleTask()
	{}

	@Override
	public void setResidenceSide(ResidenceSide side, boolean onRestore)
	{
		if(!onRestore && _residenceSide == side)
			return;

		_residenceSide = side;

		removeSkills();
		switch(_residenceSide)
		{
			case LIGHT:
				removeSkill(DARK_SIDE_SKILL);
				addSkill(LIGHT_SIDE_SKILL);
				break;
			case DARK:
				removeSkill(LIGHT_SIDE_SKILL);
				addSkill(DARK_SIDE_SKILL);
				break;
			default:
				removeSkill(LIGHT_SIDE_SKILL);
				removeSkill(DARK_SIDE_SKILL);
				break;
		}
		rewardSkills();

		if(!onRestore)
		{
			setJdbcState(JdbcEntityState.UPDATED);
			update();
		}
	}

	@Override
	public ResidenceSide getResidenceSide()
	{
		return _residenceSide;
	}

	@Override
	public void broadcastResidenceState()
	{
		Announcements.announceToAll(new ExCastleState(this));
	}
}