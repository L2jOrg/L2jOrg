/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package events.WatermelonNinja;

import events.ScriptEvent;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.LongTimeEvent;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

import java.util.Arrays;
import java.util.List;

/**
 * @URL https://eu.4gameforum.com/threads/653089/
 * @author vGodFather
 */
public class WatermelonNinja extends LongTimeEvent implements ScriptEvent
{
	private static final int MANAGER = 31860;
	private static final int NECTAR_SKILL = 2005;
	
	private static final List<Integer> SQUASH_LIST = Arrays.asList(13271, 13272, 13273, 13274, 13275, 13276, 13277, 13278);
	private static final List<Integer> LARGE_SQUASH_LIST = Arrays.asList(13274, 13278);
	private static final List<Integer> CHRONO_LIST = Arrays.asList(4202, 5133, 5817, 7058, 8350);
	
	//@formatter:off
	private static final String[] _NOCHRONO_TEXT =
	{
		"You cannot kill me without Chrono",
		"Hehe...keep trying...",
		"Nice try...",
		"Tired ?",
		"Go go ! haha..."
	};
	private static final String[] _CHRONO_TEXT =
	{
		"Arghh... Chrono weapon...",
		"My end is coming...",
		"Please leave me!",
		"Heeellpppp...",
		"Somebody help me please..."
	};
	private static final String[] _NECTAR_TEXT =
	{
		"Yummie... Nectar...",
		"Plase give me more...",
		"Hmmm.. More.. I need more...",
		"I would like you more, if you give me more...",
		"Hmmmmmmm...",
		"My favourite..."
	};
	
	// Weapon 
	private static final int Atuba_Hammer = 187;
	private static final int Gastraphetes = 278;
	private static final int Maingauche = 224;
	private static final int Staff_of_Life = 189;
	private static final int Sword_of_Revolution = 129;
	private static final int War_Pick = 294;
	private static final int Battle_Axe = 160;
	private static final int Crystal_Staff = 192;
	private static final int Crystallized_Ice_Bow = 281;
	private static final int Flamberge = 71;
	private static final int Orcish_Glaive = 298;
	private static final int Stick_of_Faith = 193;
	private static final int Stormbringer = 72;
	private static final int Berserker_Blade = 5286;
	private static final int Dark_Screamer = 233;
	private static final int Eminence_Bow = 286;
	private static final int Fisted_Blade = 265;
	private static final int Homunkulus_Sword = 84;
	private static final int Poleaxe = 95;
	private static final int Sage_Staff = 200;
	private static final int Sword_of_Nightmare = 134;
	
	// Armor
	private static final int Divine_Gloves = 2463;
	private static final int Divine_Stockings = 473;
	private static final int Divine_Tunic = 442;
	private static final int Drake_Leather_Armor = 401;
	private static final int Drake_Leather_Boots = 2437;
	private static final int Full_Plate_Armor = 356;
	private static final int Full_Plate_Helmet = 2414;
	private static final int Full_Plate_Shield = 2497;
	private static final int Avadon_Robe = 2406;
	private static final int Blue_Wolf_Breastplate = 358;
	private static final int Blue_Wolf_Gaiters = 2380;
	private static final int Leather_Armor_of_Doom = 2392;
	private static final int Sealed_Avadon_Boots = 600;
	private static final int Sealed_Avadon_Circlet = 2415;
	private static final int Sealed_Avadon_Gloves = 2464;
	private static final int Sealed_Blue_Wolf_Boots = 2439;
	private static final int Sealed_Blue_Wolf_Gloves = 2487;
	private static final int Sealed_Blue_Wolf_Helmet = 2416;
	private static final int Sealed_Doom_Boots = 601;
	private static final int Sealed_Doom_Gloves = 2475;
	private static final int Sealed_Doom_Helmet = 2417;
	
