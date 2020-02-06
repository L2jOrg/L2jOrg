package handlers.skillconditionhandlers;

import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 */
public class OpCheckResidenceSkillCondition implements ISkillCondition {
	public final List<Integer> residencesId;
	public final boolean isWithin;
	
	public OpCheckResidenceSkillCondition(StatsSet params) {
		residencesId = params.getList("residencesId", Integer.class);
		isWithin = params.getBoolean("isWithin");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (isPlayer(caster)) {
			final Clan clan = caster.getActingPlayer().getClan();
			if (nonNull(clan)) {
				final ClanHall clanHall = ClanHallData.getInstance().getClanHallByClan(clan);
				if (nonNull(clanHall)) {
					return isWithin == residencesId.contains(clanHall.getResidenceId());
				}
			}
		}
		return false;
	}
}