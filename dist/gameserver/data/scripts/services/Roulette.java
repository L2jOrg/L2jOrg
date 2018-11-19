package services;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Util;

public class Roulette
{
	private static final String R = "red";
	private static final String B = "black";
	private static final String fst = "first";
	private static final String snd = "second";
	private static final String trd = "third";
	private static final String E = "even";
	private static final String O = "odd";
	private static final String L = "low";
	private static final String H = "high";
	private static final String Z = "zero";

	private static final String[][] Numbers = {
			// # Color Dozen Column Evenness Highness
			{ "0", Z, Z, Z, Z, Z },
			{ "1", R, fst, fst, O, L, },
			{ "2", B, fst, snd, E, L, },
			{ "3", R, fst, trd, O, L, },
			{ "4", B, fst, fst, E, L, },
			{ "5", R, fst, snd, O, L, },
			{ "6", B, fst, trd, E, L, },
			{ "7", R, fst, fst, O, L, },
			{ "8", B, fst, snd, E, L, },
			{ "9", R, fst, trd, O, L, },
			{ "10", B, fst, fst, E, L, },
			{ "11", B, fst, snd, O, L, },
			{ "12", R, fst, trd, E, L, },
			{ "13", B, snd, fst, O, L, },
			{ "14", R, snd, snd, E, L, },
			{ "15", B, snd, trd, O, L, },
			{ "16", R, snd, fst, E, L, },
			{ "17", B, snd, snd, O, L, },
			{ "18", R, snd, trd, E, L, },
			{ "19", R, snd, fst, O, H, },
			{ "20", B, snd, snd, E, H, },
			{ "21", R, snd, trd, O, H, },
			{ "22", B, snd, fst, E, H, },
			{ "23", R, snd, snd, O, H, },
			{ "24", B, snd, trd, E, H, },
			{ "25", R, trd, fst, O, H, },
			{ "26", B, trd, snd, E, H, },
			{ "27", R, trd, trd, O, H, },
			{ "28", B, trd, fst, E, H, },
			{ "29", B, trd, snd, O, H, },
			{ "30", R, trd, trd, E, H, },
			{ "31", B, trd, fst, O, H, },
			{ "32", R, trd, snd, E, H, },
			{ "33", B, trd, trd, O, H, },
			{ "34", R, trd, fst, E, H, },
			{ "35", B, trd, snd, O, H, },
			{ "36", R, trd, trd, E, H, }, };

	/*
	 * type это тип ставки, number это на что именно ставка
	 * type 1:
	 * Ставка на один номер (Straight Up), number соответствует номеру, выплата 35:1
	 * type 10:
	 * Column Bet, на столбец, number номер столбца, выплата 2:1, zero автоматический проигрыш
	 * type 11:
	 * Dozen Bet, на дюжину, number номер дюжины, выплата 2:1, zero автоматический проигрыш
	 * type 12:
	 * Red or Black, на цвет, number цвет (0=R,1=B), выплата 1:1, zero автоматический проигрыш
	 * type 13:
	 * Even or Odd, чет-нечет, number тип (0=even,1=odd), выплата 1:1, zero автоматический проигрыш
	 * type 14:
	 * Low or High, 0=1-18,1=19-36, выплата 1:1, zero автоматический проигрыш
	 */
	private static enum GameType
	{
		StraightUp,
		ColumnBet,
		DozenBet,
		RedOrBlack,
		EvenOrOdd,
		LowOrHigh;
	}

	@Bypass("services.Roulette:dialog")
	public void dialog(Player player, NpcInstance npc, String[] param)
	{
		Functions.show(HtmCache.getInstance().getHtml("scripts/services/roulette.htm", player).replaceFirst("%min%", Util.formatAdena(Config.SERVICES_ROULETTE_MIN_BET)).replaceFirst("%max%", Util.formatAdena(Config.SERVICES_ROULETTE_MAX_BET)), player);
	}

