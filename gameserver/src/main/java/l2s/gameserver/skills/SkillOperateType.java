package l2s.gameserver.skills;

/**
 * This enum class holds the skill operative types:
 * <ul>
 * <li>A1</li>
 * <li>A2</li>
 * <li>A3</li>
 * <li>A4</li>
 * <li>CA1</li>
 * <li>CA5</li>
 * <li>DA1</li>
 * <li>DA2</li>
 * <li>P</li>
 * <li>T</li>
 * </ul>
 * @author Zoey76
 */
public enum SkillOperateType
{
    /**
     * Active Skill with "Instant Effect" (for example damage skills heal/pdam/mdam/cpdam skills).
     */
    A1,

    /**
     * Active Skill with "Continuous effect + Instant effect" (for example buff/debuff or damage/heal over time skills).
     */
    A2,

    /**
     * Active Skill with "Instant effect + Continuous effect"
     */
    A3,

    /**
     * Active Skill with "Instant effect + ?" used for special event herb.
     */
    A4,

    /**
     * Aura Active Skill
     */
    A5,

    /**
     * Synergy Active Skill
     */
    A6,

    /**
     * Continuous Active Skill with "instant effect" (instant effect casted by ticks).
     */
    CA1,

    /**
     * ?
     */
    CA2,

    /**
     * Continuous Active Skill with "continuous effect" (continuous effect casted by ticks).
     */
    CA5,

    /**
     * Directional Active Skill with "Charge/Rush instant effect".
     */
    DA1,

    /**
     * Directional Active Skill with "Charge/Rush Continuous effect".
     */
    DA2,

    /**
     * Directional Active Skill with Blink effect
     */
    DA3,

    /**
     * Passive Skill.
     */
    P,

    /**
     * Toggle Skill.
     */
    T,

    /**
     * Toggle Skill with Group.
     */
    TG,

    /**
     * Aura Skill.
     */
    AU;

    /**
     * Verifies if the operative type correspond to an active skill.
     * @return {@code true} if the operative skill type is active, {@code false} otherwise
     */
    public boolean isActive()
    {
        switch (this)
        {
            case A1:
            case A2:
            case A3:
            case A4:
            case A5:
            case A6:
            case CA1:
            case CA5:
            case DA1:
            case DA2:
                return true;
            default:
                return false;
        }
    }

    /**
     * Verifies if the operative type correspond to a continuous skill.
     * @return {@code true} if the operative skill type is continuous, {@code false} otherwise
     */
    public boolean isContinuous()
    {
        switch (this)
        {
            case A2:
            case A4:
            case A5:
            case A6:
            case DA2:
                return true;
            default:
                return false;
        }
    }

    /**
     * Verifies if the operative type correspond to a continuous skill.
     * @return {@code true} if the operative skill type is continuous, {@code false} otherwise
     */
    public boolean isSelfContinuous()
    {
        return (this == A3);
    }

    /**
     * Verifies if the operative type correspond to a passive skill.
     * @return {@code true} if the operative skill type is passive, {@code false} otherwise
     */
    public boolean isPassive()
    {
        return (this == P);
    }

    /**
     * Verifies if the operative type correspond to a toggle skill.
     * @return {@code true} if the operative skill type is toggle, {@code false} otherwise
     */
    public boolean isToggle()
    {
        return (this == T) || (this == TG) || (this == AU);
    }

    public boolean isToggleGrouped()
    {
        return (this == TG) || (this == AU);
    }

    /**
     * Verifies if the operative type correspond to a active aura skill.
     * @return {@code true} if the operative skill type is active aura, {@code false} otherwise
     */
    public boolean isAura()
    {
        return (this == A5) || (this == A6) || (this == AU);
    }

    /**
     * @return {@code true} if the operate type skill type should not send messeges for start/finish, {@code false} otherwise
     */
    public boolean isHidingMesseges()
    {
        return (this == A5) || (this == A6) || (this == TG);
    }

    /**
     * @return {@code true} if the operate type skill type should not be broadcasted as MagicSkillUse, MagicSkillLaunched, {@code false} otherwise
     */
    public boolean isNotBroadcastable()
    {
        return (this == A5) || (this == A6) || (this == AU) || (this == TG);
    }

    /**
     * Verifies if the operative type correspond to a channeling skill.
     * @return {@code true} if the operative skill type is channeling, {@code false} otherwise
     */
    public boolean isChanneling()
    {
        switch (this)
        {
            case CA1:
            case CA2:
            case CA5:
                return true;
            default:
                return false;
        }
    }

    /**
     * Verifies if the operative type correspond to a synergy skill.
     * @return {@code true} if the operative skill type is synergy, {@code false} otherwise
     */
    public boolean isSynergy()
    {
        return (this == A6);
    }
}