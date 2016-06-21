package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ControllerDevice implements Device {

    private final String family = "controller";

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String model;

    @XmlAttribute
    private Integer index;

    @XmlElement
    private PciAddress address;
}

