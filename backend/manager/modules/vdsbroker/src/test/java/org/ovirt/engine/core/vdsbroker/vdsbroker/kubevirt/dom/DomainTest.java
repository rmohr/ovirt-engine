package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices.ControllerDevice;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices.Devices;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices.GraphicsDevice;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices.PciAddress;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.devices.VideoDevice;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features.AcpiFeature;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features.ApicFeature;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features.Feature;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features.PaeFeature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class DomainTest {


    @Test
    public void shouldConvertToXml() throws JsonProcessingException {
        Map<String, Feature> features =  new HashMap<>();
        features.put(Feature.ACPI, new AcpiFeature());
        features.put(Feature.APIC, new ApicFeature());
        features.put(Feature.PAE, new PaeFeature());
        Domain domain = Domain.builder()
                .uuid(new Value<UUID>(UUID.randomUUID()))
                .memory(Quantity.<Integer>builder().value(1234).unit("KiB").build())
                .type("kvm")
                .devices(Devices.builder().emulator("/usr/bin/qemu-kvm")
                        .video(Arrays.asList(
                                VideoDevice.builder().type("cirrus").heads(1).vram(16384).address(
                                        PciAddress.builder().domain(0).bus(0).slot(2).function(0).build()
                                ).build()
                        ))
                        .graphics(Arrays.asList(new GraphicsDevice("vnc", "-1", true)))
                        .controller(Arrays.asList(ControllerDevice.builder().type("usb").model("ich9-ehci1").index(1).build()))
                        .build())
                .id(23)
                .features(features)
                .onCrash("restart")
                .onPoweroff("destroy")
                .onReboot("restart")
                .clock(Clock.builder().offset("utc").timer(
                        Arrays.asList(
                                Clock.Timer.builder().name("rtc").tickpolicy("catchup").build(),
                                Clock.Timer.builder().name("pit").tickpolicy("delay").build(),
                                Clock.Timer.builder().name("hpet").present(false).build()
                        ))
                        .build())
                .cpu(Cpu.builder().match("exact").mode("custom").model(new Cpu.Model("allow", "kvm64"))
                        .build())
                .powerManagement(PowerManagement.builder()
                        .suspendToDisk(new PowerManagement.Suspend(true))
                        .suspendToMem(new PowerManagement.Suspend(false))
                        .build())
                .build();
        ObjectMapper xmlMapper = new XmlMapper();
        ObjectMapper jsonMapper = new ObjectMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonMapper.registerModule(new GuavaModule());
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(xmlMapper.getTypeFactory());
        // if ONLY using JAXB annotations:
        xmlMapper.setAnnotationIntrospector(introspector);

        System.out.println(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(domain));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(domain));
    }

}
