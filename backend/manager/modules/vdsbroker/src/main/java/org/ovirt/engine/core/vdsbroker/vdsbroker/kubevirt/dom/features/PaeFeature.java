package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.features;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = Feature.PAE)
public class PaeFeature implements Feature {

    @Override
    public String getType() {
        return Feature.PAE;
    }
}
