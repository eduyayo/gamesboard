package com.pigdroid.android.hateaidl;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.pigdroid.android.hateaidl.aidl.InvocationParametersBean;
import com.pigdroid.android.hateaidl.aidl.InvocationReturnBean;
import com.pigdroid.android.hateaidl.aidl.RemoteServiceAIDL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HateAIDLConnection<T> {

    private final Class<T> clazz;
    private Context context;

    private RemoteServiceAIDL client;
    private ServiceConnection serviceConnection;
    private boolean disconnecting = false;

    BroadcastReceiver broadcastReceiver;

    private T proxy;

    public static interface Listener<T> {
        void bound(T proxy);
    }

    private Class<T> getGeneric() {
        return clazz;
    }

    public HateAIDLConnection(Context context, final Listener<T> listener, Class<T> clazz) {
        this.context = context;
        this.clazz = clazz;

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                InvocationParametersBean invocation = extras.getParcelable(Intent.EXTRA_KEY_EVENT);
                InvocationReturnBean ret = extras.getParcelable(Intent.EXTRA_RETURN_RESULT);
                Class<?> interfaceClass = null;
                try {
                    interfaceClass = Class.forName(invocation.getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (interfaceClass.isInstance(listener)) {
                    try {
                        if (!"toString".equals(invocation.getMethodName())) {
                            Method method = ClassUtils.findMethod(interfaceClass, invocation);
                            ret.setReturnValue(method.invoke(listener, invocation.getParameters()));
                        }
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(invocation.toString(), e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("com.pigdroid.android.hateaidl.NOTIFY_LISTENER");
        context.registerReceiver(broadcastReceiver, intentFilter);

        Class[] interfaces = {getGeneric()};
        proxy = (T) java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (client == null) {
                    new AsyncTask<Void, Void, Object>() {

                        @Override
                        protected Object doInBackground(Void... params) {
                            while (client == null) {
                                try {
                                    Thread.sleep(100L);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            InvocationReturnBean ret = new InvocationReturnBean();
                            try {
                                client.invoke(ClassUtils.getInvocationParametersBean(method, args), ret);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                            return ret.getReturnValue();
                        }

                    }.execute();
                    return getSafeReturn(method);
                }
                InvocationReturnBean ret = new InvocationReturnBean();
                client.invoke(ClassUtils.getInvocationParametersBean(method, args), ret);
                if (ret.isError()) {
                    throw ret.getErrorClass().newInstance();
                }
                return ret.getReturnValue();
            }
        });

        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                client = (RemoteServiceAIDL) RemoteServiceAIDL.Stub.asInterface(binder);
                listener.bound(proxy);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //TODO
                client = null;
                initConnection();
            }
//            proxy = (T) Proxy.newProxyInstance();

        };

        initConnection();

    }

//    private String[] getParameterClassNames(Method method) {
//        Class<?>[] types = method.getParameterTypes();
//        int max = types.length;
//        String[] ret = new String[max];
//        for (int i = 0; i < max; i++) {
//            ret[i] = types.getClass().getName();
//        }
//        return ret;
//    }

    public Object getSafeReturn(Method method) {
        return null;
    }

    private void initConnection() {
        if (!disconnecting) {
            Intent intent = null;
            try {
                intent = new Intent(context, Class.forName(getGeneric().getName() + "Impl"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void disconnect() {
        context.unregisterReceiver(broadcastReceiver);
        disconnecting = true;
        context.unbindService(serviceConnection);
        context = null;
        client = null;
        proxy = null;
    }

}
