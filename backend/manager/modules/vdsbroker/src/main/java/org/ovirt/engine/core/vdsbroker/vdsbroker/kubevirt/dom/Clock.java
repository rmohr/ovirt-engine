package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.YesNoJsonDeserializer;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.YesNoJsonSerializer;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson.YesNoMarshaller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Value
@AllArgsConstructor
@Builder
public class Clock {

    @XmlAttribute
    private String offset;

    @XmlElement
    private List<Timer> timer;

    @lombok.Value
    @AllArgsConstructor
    @Builder
    public static class Timer {

        @XmlAttribute
        String name;

        @XmlAttribute
        String tickpolicy;

        @JsonSerialize(using = YesNoJsonSerializer.class)
        @JsonDeserialize(using = YesNoJsonDeserializer.class)
        @XmlJavaTypeAdapter(YesNoMarshaller.class)
        @XmlAttribute
        Boolean present;

        @XmlAttribute
        String track;

        @XmlAttribute
        Integer frequency;

    }
}
