package org.qtproject.qt5.android.bindings;

import android.app.Application;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class QtApplication extends Application {
    public static final String QtTAG = "Qt";
    private static String activityClassName;
    public static Object m_delegateObject = null;
    public static HashMap<String, ArrayList<Method>> m_delegateMethods = new HashMap<>();
    public static Method dispatchKeyEvent = null;
    public static Method dispatchPopulateAccessibilityEvent = null;
    public static Method dispatchTouchEvent = null;
    public static Method dispatchTrackballEvent = null;
    public static Method onKeyDown = null;
    public static Method onKeyMultiple = null;
    public static Method onKeyUp = null;
    public static Method onTouchEvent = null;
    public static Method onTrackballEvent = null;
    public static Method onActivityResult = null;
    public static Method onCreate = null;
    public static Method onKeyLongPress = null;
    public static Method dispatchKeyShortcutEvent = null;
    public static Method onKeyShortcut = null;
    public static Method dispatchGenericMotionEvent = null;
    public static Method onGenericMotionEvent = null;
    public static Method onRequestPermissionsResult = null;
    private static int stackDeep = -1;

    /* loaded from: classes2.dex */
    public static class InvokeResult {
        public boolean invoked = false;
        public Object methodReturns = null;
    }

    public static void setQtContextDelegate(Class<?> clazz, Object listener) {
        Method[] methods;
        Field[] fields;
        m_delegateObject = listener;
        activityClassName = clazz.getCanonicalName();
        ArrayList<Method> delegateMethods = new ArrayList<>();
        for (Method m : listener.getClass().getMethods()) {
            if (m.getDeclaringClass().getName().startsWith("org.qtproject.qt5.android")) {
                delegateMethods.add(m);
            }
        }
        ArrayList<Field> applicationFields = new ArrayList<>();
        for (Field f : QtApplication.class.getFields()) {
            if (f.getDeclaringClass().getName().equals(QtApplication.class.getName())) {
                applicationFields.add(f);
            }
        }
        Iterator<Method> it = delegateMethods.iterator();
        while (it.hasNext()) {
            Method delegateMethod = it.next();
            try {
                clazz.getDeclaredMethod(delegateMethod.getName(), delegateMethod.getParameterTypes());
                if (m_delegateMethods.containsKey(delegateMethod.getName())) {
                    m_delegateMethods.get(delegateMethod.getName()).add(delegateMethod);
                } else {
                    ArrayList<Method> delegateSet = new ArrayList<>();
                    delegateSet.add(delegateMethod);
                    m_delegateMethods.put(delegateMethod.getName(), delegateSet);
                }
                Iterator<Field> it2 = applicationFields.iterator();
                while (it2.hasNext()) {
                    Field applicationField = it2.next();
                    if (applicationField.getName().equals(delegateMethod.getName())) {
                        try {
                            applicationField.set(null, delegateMethod);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e2) {
            }
        }
    }

    @Override // android.app.Application
    public void onTerminate() {
        if (m_delegateObject != null && m_delegateMethods.containsKey("onTerminate")) {
            invokeDelegateMethod(m_delegateMethods.get("onTerminate").get(0), new Object[0]);
        }
        super.onTerminate();
    }

    public static InvokeResult invokeDelegate(Object... args) {
        InvokeResult result = new InvokeResult();
        if (m_delegateObject == null) {
            return result;
        }
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (-1 == stackDeep) {
            int it = 0;
            while (true) {
                if (it >= elements.length) {
                    break;
                } else if (!elements[it].getClassName().equals(activityClassName)) {
                    it++;
                } else {
                    stackDeep = it;
                    break;
                }
            }
        }
        int it2 = stackDeep;
        if (-1 == it2) {
            return result;
        }
        String methodName = elements[it2].getMethodName();
        if (!m_delegateMethods.containsKey(methodName)) {
            return result;
        }
        Iterator<Method> it3 = m_delegateMethods.get(methodName).iterator();
        while (it3.hasNext()) {
            Method m = it3.next();
            if (m.getParameterTypes().length == args.length) {
                result.methodReturns = invokeDelegateMethod(m, args);
                result.invoked = true;
                return result;
            }
        }
        return result;
    }

    public static Object invokeDelegateMethod(Method m, Object... args) {
        try {
            return m.invoke(m_delegateObject, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
