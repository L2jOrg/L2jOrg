package org.l2j.gameserver.data.xml;

import org.l2j.gameserver.data.string.ItemNameHolder;
import org.l2j.gameserver.data.string.Messages;
import org.l2j.gameserver.data.string.SkillNameHolder;
import org.l2j.gameserver.data.xml.parser.*;
import org.l2j.gameserver.instancemanager.ReflectionManager;

/**
 * @author VISTALL
 * @date 20:55/30.11.2010
 */
public abstract class Parsers
{
    public static void parseAll()
    {

        SkillNameHolder.getInstance().load();
        //
        SkillParser.getInstance().load();
        OptionDataParser.getInstance().load();
        VariationDataParser.getInstance().load();
        ItemParser.getInstance().load();
	    EnsoulParser.getInstance().load();
        RecipeParser.getInstance().load();
        SynthesisDataParser.getInstance().load();
        //
	    ExperienceDataParser.getInstance().load();
	    BaseStatsBonusParser.getInstance().load();
        LevelBonusParser.getInstance().load();
        KarmaIncreaseDataParser.getInstance().load();
        HitCondBonusParser.getInstance().load();
        PlayerTemplateParser.getInstance().load();
        ClassDataParser.getInstance().load();
        TransformTemplateParser.getInstance().load();
        NpcParser.getInstance().load();
        PetDataParser.getInstance().load();

        DomainParser.getInstance().load();
        RestartPointParser.getInstance().load();

        StaticObjectParser.getInstance().load();
        DoorParser.getInstance().load();
        ZoneParser.getInstance().load();
        SpawnParser.getInstance().load();
        InstantZoneParser.getInstance().load();

        ReflectionManager.getInstance().init();
        //
        SkillAcquireParser.getInstance().load();
        //
	    ResidenceFunctionsParser.getInstance().load();
        ResidenceParser.getInstance().load();
        ShuttleTemplateParser.getInstance().load();
        EventParser.getInstance().load();
        // support(cubic & agathion)
        CubicParser.getInstance().load();
        //
        BuyListParser.getInstance().load();
        MultiSellParser.getInstance().load();
        ProductDataParser.getInstance().load();
	    AttendanceRewardParser.getInstance().load();
        // item support
        HennaParser.getInstance().load();
        EnchantItemParser.getInstance().load();
        SoulCrystalParser.getInstance().load();
        ArmorSetsParser.getInstance().load();
        FishDataParser.getInstance().load();

        LevelUpRewardParser.getInstance().load();

	    PremiumAccountParser.getInstance().load();

        // etc
        PetitionGroupParser.getInstance().load();
	    BotReportPropertiesParser.getInstance().load();

	    DailyMissionsParser.getInstance().load();
    }
}