package l2s.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.StatsSet;

public class ExtractStone extends Skill
{
	private final static int ExtractScrollSkill = 2630;
	private final static int ExtractedCoarseRedStarStone = 13858;
	private final static int ExtractedCoarseBlueStarStone = 13859;
	private final static int ExtractedCoarseGreenStarStone = 13860;

	private final static int ExtractedRedStarStone = 14009;
	private final static int ExtractedBlueStarStone = 14010;
	private final static int ExtractedGreenStarStone = 14011;

	private final static int RedStarStone1 = 18684;
	private final static int RedStarStone2 = 18685;
	private final static int RedStarStone3 = 18686;

	private final static int BlueStarStone1 = 18687;
	private final static int BlueStarStone2 = 18688;
	private final static int BlueStarStone3 = 18689;

	private final static int GreenStarStone1 = 18690;
	private final static int GreenStarStone2 = 18691;
	private final static int GreenStarStone3 = 18692;

	private final static int FireEnergyCompressionStone = 14015;
	private final static int WaterEnergyCompressionStone = 14016;
	private final static int WindEnergyCompressionStone = 14017;
	private final static int EarthEnergyCompressionStone = 14018;
	private final static int DarknessEnergyCompressionStone = 14019;
	private final static int SacredEnergyCompressionStone = 14020;

	private final static int SeedFire = 18679;
	private final static int SeedWater = 18678;
	private final static int SeedWind = 18680;
	private final static int SeedEarth = 18681;
	private final static int SeedDarkness = 18683;
	private final static int SeedDivinity = 18682;

	private List<Integer> _npcIds = new ArrayList<Integer>();

	public ExtractStone(StatsSet set)
	{
		super(set);
		StringTokenizer st = new StringTokenizer(set.getString("npcIds", ""), ";");
		while(st.hasMoreTokens())
			_npcIds.add(Integer.valueOf(st.nextToken()));
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(target == null || !target.isNpc() || getItemId(target.getNpcId()) == 0)
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		if(!_npcIds.isEmpty() && !_npcIds.contains(new Integer(target.getNpcId())))
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		return true;
	}

	/**
	 * Возвращает ID предмета получаемого из npcId.
	 * @return
	 */
	private int getItemId(int npcId)
	{
		switch(npcId)
		{
			case RedStarStone1:
			case RedStarStone2:
			case RedStarStone3:
				if(getId() == ExtractScrollSkill)
					return ExtractedCoarseRedStarStone;
				return ExtractedRedStarStone;
			case BlueStarStone1:
			case BlueStarStone2:
			case BlueStarStone3:
				if(getId() == ExtractScrollSkill)
					return ExtractedCoarseBlueStarStone;
				return ExtractedBlueStarStone;
			case GreenStarStone1:
			case GreenStarStone2:
			case GreenStarStone3:
				if(getId() == ExtractScrollSkill)
					return ExtractedCoarseGreenStarStone;
				return ExtractedGreenStarStone;
			case SeedFire:
				return FireEnergyCompressionStone;
			case SeedWater:
				return WaterEnergyCompressionStone;
			case SeedWind:
				return WindEnergyCompressionStone;
			case SeedEarth:
				return EarthEnergyCompressionStone;
			case SeedDarkness:
				return DarknessEnergyCompressionStone;
			case SeedDivinity:
				return SacredEnergyCompressionStone;
			default:
				return 0;
		}
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		Player player = activeChar.getPlayer();
		if(player == null)
			return;

		if(getItemId(target.getNpcId()) <= 0)
			return;

		final long count = getId() == ExtractScrollSkill ? 1 : Math.min(10, Rnd.get((int) (getLevel() * player.getRateQuestsDrop() + 1)));
		final int itemId = getItemId(target.getNpcId());
		if(count > 0)
		{
			player.getInventory().addItem(itemId, count);
			player.sendPacket(new PlaySoundPacket(Quest.SOUND_ITEMGET));
			player.sendPacket(SystemMessagePacket.obtainItems(itemId, count, 0));
			player.sendChanges();
		}
		else
			player.sendPacket(SystemMsg.THE_COLLECTION_HAS_FAILED);

		target.doDie(player);
	}
}