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
package custom.NpcLocationInfo;

import ai.AbstractNpcAI;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.commons.util.Util.isInteger;

/**
 * Npc Location Info AI.
 * @author Nyaran
 */
public class NpcLocationInfo extends AbstractNpcAI
{
	private static final List<Integer> NPC = new ArrayList<>();
	{
		NPC.add(30598);
		NPC.add(30599);
		NPC.add(30600);
		NPC.add(30601);
		NPC.add(30602);
	}
	private static final List<Integer> NPCRADAR = new ArrayList<>();
	{
		// Talking Island
		NPCRADAR.add(30006); // Gatekeeper Roxxy
		NPCRADAR.add(30039); // Captain Gilbert
		NPCRADAR.add(30040); // Guard Leon
		NPCRADAR.add(30041); // Guard Arnold
		NPCRADAR.add(30042); // Guard Abellos
		NPCRADAR.add(30043); // Guard Johnstone
		NPCRADAR.add(30044); // Guard Chiperan
		NPCRADAR.add(30045); // Guard Kenyos
		NPCRADAR.add(30046); // Guard Hanks
		NPCRADAR.add(30283); // Blacksmith Altran
		NPCRADAR.add(30003); // Trader Silvia
		NPCRADAR.add(30004); // Trader Katerina
		NPCRADAR.add(30001); // Trader Lector
		NPCRADAR.add(30002); // Trader Jackson
		NPCRADAR.add(30031); // High Priest Biotin
		NPCRADAR.add(30033); // Magister Baulro
		NPCRADAR.add(30035); // Magister Harrys
		NPCRADAR.add(30032); // Priest Yohanes
		NPCRADAR.add(30036); // Priest Petron
		NPCRADAR.add(30026); // Grand Master Bitz
		NPCRADAR.add(30027); // Master Gwinter
		NPCRADAR.add(30029); // Master Minia
		NPCRADAR.add(30028); // Master Pintage
		NPCRADAR.add(30054); // Warehouse Keeper Rant
		NPCRADAR.add(30055); // Warehouse Keeper Rolfe
		NPCRADAR.add(30005); // Warehouse Keeper Wilford
		NPCRADAR.add(30048); // Darin
		NPCRADAR.add(30312); // Lighthouse Keeper Rockswell
		NPCRADAR.add(30368); // Lilith
		NPCRADAR.add(30049); // Bonnie
		NPCRADAR.add(30047); // Wharf Manager Firon
		NPCRADAR.add(30497); // Edmond
		NPCRADAR.add(30050); // Elias
		NPCRADAR.add(30311); // Sir Collin Windawood
		NPCRADAR.add(30051); // Cristel
		// Dark Elf Village
		NPCRADAR.add(30134); // Gatekeeper Jasmine
		NPCRADAR.add(30224); // Sentry Knight Rayla
		NPCRADAR.add(30348); // Sentry Nelsya
		NPCRADAR.add(30355); // Sentry Roselyn
		NPCRADAR.add(30347); // Sentry Marion
		NPCRADAR.add(30432); // Sentry Irene
		NPCRADAR.add(30356); // Sentry Altima
		NPCRADAR.add(30349); // Sentry Jenna
		NPCRADAR.add(30346); // Sentry Kayleen
		NPCRADAR.add(30433); // Sentry Kathaway
		NPCRADAR.add(30357); // Sentry Kristin
		NPCRADAR.add(30431); // Sentry Eriel
		NPCRADAR.add(30430); // Sentry Trionell
		NPCRADAR.add(30307); // Blacksmith Karrod
		NPCRADAR.add(30138); // Trader Minaless
		NPCRADAR.add(30137); // Trader Vollodos
		NPCRADAR.add(30135); // Trader Iria
		NPCRADAR.add(30136); // Trader Payne
		NPCRADAR.add(30143); // Master Trudy
		NPCRADAR.add(30360); // Master Harant
		NPCRADAR.add(30145); // Master Vlasty
		NPCRADAR.add(30135); // Magister Harne
		NPCRADAR.add(30144); // Tetrarch Vellior
		NPCRADAR.add(30358); // Tetrarch Thifiell
		NPCRADAR.add(30359); // Tetrarch Kaitar
		NPCRADAR.add(30141); // Tetrarch Talloth
		NPCRADAR.add(30139); // Warehouse Keeper Dorankus
		NPCRADAR.add(30140); // Warehouse Keeper Erviante
		NPCRADAR.add(30350); // Warehouse Freightman Carlon
		NPCRADAR.add(30421); // Varika
		NPCRADAR.add(30419); // Arkenia
		NPCRADAR.add(30130); // Abyssal Celebrant Undrias
		NPCRADAR.add(30351); // Astaron
		NPCRADAR.add(30353); // Jughead
		NPCRADAR.add(30354); // Jewel
		// Elven Village
		NPCRADAR.add(30146); // Gatekeeper Mirabel
		NPCRADAR.add(30285); // Sentinel Gartrandell
		NPCRADAR.add(30284); // Sentinel Knight Alberius
		NPCRADAR.add(30221); // Sentinel Rayen
		NPCRADAR.add(30217); // Sentinel Berros
		NPCRADAR.add(30219); // Sentinel Veltress
		NPCRADAR.add(30220); // Sentinel Starden
		NPCRADAR.add(30218); // Sentinel Kendell
		NPCRADAR.add(30216); // Sentinel Wheeler
		NPCRADAR.add(30363); // Blacksmith Aios
		NPCRADAR.add(30149); // Trader Creamees
		NPCRADAR.add(30150); // Trader Herbiel
		NPCRADAR.add(30148); // Trader Ariel
		NPCRADAR.add(30147); // Trader Unoren
		NPCRADAR.add(30155); // Master Ellenia
		NPCRADAR.add(30156); // Master Cobendell
		NPCRADAR.add(30157); // Magister Greenis
		NPCRADAR.add(30158); // Magister Esrandell
		NPCRADAR.add(30154); // Hierarch Asterios
		NPCRADAR.add(30153); // Warehouse Keeper Markius
		NPCRADAR.add(30152); // Warehouse Keeper Julia
		NPCRADAR.add(30151); // Warehouse Freightman Chad
		NPCRADAR.add(30423); // Northwind
		NPCRADAR.add(30414); // Rosella
		NPCRADAR.add(31853); // Treant Bremec
		NPCRADAR.add(30223); // Arujien
		NPCRADAR.add(30362); // Andellia
		NPCRADAR.add(30222); // Alshupes
		NPCRADAR.add(30371); // Thalia
		NPCRADAR.add(31852); // Pixy Murika
		// Dwarven Village
		NPCRADAR.add(30540); // Gatekeeper Wirphy
		NPCRADAR.add(30541); // Protector Paion
		NPCRADAR.add(30542); // Defender Runant
		NPCRADAR.add(30543); // Defender Ethan
		NPCRADAR.add(30544); // Defender Cromwell
		NPCRADAR.add(30545); // Defender Proton
		NPCRADAR.add(30546); // Defender Dinkey
		NPCRADAR.add(30547); // Defender Tardyon
		NPCRADAR.add(30548); // Defender Nathan
		NPCRADAR.add(30531); // Iron Gate's Lockirin
		NPCRADAR.add(30532); // Golden Wheel's Spiron
		NPCRADAR.add(30533); // Silver Scale's Balanki
		NPCRADAR.add(30534); // Bronze Key's Keef
		NPCRADAR.add(30535); // Filaur of the Gray Pillar
		NPCRADAR.add(30536); // Black Anvil's Arin
		NPCRADAR.add(30525); // Head Blacksmith Bronk
		NPCRADAR.add(30526); // Blacksmith Brunon
		NPCRADAR.add(30527); // Blacksmith Silvera
		NPCRADAR.add(30518); // Trader Garita
		NPCRADAR.add(30519); // Trader Mion
		NPCRADAR.add(30516); // Trader Reep
		NPCRADAR.add(30517); // Trader Shari
		NPCRADAR.add(30520); // Warehouse Chief Reed
		NPCRADAR.add(30521); // Warehouse Freightman Murdoc
		NPCRADAR.add(30522); // Warehouse Keeper Airy
		NPCRADAR.add(30523); // Collector Gouph
		NPCRADAR.add(30524); // Collector Pippi
		NPCRADAR.add(30537); // Daichir, Priest of the Eart
		NPCRADAR.add(30650); // Priest of the Earth Gerald
		NPCRADAR.add(30538); // Priest of the Earth Zimenf
		NPCRADAR.add(30539); // Priestess of the Earth Chichirin
		NPCRADAR.add(30671); // Captain Croto
		NPCRADAR.add(30651); // Wanderer Dorf
		NPCRADAR.add(30550); // Gauri Twinklerock
		NPCRADAR.add(30554); // Miner Bolter
		NPCRADAR.add(30553); // Maryse Redbonnet
		// Orc Village
		NPCRADAR.add(30576); // Gatekeeper Tamil
		NPCRADAR.add(30577); // Praetorian Rukain
		NPCRADAR.add(30578); // Centurion Nakusin
		NPCRADAR.add(30579); // Centurion Tamai
		NPCRADAR.add(30580); // Centurion Parugon
		NPCRADAR.add(30581); // Centurion Orinak
		NPCRADAR.add(30582); // Centurion Tiku
		NPCRADAR.add(30583); // Centurion Petukai
		NPCRADAR.add(30584); // Centurion Vapook
		NPCRADAR.add(30569); // Prefect Brukurse
		NPCRADAR.add(30570); // Prefect Karukia
		NPCRADAR.add(30571); // Seer Tanapi
		NPCRADAR.add(30572); // Seer Livina
		NPCRADAR.add(30564); // Blacksmith Sumari
		NPCRADAR.add(30560); // Trader Uska
		NPCRADAR.add(30561); // Trader Papuma
		NPCRADAR.add(30558); // Trader Jakal
		NPCRADAR.add(30559); // Trader Kunai
		NPCRADAR.add(30562); // Warehouse Keeper Grookin
		NPCRADAR.add(30563); // Warehouse Keeper Imantu
		NPCRADAR.add(30565); // Flame Lord Kakai
		NPCRADAR.add(30566); // Atuba Chief Varkees
		NPCRADAR.add(30567); // Neruga Chief Tantus
		NPCRADAR.add(30568); // Urutu Chief Hatos
		NPCRADAR.add(30585); // Tataru Zu Hestui
		NPCRADAR.add(30587); // Gantaki Zu Urutu
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		if (isInteger(event))
		{
			htmltext = null;
			final int npcId = Integer.parseInt(event);
			if (NPCRADAR.contains(npcId))
			{
				int x = 0, y = 0, z = 0;
				final Spawn spawn = SpawnTable.getInstance().getAnySpawn(npcId);
				if (spawn != null)
				{
					x = spawn.getX();
					y = spawn.getY();
					z = spawn.getZ();
				}
				addRadar(player, x, y, z);
				htmltext = "MoveToLoc.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		int npcId = npc.getId();
		if (NPC.contains(npcId))
		{
			htmltext = npcId + ".htm";
		}
		return htmltext;
	}
	
	private NpcLocationInfo()
	{
		addStartNpc(NPC);
		addTalkId(NPC);
	}
	
	public static AbstractNpcAI provider() {
		return new NpcLocationInfo();
	}
}
