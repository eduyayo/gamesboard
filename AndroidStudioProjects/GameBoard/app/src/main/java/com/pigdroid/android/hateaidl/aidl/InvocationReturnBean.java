package com.pigdroid.android.hateaidl.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edu on 15/06/2015.
 */
public class InvocationReturnBean implements Parcelable {

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    private Object returnValue;
    private String errorClassName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(returnValue);
        dest.writeString(errorClassName);
    }
    public static final Parcelable.Creator<InvocationReturnBean> CREATOR
            = new Parcelable.Creator<InvocationReturnBean>() {
        public InvocationReturnBean createFromParcel(Parcel in) {
            return new InvocationReturnBean(in);
        }

        public InvocationReturnBean[] newArray(int size) {
            return new InvocationReturnBean[size];
        }
    };

    public InvocationReturnBean() {

    }

    private InvocationReturnBean(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        returnValue = in.readValue(getClass().getClassLoader());
        errorClassName = in.readString();
    }

    public String getErrorClassName() {
        return errorClassName;
    }

    public void setErrorClassName(String errorClassName) {
        this.errorClassName = errorClassName;
    }

    public Class<Throwable> getErrorClass() {
        String name = getErrorClassName();
        if (name != null) {
            try {
                return (Class<Throwable>) Class.forName(getErrorClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean isError() {
        return getErrorClassName() != null;
    }

}
