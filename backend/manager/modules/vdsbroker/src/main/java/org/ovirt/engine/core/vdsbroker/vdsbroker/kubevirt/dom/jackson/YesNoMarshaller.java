package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class YesNoMarshaller extends XmlAdapter<String, Boolean> {

    @Override
    public Boolean unmarshal(String s) throws Exception {
        return s.equals("yes") ? true : false;
    }

    @Override
    public String marshal(Boolean aBoolean) throws Exception {
        return aBoolean ? "yes" : "no";
    }
}
