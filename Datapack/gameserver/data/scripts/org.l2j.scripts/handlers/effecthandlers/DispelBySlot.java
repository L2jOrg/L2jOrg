package handlers.effecthandlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.Skill;

import static java.util.Objects.nonNull;

/**
 * Dispel By Slot effect implementation.
 * @author Gnacik, Zoey76, Adry_85
 */
public final class DispelBySlot extends AbstractEffect {

	private final String dispel;
	private final Map<AbnormalType, Short> dispelAbnormals;
	
	public DispelBySlot(StatsSet params) {
		dispel = params.getString("dispel");
		if (Util.isNotEmpty(dispel)) {
			dispelAbnormals = new HashMap<>();
			for (String ngtStack : dispel.split(";")) {
				final String[] ngt = ngtStack.split(",");
				dispelAbnormals.put(AbnormalType.getAbnormalType(ngt[0]), Short.parseShort(ngt[1]));
			}
		} else {
			dispelAbnormals = Collections.emptyMap();
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DISPEL_BY_SLOT;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (dispelAbnormals.isEmpty()) {
			return;
		}
		
		// Continue only if target has any of the abnormals. Save useless cycles.
		if (effected.getEffectList().hasAbnormalType(dispelAbnormals.keySet())) {
			// Dispel transformations (buff and by GM)
			final Short transformToDispel = dispelAbnormals.get(AbnormalType.TRANSFORM);
			if (nonNull(transformToDispel) && ((transformToDispel == effected.getTransformationId()) || (transformToDispel < 0))) {
				effected.stopTransformation(true);
			}
			
			effected.getEffectList().stopEffects(info -> {
				// We have already dealt with transformation from above.
				if (info.isAbnormalType(AbnormalType.TRANSFORM)) {
					return false;
				}
				
				final Short abnormalLevel = dispelAbnormals.get(info.getSkill().getAbnormalType());
				return (abnormalLevel != null) && ((abnormalLevel < 0) || (abnormalLevel >= info.getSkill().getAbnormalLvl()));
			}, true, true);
		}
	}
}
