package org.ovirt.engine.api.restapi.resource;

import org.ovirt.engine.api.model.QuotaClusterLimit;
import org.ovirt.engine.api.model.QuotaClusterLimits;
import org.ovirt.engine.api.resource.QuotaClusterLimitResource;
import org.ovirt.engine.api.resource.QuotaClusterLimitsResource;
import org.ovirt.engine.core.common.businessentities.Quota;
import org.ovirt.engine.core.common.businessentities.QuotaCluster;
import org.ovirt.engine.core.compat.Guid;

public class BackendQuotaClusterLimitsResource
        extends BackendQuotaLimitsResource<QuotaClusterLimits, QuotaClusterLimit>
        implements QuotaClusterLimitsResource {

    protected BackendQuotaClusterLimitsResource(Guid quotaId) {
        super(quotaId, QuotaClusterLimit.class);
    }

    @Override
    public QuotaClusterLimits list() {
        Quota quota = getQuota();
        QuotaClusterLimits limits = new QuotaClusterLimits();
        if (quota.getGlobalQuotaCluster() != null) {
            addLimit(quota.getGlobalQuotaCluster(), quotaId.toString(), limits, quota);
        } else if (quota.getQuotaClusters() != null) {
            for (QuotaCluster quotaCluster : quota.getQuotaClusters()) {
                addLimit(quotaCluster, quotaCluster.getClusterId().toString(), limits, quota);
            }
        }
        return limits;
    }

    private void addLimit(QuotaCluster quotaCluster, String id, QuotaClusterLimits limits, Quota quota) {
        QuotaClusterLimit limit = new QuotaClusterLimit();
        limit.setId(id);
        limits.getQuotaClusterLimits().add(addLinks(map(quota, limit)));
    }

    @Override
    public QuotaClusterLimitResource getLimitResource(String id) {
        return inject(new BackendQuotaClusterLimitResource(id, quotaId));
    }

    @Override
    protected void updateIncomingId(QuotaClusterLimit incoming, Quota entity) {
        if (incoming.isSetCluster() && incoming.getCluster().isSetId()) {
            incoming.setId(incoming.getCluster().getId());
        } else {
            incoming.setId(entity.getId().toString());
        }
    }
}