	// Misc
	private static final int Class_Buff_Scroll_1st = 29011;
	private static final int Angel_Cat_Blessing_Chest = 29584;
	private static final int Major_Healing_Potion = 1539;
	private static final int Rice_Cake_of_Fighting_Spirit = 49080;
	private static final int XP_SP_Scroll_Normal = 29648;
	private static final int XP_SP_Scroll_Medium = 29519;
	private static final int Greater_CP_Potion = 5592;
	private static final int Quick_Healing_Potion = 1540;
	private static final int Class_Buff_Scroll_2nd = 29698;
	private static final int Scroll_Enchant_Armor_D = 956;
	private static final int Scroll_Enchant_Weapon_D = 955;
	private static final int Scroll_Enchant_Armor_C = 952;
	private static final int Scroll_Enchant_Weapon_C = 951;
	private static final int Blessed_Scroll_Enchant_Armor_C = 29022;
	private static final int Blessed_Scroll_Enchant_Weapon_C = 29021;
	private static final int Blessed_Scroll_Enchant_Armor_D = 29020;
	private static final int Blessed_Scroll_Enchant_Weapon_D = 29019;
	private static final int Special_Pirate_Fruit = 49518;
	private static final int XP_SP_Scroll_High = 29010;
	private static final int Blessed_Scroll_of_Escape = 1538;
	private static final int Blessed_Scroll_of_Resurrection = 3936;
	private static final int Rice_Cake_of_Flaming_Fighting_Spirit = 49081;
	
	// Buff Scroll
	private static final int Scroll_Acumen = 3929;
	private static final int Scroll_Berserker_Spirit = 49435;
	private static final int Scroll_Blessed_Body = 29690;
	private static final int Scroll_Death_Whisper = 3927;
	private static final int Scroll_Guidance = 3926;
	private static final int Scroll_Haste = 3930;
	private static final int Scroll_Magic_Barrier = 29689;
	private static final int Scroll_Mana_Regeneration = 4218;
	private static final int Scroll_Regeneration = 29688;
	private static final int Scroll_Dance_of_Fire = 29014;
	private static final int Scroll_Hunter_Song = 29013;
	
	// Recipe
	private static final int Recipe_Atuba_Hammer = 2287;
	private static final int Recipe_Gastraphetes = 2267;
	private static final int Recipe_Maingauche = 2276;
	private static final int Recipe_Staff_of_Life = 2289;
	private static final int Recipe_Sword_of_Revolution = 2272;
	private static final int Recipe_Battle_Axe = 2301;
	private static final int Recipe_Blue_Wolf_Gaiters = 4982;
	private static final int Recipe_Crystal_Staff = 2305;
	private static final int Recipe_Crystallized_Ice_Bow = 2312;
	private static final int Recipe_Divine_Gloves = 3017;
	private static final int Recipe_Divine_Stockings = 2234;
	private static final int Recipe_Flamberge = 2297;
	private static final int Recipe_Full_Plate_Helmet = 3012;
	private static final int Recipe_Full_Plate_Shield = 3019;
	private static final int Recipe_Orcish_Glaive = 2317;
	private static final int Recipe_Sealed_Avadon_Boots = 4959;
	private static final int Recipe_Sealed_Avadon_Gloves = 4953;
	private static final int Recipe_Sealed_Blue_Wolf_Boots = 4992;
	private static final int Recipe_Sealed_Blue_Wolf_Gloves = 4998;
	private static final int Recipe_Stick_of_Faith = 2306;
	private static final int Recipe_Stormbringer = 2298;
	private static final int Recipe_Avadon_Robe = 4951;
	private static final int Recipe_Berserker_Blade = 5436;
	private static final int Recipe_Blue_Wolf_Breastplate = 4981;
	private static final int Recipe_Dark_Screamer = 2345;
	private static final int Recipe_Divine_Tunic = 2233;
	private static final int Recipe_Eminence_Bow = 2359;
	private static final int Recipe_Fisted_Blade = 2346;
	private static final int Recipe_Full_Plate_Armor = 2231;
	private static final int Recipe_Homunkulus_Sword = 2330;
	private static final int Recipe_Leather_Armor_of_Doom = 4985;
	private static final int Recipe_Poleaxe = 2331;
	private static final int Recipe_Sage_Staff = 2341;
	private static final int Recipe_Sealed_Avadon_Circlet = 4952;
	private static final int Recipe_Sealed_Blue_Wolf_Helmet = 4990;
	private static final int Recipe_Sealed_Doom_Helmet = 4991;
	private static final int Recipe_Sword_of_Nightmare = 2333;
	
