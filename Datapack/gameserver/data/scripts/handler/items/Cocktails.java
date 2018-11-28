package handler.items;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;

public class Cocktails extends SimpleItemHandler
{
	// Sweet Fruit Cocktail
	private static final int[] sweet_list = { 2404, // Might
			2405, // Shield
			2406, // Wind Walk
			2407, // Focus
			2408, // Death Whisper
			2409, // Guidance
			2410, // Bless Shield
			2411, // Bless Body
			2412, // Haste
			2413, // Vampiric Rage
	};

	// Fresh Fruit Cocktail
	private static final int[] fresh_list = { 2414, // Berserker Spirit
			2411, // Bless Body
			2415, // Magic Barrier
			2405, // Shield
			2406, // Wind Walk
			2416, // Bless Soul
			2417, // Empower
			2418, // Acumen
			2419, // Clarity
	};

	//Event - Fresh Milk
	private static final int[] milk_list = { 2873, 2874, 2875, 2876, 2877, 2878, 2879, 2885, 2886, 2887, 2888, 2889, 2890, };

	//Elixir of Blessing
	private static final int[] ELIXIR_OF_BLESSING_LIST = { 9198, 9200, 9201, 9202, 9203, 9199 };
	private static final int[] ELIXIR_OF_BLESSING_LIST_2 = { 9198, 9200, 9201, 9202, 9203, 9199 };

	// Chaos Festival Elixirs
	private static final int[] CHAOS_ELIXIR_GUARD_LIST = { 9545, 9546, 9547, 9548, 9549, 9550, 9551 };
	private static final int[] CHAOS_ELIXIR_BERSERK_LIST = { 9545, 9546, 9547, 9548, 9549, 9550, 9552 };
	private static final int[] CHAOS_ELIXIR_MAGIC_LIST = { 9545, 9546, 9547, 9548, 9549, 9550, 9553 };

	private static final int[] BUFF_LIST_20876 = { 22145, 22153 };
	private static final int[] BUFF_LIST_20877 = { 22143, 22144, 22154 };
	private static final int[] BUFF_LIST_20878 = { 22155, 22142, 22150 };
	private static final int[] BUFF_LIST_20879 = { 22149, 22157, 22147 };
	private static final int[] BUFF_LIST_20880 = { 22146, 22151, 22152 };
	private static final int[] BUFF_LIST_20881 = { 22140, 22156 };
	private static final int[] BUFF_LIST_20882 = { 22148, 22141, 22139 };

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		if(!reduceItem(player, item))
			return false;

		sendUseMessage(player, item);

		switch(itemId)
		{
			// Sweet Fruit Cocktail
			case 10178:
			case 15356:
			case 20393:
				for(int skill : sweet_list)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			// Fresh Fruit Cocktail				
			case 10179:
			case 15357:
			case 20394:
				for(int skill : fresh_list)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			//Event - Fresh Milk				
			case 14739:
				player.forceUseSkill(SkillHolder.getInstance().getSkill(2891, 6), player);
				for(int skill : milk_list)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			case 32316:
			case 33766:
			case 33862:
				for(int skill_id : ELIXIR_OF_BLESSING_LIST)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill_id, 1), player);
				break;
			case 34620:
				for(int skill_id : ELIXIR_OF_BLESSING_LIST_2)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill_id, 1), player);
				break;
			case 35991:
				for(int skill_id : CHAOS_ELIXIR_BERSERK_LIST)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill_id, 1), player);
				break;
			case 35992:
				for(int skill_id : CHAOS_ELIXIR_MAGIC_LIST)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill_id, 1), player);
				break;
			case 35993:
				for(int skill_id : CHAOS_ELIXIR_GUARD_LIST)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill_id, 1), player);
				break;
			case 20876:
				for(int skill : BUFF_LIST_20876)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			case 20877:
				for(int skill : BUFF_LIST_20877)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			case 20878:
				for(int skill : BUFF_LIST_20878)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			case 20879:
				for(int skill : BUFF_LIST_20879)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			case 20880:
				for(int skill : BUFF_LIST_20880)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			case 20881:
				for(int skill : BUFF_LIST_20881)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			case 20882:
				for(int skill : BUFF_LIST_20882)
					player.forceUseSkill(SkillHolder.getInstance().getSkill(skill, 1), player);
				break;
			case 37100:	// Elixir of Protection
				player.forceUseSkill(SkillHolder.getInstance().getSkill(11523, 1), player);
				break;
			case 37101:	// Elixir of Magic
				player.forceUseSkill(SkillHolder.getInstance().getSkill(11525, 1), player);
				break;
			case 37102:	// Elixir of Aggression
				player.forceUseSkill(SkillHolder.getInstance().getSkill(11524, 1), player);
				break;
			default:
				return false;
		}

		return true;
	}
}