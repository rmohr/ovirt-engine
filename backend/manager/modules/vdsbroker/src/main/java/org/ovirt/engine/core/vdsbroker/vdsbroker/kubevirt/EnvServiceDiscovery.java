package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt;

import java.util.Objects;

import javax.enterprise.inject.Alternative;

import org.ovirt.engine.core.compat.Guid;

/**
 * Implementation mostly for testing purposes when running everything on a single node.
 */
@Alternative
public class EnvServiceDiscovery implements ServiceDiscovery {

    @Override
    public String discover(String serviceName, String servicePort) {
        Objects.requireNonNull(serviceName);
        String host = System.getenv(serviceName.toUpperCase() + "_SERVICE_HOST");
        String port = System.getenv(serviceName.toUpperCase() + "_SERVICE_PORT");
        return String.format("http://%s:%s", host, port);
    }

    @Override
    public String discover(String serviceName, Guid vdsId) {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(vdsId);
        return discover(serviceName);
    }
}
