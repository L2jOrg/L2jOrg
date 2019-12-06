package org.l2j.gameserver.model.base;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import static org.l2j.gameserver.enums.Race.DARK_ELF;
import static org.l2j.gameserver.enums.Race.ELF;

/**
 * @author luisantonioa
 */
public enum PlayerClass {
    HumanFighter(Race.HUMAN, ClassType.Fighter, ClassLevel.FIRST),
    Warrior(Race.HUMAN, ClassType.Fighter, ClassLevel.SECOND),
    Gladiator(Race.HUMAN, ClassType.Fighter, ClassLevel.THIRD),
    Warlord(Race.HUMAN, ClassType.Fighter, ClassLevel.THIRD),
    HumanKnight(Race.HUMAN, ClassType.Fighter, ClassLevel.SECOND),
    Paladin(Race.HUMAN, ClassType.Fighter, ClassLevel.THIRD),
    DarkAvenger(Race.HUMAN, ClassType.Fighter, ClassLevel.THIRD),
    Rogue(Race.HUMAN, ClassType.Fighter, ClassLevel.SECOND),
    TreasureHunter(Race.HUMAN, ClassType.Fighter, ClassLevel.THIRD),
    Hawkeye(Race.HUMAN, ClassType.Fighter, ClassLevel.THIRD),
    HumanMystic(Race.HUMAN, ClassType.Mystic, ClassLevel.FIRST),
    HumanWizard(Race.HUMAN, ClassType.Mystic, ClassLevel.SECOND),
    Sorceror(Race.HUMAN, ClassType.Mystic, ClassLevel.THIRD),
    Necromancer(Race.HUMAN, ClassType.Mystic, ClassLevel.THIRD),
    Warlock(Race.HUMAN, ClassType.Mystic, ClassLevel.THIRD),
    Cleric(Race.HUMAN, ClassType.Priest, ClassLevel.SECOND),
    Bishop(Race.HUMAN, ClassType.Priest, ClassLevel.THIRD),
    Prophet(Race.HUMAN, ClassType.Priest, ClassLevel.THIRD),

    ElvenFighter(ELF, ClassType.Fighter, ClassLevel.FIRST),
    ElvenKnight(ELF, ClassType.Fighter, ClassLevel.SECOND),
    TempleKnight(ELF, ClassType.Fighter, ClassLevel.THIRD),
    Swordsinger(ELF, ClassType.Fighter, ClassLevel.THIRD),
    ElvenScout(ELF, ClassType.Fighter, ClassLevel.SECOND),
    Plainswalker(ELF, ClassType.Fighter, ClassLevel.THIRD),
    SilverRanger(ELF, ClassType.Fighter, ClassLevel.THIRD),
    ElvenMystic(ELF, ClassType.Mystic, ClassLevel.FIRST),
    ElvenWizard(ELF, ClassType.Mystic, ClassLevel.SECOND),
    Spellsinger(ELF, ClassType.Mystic, ClassLevel.THIRD),
    ElementalSummoner(ELF, ClassType.Mystic, ClassLevel.THIRD),
    ElvenOracle(ELF, ClassType.Priest, ClassLevel.SECOND),
    ElvenElder(ELF, ClassType.Priest, ClassLevel.THIRD),

    DarkElvenFighter(DARK_ELF, ClassType.Fighter, ClassLevel.FIRST),
    PalusKnight(DARK_ELF, ClassType.Fighter, ClassLevel.SECOND),
    ShillienKnight(DARK_ELF, ClassType.Fighter, ClassLevel.THIRD),
    Bladedancer(DARK_ELF, ClassType.Fighter, ClassLevel.THIRD),
    Assassin(DARK_ELF, ClassType.Fighter, ClassLevel.SECOND),
    AbyssWalker(DARK_ELF, ClassType.Fighter, ClassLevel.THIRD),
    PhantomRanger(DARK_ELF, ClassType.Fighter, ClassLevel.THIRD),
    DarkElvenMystic(DARK_ELF, ClassType.Mystic, ClassLevel.FIRST),
    DarkElvenWizard(DARK_ELF, ClassType.Mystic, ClassLevel.SECOND),
    Spellhowler(DARK_ELF, ClassType.Mystic, ClassLevel.THIRD),
    PhantomSummoner(DARK_ELF, ClassType.Mystic, ClassLevel.THIRD),
    ShillienOracle(DARK_ELF, ClassType.Priest, ClassLevel.SECOND),
    ShillienElder(DARK_ELF, ClassType.Priest, ClassLevel.THIRD),

