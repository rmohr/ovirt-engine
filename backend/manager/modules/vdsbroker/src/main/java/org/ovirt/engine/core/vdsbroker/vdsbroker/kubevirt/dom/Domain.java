package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices.Devices;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features.Feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Value
@AllArgsConstructor
@Builder
@XmlRootElement(name = "domain")
public class Domain {

    @XmlElement
    @JsonProperty
    private Value<UUID> uuid;

    @XmlElement
    private Quantity<Integer> memory;

    @XmlElement
    private Quantity<Integer> currentMemory;

    @XmlElement
    private VCpu vcpu;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private Integer id;

    @XmlElement
    private Map<String, Feature> features;

    @XmlElement(name = "on_poweroff")
    @JsonProperty("on_poweroff")
    private String onPoweroff;

    @XmlElement(name = "on_reboot")
    @JsonProperty("on_reboot")
    private String onReboot;

    @XmlElement(name = "on_crash")
    @JsonProperty("on_crash")
    private String onCrash;

    @XmlElement(name = "pm")
    @JsonProperty("pm")
    private PowerManagement powerManagement;

    @XmlElement
    private Cpu cpu;

    @XmlElement
    private Clock clock;

    @XmlElement
    private Devices devices;
}