	@Bypass("services.Roulette:play")
	public void play(Player player, NpcInstance npc, String[] param)
	{
		GameType type;
		long bet = 0;
		String betID = "";
		try
		{
			if(param.length != 3)
				throw new NumberFormatException();

			type = GameType.valueOf(param[0]);
			betID = param[1].trim();
			bet = Long.parseLong(param[2]);

			if(type == GameType.StraightUp && (betID.length() > 2 || Integer.parseInt(betID) < 0 || Integer.parseInt(betID) > 36))
				throw new NumberFormatException();
		}
		catch(NumberFormatException e)
		{
			Functions.show("Invalid value input!<br><Button ALIGN=LEFT ICON=\"RETURN\" action=\"bypass -h htmbypass_services.Roulette:dialog\">Back</button>", player);
			return;
		}

		if(bet < Config.SERVICES_ROULETTE_MIN_BET)
		{
			Functions.show("Too small bet!<br><Button ALIGN=LEFT ICON=\"RETURN\" action=\"bypass -h htmbypass_services.Roulette:dialog\">Back</button>", player);
			return;
		}
		if(bet > Config.SERVICES_ROULETTE_MAX_BET)
		{
			Functions.show("Too large bet!<br><Button ALIGN=LEFT ICON=\"RETURN\" action=\"bypass -h htmbypass_services.Roulette:dialog\">Back</button>", player);
			return;
		}

		if(player.getAdena() < bet)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			Functions.show("You do not have enough adena!<br><Button ALIGN=LEFT ICON=\"RETURN\" action=\"bypass -h htmbypass_services.Roulette:dialog\">Back</button>", player);
			return;
		}

		String[] roll = Numbers[Rnd.get(Numbers.length)];
		int result = check(betID, roll, type);

		String ret = HtmCache.getInstance().getHtml("scripts/services/rouletteresult.htm", player);

		if(result == 0)
		{
			ItemFunctions.deleteItem(player, 57, bet);
			ret = ret.replace("%result%", "<font color=\"FF0000\">Fail!</font>");
		}
		else
		{
			ItemFunctions.addItem(player, 57, bet * result);
			ret = ret.replace("%result%", "<font color=\"00FF00\">Succes!</font>");
		}

		ret = ret.replace("%bettype%", new CustomMessage("Roulette." + type.toString()).toString(player));
		ret = ret.replace("%betnumber%", type == GameType.StraightUp ? betID : new CustomMessage("Roulette." + betID).toString(player));
		ret = ret.replace("%number%", roll[0]);
		ret = ret.replace("%color%", new CustomMessage("Roulette." + roll[1]).toString(player));
		ret = ret.replace("%evenness%", new CustomMessage("Roulette." + roll[4]).toString(player));
		ret = ret.replace("%column%", new CustomMessage("Roulette." + roll[3]).toString(player));
		ret = ret.replace("%dozen%", new CustomMessage("Roulette." + roll[2]).toString(player));
		ret = ret.replace("%highness%", new CustomMessage("Roulette." + roll[5]).toString(player));
		ret = ret.replace("%param%", param[0] + " " + param[1] + " " + param[2]);

		Functions.show(ret, player);
	}

	/**
	 * Возвращает множитель ставки или 0 при проигрыше
	 */
	private static final int check(String betID, String[] roll, GameType type)
	{
		switch(type)
		{
			case StraightUp:
				if(betID.equals(roll[0]))
					return 35;
				return 0;
			case ColumnBet:
				if(betID.equals(roll[3]))
					return 2;
				return 0;
			case DozenBet:
				if(betID.equals(roll[2]))
					return 2;
				return 0;
			case RedOrBlack:
				if(betID.equals(roll[1]))
					return 1;
				return 0;
			case EvenOrOdd:
				if(betID.equals(roll[4]))
					return 1;
				return 0;
			case LowOrHigh:
				if(betID.equals(roll[5]))
					return 1;
				return 0;
			default:
				return 0; // WTF?
		}
	}
}