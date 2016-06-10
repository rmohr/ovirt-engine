package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public interface Device {

    @XmlAttribute
    String getType();

    @XmlTransient
    String getFamily();
}
