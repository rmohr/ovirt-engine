package org.ovirt.engine.arquillian.kubevirt;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ovirt.engine.arquillian.IntegrationTest;
import org.ovirt.engine.arquillian.TransactionalTestBase;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.di.Injector;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.HttpClientConnectionFactory;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.JacksonFactory;
import org.ovirt.engine.core.vdsbroker.vdsbroker.kubevirt.KubevirtVdsServer;

@Category(IntegrationTest.class)
public class KubevirtVdsServerTest extends TransactionalTestBase {

    @Before
    public void setUp() {
        System.out.println("test");
    }

    @Test
    public void test(){
        KubevirtVdsServer server = Injector.injectMembers(new KubevirtVdsServer(Guid.Empty));
    }

    @Deployment(name = "KubevirtVdsServerTest")
    public static JavaArchive deploy() {
        return createDeployment().addClasses(
                KubevirtVdsServer.class,
                HttpClientConnectionFactory.class,
                FakeServiceDiscovery.class,
                JacksonFactory.class
        );
    }
}
