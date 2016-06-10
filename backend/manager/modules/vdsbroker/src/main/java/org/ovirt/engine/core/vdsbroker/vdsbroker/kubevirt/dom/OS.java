package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.*;

@lombok.Value
@AllArgsConstructor
@Builder
public class OS {

    private Type type;

    @lombok.Value
    @AllArgsConstructor
    @Builder
    public static class Type {

        @XmlAttribute
        private String arch;

        @XmlAttribute
        private String machine;

        @XmlValue
        private String value;

        @XmlElement
        private Boot boot;

        @XmlElement
        private BootMenu bootMenu;

    }

    @lombok.Value
    @AllArgsConstructor
    @Builder
    public static class Boot {
        @XmlAttribute
        private String dev;
    }

    @lombok.Value
    @AllArgsConstructor
    @Builder
    public static class BootMenu {
        @XmlAttribute
        private Boolean enabled;
    }
}
