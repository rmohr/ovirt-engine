package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

// TODO make that class useless (vAdvisor should map that to just a value in json)
@lombok.Value
@AllArgsConstructor
public class Value<T> {

    @XmlValue
    @JsonProperty("value")
    private T value;

}
