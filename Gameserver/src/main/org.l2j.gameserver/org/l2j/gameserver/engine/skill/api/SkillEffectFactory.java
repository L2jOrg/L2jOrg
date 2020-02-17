package org.l2j.gameserver.engine.skill.api;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

/**
 * @author JoeAlisson
 */
public interface SkillEffectFactory {

   AbstractEffect create(StatsSet data);

   String effectName();
}
