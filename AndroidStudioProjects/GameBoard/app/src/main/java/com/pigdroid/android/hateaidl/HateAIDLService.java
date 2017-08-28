package com.pigdroid.android.hateaidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import com.pigdroid.android.hateaidl.aidl.InvocationParametersBean;
import com.pigdroid.android.hateaidl.aidl.InvocationReturnBean;
import com.pigdroid.android.hateaidl.aidl.RemoteServiceAIDL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HateAIDLService extends Service {

    public HateAIDLService() {
    }

    RemoteServiceAIDL.Stub binder = new RemoteServiceAIDL.Stub() {
        @Override
        public void invoke(InvocationParametersBean invocation, InvocationReturnBean ret) throws RemoteException {
            Method method = null;
            try {
               method = ClassUtils.findMethod(HateAIDLService.this.getClass(), invocation);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            Object value = null;
            try {
                value = method.invoke(HateAIDLService.this, invocation.getParameters());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            ret.setReturnValue(value);
        }
    };

    protected <T> T createListenerProxy(final Class<T> clazz) {
        Class[] interfaces = {clazz};
        T ret = (T) java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                InvocationReturnBean ret = new InvocationReturnBean();
                Intent intent = new Intent("com.pigdroid.android.hateaidl.NOTIFY_LISTENER");
                intent.putExtra(Intent.EXTRA_KEY_EVENT, ClassUtils.getInvocationParametersBean(method, args, clazz));
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, ret);
                sendBroadcast(intent);
//                LocalBroadcastManager.getInstance(HateAIDLService.this).sendBroadcastSync(intent);
                ret = intent.getExtras().getParcelable(Intent.EXTRA_RETURN_RESULT);
                if (ret.isError()) {
                    throw ret.getErrorClass().newInstance();
                }
                return ret.getReturnValue();
            }
        });
        return ret;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
