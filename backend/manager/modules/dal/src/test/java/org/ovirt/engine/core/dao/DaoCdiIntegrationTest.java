package org.ovirt.engine.core.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;

import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.Reflections;

public class DaoCdiIntegrationTest {

    private static Set<Class<? extends Dao>> daoClasses;

    @BeforeClass
    public static void setUp() throws Exception {
        final Reflections reflections = new Reflections("org.ovirt.engine");

        daoClasses = Collections.unmodifiableSet(reflections.getSubTypesOf(Dao.class));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testSingletonDaoAnnotationPresent() {

        for (Class daoClass : daoClasses) {
            if (isConcreteClass(daoClass)) {
                assertTrue("A concrete Dao class has to be annotated with @ApplicationScoped or @Singleton: "
                        + daoClass.getCanonicalName(),
                        daoClass.isAnnotationPresent(ApplicationScoped.class)
                                || daoClass.isAnnotationPresent(Singleton.class));
            }
        }
    }

    @Test
    public void testSingletonDaoAnnotationNotPresentOnAbstractClass() {
        for (Class daoClass : daoClasses) {
            if (isAbstractClass(daoClass)) {
                assertFalse("An abstract Dao class cannot be annotated with @ApplicationScoped or @Singleton: "
                        + daoClass.getCanonicalName(),
                        daoClass.isAnnotationPresent(ApplicationScoped.class)
                                || daoClass.isAnnotationPresent(Singleton.class));
            }
        }
    }

    @Test
    public void testSingletonDaoAnnotationNotPresentOnParametrizedClass() {
        for (Class daoClass : daoClasses) {
            if (isParametrizedClass(daoClass)) {
                assertFalse(
                        "A parametrized Dao class cannot be annotated with @ApplicationScoped or @Singleton: "
                                + daoClass.getCanonicalName(),
                        daoClass.isAnnotationPresent(ApplicationScoped.class)
                                || daoClass.isAnnotationPresent(Singleton.class));
            }
        }
    }

    private boolean isParametrizedClass(Class clazz) {
        return clazz.getTypeParameters().length > 0;
    }

    private boolean isAbstractClass(Class clazz) {
        return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
    }

    private boolean isConcreteClass(Class daoClass) {
        return !isAbstractClass(daoClass);
    }
}