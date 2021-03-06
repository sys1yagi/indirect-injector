package com.sys1yagi.indirectinjector;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IndirectInjector {

    private static final ObjectMap OBJECT_WEAK_MAP = new ObjectMap();

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Object> STRONG_REFERENCE_LIST = new ArrayList<Object>();

    public synchronized static void addDependency(Object context, Object dependency) {
        addDependency(context, dependency, false);
    }

    public synchronized static void addDependency(Object context, Object dependency,
            boolean isStrong) {
        Logger.d("addDependency:" + context.hashCode());
        Dependencies dependencies = getDependencies(context);
        dependencies.add(new WeakReference<Object>(dependency));
        if (isStrong) {
            STRONG_REFERENCE_LIST.add(dependency);
        }
    }

    public synchronized static void releaseDependencies(Object context) {
        Dependencies dependencies = getDependencies(context, false);
        if (dependencies != null) {
            for (WeakReference<Object> dependency : dependencies) {
                STRONG_REFERENCE_LIST.remove(dependency.get());
            }
        }
    }

    public synchronized static void inject(Object context, Object target) {
        Logger.d("inject...:" + context.hashCode());
        List<WeakReference<Object>> dependencies = getDependencies(context);

        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            Logger.d("field:" + field.getName());
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (javax.inject.Inject.class.getName()
                        .equals(annotation.annotationType().getName())) {
                    Logger.d("match");
                    inject(target, field, dependencies);
                }
            }
        }
        Logger.d("inject...end");
    }

    public synchronized static void sweep() {
        OBJECT_WEAK_MAP.sweep(new ObjectMap.OnReleasedListener() {
            @Override
            public void onReleased(WeakReference<Object> context, WeakReference<Object> object) {
                Object dependency = object.get();
                if (dependency != null) {
                    STRONG_REFERENCE_LIST.remove(dependency);
                }
            }
        });
    }

    private static void inject(Object target, Field field,
            List<WeakReference<Object>> dependencies) {
        field.setAccessible(true);
        Class<?> type = field.getType();
        Logger.d("type:" + type);
        Logger.d("map size:" + dependencies.size());
        try {
            for (WeakReference<Object> dependency : dependencies) {
                Object object = dependency.get();
                if (object != null) {
                    Logger.d("seek:" + object.getClass().getName());
                    if (type.isAssignableFrom(object.getClass())) {
                        Logger.d("do inject!");
                        try {
                            Logger.d("set dependency!");
                            field.set(target, object);
                        } catch (IllegalAccessException e) {
                            Logger.d("inject failed!");
                            e.printStackTrace();
                        }
                        return;
                    }
                } else {
                    Logger.d("object null");
                }
            }
        } finally {
            field.setAccessible(false);
        }
    }

    private synchronized static Dependencies getDependencies(Object context) {
        return getDependencies(context, true);
    }

    private synchronized static Dependencies getDependencies(Object context,
            boolean isCreateNewDependenciesIfNotFound) {
        sweep();
        Dependencies dependencies = null;

        for (Entry<WeakReference<Object>, Dependencies> entry : OBJECT_WEAK_MAP
                .entrySet()) {
            Object object = entry.getKey().get();
            if (context.equals(object)) {
                Logger.d("find object map");
                dependencies = entry.getValue();
                break;
            }
        }

        if (dependencies == null && isCreateNewDependenciesIfNotFound) {
            dependencies = new Dependencies();
            WeakReference<Object> objectWeakReference = new WeakReference<Object>(context);
            Logger.d("new map : " + objectWeakReference.hashCode() + " ," + context.hashCode());
            OBJECT_WEAK_MAP.put(objectWeakReference, dependencies);
        }

        return dependencies;
    }
}
