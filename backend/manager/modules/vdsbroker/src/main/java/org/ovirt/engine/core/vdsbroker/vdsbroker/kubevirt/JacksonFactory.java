package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

@Singleton
public class JacksonFactory {

        private XmlMapper xmlMapper;
        private ObjectMapper jsonMapper;

        @PostConstruct
        public void prepare() {
                xmlMapper = new XmlMapper();
                jsonMapper = new ObjectMapper();
                xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(xmlMapper.getTypeFactory());
                xmlMapper.setAnnotationIntrospector(introspector);
        }

        @Singleton
        public XmlMapper getXmlMapper() {
                return xmlMapper;
        }

        @Singleton
        public ObjectMapper getJsonMapper() {
                return jsonMapper;
        }
}
