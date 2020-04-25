package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public class SkillUseHolder extends SkillHolder {
    private final Item _item;
    private final boolean _ctrlPressed;
    private final boolean _shiftPressed;

    public SkillUseHolder(Skill skill, Item item, boolean ctrlPressed, boolean shiftPressed) {
        super(skill);
        _item = item;
        _ctrlPressed = ctrlPressed;
        _shiftPressed = shiftPressed;
    }

    public Item getItem() {
        return _item;
    }

    public boolean isCtrlPressed() {
        return _ctrlPressed;
    }

    public boolean isShiftPressed() {
        return _shiftPressed;
    }
}
