package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = Feature.APIC)
public class ApicFeature implements Feature {

    @Override
    public String getType() {
        return Feature.APIC;
    }
}
