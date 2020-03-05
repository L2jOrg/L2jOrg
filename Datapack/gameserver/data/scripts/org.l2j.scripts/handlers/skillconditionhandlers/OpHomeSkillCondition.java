package handlers.skillconditionhandlers;

import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.ResidenceType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpHomeSkillCondition implements SkillCondition {

	public final ResidenceType type;

	private OpHomeSkillCondition(ResidenceType type) {
		this.type = type;
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (isPlayer(caster)) {
			final Clan clan = caster.getActingPlayer().getClan();
			if (nonNull(clan)) {
				return switch (type) {
					case CASTLE -> nonNull(CastleManager.getInstance().getCastleByOwner(clan));
					case FORTRESS -> nonNull(FortDataManager.getInstance().getFortByOwner(clan));
					case CLANHALL -> nonNull(ClanHallManager.getInstance().getClanHallByClan(clan));
				};
			}
		}
		return false;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new OpHomeSkillCondition(parseEnum(xmlNode.getAttributes(), ResidenceType.class, "Type"));
		}

		@Override
		public String conditionName() {
			return "residence";
		}
	}
}
