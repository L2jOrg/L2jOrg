package handler.voicecommands;

import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.components.HtmlMessage;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.l2j.gameserver.utils.HtmlUtils;

import java.text.NumberFormat;
import java.util.Locale;

import static org.l2j.commons.util.Util.replaceFirst;

public class WhoAmI extends ScriptVoiceCommandHandler
{
	private final String[] COMMANDS = new String[] { "whoami", "whoiam" };

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		Creature target = null;

		//TODO [G1ta0] добавить рефлекты
		//TODO [G1ta0] возможно стоит показывать статы в зависимости от цели
		double hpRegen = player.getHpRegen();
		double cpRegen = player.getCpRegen();
		double mpRegen = player.getMpRegen();
		double hpDrain = player.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0., target, null);
		double mpDrain = player.calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0., target, null);
		double hpGain = player.calcStat(Stats.HEAL_EFFECTIVNESS, 100., target, null);
		double mpGain = player.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., target, null);
		double pCritPerc = player.calcStat(Stats.P_CRITICAL_DAMAGE_PER, target, null);
		double pCritStatic = player.calcStat(Stats.P_CRITICAL_DAMAGE_DIFF, target, null);
		double mCritPerc = player.calcStat(Stats.P_MAGIC_CRITICAL_DMG_PER, target, null);
		double mCritStatic = player.calcStat(Stats.P_MAGIC_CRITICAL_DMG_DIFF, target, null);
		double blowRate = player.calcStat(Stats.FATALBLOW_RATE, target, null) * 100. - 100.;

		ItemInstance shld = player.getSecondaryWeaponInstance();
		boolean shield = shld != null && shld.getItemType() == WeaponType.NONE;

		double shieldDef = shield ? player.calcStat(Stats.SHIELD_DEFENCE, player.getBaseStats().getShldDef(), target, null) : 0.;
		double shieldRate = shield ? player.calcStat(Stats.SHIELD_RATE, target, null) : 0.;

		double xpRate = player.getRateExp();
		double spRate = player.getRateSp();
		double dropRate = player.getRateItems();
		double adenaRate = player.getRateAdena();
		double spoilRate = player.getRateSpoil();

		double fireResist = player.calcStat(Element.FIRE.getDefence(), 0., target, null);
		double windResist = player.calcStat(Element.WIND.getDefence(), 0., target, null);
		double waterResist = player.calcStat(Element.WATER.getDefence(), 0., target, null);
		double earthResist = player.calcStat(Element.EARTH.getDefence(), 0., target, null);
		double holyResist = player.calcStat(Element.HOLY.getDefence(), 0., target, null);
		double unholyResist = player.calcStat(Element.UNHOLY.getDefence(), 0., target, null);

		double bleedPower = player.calcStat(Stats.ATTACK_TRAIT_BLEED);
		double bleedResist = player.calcStat(Stats.DEFENCE_TRAIT_BLEED);
		double poisonPower = player.calcStat(Stats.ATTACK_TRAIT_POISON);
		double poisonResist = player.calcStat(Stats.DEFENCE_TRAIT_POISON);
		double stunPower = player.calcStat(Stats.ATTACK_TRAIT_SHOCK);
		double stunResist = player.calcStat(Stats.DEFENCE_TRAIT_SHOCK);
		double rootPower = player.calcStat(Stats.ATTACK_TRAIT_HOLD);
		double rootResist = player.calcStat(Stats.DEFENCE_TRAIT_HOLD);
		double sleepPower = player.calcStat(Stats.ATTACK_TRAIT_SLEEP);
		double sleepResist = player.calcStat(Stats.DEFENCE_TRAIT_SLEEP);
		double paralyzePower = player.calcStat(Stats.ATTACK_TRAIT_PARALYZE);
		double paralyzeResist = player.calcStat(Stats.DEFENCE_TRAIT_PARALYZE);
		double mentalPower = player.calcStat(Stats.ATTACK_TRAIT_DERANGEMENT);
		double mentalResist = player.calcStat(Stats.DEFENCE_TRAIT_DERANGEMENT);
		double debuffResist = player.calcStat(Stats.RESIST_ABNORMAL_DEBUFF, target, null);
		double cancelPower = player.calcStat(Stats.CANCEL_POWER, target, null);
		double cancelResist = player.calcStat(Stats.CANCEL_RESIST, target, null);

		double swordResist = player.calcStat(Stats.DEFENCE_TRAIT_SWORD);
		double dualResist = player.calcStat(Stats.DEFENCE_TRAIT_DUAL);
		double bluntResist = player.calcStat(Stats.DEFENCE_TRAIT_BLUNT);
		double daggerResist = player.calcStat(Stats.DEFENCE_TRAIT_DAGGER);
		double bowResist = player.calcStat(Stats.DEFENCE_TRAIT_BOW);
		double crossbowResist = player.calcStat(Stats.DEFENCE_TRAIT_CROSSBOW);
		double twoHandCrossbowResist = player.calcStat(Stats.DEFENCE_TRAIT_TWOHANDCROSSBOW);
		double poleResist = player.calcStat(Stats.DEFENCE_TRAIT_POLE);
		double fistResist = player.calcStat(Stats.DEFENCE_TRAIT_FIST);

		double critChanceResist = 100. - player.calcStat(Stats.P_CRIT_CHANCE_RECEPTIVE, target, null);
		double critDamResist = 100. - player.calcStat(Stats.P_CRIT_DAMAGE_RECEPTIVE, target, null);

		String dialog = HtmCache.getInstance().getHtml("command/whoami.htm", player);

		NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(0);

		StringBuilder sb = new StringBuilder(dialog);
		replaceFirst(sb,"%hpRegen%", df.format(hpRegen));
		replaceFirst(sb,"%cpRegen%", df.format(cpRegen));
		replaceFirst(sb,"%mpRegen%", df.format(mpRegen));
		replaceFirst(sb,"%hpDrain%", df.format(hpDrain));
		replaceFirst(sb,"%mpDrain%", df.format(mpDrain));
		replaceFirst(sb,"%hpGain%", df.format(hpGain));
		replaceFirst(sb,"%mpGain%", df.format(mpGain));
		replaceFirst(sb,"%pCritPerc%", df.format(pCritPerc));
		replaceFirst(sb,"%pCritStatic%", df.format(pCritStatic));
		replaceFirst(sb,"%mCritPerc%", df.format(mCritPerc));
		replaceFirst(sb,"%mCritStatic%", df.format(mCritStatic));
		replaceFirst(sb,"%blowRate%", df.format(blowRate));
		replaceFirst(sb,"%shieldDef%", df.format(shieldDef));
		replaceFirst(sb,"%shieldRate%", df.format(shieldRate));
		replaceFirst(sb,"%xpRate%", df.format(xpRate));
		replaceFirst(sb,"%spRate%", df.format(spRate));
		replaceFirst(sb,"%dropRate%", df.format(dropRate));
		replaceFirst(sb,"%adenaRate%", df.format(adenaRate));
		replaceFirst(sb,"%spoilRate%", df.format(spoilRate));
		replaceFirst(sb,"%fireResist%", df.format(fireResist));
		replaceFirst(sb,"%windResist%", df.format(windResist));
		replaceFirst(sb,"%waterResist%", df.format(waterResist));
		replaceFirst(sb,"%earthResist%", df.format(earthResist));
		replaceFirst(sb,"%holyResist%", df.format(holyResist));
		replaceFirst(sb,"%darkResist%", df.format(unholyResist));
		replaceFirst(sb,"%bleedPower%", bleedPower == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(bleedPower));
		replaceFirst(sb,"%bleedResist%", bleedResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(bleedResist));
		replaceFirst(sb,"%poisonPower%", poisonPower == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(poisonPower));
		replaceFirst(sb,"%poisonResist%", poisonResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(poisonResist));
		replaceFirst(sb,"%stunPower%", stunPower == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(stunPower));
		replaceFirst(sb,"%stunResist%", stunResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(stunResist));
		replaceFirst(sb,"%rootPower%", rootPower == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(rootPower));
		replaceFirst(sb,"%rootResist%", rootResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(rootResist));
		replaceFirst(sb,"%sleepPower%", sleepPower == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(sleepPower));
		replaceFirst(sb,"%sleepResist%", sleepResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(sleepResist));
		replaceFirst(sb,"%paralyzePower%", paralyzePower == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(paralyzePower));
		replaceFirst(sb,"%paralyzeResist%", paralyzeResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(paralyzeResist));
		replaceFirst(sb,"%mentalPower%", mentalPower == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(mentalPower));
		replaceFirst(sb,"%mentalResist%", mentalResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(mentalResist));
		replaceFirst(sb,"%debuffResist%", df.format(debuffResist));
		replaceFirst(sb,"%cancelPower%", df.format(cancelPower));
		replaceFirst(sb,"%cancelResist%", df.format(cancelResist));
		replaceFirst(sb,"%swordResist%", swordResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(swordResist));
		replaceFirst(sb,"%dualResist%", dualResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(dualResist));
		replaceFirst(sb,"%bluntResist%", bluntResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(bluntResist));
		replaceFirst(sb,"%daggerResist%", daggerResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(daggerResist));
		replaceFirst(sb,"%bowResist%", bowResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(bowResist));
		replaceFirst(sb,"%crossbowResist%", crossbowResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(crossbowResist));
		replaceFirst(sb,"%twoHandCrossbowResist%", twoHandCrossbowResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(twoHandCrossbowResist));
		replaceFirst(sb,"%fistResist%", fistResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(fistResist));
		replaceFirst(sb,"%poleResist%", poleResist == Double.POSITIVE_INFINITY ? (player.isLangRus() ? "Неуяз." : "Invul.") : df.format(poleResist));
		replaceFirst(sb,"%critChanceResist%", df.format(critChanceResist));
		replaceFirst(sb,"%critDamResist%", df.format(critDamResist));

		HtmlMessage msg = new HtmlMessage(0);
		msg.setHtml(HtmlUtils.bbParse(sb.toString()));
		player.sendPacket(msg);

		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
