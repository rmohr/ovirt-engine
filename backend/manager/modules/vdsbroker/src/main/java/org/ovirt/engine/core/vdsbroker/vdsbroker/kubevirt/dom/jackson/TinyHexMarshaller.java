package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.dom.jackson;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TinyHexMarshaller extends XmlAdapter<String, Integer> {
    @Override
    public Integer unmarshal(String s) throws Exception {
        return Integer.parseInt(s, 16);
    }

    @Override
    public String marshal(Integer integer) throws Exception {
        return (String.format("0x%01X", integer));
    }
}
