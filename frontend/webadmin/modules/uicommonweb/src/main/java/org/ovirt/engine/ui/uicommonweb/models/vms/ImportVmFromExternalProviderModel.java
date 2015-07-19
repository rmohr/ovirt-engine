package org.ovirt.engine.ui.uicommonweb.models.vms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ovirt.engine.core.common.businessentities.ArchitectureType;
import org.ovirt.engine.core.common.businessentities.Quota;
import org.ovirt.engine.core.common.businessentities.QuotaEnforcementTypeEnum;
import org.ovirt.engine.core.common.businessentities.StorageDomain;
import org.ovirt.engine.core.common.businessentities.StoragePool;
import org.ovirt.engine.core.common.businessentities.VDSGroup;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.network.Network;
import org.ovirt.engine.core.common.businessentities.network.VmNetworkInterface;
import org.ovirt.engine.core.common.businessentities.network.VnicProfileView;
import org.ovirt.engine.core.common.businessentities.storage.Disk;
import org.ovirt.engine.core.common.businessentities.storage.DiskImage;
import org.ovirt.engine.core.common.businessentities.storage.VolumeType;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.VdcQueryParametersBase;
import org.ovirt.engine.core.common.queries.VdcQueryReturnValue;
import org.ovirt.engine.core.common.queries.VdcQueryType;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.frontend.INewAsyncCallback;
import org.ovirt.engine.ui.uicommonweb.Linq;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.dataprovider.AsyncDataProvider;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.ListModel;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;
import org.ovirt.engine.ui.uicommonweb.models.clusters.ClusterListModel;
import org.ovirt.engine.ui.uicommonweb.models.quota.QuotaListModel;
import org.ovirt.engine.ui.uicompat.ConstantsManager;
import org.ovirt.engine.ui.uicompat.Event;
import org.ovirt.engine.ui.uicompat.EventArgs;
import org.ovirt.engine.ui.uicompat.FrontendMultipleQueryAsyncResult;
import org.ovirt.engine.ui.uicompat.IEventListener;
import org.ovirt.engine.ui.uicompat.IFrontendMultipleQueryAsyncCallback;

public abstract class ImportVmFromExternalProviderModel extends ImportVmModel {
    public static final String ON_DISK_LOAD = "OnDiskLoad"; //$NON-NLS-1$

    private Map<Guid, ArrayList<Quota>> storageQuotaMap;
    private ListModel<StorageDomain> storage;
    private ListModel<VolumeType> allocation;
    private final Map<String, ImportDiskData> diskImportDataMap = new HashMap<String, ImportDiskData>();
    private VmImportDiskListModel importDiskListModel;
    private List<Network> networks;
    private List<VnicProfileView> networkProfiles;
    private ListModel<String> iso;
    private EntityModel<Boolean> attachDrivers;

    protected ImportVmFromExternalProviderModel(VmImportGeneralModel vmImportGeneralModel, VmImportDiskListModel importDiskListModel,
            VmImportInterfaceListModel vmImportInterfaceListModel, final ClusterListModel<Void> cluster, final QuotaListModel clusterQuota) {
        super(cluster, clusterQuota);
        this.importDiskListModel = importDiskListModel;
        setStorage(new ListModel<StorageDomain>());
        setAllocation(new ListModel<VolumeType>());
        getAllocation().setItems(Arrays.asList(VolumeType.Sparse, VolumeType.Preallocated));
        setIso(new ListModel<String>());
        getIso().setIsChangeable(false);
        setAttachDrivers(new EntityModel<Boolean>(false));
        getAttachDrivers().getEntityChangedEvent().addListener(new IEventListener<EventArgs>() {
            @Override
            public void eventRaised(Event<? extends EventArgs> ev, Object sender, EventArgs args) {
                getIso().setIsChangeable(getAttachDrivers().getEntity());
            }
        });
        getClusterQuota().setIsAvailable(false);
        setDetailList(vmImportGeneralModel, vmImportInterfaceListModel, importDiskListModel);
    }

    private final Map<String, ImportNetworkData> networkImportDataMap = new HashMap<String, ImportNetworkData>();

    public List<Network> getNetworks() {
        return networks;
    }

    public List<VnicProfileView> getNetworkProfiles() {
        return networkProfiles;
    }

    public SearchableListModel getImportDiskListModel() {
        return importDiskListModel;
    }

