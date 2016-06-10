package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Value
@AllArgsConstructor
@Builder
public class Cpu {

    @XmlAttribute
    private String mode;

    @XmlAttribute
    private String match;

    @XmlElement
    private Model model;

    @lombok.Value
    @AllArgsConstructor
    public static class Model {

        @XmlAttribute
        private String fallback;

        @XmlValue
        private String value;
    }
}
