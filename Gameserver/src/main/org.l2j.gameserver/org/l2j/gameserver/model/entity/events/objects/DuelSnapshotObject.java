package org.l2j.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 2:17/26.06.2011
 */
public class DuelSnapshotObject
{
	private static final long serialVersionUID = 1L;
	private final TeamType _team;
	private Player _player;
	//
	private int _classIndex;
	private Location _returnLoc;
	private double _currentHp;
	private double _currentMp;
	private double _currentCp;
	private List<Abnormal> _abnormals = Collections.emptyList();

	private boolean _isDead;

	public DuelSnapshotObject(Player player, TeamType team, boolean store)
	{
		_player = player;
		_team = team;
		if(store)
			store();
	}

	public void store()
	{
		_classIndex = _player.getActiveSubClass().getIndex();
		_returnLoc = _player.getStablePoint() == null ? _player.getReflection().getReturnLoc() == null ? _player.getLoc() : _player.getReflection().getReturnLoc() : _player.getStablePoint();
		_currentCp = _player.getCurrentCp();
		_currentHp = _player.getCurrentHp();
		_currentMp = _player.getCurrentMp();
		_abnormals = new ArrayList<Abnormal>(_player.getAbnormalList().values());
	}

	public void restore()
	{
		if(_player == null)
			return;

		for(Abnormal abnormal : _player.getAbnormalList())
		{
			if(!abnormal.isOffensive())
				continue;

			if(_abnormals.contains(abnormal))
				continue;

			abnormal.exit();
		}

		if(_classIndex == _player.getActiveSubClass().getIndex()) // если саб был сменен во время дуэли бафы не восстанавливаем
		{
			_player.setCurrentCp(_currentCp);
			_player.setCurrentHpMp(_currentHp, _currentMp);
		}
		else
		{
			_player.setCurrentCp(_player.getMaxCp());
			_player.setCurrentHpMp(_player.getMaxHp(), _player.getMaxMp());
		}
	}

	public void teleportBack()
	{
		if(_player == null)
			return;

		_player.setStablePoint(null);

		ThreadPoolManager.getInstance().schedule(()->
		{
			_player.getFlags().getFrozen().stop();
			_player.teleToLocation(_returnLoc, ReflectionManager.MAIN);
		}, 5000L);
	}

	public void blockUnblock()
	{
		if(_player == null)
			return;

		_player.block();
		final List<Servitor> servitors = _player.getServitors();
		for(Servitor servitor : servitors)
			servitor.block();

		ThreadPoolManager.getInstance().schedule(()->
		{
			_player.unblock();
			for (Servitor servitor : servitors)
				servitor.unblock();

		}, 3000L);
	}

	public Player getPlayer()
	{
		return _player;
	}

	public boolean isDead()
	{
		return _isDead;
	}

	public void setDead()
	{
		_isDead = true;
	}

	public Location getLoc()
	{
		return _returnLoc;
	}

	public TeamType getTeam()
	{
		return _team;
	}

	public Location getReturnLoc()
	{
		return _returnLoc;
	}

	public void clear()
	{
		_player = null;
	}
}