    public void init(List<VM> externalVms, final Guid dataCenterId) {
        setCloseCommand(new UICommand(null, this)
        .setTitle(ConstantsManager.getInstance().getConstants().close())
        .setIsDefault(true)
        .setIsCancel(true));

        setTargetArchitecture(externalVms.iterator().next().getClusterArch());
        super.setItems(
                new INewAsyncCallback() {
                    @Override
                    public void onSuccess(Object model, Object returnValue) {
                        doInit(dataCenterId);
                    }
                },
                externalVms);
    }

    protected void doInit(final Guid dataCenterId) {
        AsyncDataProvider.getInstance().getDataCenterById(new AsyncQuery(this, new INewAsyncCallback() {
            @Override
            public void onSuccess(Object model, Object returnValue) {
                final StoragePool dataCenter = (StoragePool) returnValue;
                if (dataCenter == null) {
                    return;
                }

                setStoragePool(dataCenter);
                getClusterQuota().setIsAvailable(dataCenter.getQuotaEnforcementType() != QuotaEnforcementTypeEnum.DISABLED);
                getCluster().getSelectedItemChangedEvent().addListener(clusterChangedListener);

                // get cluster
                getCluster().setItems(null);
                AsyncDataProvider.getInstance().getNetworkList(new AsyncQuery(ImportVmFromExternalProviderModel.this, new INewAsyncCallback() {
                    @Override
                    public void onSuccess(Object model, Object returnValue) {
                        networks = (List<Network>) returnValue;

                        AsyncDataProvider.getInstance().getVnicProfilesByDcId(new AsyncQuery(ImportVmFromExternalProviderModel.this,  new INewAsyncCallback() {
                            @Override
                            public void onSuccess(Object model, Object returnValue) {
                                networkProfiles = (List<VnicProfileView>) returnValue;
                                initClusterAndStorage(dataCenter);
                            }
                        }), dataCenter.getId());
                    }
                }), dataCenter.getId());
            }
        }),
        dataCenterId);
     }

    private void initClusterAndStorage(StoragePool dataCenter) {
        AsyncDataProvider.getInstance().getClusterByServiceList(new AsyncQuery(ImportVmFromExternalProviderModel.this, new INewAsyncCallback() {
            @Override
            public void onSuccess(Object model, Object returnValue) {
                List<VDSGroup> clusters = (List<VDSGroup>) returnValue;

                ArchitectureType targetArch = getTargetArchitecture();
                if (targetArch != null) {
                    clusters = AsyncDataProvider.getInstance().filterByArchitecture(clusters, targetArch);
                }
                getCluster().setItems(clusters);
                getCluster().setSelectedItem(Linq.firstOrDefault(clusters));

                // get storage domains
                AsyncDataProvider.getInstance().getStorageDomainList(new AsyncQuery(ImportVmFromExternalProviderModel.this,
                        new INewAsyncCallback() {

                    @Override
                    public void onSuccess(Object model, Object returnValue) {
                        List<StorageDomain> storageDomains = (List<StorageDomain>) returnValue;
                        // filter storage domains
                        List<StorageDomain> filteredStorageDomains = new ArrayList<>();
                        for (StorageDomain domain : storageDomains) {
                            if (Linq.isDataActiveStorageDomain(domain)) {
                                filteredStorageDomains.add(domain);
                            }
                        }

                        getStorage().setItems(filteredStorageDomains);
                        if (getClusterQuota().getIsAvailable()) {
                            initQuotaForStorageDomains();
                        } else {
                            initDisksStorageDomainsList();
                        }
                    }

                }),
                getStoragePool().getId());
            }
        }),
        dataCenter.getId(), true, false);
    }

    protected void addDiskImportData(String alias,
            ArrayList<StorageDomain> storageDomains,
            VolumeType volumeType, EntityModel collapseSnapshots) {
        ImportDiskData data = new ImportDiskData();
        data.setCollapseSnapshot(collapseSnapshots);
        data.setAllStorageDomains(new ArrayList<StorageDomain>());
        data.setStorageDomains(storageDomains);
        data.setVolumeType(volumeType);
        data.setStorageQuotaList(storageQuotaMap);
        diskImportDataMap.put(alias, data);
    }

    protected void addNetworkImportData(String macAddr,
            List<Network> networks) {
        ImportNetworkData data = new ImportNetworkData();
        data.setNetworks(networks);
        networkImportDataMap.put(macAddr, data);
    }

    protected void initNetworksList() {
        for (Object item : getItems()) {
            ImportVmData importVmData = (ImportVmData) item;
            VM vm = importVmData.getVm();
            for (VmNetworkInterface inface : vm.getInterfaces()) {
                addNetworkImportData(inface.getMacAddress(), networks);
            }
        }
    }

