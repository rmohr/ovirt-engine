package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class GraphicsDevice implements Device {

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String port;

    @XmlAttribute(name = "autoport")
    @JsonProperty("autoport")
    private Boolean autoPort;

    @Override
    public String getFamily() {
        return "graphics";
    }

    @Override
    public String getType() {
        return type;
    }
}
