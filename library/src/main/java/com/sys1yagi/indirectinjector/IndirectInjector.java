package com.sys1yagi.indirectinjector;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndirectInjector {

    private static Map<WeakReference<Object>, List<WeakReference<Object>>> OBJECT_WEAK_MAP
            = new HashMap<WeakReference<Object>, List<WeakReference<Object>>>();

    private static List<Object> STRONG_REFERENCE_LIST = new ArrayList<Object>();

    public synchronized static void addDependency(Object context, Object dependency) {
        addDependency(context, dependency, false);
    }

    public synchronized static void addDependency(Object context, Object dependency,
            boolean isStrong) {
        Logger.d("addDependency:" + context.hashCode());
        List<WeakReference<Object>> dependencies = getDependencies(context);
        dependencies.add(new WeakReference<Object>(dependency));
        if (isStrong) {
            STRONG_REFERENCE_LIST.add(dependency);
        }
    }

    public synchronized static <T> void inject(Object context, T target) {
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

        Map<WeakReference<Object>, List<WeakReference<Object>>> newMap
                = new HashMap<WeakReference<Object>, List<WeakReference<Object>>>();

        for (WeakReference<Object> objectWeakReference : OBJECT_WEAK_MAP.keySet()) {
            if (objectWeakReference.get() != null) {
                newMap.put(objectWeakReference, OBJECT_WEAK_MAP.get(objectWeakReference));
            } else {
                List<WeakReference<Object>> dependencies = OBJECT_WEAK_MAP.get(objectWeakReference);
                for (Object dependency : dependencies) {
                    STRONG_REFERENCE_LIST.remove(dependency);
                }
                Logger.d("release item:" + objectWeakReference.hashCode());
            }
        }

        OBJECT_WEAK_MAP = newMap;
    }

    private static <T> void inject(T target, Field field,
            List<WeakReference<Object>> dependencies) {
        field.setAccessible(true);
        Class type = field.getType();
        Logger.d("type:" + field.getType());
        Logger.d("map size:" + dependencies.size());
        try {
            for (WeakReference<Object> dependency : dependencies) {
                Object object = dependency.get();
                if (object != null) {
                    Logger.d("seek:" + object.getClass().getName());
                    if (object.getClass().equals(type)
                            || type.isAssignableFrom(object.getClass())
                            ) {
                        Logger.d("do inject!");
                        try {
                            Logger.d("set dependency!");
                            field.set((T) target, object);
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

    private synchronized static List<WeakReference<Object>> getDependencies(Object context) {
        sweep();
        List<WeakReference<Object>> dependencies = null;

        for (WeakReference<Object> objectWeakReference : OBJECT_WEAK_MAP.keySet()) {
            Object object = objectWeakReference.get();
            if (context.equals(object)) {
                Logger.d("find object map");
                dependencies = OBJECT_WEAK_MAP.get(objectWeakReference);
                break;
            }
        }

        if (dependencies == null) {
            dependencies = new ArrayList<WeakReference<Object>>();
            WeakReference<Object> objectWeakReference = new WeakReference<Object>(context);
            Logger.d("new map : " + objectWeakReference.hashCode() + " ," + context.hashCode());
            OBJECT_WEAK_MAP.put(objectWeakReference, dependencies);
        }

        return dependencies;
    }
}
