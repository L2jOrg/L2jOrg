package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * AI боса Core:
 * <br> - Бубнит при атаке и смерти.
 * <br> - При смерти играет музыку и спаунит обратные порталы, которые удаляются через 15 минут
 *
 * @author SYS
 * @reworked by Bonux
 */
public class Core extends Fighter
{
	private boolean _firstTimeAttacked = true;
	private static final int TELEPORTATION_CUBIC_ID = 31843;
	private static final Location CUBIC_1_POSITION = new Location(16502, 110165, -6394, 0);
	private static final Location CUBIC_2_POSITION = new Location(18948, 110165, -6394, 0);
	private static final int CUBIC_DESPAWN_TIME = 15 * 60 * 1000; // 15 min

	public Core(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(_firstTimeAttacked)
		{
			Functions.npcSay(actor, NpcString.A_NONPERMITTED_TARGET_HAS_BEEN_DISCOVERED);
			Functions.npcSay(actor, NpcString.INTRUDER_REMOVAL_SYSTEM_INITIATED);
			_firstTimeAttacked = false;
		}
		else if(Rnd.chance(1))
			Functions.npcSay(actor, NpcString.REMOVING_INTRUDERS);

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		actor.broadcastPacket(new PlaySoundPacket(PlaySoundPacket.Type.MUSIC, "BS02_D", 1, 0, actor.getLoc()));
		Functions.npcSay(actor, NpcString.A_FATAL_ERROR_HAS_OCCURRED);
		Functions.npcSay(actor, NpcString.SYSTEM_IS_BEING_SHUT_DOWN);
		Functions.npcSay(actor, NpcString._DOT_DOT_DOT_DOT_DOT_DOT_);

		NpcUtils.spawnSingle(TELEPORTATION_CUBIC_ID, CUBIC_1_POSITION, actor.getReflection(), CUBIC_DESPAWN_TIME);
		NpcUtils.spawnSingle(TELEPORTATION_CUBIC_ID, CUBIC_2_POSITION, actor.getReflection(), CUBIC_DESPAWN_TIME);

		_firstTimeAttacked = true;

		super.onEvtDead(killer);
	}
}