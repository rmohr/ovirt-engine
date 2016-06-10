package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Value
@Builder
@AllArgsConstructor
public class Quantity<T> {

        @XmlValue
        private T value;

        @XmlAttribute
        private String unit;
}
