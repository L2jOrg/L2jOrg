package org.l2j.gameserver.model.quest.startcondition.impl;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.model.quest.startcondition.ICheckStartCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Ragnarok
 * @date : 02.04.12  21:50
 */
public class PlayerRaceCondition implements ICheckStartCondition
{
	private final boolean _classRace;
	private final List<Race> _races = new ArrayList<Race>(Race.VALUES.length);

	public PlayerRaceCondition(boolean human, boolean elf, boolean delf, boolean orc, boolean dwarf)
	{
		_classRace = false;
		if(human)
			_races.add(Race.HUMAN);
		if(elf)
			_races.add(Race.ELF);
		if(delf)
			_races.add(Race.DARKELF);
		if(orc)
			_races.add(Race.ORC);
		if(dwarf)
			_races.add(Race.DWARF);
	}

	public PlayerRaceCondition(boolean classRace, Race[] races)
	{
		_classRace = classRace;
		for(Race race : races)
			_races.add(race);
	}

	@Override
	public boolean checkCondition(Player player)
	{
		if(_races.isEmpty())
			return true;

		Race race = player.getClassId().getRace();
		if(!_classRace || race == null)
			race = player.getRace();

		return _races.contains(race);
	}
}