	// Main Material
	private static final int Animal_Bone = 1872;
	private static final int Coal = 1870;
	private static final int Varnish = 1865;
	private static final int Stone_of_Purity = 1875;
	private static final int Steel = 1880;
	private static final int Mithril_Ore = 1876;
	private static final int Leather = 1882;
	private static final int Cokes = 1879;
	private static final int Coarse_Bone_Powder = 1881;
	private static final int Adamantite_Nugget = 1877;
	private static final int Asofe = 4043;
	private static final int Mold_Glue = 4039;
	private static final int Oriharukon_Ore = 1874;
	private static final int Steel_Mold = 1883;
	private static final int Synthetic_Braid = 1889;
	private static final int Synthetic_Cokes = 1888;
	private static final int Varnish_of_Purity = 1887;
	private static final int High_grade_Suede = 1885;
	private static final int Enria = 4042;
	private static final int Mithril_Alloy = 1890;
	private static final int Mold_Hardener = 4041;
	private static final int Mold_Lubricant = 4040;
	private static final int Crystal_D = 1458;
	private static final int Crystal_C = 1459;
	private static final int Crystal_B = 1460;
	private static final int Silver_Mold = 1886;
	private static final int Oriharukon = 1893;
	
	// Sub Material
	private static final int Atuba_Hammer_Head = 2049;
	private static final int Gastraphetes_Shaft = 2029;
	private static final int Maingauche_Edge = 2038;
	private static final int Staff_of_Life_Shaft = 2051;
	private static final int Sword_of_Revolution_Blade = 2034;
	private static final int Stormbringer_Blade = 2060;
	private static final int Stick_of_Faith_Shaft = 2068;
	private static final int Sealed_Blue_Wolf_Glove_Fabric = 4096;
	private static final int Sealed_Blue_Wolf_Boot_Design = 4090;
	private static final int Sealed_Avadon_Glove_Fragment = 4073;
	private static final int Sealed_Avadon_Boot_Design = 4098;
	private static final int Orcish_Glaive_Blade = 2075;
	private static final int Flamberge_Blade = 2059;
	private static final int Crystallized_Ice_Bow_Shaft = 2074;
	private static final int Crystal_Staff_Head = 2067;
	private static final int Blue_Wolf_Gaiter_Material = 4080;
	private static final int Battle_Axe_Head = 2063;
	private static final int Avadon_Robe_Fabric = 4071;
	private static final int Berserker_Blade_Edge = 5530;
	private static final int Blue_Wolf_Breastplate_Part = 4078;
	private static final int Dark_Screamer_Edge = 2107;
	private static final int Divine_Tunic_Fabric = 1988;
	private static final int Eminence_Bow_Shaft = 2121;
	private static final int Fisted_Blade_Piece = 2108;
	private static final int Full_Plate_Armor_Temper = 1986;
	private static final int Poleaxe_Blade = 2093;
	private static final int Sage_Staff_Head = 2109;
	private static final int Sealed_Avadon_Circlet_Pattern = 4072;
	private static final int Sealed_Blue_Wolf_Helmet_Design = 4088;
	private static final int Sealed_Doom_Helmet_Design = 4089;
	private static final int Sword_of_Nightmare_Blade = 2095;
	
