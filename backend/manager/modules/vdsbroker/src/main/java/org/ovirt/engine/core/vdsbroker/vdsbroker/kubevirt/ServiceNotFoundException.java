package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt;

public class ServiceNotFoundException extends Exception {

    public ServiceNotFoundException(Throwable cause) {
        super(cause);
    }

    public ServiceNotFoundException(String format) {
        super(format);
    }
}
