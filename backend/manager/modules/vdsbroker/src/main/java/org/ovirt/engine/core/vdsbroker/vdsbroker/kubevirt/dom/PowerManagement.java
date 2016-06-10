package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.YesNoJsonDeserializer;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.YesNoJsonSerializer;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.YesNoMarshaller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Value
@AllArgsConstructor
@Builder
public class PowerManagement {

    @XmlElement(name = "suspend-to-mem")
    @JsonProperty("suspend-to-mem")
    private Suspend suspendToMem;

    @XmlElement(name = "suspend-to-disk")
    @JsonProperty("suspend-to-disk")
    private Suspend suspendToDisk;

    @lombok.Value
    @AllArgsConstructor
    public static class Suspend {

        @JsonSerialize(using = YesNoJsonSerializer.class)
        @JsonDeserialize(using = YesNoJsonDeserializer.class)
        @XmlJavaTypeAdapter(YesNoMarshaller.class)
        @XmlAttribute
        Boolean enabled;

    }
}
