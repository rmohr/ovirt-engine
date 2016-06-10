package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Builder
@Value
@JsonIgnoreProperties({"graphics", "video", "controller"})
public class Devices {

    @XmlElement
    private String emulator;

    @XmlElement
    private List<GraphicsDevice> graphics;

    @XmlElement
    private List<VideoDevice> video;

    @XmlElement
    private List<ControllerDevice> controller;

    @XmlTransient
    public Iterable<Device> getDevices() {
        return Iterables.concat(graphics, video, controller);
    }

}
