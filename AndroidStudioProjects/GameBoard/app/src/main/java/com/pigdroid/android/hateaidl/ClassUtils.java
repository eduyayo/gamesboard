package com.pigdroid.android.hateaidl;

import com.google.common.primitives.Primitives;
import com.pigdroid.android.hateaidl.aidl.InvocationParametersBean;

import java.lang.reflect.Method;

/**
 * Created by edu on 16/06/2015.
 */
public class ClassUtils {

    private ClassUtils() {

    }

    public static final Class<?>[] getParameterTypes(InvocationParametersBean invocation) {
        String[] parameterTypes = invocation.getParameterClassNames();
        int max = parameterTypes.length;
        Class<?>[] clazzez = new Class<?>[max];
        for (int i = 0; i < max; i++) {
            try {
                clazzez[i] = Class.forName(parameterTypes[i]);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return clazzez;
    }

    public static final Method findMethod(Class<?> clazz, InvocationParametersBean invocation) throws NoSuchMethodException {
        Method ret = null;
        Class<?>[] types = getParameterTypes(invocation);
        try {
            ret = clazz.getMethod(invocation.getMethodName(), types);
        } catch (NoSuchMethodException e) {
        }
        if (ret == null && types != null) {
            int max = types.length;
            for (int i = 0; i < max; i++) {
                if (Primitives.isWrapperType(types[i])) {
                    types[i] = Primitives.unwrap(types[i]);
                }
            }
            ret = clazz.getMethod(invocation.getMethodName(), types);
        }
        return ret;
    }

    public static final InvocationParametersBean getInvocationParametersBean(Method method, Object[] args) {
        InvocationParametersBean invocation = new InvocationParametersBean();
        invocation.setMethodName(method.getName());
        invocation.setParameters(args);
        invocation.setParameterClassNames(getParameterClassNames(method));
        return invocation;
    }

    private static String[] getParameterClassNames(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        int max = parameterTypes.length;
        String[] ret = new String[max];
        for (int i = 0; i < max; i++) {
            ret[i] = parameterTypes[i].getName();
        }
        return ret;
    }

    public static <T> InvocationParametersBean getInvocationParametersBean(Method method, Object[] args, Class<T> clazz) {
        InvocationParametersBean invocation = getInvocationParametersBean(method, args);
        invocation.setClassName(clazz.getName());
        return invocation;
    }
}
