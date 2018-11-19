package npc.model.events;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class LettersManagerInstance extends NpcInstance
{
	private static final int LETTER_A = 3875;	// Letter A
	private static final int LETTER_C = 3876;	// Letter C
	private static final int LETTER_E = 3877;	// Letter E
	private static final int LETTER_G = 3879;	// Letter G
	private static final int LETTER_H = 3880;	// Letter H
	private static final int LETTER_I = 3881;	// Letter I
	private static final int LETTER_L = 3882;	// Letter L
	private static final int LETTER_M = 34956;	// Letter M
	private static final int LETTER_N = 3883;	// Letter N
	private static final int LETTER_O = 3884;	// Letter O
	private static final int LETTER_R = 3885;	// Letter R
	private static final int LETTER_S = 3886;	// Letter S
	private static final int NUMBER_II = 3888;	// Number II
	private static final int LINEAGE_II_CHEST = 29581;	// Lineage II Chest
	private static final int CHRONICLE_CHEST = 29582;	// Chronicle Chest
	private static final int MEMORIES_CHEST = 29583;	// Memories Chest

	private static final int[] ALL_LETTERS = { LETTER_A, LETTER_C, LETTER_E, LETTER_G, LETTER_H, LETTER_I, LETTER_L, LETTER_M, LETTER_N, LETTER_O, LETTER_R, LETTER_S, NUMBER_II };

	private static final int[][] LINEAGE_II_WORD = new int[][]{
		{ LETTER_A, 1 },
		{ LETTER_E, 2 },
		{ LETTER_G, 1 },
		{ LETTER_I, 1 },
		{ LETTER_L, 1 },
		{ LETTER_N, 1 },
		{ NUMBER_II, 1 }
	};

	private static final int[][] MEMORIES_WORD = new int[][]{
		{ LETTER_E, 2 },
		{ LETTER_I, 1 },
		{ LETTER_M, 2 },
		{ LETTER_O, 1 },
		{ LETTER_R, 1 },
		{ LETTER_S, 1 }
	};

	private static final int[][] CHRONICLE_WORD = new int[][]{
		{ LETTER_C, 2 },
		{ LETTER_E, 1 },
		{ LETTER_H, 1 },
		{ LETTER_I, 1 },
		{ LETTER_L, 1 },
		{ LETTER_N, 1 },
		{ LETTER_O, 1 },
		{ LETTER_R, 1 },
	};

	public LettersManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -1000)
		{
			if(reply == 1)
			{
				for(int[] item : LINEAGE_II_WORD)
				{
					if(!ItemFunctions.haveItem(player, item[0], item[1]))
					{
						showChatWindow(player, "events/in_the_pursuit_of_letters/" + getNpcId() + "-no_have_lineageii.htm", false);
						return;
					}
				}

				for(int[] item : LINEAGE_II_WORD)
					ItemFunctions.deleteItem(player, item[0], item[1]);

				ItemFunctions.addItem(player, LINEAGE_II_CHEST, 1);
			}
			else if(reply == 2)
			{
				for(int[] item : MEMORIES_WORD)
				{
					if(!ItemFunctions.haveItem(player, item[0], item[1]))
					{
						showChatWindow(player, "events/in_the_pursuit_of_letters/" + getNpcId() + "-no_have_memories.htm", false);
						return;
					}
				}

				for(int[] item : MEMORIES_WORD)
					ItemFunctions.deleteItem(player, item[0], item[1]);

				ItemFunctions.addItem(player, MEMORIES_CHEST, 1);
			}
			else if(reply == 3)
			{
				for(int[] item : CHRONICLE_WORD)
				{
					if(!ItemFunctions.haveItem(player, item[0], item[1]))
					{
						showChatWindow(player, "events/in_the_pursuit_of_letters/" + getNpcId() + "-no_have_chronicle.htm", false);
						return;
					}
				}

				for(int[] item : CHRONICLE_WORD)
					ItemFunctions.deleteItem(player, item[0], item[1]);

				ItemFunctions.addItem(player, CHRONICLE_CHEST, 1);
			}
		}
		else if(ask == -1001)
		{
			int letterId = 0;
			long lettersCount = 0;
			if(reply == 1)
			{
				letterId = LETTER_A;
				lettersCount = 2;
			}
			else if(reply == 2)
			{
				letterId = LETTER_G;
				lettersCount = 2;
			}
			else if(reply == 3)
			{
				letterId = LETTER_R;
				lettersCount = 2;
			}
			else if(reply == 4)
			{
				letterId = LETTER_E;
				lettersCount = 2;
			}
			else if(reply == 5)
			{
				letterId = LETTER_L;
				lettersCount = 2;
			}
			else if(reply == 6)
			{
				letterId = LETTER_N;
				lettersCount = 2;
			}
			else if(reply == 7)
			{
				letterId = LETTER_C;
				lettersCount = 2;
			}
			else if(reply == 8)
			{
				letterId = LETTER_O;
				lettersCount = 2;
			}
			else if(reply == 9)
			{
				letterId = LETTER_M;
				lettersCount = 2;
			}
			else if(reply == 10)
			{
				letterId = LETTER_I;
				lettersCount = 2;
			}
			else if(reply == 11)
			{
				letterId = LETTER_S;
				lettersCount = 1;
			}
			else if(reply == 12)
			{
				letterId = LETTER_H;
				lettersCount = 1;
			}
			else if(reply == 13)
			{
				letterId = NUMBER_II;
				lettersCount = 1;
			}

			if(letterId == 0 || lettersCount == 0)
				return;

			if(!ItemFunctions.deleteItem(player, letterId, lettersCount))
			{
				showChatWindow(player, "events/in_the_pursuit_of_letters/" + getNpcId() + "-no_have_letters.htm", false);
				return;
			}

			ItemFunctions.addItem(player, Rnd.get(ALL_LETTERS), 1);
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "events/in_the_pursuit_of_letters/";
	}
}