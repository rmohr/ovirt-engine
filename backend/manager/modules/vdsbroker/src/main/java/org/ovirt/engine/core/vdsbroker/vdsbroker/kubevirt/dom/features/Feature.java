package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Feature {

    @JsonIgnore
    @XmlTransient
    public String getType();

    String ACPI = "acpi";
    String APIC = "apic";
    String PAE = "pae";
}
