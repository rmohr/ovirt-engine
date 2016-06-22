package org.ovirt.engine.arquillian.kubevirt;

import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.ServiceDiscovery;

public class FakeServiceDiscovery implements ServiceDiscovery{

    @Override
    public String discover(String serviceName) {
        return null;
    }

    @Override
    public String discover(String serviceName, Guid vdsId) {
        return null;
    }
}
