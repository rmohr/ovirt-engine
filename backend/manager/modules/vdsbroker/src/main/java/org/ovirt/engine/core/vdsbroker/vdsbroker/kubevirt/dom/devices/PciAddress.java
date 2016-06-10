package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.BigHexMarshaller;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.BigHexSerializer;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.HexDeserializer;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.SmallHexMarshaller;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.SmallHexSerializer;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.TinyHexMarshaller;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.TinyHexSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class PciAddress implements Address {

    @XmlAttribute
    @JsonSerialize(using = BigHexSerializer.class)
    @JsonDeserialize(using = HexDeserializer.class)
    @XmlJavaTypeAdapter(BigHexMarshaller.class)
    private Integer domain;

    @XmlAttribute
    @JsonSerialize(using = SmallHexSerializer.class)
    @JsonDeserialize(using = HexDeserializer.class)
    @XmlJavaTypeAdapter(SmallHexMarshaller.class)
    private Integer bus;

    @XmlAttribute
    @JsonSerialize(using = SmallHexSerializer.class)
    @JsonDeserialize(using = HexDeserializer.class)
    @XmlJavaTypeAdapter(SmallHexMarshaller.class)
    private Integer slot;

    @XmlAttribute
    @JsonSerialize(using = TinyHexSerializer.class)
    @JsonDeserialize(using = HexDeserializer.class)
    @XmlJavaTypeAdapter(TinyHexMarshaller.class)
    private Integer function;

    @XmlAttribute(name = "multifunction")
    private String multiFunction;

    @Override
    @XmlAttribute
    public String getType() {
        return "pci";
    }
}