	private static final int[][] DROPLIST =
	{
		// Rain Watermelon
		{ 13273, Class_Buff_Scroll_1st, 70 },
		{ 13273, Angel_Cat_Blessing_Chest, 60 },
		{ 13273, Major_Healing_Potion, 70 },
		{ 13273, Rice_Cake_of_Fighting_Spirit, 60 },
		{ 13273, XP_SP_Scroll_Normal, 50 },
		{ 13273, XP_SP_Scroll_Medium, 40 },
		{ 13273, Steel, 50 },
		{ 13273, Adamantite_Nugget, 50 },
		{ 13273, Mithril_Ore, 50 },
		{ 13273, Leather, 50 },
		{ 13273, Cokes, 50 },
		{ 13273, Coarse_Bone_Powder, 50 },
		{ 13273, Stone_of_Purity, 50 },
		{ 13273, Stormbringer_Blade, 50 },
		{ 13273, Stick_of_Faith_Shaft, 50 },
		{ 13273, Sealed_Blue_Wolf_Glove_Fabric, 50 },
		{ 13273, Sealed_Blue_Wolf_Boot_Design, 50 },
		{ 13273, Sealed_Avadon_Glove_Fragment, 50 },
		{ 13273, Sealed_Avadon_Boot_Design, 50 },
		{ 13273, Orcish_Glaive_Blade, 50 },
		{ 13273, Flamberge_Blade, 50 },
		{ 13273, Crystallized_Ice_Bow_Shaft, 50 },
		{ 13273, Crystal_Staff_Head, 50 },
		{ 13273, Blue_Wolf_Gaiter_Material, 50 },
		{ 13273, Battle_Axe_Head, 50 },
		{ 13273, Recipe_Battle_Axe, 50 },
		{ 13273, Recipe_Blue_Wolf_Gaiters, 50 },
		{ 13273, Recipe_Crystal_Staff, 50 },
		{ 13273, Recipe_Crystallized_Ice_Bow, 50 },
		{ 13273, Recipe_Divine_Gloves, 50 },
		{ 13273, Recipe_Divine_Stockings, 50 },
		{ 13273, Recipe_Flamberge, 50 },
		{ 13273, Recipe_Full_Plate_Helmet, 50 },
		{ 13273, Recipe_Full_Plate_Shield, 50 },
		{ 13273, Recipe_Orcish_Glaive, 50 },
		{ 13273, Recipe_Sealed_Avadon_Boots, 50 },
		{ 13273, Recipe_Sealed_Avadon_Gloves, 50 },
		{ 13273, Recipe_Sealed_Blue_Wolf_Boots, 50 },
		{ 13273, Recipe_Sealed_Blue_Wolf_Gloves, 50 },
		{ 13273, Recipe_Stick_of_Faith, 50 },
		{ 13273, Recipe_Stormbringer, 50 },
		
		// Defective Watermelon
		{ 13272, Class_Buff_Scroll_1st, 70 },
		{ 13272, Angel_Cat_Blessing_Chest, 60 },
		{ 13272, Major_Healing_Potion, 70 },
		{ 13272, Rice_Cake_of_Fighting_Spirit, 60 },
		{ 13272, XP_SP_Scroll_Normal, 50 },
		{ 13272, Coal, 50 },
		{ 13272, Animal_Bone, 50 },
		{ 13272, Varnish, 50 },
		{ 13272, Recipe_Atuba_Hammer, 50 },
		{ 13272, Recipe_Gastraphetes, 50 },
		{ 13272, Recipe_Maingauche, 50 },
		{ 13272, Recipe_Staff_of_Life, 50 },
		{ 13272, Recipe_Sword_of_Revolution, 50 },
		{ 13272, Atuba_Hammer_Head, 50 },
		{ 13272, Gastraphetes_Shaft, 50 },
		{ 13272, Maingauche_Edge, 50 },
		{ 13272, Staff_of_Life_Shaft, 50 },
		{ 13272, Sword_of_Revolution_Blade, 50 },
		
		// Large rain Watermelon
		{ 13274, Battle_Axe, 5 },
		{ 13274, Crystal_Staff, 5 },
		{ 13274, Crystallized_Ice_Bow, 5 },
		{ 13274, Flamberge, 5 },
		{ 13274, Orcish_Glaive, 5 },
		{ 13274, Stick_of_Faith, 5 },
		{ 13274, Stormbringer, 5 },
		{ 13274, Divine_Gloves, 5 },
		{ 13274, Divine_Stockings, 5 },
		{ 13274, Divine_Tunic, 5 },
		{ 13274, Drake_Leather_Armor, 5 },
		{ 13274, Drake_Leather_Boots, 5 },
		{ 13274, Full_Plate_Armor, 5 },
		{ 13274, Full_Plate_Helmet, 5 },
		{ 13274, Full_Plate_Shield, 5 },
		{ 13274, Class_Buff_Scroll_2nd , 50 },
		{ 13274, Angel_Cat_Blessing_Chest, 50 },
		{ 13274, Blessed_Scroll_of_Escape, 50 },
		{ 13274, Blessed_Scroll_of_Resurrection, 50 },
		{ 13274, Greater_CP_Potion, 50 },
		{ 13274, Quick_Healing_Potion, 50 },
		{ 13274, Rice_Cake_of_Flaming_Fighting_Spirit, 50 },
		{ 13274, Special_Pirate_Fruit, 50 },
		{ 13274, XP_SP_Scroll_High, 50 },
		{ 13274, XP_SP_Scroll_Medium, 50 },
		{ 13274, Crystal_C, 50 },
		{ 13274, Scroll_Enchant_Armor_C, 50 },
		{ 13274, Scroll_Enchant_Weapon_C, 50 },
		{ 13274, Scroll_Dance_of_Fire, 70 },
		{ 13274, Scroll_Hunter_Song, 70 },
		{ 13274, Mithril_Alloy, 50 },
		{ 13274, Mold_Hardener, 50 },
		{ 13274, Oriharukon, 50 },
		{ 13274, Silver_Mold, 50 },
		
		// Defective Honey Watermelon
		{ 13276, Atuba_Hammer, 20 },
		{ 13276, Gastraphetes, 20 },
		{ 13276, Maingauche, 20 },
		{ 13276, Staff_of_Life, 20 },
		{ 13276, Sword_of_Revolution, 20 },
		{ 13276, War_Pick, 20 },
		{ 13276, Class_Buff_Scroll_1st, 50 },
		{ 13276, Class_Buff_Scroll_2nd , 50 },
		{ 13276, Angel_Cat_Blessing_Chest, 50 },
		{ 13276, Greater_CP_Potion, 50 },
		{ 13276, Rice_Cake_of_Fighting_Spirit, 50 },
		{ 13276, Special_Pirate_Fruit, 50 },
		{ 13276, XP_SP_Scroll_High, 50 },
		{ 13276, XP_SP_Scroll_Medium, 50 },
		{ 13276, Crystal_D, 50 },
		{ 13276, Scroll_Enchant_Armor_D, 50 },
		{ 13276, Scroll_Enchant_Weapon_D, 50 },
		{ 13276, Scroll_Acumen, 70 },
		{ 13276, Scroll_Berserker_Spirit, 70 },
		{ 13276, Scroll_Blessed_Body, 70 },
		{ 13276, Scroll_Death_Whisper, 70 },
		{ 13276, Scroll_Guidance, 70 },
		{ 13276, Scroll_Haste, 70 },
		{ 13276, Scroll_Magic_Barrier, 70 },
		{ 13276, Scroll_Mana_Regeneration, 70 },
		{ 13276, Scroll_Regeneration, 70 },
		{ 13276, Enria, 50 },
		{ 13276, Mithril_Alloy, 50 },
		{ 13276, Mold_Hardener, 50 },
		{ 13276, Mold_Lubricant, 50 },
		{ 13276, Silver_Mold, 50 },
		{ 13276, Varnish_of_Purity, 50 },

		// Rain Honey Watermelon
		{ 13277, Class_Buff_Scroll_1st, 70 },
		{ 13277, Angel_Cat_Blessing_Chest, 60 },
		{ 13277, Greater_CP_Potion, 60 },
		{ 13277, Quick_Healing_Potion, 60 },
		{ 13277, Rice_Cake_of_Fighting_Spirit, 60 },
		{ 13277, Adamantite_Nugget, 50 },
		{ 13277, Asofe, 50 },
		{ 13277, Coarse_Bone_Powder, 50 },
		{ 13277, Cokes, 50 },
		{ 13277, High_grade_Suede, 50 },
		{ 13277, Mithril_Ore, 50 },
		{ 13277, Mold_Glue, 50 },
		{ 13277, Oriharukon_Ore, 50 },
		{ 13277, Steel, 50 },
		{ 13277, Steel_Mold, 50 },
		{ 13277, Stone_of_Purity, 50 },
		{ 13277, Synthetic_Braid, 50 },
		{ 13277, Synthetic_Cokes, 50 },
		{ 13277, Varnish_of_Purity, 50 },
		{ 13277, Avadon_Robe_Fabric, 50 },
		{ 13277, Berserker_Blade_Edge, 50 },
		{ 13277, Blue_Wolf_Breastplate_Part, 50 },
		{ 13277, Dark_Screamer_Edge, 50 },
		{ 13277, Divine_Tunic_Fabric, 50 },
		{ 13277, Eminence_Bow_Shaft, 50 },
		{ 13277, Fisted_Blade_Piece, 50 },
		{ 13277, Full_Plate_Armor_Temper, 50 },
		{ 13277, Poleaxe_Blade, 50 },
		{ 13277, Sage_Staff_Head, 50 },
		{ 13277, Sealed_Avadon_Circlet_Pattern, 50 },
		{ 13277, Sealed_Blue_Wolf_Helmet_Design, 50 },
		{ 13277, Sealed_Doom_Helmet_Design, 50 },
		{ 13277, Sword_of_Nightmare_Blade, 50 },
		{ 13277, Recipe_Avadon_Robe, 50 },
		{ 13277, Recipe_Berserker_Blade, 50 },
		{ 13277, Recipe_Blue_Wolf_Breastplate, 50 },
		{ 13277, Recipe_Dark_Screamer, 50 },
		{ 13277, Recipe_Divine_Tunic, 50 },
		{ 13277, Recipe_Eminence_Bow, 50 },
		{ 13277, Recipe_Fisted_Blade, 50 },
		{ 13277, Recipe_Full_Plate_Armor, 50 },
		{ 13277, Recipe_Homunkulus_Sword, 50 },
		{ 13277, Recipe_Leather_Armor_of_Doom, 50 },
		{ 13277, Recipe_Poleaxe, 50 },
		{ 13277, Recipe_Sage_Staff, 50 },
		{ 13277, Recipe_Sealed_Avadon_Circlet, 50 },
		{ 13277, Recipe_Sealed_Blue_Wolf_Helmet, 50 },
		{ 13277, Recipe_Sealed_Doom_Helmet, 50 },
		{ 13277, Recipe_Sword_of_Nightmare, 50 },
		
		// Large Rain Honey Watermelon
		{ 13278, Berserker_Blade, 5 },
		{ 13278, Dark_Screamer, 5 },
		{ 13278, Eminence_Bow, 5 },
		{ 13278, Fisted_Blade, 5 },
		{ 13278, Homunkulus_Sword, 5 },
		{ 13278, Poleaxe, 5 },
		{ 13278, Sage_Staff, 5 },
		{ 13278, Sword_of_Nightmare, 5 },
		{ 13278, Avadon_Robe, 5 },
		{ 13278, Blue_Wolf_Breastplate, 5 },
		{ 13278, Blue_Wolf_Gaiters, 5 },
		{ 13278, Leather_Armor_of_Doom, 5 },
		{ 13278, Sealed_Avadon_Boots, 10 },
		{ 13278, Sealed_Avadon_Circlet, 10 },
		{ 13278, Sealed_Avadon_Gloves, 10 },
		{ 13278, Sealed_Blue_Wolf_Boots, 10 },
		{ 13278, Sealed_Blue_Wolf_Gloves, 10 },
		{ 13278, Sealed_Blue_Wolf_Helmet, 10 },
		{ 13278, Sealed_Doom_Boots, 10 },
		{ 13278, Sealed_Doom_Gloves, 10 },
		{ 13278, Sealed_Doom_Helmet, 10 },
		{ 13278, Class_Buff_Scroll_2nd , 50 },
		{ 13278, Angel_Cat_Blessing_Chest, 50 },
		{ 13278, Blessed_Scroll_of_Escape, 50 },
		{ 13278, Blessed_Scroll_of_Resurrection, 50 },
		{ 13278, Blessed_Scroll_Enchant_Armor_C, 30 },
		{ 13278, Blessed_Scroll_Enchant_Armor_D, 30 },
		{ 13278, Blessed_Scroll_Enchant_Weapon_C, 20 },
		{ 13278, Blessed_Scroll_Enchant_Weapon_D, 20 },
		{ 13278, Crystal_B, 50 },
		{ 13278, Crystal_C, 50 },
		{ 13278, Greater_CP_Potion, 50 },
		{ 13278, Major_Healing_Potion, 50 },
		{ 13278, Quick_Healing_Potion, 70 },
		{ 13278, Rice_Cake_of_Flaming_Fighting_Spirit, 60 },
		{ 13278, Scroll_Dance_of_Fire, 60 },
		{ 13278, Scroll_Hunter_Song, 60 },
		{ 13278, Scroll_Enchant_Armor_C, 50 },
		{ 13278, Scroll_Enchant_Weapon_C, 40 },
		{ 13278, Special_Pirate_Fruit, 60 },
		{ 13278, XP_SP_Scroll_High, 60 },
		{ 13278, XP_SP_Scroll_Medium, 60 },
	};
	//@formatter:on
	