    protected void initDisksStorageDomainsList() {
        for (Object item : getItems()) {
            ImportVmData importVmData = (ImportVmData) item;
            VM vm = importVmData.getVm();

            for (Disk disk : vm.getDiskMap().values()) {
                DiskImage diskImage = (DiskImage) disk;
                addDiskImportData(
                        diskImage.getDiskAlias(),
                        new ArrayList<StorageDomain>(),
                        diskImage.getVolumeType(),
                        importVmData.getCollapseSnapshots());
            }
        }
        initIsoImages();
        postInitDisks();
    }

    private void postInitDisks() {
        stopProgress();
    }

    private void initIsoImages() {
        AsyncDataProvider.getInstance().getIrsImageList(new AsyncQuery(this,
                new INewAsyncCallback() {
                    @Override
                    public void onSuccess(Object target, Object returnValue) {
                        List<String> images = (List<String>) returnValue;
                        getIso().setItems(images);
                        getIso().setSelectedItem(tryToFindVirtioTools(images));
                    }
                }),
                getStoragePool().getId(),
                false);
    }

    private String tryToFindVirtioTools(List<String> isos) {
        for (String iso : isos) {
            if (iso.startsWith("virtio-win")) { //$NON-NLS-1$
                return iso;
            }
        }
        return isos.isEmpty() ? null : isos.get(0);
    }

    private void initQuotaForStorageDomains() {
        List<VdcQueryType> queryTypeList = new ArrayList<>();
        List<VdcQueryParametersBase> queryParamsList = new ArrayList<>();
        for (StorageDomain storage : getStorage().getItems()) {
            queryTypeList.add(VdcQueryType.GetAllRelevantQuotasForStorage);
            queryParamsList.add(new IdQueryParameters(storage.getId()));
        }
        storageQuotaMap = new HashMap<Guid, ArrayList<Quota>>();
        Frontend.getInstance().runMultipleQueries(queryTypeList,
                queryParamsList,
                new IFrontendMultipleQueryAsyncCallback() {
                    @Override
                    public void executed(FrontendMultipleQueryAsyncResult result) {
                        Iterator<VdcQueryReturnValue> returnValuesIter = result.getReturnValues().iterator();
                        Iterator<StorageDomain> domainsIter = getStorage().getItems().iterator();
                        boolean noQuota = true;
                        while(domainsIter.hasNext()) {
                            ArrayList<Quota> quotaList = returnValuesIter.next().getReturnValue();
                            noQuota = noQuota && quotaList.isEmpty();
                            storageQuotaMap.put(
                                    domainsIter.next().getId(),
                                    quotaList);
                        }
                        if (noQuota
                                && QuotaEnforcementTypeEnum.HARD_ENFORCEMENT.equals(storagePool.getQuotaEnforcementType())) {
                            showCloseMessage(ConstantsManager.getInstance()
                                    .getConstants().missingQuotaStorageEnforceMode());
                        }
                        initDisksStorageDomainsList();
                    }
                });
    }

    @Override
    protected String getListName() {
        return "ImportVmFromExternalProviderModel"; //$NON-NLS-1$
    }

    public ListModel<StorageDomain> getStorage() {
        return storage;
    }

    public void setStorage(ListModel<StorageDomain> storage) {
        this.storage = storage;
    }

    public ListModel<VolumeType> getAllocation() {
        return allocation;
    }

    public void setAllocation(ListModel<VolumeType> allocation) {
        this.allocation = allocation;
    }

    public ImportDiskData getDiskImportData(String alias) {
        ImportDiskData importData = diskImportDataMap.get(alias);
        if (importData != null) {
            if (storage.getSelectedItem() == null) {
                importData.setSelectedStorageDomain((StorageDomain) storage.getSelectedItem());
            }
        }
        return importData;
    }

    public ImportNetworkData getNetworkImportData(String macAddr) {
        ImportNetworkData importData = networkImportDataMap.get(macAddr);
        return importData;
    }

    @Override
    public boolean validate() {
        return true;
    }

    public ListModel<String> getIso() {
        return iso;
    }

    public void setIso(ListModel<String> iso) {
        this.iso = iso;
    }

    public EntityModel<Boolean> getAttachDrivers() {
        return attachDrivers;
    }

    public void setAttachDrivers(EntityModel<Boolean> attachTools) {
        this.attachDrivers = attachTools;
    }
}