package village.master;

import village.master.Alliance.Alliance;
import village.master.Clan.Clan;
import village.master.DarkElfChange1.DarkElfChange1;
import village.master.DarkElfChange2.DarkElfChange2;
import village.master.DwarfBlacksmithChange1.DwarfBlacksmithChange1;
import village.master.DwarfBlacksmithChange2.DwarfBlacksmithChange2;
import village.master.DwarfWarehouseChange1.DwarfWarehouseChange1;
import village.master.DwarfWarehouseChange2.DwarfWarehouseChange2;
import village.master.ElfHumanClericChange2.ElfHumanClericChange2;
import village.master.ElfHumanFighterChange1.ElfHumanFighterChange1;
import village.master.ElfHumanFighterChange2.ElfHumanFighterChange2;
import village.master.ElfHumanWizardChange1.ElfHumanWizardChange1;
import village.master.ElfHumanWizardChange2.ElfHumanWizardChange2;
import village.master.FirstClassTransferTalk.FirstClassTransferTalk;
import village.master.OrcChange1.OrcChange1;
import village.master.OrcChange2.OrcChange2;

public class VillageMasterHandler {

    public static void main(String[] args) {
        Alliance.init();
        Clan.init();
        DarkElfChange1.init();
        DarkElfChange2.init();
        DwarfBlacksmithChange1.init();
        DwarfBlacksmithChange2.init();
        DwarfWarehouseChange1.init();
        DwarfWarehouseChange2.init();
        ElfHumanClericChange2.init();
        ElfHumanFighterChange1.init();
        ElfHumanFighterChange2.init();
        ElfHumanWizardChange1.init();
        ElfHumanWizardChange2.init();
        FirstClassTransferTalk.init();
        OrcChange1.init();
        OrcChange2.init();

    }
}