	private WatermelonNinja()
	{
		addAttackId(SQUASH_LIST);
		addKillId(SQUASH_LIST);
		addSpawnId(SQUASH_LIST);
		addSpawnId(LARGE_SQUASH_LIST);
		addSkillSeeId(SQUASH_LIST);
		
		addStartNpc(MANAGER);
		addFirstTalkId(MANAGER);
		addTalkId(MANAGER);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setIsImmobilized(true);
		npc.disableCoreAI(true);
		if (LARGE_SQUASH_LIST.contains(npc.getId()))
		{
			npc.setIsInvul(true);
		}
		return null;
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet)
	{
		if (LARGE_SQUASH_LIST.contains(npc.getId()))
		{
			if ((attacker.getActiveWeaponItem() != null) && CHRONO_LIST.contains(attacker.getActiveWeaponItem().getId()))
			{
				ChronoText(npc);
				npc.setIsInvul(false);
				npc.getStatus().reduceHp(10, attacker);
			}
			else
			{
				noChronoText(npc);
				npc.setIsInvul(true);
			}
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onSkillSee(Npc npc, Player caster, Skill skill, WorldObject[] targets, boolean isPet)
	{
		if (SQUASH_LIST.contains(npc.getId()) && (skill.getId() == NECTAR_SKILL))
		{
			switch (npc.getId())
			{
				case 13271: // Watermelon Seed
				{
					randomSpawn(13274, 13273, 13272, npc, true);
					break;
				}
				case 13275: // Honey Watermelon Seed
				{
					randomSpawn(13278, 13277, 13276, npc, true);
					break;
				}
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		if (SQUASH_LIST.contains(npc.getId()))
		{
			dropItem(npc, killer);
		}
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".htm";
	}
	
	private static final void dropItem(Npc mob, Player player)
	{
		final int npcId = mob.getId();
		final int chance = Rnd.get(100);
		for (int[] drop : DROPLIST)
		{
			if (npcId == drop[0])
			{
				if (chance < drop[2])
				{
					if (drop[1] > 6000)
					{
						((Monster) mob).dropItem(player, drop[1], 1);
					}
					else
					{
						((Monster) mob).dropItem(player, drop[1], 1);
					}
					continue;
				}
			}
			if (npcId < drop[0])
			{
				return;
			}
		}
	}
	
	private void randomSpawn(int low, int medium, int high, Npc npc, boolean delete)
	{
		final int _random = Rnd.get(100);
		if (_random < 5)
		{
			spawnNext(low, npc);
		}
		if (_random < 10)
		{
			spawnNext(medium, npc);
		}
		else if (_random < 30)
		{
			spawnNext(high, npc);
		}
		else
		{
			nectarText(npc);
		}
	}
	
	private void ChronoText(Npc npc)
	{
		if (Rnd.get(100) < 20)
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getName(), _CHRONO_TEXT[Rnd.get(_CHRONO_TEXT.length)]));
		}
	}
	
	private void noChronoText(Npc npc)
	{
		if (Rnd.get(100) < 20)
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getName(), _NOCHRONO_TEXT[Rnd.get(_NOCHRONO_TEXT.length)]));
		}
	}
	
	private void nectarText(Npc npc)
	{
		if (Rnd.get(100) < 30)
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getName(), _NECTAR_TEXT[Rnd.get(_NECTAR_TEXT.length)]));
		}
	}
	
	private void spawnNext(int npcId, Npc npc)
	{
		addSpawn(npcId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 60000);
		npc.deleteMe();
	}
	
	public static ScriptEvent provider()
	{
		return new WatermelonNinja();
	}
}