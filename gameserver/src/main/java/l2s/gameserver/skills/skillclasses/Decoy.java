package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

/**
 * 
 * @author n0nam3
 * @date 23/07/2010 19:14
 */
public class Decoy extends Skill
{
	private final int _npcId;
	private final int _lifeTime;
	private final int _numbersOfDecoys;

	public Decoy(StatsSet set)
	{
		super(set);

		_npcId = set.getInteger("npcId", 0);
		_lifeTime = set.getInteger("lifeTime", 1200) * 1000;
		_numbersOfDecoys = set.getInteger("decoyCount", 1);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(activeChar.isAlikeDead() || !activeChar.isPlayer() || activeChar != target) // only TARGET_SELF
			return false;

		if(_npcId <= 0)
			return false;

		/* need correct
		if(activeChar.getServitor() != null || activeChar.getPlayer().isMounted())
		{
			activeChar.getPlayer().sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
			return false;
		}
		 */
		return true;
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(!activeChar.isPlayer())
			return;

		Player player = activeChar.getPlayer();

		NpcTemplate DecoyTemplate = NpcHolder.getInstance().getTemplate(getNpcId());
		for(int i = 0; i < _numbersOfDecoys; i++)
		{
			DecoyInstance decoy = new DecoyInstance(IdFactory.getInstance().getNextId(), DecoyTemplate, player, _lifeTime);
			decoy.setCurrentHp(decoy.getMaxHp(), false);
			decoy.setCurrentMp(decoy.getMaxMp());
			decoy.setHeading(player.getHeading());
			decoy.setReflection(player.getReflection());

			player.addDecoy(decoy);

			decoy.spawnMe(Location.findAroundPosition(player, 50, 70));

			decoy.transferOwnerBuffs();
		}
	}
}