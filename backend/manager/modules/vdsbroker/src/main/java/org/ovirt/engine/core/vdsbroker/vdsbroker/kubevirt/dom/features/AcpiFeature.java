package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = Feature.ACPI)
public class AcpiFeature implements Feature {

    @Override
    public String getType() {
        return Feature.ACPI;
    }
}
