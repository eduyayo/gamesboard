// RemoteServiceAIDL.aidl
package com.pigdroid.android.hateaidl.aidl;

import com.pigdroid.android.hateaidl.aidl.InvocationParametersBean;
import com.pigdroid.android.hateaidl.aidl.InvocationReturnBean;

// Declare any non-default types here with import statements

interface RemoteServiceAIDL {

  void invoke(in InvocationParametersBean invocation, out InvocationReturnBean ret);

}
