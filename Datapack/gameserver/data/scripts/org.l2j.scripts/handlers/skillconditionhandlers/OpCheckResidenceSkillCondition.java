package handlers.skillconditionhandlers;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.entity.ClanHall;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @JoeAlisson
 */
public class OpCheckResidenceSkillCondition implements SkillCondition {
	public final IntSet residencesId;
	public final boolean isWithin;

	private OpCheckResidenceSkillCondition(IntSet ids, boolean isWithin) {
		this.residencesId = ids;
		this.isWithin = isWithin;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (isPlayer(caster)) {
			final Clan clan = caster.getActingPlayer().getClan();
			if (nonNull(clan)) {
				final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByClan(clan);
				if (nonNull(clanHall)) {
					return isWithin == residencesId.contains(clanHall.getId());
				}
			}
		}
		return false;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var isWithin = parseBoolean(xmlNode.getAttributes(), "is-within");
			IntSet ids = parseIntSet(xmlNode.getFirstChild());
			return new OpCheckResidenceSkillCondition(ids, isWithin);
		}

		@Override
		public String conditionName() {
			return "check-residence";
		}
	}
}