package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Creature;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.npc.NpcTemplate;

public class ConditionTargetRace extends Condition
{
	private final int _race;

	public ConditionTargetRace(String race)
	{
		// Раса определяется уровнем(1-25) скила 4416
		if(race.equalsIgnoreCase("Undead"))
			_race = 1;
		else if(race.equalsIgnoreCase("MagicCreatures"))
			_race = 2;
		else if(race.equalsIgnoreCase("Beasts"))
			_race = 3;
		else if(race.equalsIgnoreCase("Animals"))
			_race = 4;
		else if(race.equalsIgnoreCase("Plants"))
			_race = 5;
		else if(race.equalsIgnoreCase("Humanoids"))
			_race = 6;
		else if(race.equalsIgnoreCase("Spirits"))
			_race = 7;
		else if(race.equalsIgnoreCase("Angels"))
			_race = 8;
		else if(race.equalsIgnoreCase("Demons"))
			_race = 9;
		else if(race.equalsIgnoreCase("Dragons"))
			_race = 10;
		else if(race.equalsIgnoreCase("Giants"))
			_race = 11;
		else if(race.equalsIgnoreCase("Bugs"))
			_race = 12;
		else if(race.equalsIgnoreCase("Fairies"))
			_race = 13;
		else if(race.equalsIgnoreCase("Humans"))
			_race = 14;
		else if(race.equalsIgnoreCase("Elves"))
			_race = 15;
		else if(race.equalsIgnoreCase("DarkElves"))
			_race = 16;
		else if(race.equalsIgnoreCase("Orcs"))
			_race = 17;
		else if(race.equalsIgnoreCase("Dwarves"))
			_race = 18;
		else if(race.equalsIgnoreCase("Others"))
			_race = 19;
		else if(race.equalsIgnoreCase("NonLivingBeings"))
			_race = 20;
		else if(race.equalsIgnoreCase("SiegeWeapons"))
			_race = 21;
		else if(race.equalsIgnoreCase("DefendingArmy"))
			_race = 22;
		else if(race.equalsIgnoreCase("Mercenaries"))
			_race = 23;
		else if(race.equalsIgnoreCase("UnknownCreature"))
			_race = 24;
		else if(race.equalsIgnoreCase("Kamael"))
			_race = 25;
		else
			throw new IllegalArgumentException("ConditionTargetRace: Invalid race name: " + race);
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		return target != null && target.getTemplate() != null && (target.isSummon() || target.isNpc()) && _race == ((NpcTemplate) target.getTemplate()).getRace();
	}
}