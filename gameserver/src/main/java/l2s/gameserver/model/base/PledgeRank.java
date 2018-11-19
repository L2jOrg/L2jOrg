package l2s.gameserver.model.base;

/**
 * @author Bonux
**/
public enum PledgeRank
{
	/*0*/VAGABOND, // Кочевник
	/*1*/VASSAL, // Вассал
	/*2*/HEIR, // Наследник
	/*3*/KNIGHT, // Рыцарь
	/*4*/WISEMAN, // Старейшина
	/*5*/BARON, // Барон
	/*6*/VISCOUNT, // Виконт
	/*7*/COUNT, // Граф
	/*8*/MARQUIS, // Маркиз
	/*9*/DUKE, // Герцог
	/*10*/GRAND_DUKE, // Великий герцог
	/*11*/DISTINGUISHED_KING, // Король
	/*12*/EMPEROR; // Император

	public static final PledgeRank[] VALUES = values();
}