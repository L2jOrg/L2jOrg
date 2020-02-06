package handlers.skillconditionhandlers;

import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.enums.ResidenceType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpHomeSkillCondition implements ISkillCondition {

	public final ResidenceType type;

	public OpHomeSkillCondition(StatsSet params) {
		type = params.getEnum("type", ResidenceType.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (isPlayer(caster)) {
			final Clan clan = caster.getActingPlayer().getClan();
			if (nonNull(clan)) {
				return switch (type) {
					case CASTLE -> nonNull(CastleManager.getInstance().getCastleByOwner(clan));
					case FORTRESS -> nonNull(FortDataManager.getInstance().getFortByOwner(clan));
					case CLANHALL -> nonNull(ClanHallData.getInstance().getClanHallByClan(clan));
				};
			}
		}
		return false;
	}
}
