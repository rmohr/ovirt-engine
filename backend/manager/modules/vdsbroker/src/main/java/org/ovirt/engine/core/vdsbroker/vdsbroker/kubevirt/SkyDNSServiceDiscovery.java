package org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt;

import java.util.Hashtable;
import java.util.Objects;

import javax.enterprise.inject.Alternative;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.ovirt.engine.core.compat.Guid;

/**
 * Look up services via DNS.
 */
@Alternative
public class SkyDNSServiceDiscovery implements ServiceDiscovery {

    private final DirContext ctx;

    public SkyDNSServiceDiscovery() throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        ctx = new InitialDirContext(env);

    }

    @Override
    public String discover(String serviceName) throws ServiceNotFoundException {
        Objects.requireNonNull(serviceName);
        try {
            Attributes attrs;
            attrs = ctx.getAttributes(""serviceName.toLowerCase(), new String[] { "SRV" });

            NamingEnumeration<?> ports = attrs.get("srv").getAll();
            if (ports.hasMore()) {
                String host = serviceName.toLowerCase();
                Integer port = Integer.valueOf((String) ports.next());
                return String.format("http://%s:%s", host, port);
            } else {
                throw new ServiceNotFoundException(
                        String.format("No port for service %s found", serviceName.toLowerCase()));
            }
        } catch (NamingException e) {
            throw new ServiceNotFoundException(e.getCause());
        }
    }

    @Override
    public String discover(String serviceName, Guid vdsId) throws ServiceNotFoundException {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(vdsId);
        return discover(serviceName);
    }

}
