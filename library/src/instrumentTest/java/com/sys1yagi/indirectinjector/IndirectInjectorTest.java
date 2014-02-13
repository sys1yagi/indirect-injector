package com.sys1yagi.indirectinjector;

import junit.framework.TestCase;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.inject.Inject;

public class IndirectInjectorTest extends TestCase {

    interface Listener {

        public void onFinish();
    }

    public static class Target {

        @Inject
        private Integer mNo = -1;

        @Inject
        private Listener mListener;

        public Target(Object context) {
            IndirectInjector.inject(context, this);
        }

        public Integer getNo() {
            return mNo;
        }

        public Listener getListener() {
            return mListener;
        }
    }

    public void testInject() {
        Object context = new Object();
        Integer dependency = new Integer(10);
        IndirectInjector.addDependency(context, dependency);

        Target target = new Target(context);

        assertEquals(dependency, target.getNo());
    }

    public void testInjectInterface() {
        Object context = new Object();
        Listener dependency = new Listener() {
            @Override
            public void onFinish() {

            }
        };
        IndirectInjector.addDependency(context, dependency);
        Target target = new Target(context);

        assertEquals(dependency, target.getListener());
    }

    public void testLeak() {
        Object context = new Object();
        Listener dependency = new Listener() {
            @Override
            public void onFinish() {

            }
        };

        WeakReference<Object> reference = new WeakReference<Object>(context);
        WeakReference<Object> reference2 = new WeakReference<Object>(dependency);

        IndirectInjector.addDependency(context, dependency);
        Target target = new Target(context);

        assertEquals(dependency, target.getListener());

        context = null;
        dependency = null;
        target.mListener = null;
        System.gc();
        assertNull(reference.get());
        assertNull(reference2.get());
    }

    public void testStrongRef() throws Exception {
        Object context = new Object();
        Object dependency = new Object();

        final int initialCount = getStrongRefCount();

        IndirectInjector.addDependency(context, dependency, true);

        assertEquals(initialCount + 1, getStrongRefCount());

        context = null;
        System.gc();
        IndirectInjector.sweep();

        assertEquals(initialCount, getStrongRefCount());
    }

    private static int getStrongRefCount() throws Exception {
        final Field listField = IndirectInjector.class.getDeclaredField("STRONG_REFERENCE_LIST");
        assert (listField.getModifiers() & Modifier.STATIC) != 0;
        try {
            listField.setAccessible(true);

            final List<?> list = (List<?>) listField.get(null);
            return list.size();
        } finally {
            listField.setAccessible(false);
        }
    }
}
