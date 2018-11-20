package ai.quests._227_TestOfTheReformer;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.Fighter;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.utils.PositionUtils;

/**
 * @author Bonux
**/
public class OlMahumBetrayer extends Fighter
{
	private Creature _fromNpc = null;

	public OlMahumBetrayer(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		onEvtArrived();
	}

	@Override
	protected void onEvtArrived()
	{
		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		getActor().setRunning();

		final int x = getActor().getX() + (int) (2000 * Math.cos(180));
		final int y = getActor().getY() + (int) (2000 * Math.sin(180));
		getActor().moveToLocation(GeoEngine.moveCheck(getActor().getX(), getActor().getY(), getActor().getZ(), x, y, getActor().getGeoIndex()), 0, false);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		//
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		//
	}

	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		//
	}
}