    OrcFighter(Race.ORC, ClassType.Fighter, ClassLevel.FIRST),
    OrcRaider(Race.ORC, ClassType.Fighter, ClassLevel.SECOND),
    Destroyer(Race.ORC, ClassType.Fighter, ClassLevel.THIRD),
    OrcMonk(Race.ORC, ClassType.Fighter, ClassLevel.SECOND),
    Tyrant(Race.ORC, ClassType.Fighter, ClassLevel.THIRD),
    OrcMystic(Race.ORC, ClassType.Mystic, ClassLevel.FIRST),
    OrcShaman(Race.ORC, ClassType.Mystic, ClassLevel.SECOND),
    Overlord(Race.ORC, ClassType.Mystic, ClassLevel.THIRD),
    Warcryer(Race.ORC, ClassType.Mystic, ClassLevel.THIRD),

    DwarvenFighter(Race.DWARF, ClassType.Fighter, ClassLevel.FIRST),
    DwarvenScavenger(Race.DWARF, ClassType.Fighter, ClassLevel.SECOND),
    BountyHunter(Race.DWARF, ClassType.Fighter, ClassLevel.THIRD),
    DwarvenArtisan(Race.DWARF, ClassType.Fighter, ClassLevel.SECOND),
    Warsmith(Race.DWARF, ClassType.Fighter, ClassLevel.THIRD),

    dummyEntry1(null, null, null),
    dummyEntry2(null, null, null),
    dummyEntry3(null, null, null),
    dummyEntry4(null, null, null),
    dummyEntry5(null, null, null),
    dummyEntry6(null, null, null),
    dummyEntry7(null, null, null),
    dummyEntry8(null, null, null),
    dummyEntry9(null, null, null),
    dummyEntry10(null, null, null),
    dummyEntry11(null, null, null),
    dummyEntry12(null, null, null),
    dummyEntry13(null, null, null),
    dummyEntry14(null, null, null),
    dummyEntry15(null, null, null),
    dummyEntry16(null, null, null),
    dummyEntry17(null, null, null),
    dummyEntry18(null, null, null),
    dummyEntry19(null, null, null),
    dummyEntry20(null, null, null),
    dummyEntry21(null, null, null),
    dummyEntry22(null, null, null),
    dummyEntry23(null, null, null),
    dummyEntry24(null, null, null),
    dummyEntry25(null, null, null),
    dummyEntry26(null, null, null),
    dummyEntry27(null, null, null),
    dummyEntry28(null, null, null),
    dummyEntry29(null, null, null),
    dummyEntry30(null, null, null),
    /*
     * (3rd classes)
     */
    duelist(Race.HUMAN, ClassType.Fighter, ClassLevel.FOURTH),
    dreadnought(Race.HUMAN, ClassType.Fighter, ClassLevel.FOURTH),
    phoenixKnight(Race.HUMAN, ClassType.Fighter, ClassLevel.FOURTH),
    hellKnight(Race.HUMAN, ClassType.Fighter, ClassLevel.FOURTH),
    sagittarius(Race.HUMAN, ClassType.Fighter, ClassLevel.FOURTH),
    adventurer(Race.HUMAN, ClassType.Fighter, ClassLevel.FOURTH),
    archmage(Race.HUMAN, ClassType.Mystic, ClassLevel.FOURTH),
    soultaker(Race.HUMAN, ClassType.Mystic, ClassLevel.FOURTH),
    arcanaLord(Race.HUMAN, ClassType.Mystic, ClassLevel.FOURTH),
    cardinal(Race.HUMAN, ClassType.Priest, ClassLevel.FOURTH),
    hierophant(Race.HUMAN, ClassType.Priest, ClassLevel.FOURTH),

    evaTemplar(ELF, ClassType.Fighter, ClassLevel.FOURTH),
    swordMuse(ELF, ClassType.Fighter, ClassLevel.FOURTH),
    windRider(ELF, ClassType.Fighter, ClassLevel.FOURTH),
    moonlightSentinel(ELF, ClassType.Fighter, ClassLevel.FOURTH),
    mysticMuse(ELF, ClassType.Mystic, ClassLevel.FOURTH),
    elementalMaster(ELF, ClassType.Mystic, ClassLevel.FOURTH),
    evaSaint(ELF, ClassType.Priest, ClassLevel.FOURTH),

    shillienTemplar(DARK_ELF, ClassType.Fighter, ClassLevel.FOURTH),
    spectralDancer(DARK_ELF, ClassType.Fighter, ClassLevel.FOURTH),
    ghostHunter(DARK_ELF, ClassType.Fighter, ClassLevel.FOURTH),
    ghostSentinel(DARK_ELF, ClassType.Fighter, ClassLevel.FOURTH),
    stormScreamer(DARK_ELF, ClassType.Mystic, ClassLevel.FOURTH),
    spectralMaster(DARK_ELF, ClassType.Mystic, ClassLevel.FOURTH),
    shillienSaint(DARK_ELF, ClassType.Priest, ClassLevel.FOURTH),

    titan(Race.ORC, ClassType.Fighter, ClassLevel.FOURTH),
    grandKhavatari(Race.ORC, ClassType.Fighter, ClassLevel.FOURTH),
    dominator(Race.ORC, ClassType.Mystic, ClassLevel.FOURTH),
    doomcryer(Race.ORC, ClassType.Mystic, ClassLevel.FOURTH),

