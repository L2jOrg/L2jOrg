package org.l2j.gameserver.model.actor.instances.player;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.dao.CharacterHennaDAO;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExPeriodicHenna;
import org.l2j.gameserver.network.l2.s2c.HennaInfoPacket;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.templates.HennaTemplate;
import org.l2j.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class HennaList
{
	public static final int MAX_SIZE = 3;

	private static final Logger _log = LoggerFactory.getLogger(HennaList.class);

	private static final int MAX_STAT_VALUE = 15;

	private List<Henna> _hennaList = Collections.emptyList();
	private Henna _premiumHenna = null;
	private int _str, _int, _dex, _men, _wit, _con;
	private Future<?> _removeTask;

	private final Player _owner;
	private final TIntObjectMap<SkillEntry> _skills = new TIntObjectHashMap<SkillEntry>();

	public HennaList(Player owner)
	{
		_owner = owner;
	}

	public void restore()
	{
		_hennaList = new ArrayList<Henna>();
		_premiumHenna = null;

		List<Henna> hennas = CharacterHennaDAO.getInstance().select(_owner);
		for(Henna henna : hennas)
		{
			if(henna.isPremium())
			{
				if(_premiumHenna != null)
					_log.warn(this + ": Contains more than one premium henna!");

				if(henna.getTemplate().getPeriod() == 0)
					_log.warn(this + ": Contains no premium henna in premium slot!");

				_premiumHenna = henna;
			}
			else
				_hennaList.add(henna);
		}

		Collections.sort(_hennaList);

		if(_hennaList.size() > MAX_SIZE)
		{
			_log.warn(this + ": Contains more than three henna's!");

			for(int i = MAX_SIZE; i < _hennaList.size(); i++)
				_hennaList.remove(i);
		}

		refreshStats(false);
		_owner.sendPacket(new HennaInfoPacket(_owner));

		stopHennaRemoveTask();
		startHennaRemoveTask();
	}

	public Henna get(int symbolId)
	{
		for(Henna henna : values(true))
		{
			if(henna.getTemplate().getSymbolId() == symbolId)
				return henna;
		}
		return null;
	}

	public int size()
	{
		return _hennaList.size();
	}

	public int getFreeSize()
	{
		return Math.max(0, MAX_SIZE - size());
	}

	public Henna[] values(boolean withPremium)
	{
		if(!withPremium)
			return _hennaList.toArray(new Henna[_hennaList.size()]);

		List<Henna> hennas = new ArrayList<Henna>(_hennaList);
		if(_premiumHenna != null)
			hennas.add(_premiumHenna);

		return hennas.toArray(new Henna[hennas.size()]);
	}

	public Henna getPremiumHenna()
	{
		return _premiumHenna;
	}

	public boolean isFull()
	{
		return getFreeSize() == 0;
	}

	public boolean canAdd(Henna henna)
	{
		if(!henna.isPremium() && isFull())
			return false;

		if(henna.isPremium() && (!Config.EX_USE_PREMIUM_HENNA_SLOT || _premiumHenna != null))
			return false;

		return true;
	}

	public boolean add(Henna henna)
	{
		if(!canAdd(henna))
			return false;

		if(CharacterHennaDAO.getInstance().insert(_owner, henna))
		{
			if(!henna.isPremium())
			{
				_hennaList.add(henna);
				Collections.sort(_hennaList);
			}
			else
				_premiumHenna = henna;

			if(refreshStats(true))
				_owner.sendSkillList();

			return true;
		}
		return false;
	}

	public boolean remove(Henna henna)
	{
		if(!remove0(henna))
			return false;

		if(refreshStats(true))
			_owner.sendSkillList();

		if(!henna.isPremium())
		{
			long removeCount = henna.getTemplate().getRemoveCount();
			if(removeCount > 0)
				ItemFunctions.addItem(_owner, henna.getTemplate().getDyeId(), henna.getTemplate().getRemoveCount(), true);
		}

		return true;
	}

	private boolean remove0(Henna henna)
	{
		if(!_hennaList.remove(henna))
		{
			if(_premiumHenna != henna)
				return false;

			stopHennaRemoveTask();
			_premiumHenna = null;
		}

		Collections.sort(_hennaList);

		return CharacterHennaDAO.getInstance().delete(_owner, henna);
	}

	public boolean isActive(Henna henna)
	{
		if(!henna.getTemplate().isForThisClass(_owner))
			return false;

		if(henna.isPremium() && !Config.EX_USE_PREMIUM_HENNA_SLOT)
			return false;

		return true;
	}

	public boolean refreshStats(boolean send)
	{
		_int = 0;
		_str = 0;
		_con = 0;
		_men = 0;
		_wit = 0;
		_dex = 0;

		boolean updateSkillList = false;
		for(int skillId : _skills.keys())
		{
			if(_owner.removeSkill(skillId, false) != null)
				updateSkillList = true;
		}

		_skills.clear();

		for(Henna henna : values(true))
		{
			if(!isActive(henna))
				continue;

			HennaTemplate template = henna.getTemplate();

			_int += template.getStatINT();
			_str += template.getStatSTR();
			_men += template.getStatMEN();
			_con += template.getStatCON();
			_wit += template.getStatWIT();
			_dex += template.getStatDEX();

			for(TIntIntIterator iterator = template.getSkills().iterator(); iterator.hasNext();)
			{
				iterator.advance();

				SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(iterator.key(), iterator.value());
				if(skillEntry == null)
					continue;

				SkillEntry tempSkillEntry = _skills.get(skillEntry.getId());
				if(tempSkillEntry == null || tempSkillEntry.getLevel() < skillEntry.getLevel())
					_skills.put(skillEntry.getId(), skillEntry);
			}
		}

		for(SkillEntry skillEntry : _skills.valueCollection())
			_owner.addSkill(skillEntry, false);

		if(!_skills.isEmpty())
			updateSkillList = true;

		_int = Math.min(_int, MAX_STAT_VALUE);
		_str = Math.min(_str, MAX_STAT_VALUE);
		_con = Math.min(_con, MAX_STAT_VALUE);
		_men = Math.min(_men, MAX_STAT_VALUE);
		_wit = Math.min(_wit, MAX_STAT_VALUE);
		_dex = Math.min(_dex, MAX_STAT_VALUE);

		if(send)
		{
			_owner.sendPacket(new HennaInfoPacket(_owner));
			_owner.sendUserInfo(true);
		}

		return updateSkillList;
	}

	public void stopHennaRemoveTask()
	{
		if(_removeTask != null)
		{
			_removeTask.cancel(false);
			_removeTask = null;
		}
	}

	private void startHennaRemoveTask()
	{
		if(_premiumHenna == null)
			return;

		if(_removeTask != null)
			return;

		_removeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> 
		{
			if(_premiumHenna.getLeftTime() <= 0)
			{
				if(remove0(_premiumHenna))
				{
					if(refreshStats(true))
						_owner.sendSkillList();
				}
			}
			else
				_owner.sendPacket(new ExPeriodicHenna(_owner));
		}, 0, 60000L);
	}

	public int getINT()
	{
		return _int;
	}

	public int getSTR()
	{
		return _str;
	}

	public int getCON()
	{
		return _con;
	}

	public int getMEN()
	{
		return _men;
	}

	public int getWIT()
	{
		return _wit;
	}

	public int getDEX()
	{
		return _dex;
	}

	@Override
	public String toString()
	{
		return "HennaList[owner=" + _owner.getName() + "]";
	}
}