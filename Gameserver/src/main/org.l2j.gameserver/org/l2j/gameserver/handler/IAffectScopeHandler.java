package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.targets.AffectScope;

import java.util.function.Consumer;

/**
 * @author Nik
 */
public interface IAffectScopeHandler {

    void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action);

    Enum<AffectScope> getAffectScopeType();
}
