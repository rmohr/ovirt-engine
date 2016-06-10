package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;

@lombok.Value
@AllArgsConstructor
public class VCpu {

    @XmlAttribute
    String placement;

    @XmlElement
    Integer value;
}
