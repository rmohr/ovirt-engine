package org.ovirt.engine.core.bll.architecture;

import java.util.List;

import org.ovirt.engine.core.bll.VmCommand;
import org.ovirt.engine.core.common.businessentities.storage.Disk;
import org.ovirt.engine.core.common.businessentities.storage.DiskInterface;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;
import org.ovirt.engine.core.utils.archstrategy.ArchCommand;


public class HasMaximumNumberOfDisks implements ArchCommand {
    private boolean hasMaximum;
    private List<Disk> allDisks;

    public HasMaximumNumberOfDisks(Guid vmId) {
        allDisks = DbFacade.getInstance().getDiskDao().getAllForVm(vmId);
    }

    private int countDisks(final DiskInterface diskType) {
        return (int) allDisks.stream().filter(a -> a.getDiskInterface() == diskType).count();
    }

    @Override
    public void runForX86_64() {
        hasMaximum = VmCommand.MAX_IDE_SLOTS == countDisks(DiskInterface.IDE);
    }

    @Override
    public void runForPPC64() {
        hasMaximum = VmCommand.MAX_SPAPR_SCSI_DISKS == countDisks(DiskInterface.SPAPR_VSCSI);
    }

    public boolean returnValue() {
        return hasMaximum;
    }
}
