package npc.bypasses.global;

import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExEnSoulExtractionShow;
import l2s.gameserver.network.l2.s2c.ExShowEnsoulWindow;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class Ensoul extends ListenerHook implements OnInitScriptListener
{
	@Override
	public void onInit()
	{
		addHookGlobal(ListenerHookType.NPC_ASK);
	}

	@Override
	public void onNpcAsk(NpcInstance npc, int ask, long reply, Player player)
	{
		if(ask == -503)
		{
			if(reply == 300)
				player.sendPacket(ExShowEnsoulWindow.STATIC);
			else if(reply == 301)
				npc.showChatWindow(player, "villagemaster/rune_exchange001.htm", true);
			else if(reply == 302)
				player.sendPacket(ExEnSoulExtractionShow.STATIC);
		}
		else if(ask == -504)
		{
			int needed_id = 0;
			int give_id = 0;
			switch((int)reply)
			{
				case 101:
					needed_id = 29818;
					give_id = 70000;
					break;
				case 102:
					needed_id = 29819;
					give_id = 70001;
					break;
				case 103:
					needed_id = 29820;
					give_id = 70002;
					break;
				case 104:
					needed_id = 29821;
					give_id = 70003;
					break;
				case 105:
					needed_id = 29822;
					give_id = 70004;
					break;
				case 106:
					needed_id = 29823;
					give_id = 70005;
					break;
				case 107:
					needed_id = 29824;
					give_id = 70006;
					break;
				case 108:
					needed_id = 29825;
					give_id = 70007;
					break;
				case 109:
					needed_id = 29826;
					give_id = 70008;
					break;
				case 110:
					needed_id = 29827;
					give_id = 70009;
					break;
				case 111:
					needed_id = 29828;
					give_id = 70010;
					break;
				case 112:
					needed_id = 29829;
					give_id = 70011;
					break;
				case 113:
					needed_id = 29830;
					give_id = 70012;
					break;
				case 201:
					needed_id = 29838;
					give_id = 70000;
					break;
				case 202:
					needed_id = 29839;
					give_id = 70001;
					break;
				case 203:
					needed_id = 29840;
					give_id = 70002;
					break;
				case 204:
					needed_id = 29841;
					give_id = 70003;
					break;
				case 205:
					needed_id = 29842;
					give_id = 70004;
					break;
				case 206:
					needed_id = 29843;
					give_id = 70005;
					break;
				case 207:
					needed_id = 29844;
					give_id = 70006;
					break;
				case 208:
					needed_id = 29845;
					give_id = 70007;
					break;
				case 209:
					needed_id = 29846;
					give_id = 70008;
					break;
				case 210:
					needed_id = 29847;
					give_id = 70009;
					break;
				case 211:
					needed_id = 29848;
					give_id = 70010;
					break;
				case 212:
					needed_id = 29849;
					give_id = 70011;
					break;
				case 213:
					needed_id = 29850;
					give_id = 70012;
					break;
				case 301:
					needed_id = 29858;
					give_id = 70000;
					break;
				case 302:
					needed_id = 29859;
					give_id = 70001;
					break;
				case 303:
					needed_id = 29860;
					give_id = 70002;
					break;
				case 304:
					needed_id = 29861;
					give_id = 70003;
					break;
				case 305:
					needed_id = 29862;
					give_id = 70004;
					break;
				case 306:
					needed_id = 29863;
					give_id = 70005;
					break;
				case 307:
					needed_id = 29864;
					give_id = 70006;
					break;
				case 308:
					needed_id = 29865;
					give_id = 70007;
					break;
				case 309:
					needed_id = 29866;
					give_id = 70008;
					break;
				case 310:
					needed_id = 29867;
					give_id = 70009;
					break;
				case 311:
					needed_id = 29868;
					give_id = 70010;
					break;
				case 312:
					needed_id = 29869;
					give_id = 70011;
					break;
				case 313:
					needed_id = 29870;
					give_id = 70012;
					break;
				case 401:
					needed_id = 29878;
					give_id = 70000;
					break;
				case 402:
					needed_id = 29879;
					give_id = 70001;
					break;
				case 403:
					needed_id = 29870;
					give_id = 70002;
					break;
				case 404:
					needed_id = 29871;
					give_id = 70003;
					break;
				case 405:
					needed_id = 29872;
					give_id = 70004;
					break;
				case 406:
					needed_id = 29873;
					give_id = 70005;
					break;
				case 407:
					needed_id = 29874;
					give_id = 70006;
					break;
				case 408:
					needed_id = 29875;
					give_id = 70007;
					break;
				case 409:
					needed_id = 29876;
					give_id = 70008;
					break;
				case 410:
					needed_id = 29877;
					give_id = 70009;
					break;
				case 411:
					needed_id = 29878;
					give_id = 70010;
					break;
				case 412:
					needed_id = 29879;
					give_id = 70011;
					break;
				case 413:
					needed_id = 29880;
					give_id = 70012;
					break;
				case 501:
					needed_id = 29898;
					give_id = 70000;
					break;
				case 502:
					needed_id = 29899;
					give_id = 70001;
					break;
				case 503:
					needed_id = 29900;
					give_id = 70002;
					break;
				case 504:
					needed_id = 29901;
					give_id = 70003;
					break;
				case 505:
					needed_id = 29902;
					give_id = 70004;
					break;
				case 506:
					needed_id = 29903;
					give_id = 70005;
					break;
				case 507:
					needed_id = 29904;
					give_id = 70006;
					break;
				case 508:
					needed_id = 29905;
					give_id = 70007;
					break;
				case 509:
					needed_id = 29906;
					give_id = 70008;
					break;
				case 510:
					needed_id = 29907;
					give_id = 70009;
					break;
				case 511:
					needed_id = 29908;
					give_id = 70010;
					break;
				case 512:
					needed_id = 29909;
					give_id = 70011;
					break;
				case 513:
					needed_id = 29910;
					give_id = 70012;
					break;
				case 601:
					needed_id = 29918;
					give_id = 70000;
					break;
				case 602:
					needed_id = 29919;
					give_id = 70001;
					break;
				case 603:
					needed_id = 29920;
					give_id = 70002;
					break;
				case 604:
					needed_id = 29921;
					give_id = 70003;
					break;
				case 605:
					needed_id = 29922;
					give_id = 70004;
					break;
				case 606:
					needed_id = 29923;
					give_id = 70005;
					break;
				case 607:
					needed_id = 29924;
					give_id = 70006;
					break;
				case 608:
					needed_id = 29925;
					give_id = 70007;
					break;
				case 609:
					needed_id = 29926;
					give_id = 70008;
					break;
				case 610:
					needed_id = 29927;
					give_id = 70009;
					break;
				case 611:
					needed_id = 29928;
					give_id = 70010;
					break;
				case 612:
					needed_id = 29929;
					give_id = 70011;
					break;
				case 613:
					needed_id = 29930;
					give_id = 70012;
					break;
				case 701:
					needed_id = 29938;
					give_id = 70000;
					break;
				case 702:
					needed_id = 29939;
					give_id = 70001;
					break;
				case 703:
					needed_id = 29940;
					give_id = 70002;
					break;
				case 704:
					needed_id = 29941;
					give_id = 70003;
					break;
				case 705:
					needed_id = 29942;
					give_id = 70004;
					break;
				case 706:
					needed_id = 29943;
					give_id = 70005;
					break;
				case 707:
					needed_id = 29944;
					give_id = 70006;
					break;
				case 708:
					needed_id = 29945;
					give_id = 70007;
					break;
				case 709:
					needed_id = 29946;
					give_id = 70008;
					break;
				case 710:
					needed_id = 29947;
					give_id = 70009;
					break;
				case 711:
					needed_id = 29948;
					give_id = 70010;
					break;
				case 712:
					needed_id = 29949;
					give_id = 70011;
					break;
				case 713:
					needed_id = 29950;
					give_id = 70012;
					break;
				case 801:
					needed_id = 29958;
					give_id = 70000;
					break;
				case 802:
					needed_id = 29959;
					give_id = 70001;
					break;
				case 803:
					needed_id = 29960;
					give_id = 70002;
					break;
				case 804:
					needed_id = 29961;
					give_id = 70003;
					break;
				case 805:
					needed_id = 29962;
					give_id = 70004;
					break;
				case 806:
					needed_id = 29963;
					give_id = 70005;
					break;
				case 807:
					needed_id = 29964;
					give_id = 70006;
					break;
				case 808:
					needed_id = 29965;
					give_id = 70007;
					break;
				case 809:
					needed_id = 29966;
					give_id = 70008;
					break;
				case 810:
					needed_id = 29967;
					give_id = 70009;
					break;
				case 811:
					needed_id = 29968;
					give_id = 70010;
					break;
				case 812:
					needed_id = 29969;
					give_id = 70011;
					break;
				case 813:
					needed_id = 29970;
					give_id = 70012;
					break;
			}

			if(needed_id == 0 || give_id == 0)
			{
				player.sendMessage("invalid ask/reply: " + reply);
				return;
			}

			long count_have = ItemFunctions.getItemCount(player, needed_id);
			if(count_have < 2) //exchange ratio 2:1
			{
				npc.showChatWindow(player, "villagemaster/rune_exchange002.htm", true);
				return;
			}

			if(count_have % 2 != 0) //odd
				count_have -= 1;

			long to_give = count_have / 2;

			ItemFunctions.deleteItem(player, needed_id, count_have); //will notify
			ItemFunctions.addItem(player, give_id, to_give); //will notify
		}
	}
}