    fortuneSeeker(Race.DWARF, ClassType.Fighter, ClassLevel.FOURTH),
    maestro(Race.DWARF, ClassType.Fighter, ClassLevel.FOURTH),

    dummyEntry31(null, null, null),
    dummyEntry32(null, null, null),
    dummyEntry33(null, null, null),
    dummyEntry34(null, null, null),

    soldier(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.FIRST),
    trooper(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.SECOND),
    warder(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.SECOND),
    soulFinder(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.SECOND),
    berserker(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.THIRD),
    soulBreaker(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.THIRD),
    soulRanger(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.THIRD),
    doomBringer(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.FOURTH),
    soulHound(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.FOURTH),
    trickster(Race.JIN_KAMAEL, ClassType.Fighter, ClassLevel.FOURTH);

    private static final Set<PlayerClass> mainSubclassSet;
    private static final Set<PlayerClass> neverSubclassed = EnumSet.of(Overlord, Warsmith);

    private static final Set<PlayerClass> subclasseSet1 = EnumSet.of(DarkAvenger, Paladin, TempleKnight, ShillienKnight);
    private static final Set<PlayerClass> subclasseSet2 = EnumSet.of(TreasureHunter, AbyssWalker, Plainswalker);
    private static final Set<PlayerClass> subclasseSet3 = EnumSet.of(Hawkeye, SilverRanger, PhantomRanger);
    private static final Set<PlayerClass> subclasseSet4 = EnumSet.of(Warlock, ElementalSummoner, PhantomSummoner);
    private static final Set<PlayerClass> subclasseSet5 = EnumSet.of(Sorceror, Spellsinger, Spellhowler);
    private static final EnumMap<PlayerClass, Set<PlayerClass>> subclassSetMap = new EnumMap<>(PlayerClass.class);

    static {
        final Set<PlayerClass> subclasses = getSet(null, ClassLevel.THIRD);
        subclasses.removeAll(neverSubclassed);

        mainSubclassSet = subclasses;

        subclassSetMap.put(DarkAvenger, subclasseSet1);
        subclassSetMap.put(Paladin, subclasseSet1);
        subclassSetMap.put(TempleKnight, subclasseSet1);
        subclassSetMap.put(ShillienKnight, subclasseSet1);

        subclassSetMap.put(TreasureHunter, subclasseSet2);
        subclassSetMap.put(AbyssWalker, subclasseSet2);
        subclassSetMap.put(Plainswalker, subclasseSet2);

        subclassSetMap.put(Hawkeye, subclasseSet3);
        subclassSetMap.put(SilverRanger, subclasseSet3);
        subclassSetMap.put(PhantomRanger, subclasseSet3);

        subclassSetMap.put(Warlock, subclasseSet4);
        subclassSetMap.put(ElementalSummoner, subclasseSet4);
        subclassSetMap.put(PhantomSummoner, subclasseSet4);

        subclassSetMap.put(Sorceror, subclasseSet5);
        subclassSetMap.put(Spellsinger, subclasseSet5);
        subclassSetMap.put(Spellhowler, subclasseSet5);
    }

    private Race _race;
    private ClassLevel _level;
    private ClassType _type;

    private PlayerClass(Race race, ClassType pType, ClassLevel pLevel) {
        _race = race;
        _level = pLevel;
        _type = pType;
    }

    public static EnumSet<PlayerClass> getSet(Race race, ClassLevel level) {
        final EnumSet<PlayerClass> allOf = EnumSet.noneOf(PlayerClass.class);

        for (PlayerClass playerClass : EnumSet.allOf(PlayerClass.class)) {
            if ((race == null) || playerClass.isOfRace(race)) {
                if ((level == null) || playerClass.isOfLevel(level)) {
                    allOf.add(playerClass);
                }
            }
        }
        return allOf;
    }

    public final Set<PlayerClass> getAvailableSubclasses(Player player) {
        Set<PlayerClass> subclasses = null;

        if (_level == ClassLevel.THIRD) {
            subclasses = EnumSet.copyOf(mainSubclassSet);

            subclasses.remove(this);

            switch (player.getRace()) {
                case ELF -> subclasses.removeAll(getSet(DARK_ELF, ClassLevel.THIRD));
                case DARK_ELF -> subclasses.removeAll(getSet(ELF, ClassLevel.THIRD));
            }

            final Set<PlayerClass> unavailableClasses = subclassSetMap.get(this);

            if (unavailableClasses != null) {
                subclasses.removeAll(unavailableClasses);
            }

        }
        return subclasses;
    }

    public final boolean isOfRace(Race pRace) {
        return _race == pRace;
    }

    public final boolean isOfType(ClassType pType) {
        return _type == pType;
    }

    public final boolean isOfLevel(ClassLevel pLevel) {
        return _level == pLevel;
    }

    public final ClassLevel getLevel() {
        return _level;
    }
}
