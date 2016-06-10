package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class VideoDevice implements Device{

    private final String family = "video";

    private String type;

    @XmlAttribute
    private Integer vram;

    @XmlAttribute
    private Integer heads;

    @XmlElement
    private PciAddress address;
}
