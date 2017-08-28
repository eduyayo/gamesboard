package com.pigdroid.android.hateaidl.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class InvocationParametersBean implements Parcelable {

    private String methodName;
    private String className;
    private Object[] parameters;
    private String[] parameterClassNames;

    public String[] getParameterClassNames() {
        return parameterClassNames;
    }

    public void setParameterClassNames(String[] parameterClassNames) {
        this.parameterClassNames = parameterClassNames;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(methodName);
        dest.writeString(className);
        dest.writeSerializable(parameters);
        dest.writeSerializable(parameterClassNames);
    }

    public static final Parcelable.Creator<InvocationParametersBean> CREATOR
            = new Parcelable.Creator<InvocationParametersBean>() {
        public InvocationParametersBean createFromParcel(Parcel in) {
            return new InvocationParametersBean(in);
        }

        public InvocationParametersBean[] newArray(int size) {
            return new InvocationParametersBean[size];
        }
    };

    public InvocationParametersBean() {

    }

    private InvocationParametersBean(Parcel in) {
        methodName = in.readString();
        className = in.readString();
        parameters = (Object[]) in.readSerializable();
        parameterClassNames = (String[]) in.readSerializable();
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append(methodName);
        buff.append(" ");
        buff.append(parameterClassNames);
        return buff.toString();
    }
}
