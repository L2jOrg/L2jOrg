package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.Location;

public class Recall extends Skill
{
	private final int _townId;
	private final boolean _clanhall;
	private final boolean _castle;
	private final boolean _toflag;
	private final Location _loc;

	public Recall(StatsSet set)
	{
		super(set);
		_townId = set.getInteger("townId", 0);
		_clanhall = set.getBool("clanhall", false);
		_castle = set.getBool("castle", false);
		_toflag = set.getBool("to_flag", false);
		String cords = set.getString("loc", null);
		if(cords != null)
			_loc = Location.parseLoc(cords);
		else
			_loc = null;
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		// BSOE в кланхолл/замок работает только при наличии оного
		if(getHitTime() == 200)
		{
			Player player = activeChar.getPlayer();
			if(_clanhall)
			{
				if(player.getClan() == null || player.getClan().getHasHideout() == 0)
				{
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
					return false;
				}
			}
			else if(_castle)
			{
				if(player.getClan() == null || player.getClan().getCastle() == 0)
				{
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
					return false;
				}
			}
		}

		if(activeChar.isPlayer())
		{
			Player p = (Player) activeChar;
			if(_toflag && p.bookmarkLocation == null)
				return false;

			if(p.getActiveWeaponFlagAttachment() != null)
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
				return false;
			}
			if(!p.isInDuel() && p.getTeam() != TeamType.NONE)
			{
				activeChar.sendMessage(new CustomMessage("common.RecallInDuel"));
				return false;
			}
			if(p.isInOlympiadMode())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_SKILL_IN_A_GRAND_OLYMPIAD_MATCH);
				return false;
			}
			
			for(Event e : p.getEvents())
			{
				if(!e.canUseTeleport(p))
				{
					if(getItemConsumeId() > 0)
						activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
					else
						activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
					return false;
				}
			}

			if(p.isEscapeBlocked())
			{
				if(getItemConsumeId() > 0)
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				else
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}
		}

		if(activeChar.isInZone(ZoneType.no_escape) || _townId > 0 && activeChar.getReflection() != null && activeChar.getReflection().getCoreLoc() != null)
		{
			if(activeChar.isPlayer())
				activeChar.sendMessage(new CustomMessage("l2s.gameserver.skills.skillclasses.Recall.Here"));
			return false;
		}

		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!target.isPlayer())
			return;

		final Player player = target.getPlayer();
		if(player == null)
			return;

		if(!player.getPlayerAccess().UseTeleport)
			return;

		if(player.isInRange(new Location(-114598,-249431,-2984), 5000))
			return;

		if(player.getActiveWeaponFlagAttachment() != null)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return;
		}

		if(player.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD);
			return;
		}
				
		if(player.isInObserverMode())
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return;
		}

		for(Event e : player.getEvents())
		{
			if(!e.canUseTeleport(player))
			{
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return;
			}
		}

		if(!player.isInDuel() && player.getTeam() != TeamType.NONE)
		{
			activeChar.sendMessage(new CustomMessage("common.RecallInDuel"));
			return;
		}

		if(isHandler())
		{
			//TODO: переделать SOE по TownId на SOE по Loc_id
			if(getItemConsumeId() == 7127) // hardin's academy
			{
				player.teleToLocation(105918, 109759, -3207, ReflectionManager.MAIN);
				return;
			}
			if(getItemConsumeId() == 7130) // ivory
			{
				player.teleToLocation(85475, 16087, -3672, ReflectionManager.MAIN);
				return;
			}
			if(getItemConsumeId() == 7618)
			{
				player.teleToLocation(149864, -81062, -5618, ReflectionManager.MAIN);
				return;
			}
			if(getItemConsumeId() == 7619)
			{
				player.teleToLocation(108275, -53785, -2524, ReflectionManager.MAIN);
				return;
			}
		}

		if(_loc != null)
		{
			player.teleToLocation(_loc, ReflectionManager.MAIN);
			return;
		}

		//FIXME [G1ta0] перенести координаты в скиллы
		switch(_townId)
		// To town by Id
		{
			case 1: // Talking Island
				player.teleToLocation(-83990, 243336, -3700, ReflectionManager.MAIN);
				return;
			case 2: // Elven Village
				player.teleToLocation(45576, 49412, -2950, ReflectionManager.MAIN);
				return;
			case 3: // Dark Elven Village
				player.teleToLocation(12501, 16768, -4500, ReflectionManager.MAIN);
				return;
			case 4: // Orc Village
				player.teleToLocation(-44884, -115063, -80, ReflectionManager.MAIN);
				return;
			case 5: // Dwarven Village
				player.teleToLocation(115790, -179146, -890, ReflectionManager.MAIN);
				return;
			case 6: // Town of Gludio
				player.teleToLocation(-14279, 124446, -3000, ReflectionManager.MAIN);
				return;
			case 7: // Gludin Village
				player.teleToLocation(-82909, 150357, -3000, ReflectionManager.MAIN);
				return;
			case 8: // Town of Dion
				player.teleToLocation(19025, 145245, -3107, ReflectionManager.MAIN);
				return;
			case 9: // Town of Giran
				player.teleToLocation(82272, 147801, -3350, ReflectionManager.MAIN);
				return;
			case 10: // Town of Oren
				player.teleToLocation(82323, 55466, -1480, ReflectionManager.MAIN);
				return;
			case 11: // Town of Aden
				player.teleToLocation(144526, 24661, -2100, ReflectionManager.MAIN);
				return;
			case 12: // Hunters Village
				player.teleToLocation(117189, 78952, -2210, ReflectionManager.MAIN);
				return;
			//case 13: // Heine
			//	player.teleToLocation(110768, 219824, -3624, ReflectionManager.MAIN);
			//	return;
			//case 14: // Rune Township
			//	player.teleToLocation(43536, -50416, -800, ReflectionManager.MAIN);
			//	return;
			//case 15: // Town of Goddard
			//	player.teleToLocation(148288, -58304, -2979, ReflectionManager.MAIN);
			//	return;
			//case 16: // Town of Schuttgart
			//	player.teleToLocation(87776, -140384, -1536, ReflectionManager.MAIN);
			//	return;
			//case 17: // Kamael Village
			//	player.teleToLocation(-117081, 44171, 507, ReflectionManager.MAIN);
			//	return;
			//case 18: // Primeval Isle
			//	player.teleToLocation(10568, -24600, -3648, ReflectionManager.MAIN);
			//	return;
			case 19: // Floran Village
				player.teleToLocation(17144, 170156, -3502, ReflectionManager.MAIN);
				return;
			//case 20: // Hellbound
			//	player.teleToLocation(-28807, 256404, -2192, ReflectionManager.MAIN);
			//	return;
			//case 21: // Keucereus Alliance Base
			//	player.teleToLocation(-184200, 243080, 1568, ReflectionManager.MAIN);
			//	return;
			//case 22: // Steel Citadel
			//	player.teleToLocation(8976, 252416, -1928, ReflectionManager.MAIN);
			//	return;
			//case 23: // Town of Arcan
			//	player.teleToLocation(207559, 86429, -1000, ReflectionManager.MAIN);
			//	return;
			//case 24: // Vernon Village
			//	player.teleToLocation(-80403, 247853, -3496, ReflectionManager.MAIN);
			//	return;
		}

		if(_castle) // To castle
		{
			player.teleToCastle();
			return;
		}

		if(_clanhall) // to clanhall
		{
			player.teleToClanhall();
			return;
		}

		if(_toflag)
		{
			player.teleToLocation(player.bookmarkLocation, ReflectionManager.MAIN);
			player.bookmarkLocation = null;
			return;
		}

		player.teleToClosestTown();
	}
}