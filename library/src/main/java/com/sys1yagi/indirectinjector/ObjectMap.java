package com.sys1yagi.indirectinjector;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ObjectMap extends HashMap<WeakReference<Object>, Dependencies> {

    public interface OnReleasedListener {

        public void onReleased(WeakReference<Object> context, WeakReference<Object> object);
    }

    public synchronized void sweep(OnReleasedListener listener) {
        Iterator<Entry<WeakReference<Object>, Dependencies>> it = entrySet()
                .iterator();
        while (it.hasNext()) {
            Entry<WeakReference<Object>, Dependencies> entry = it.next();
            WeakReference<Object> objectWeakReference = entry.getKey();

            if (objectWeakReference.get() == null) {
                it.remove();
                for (WeakReference<Object> dependencyRef : entry.getValue()) {
                    if (listener != null) {
                        listener.onReleased(objectWeakReference, dependencyRef);
                    }
                }
                Logger.d("release item:" + objectWeakReference.hashCode());
            }
        }
    }
}
