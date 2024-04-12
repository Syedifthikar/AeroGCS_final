package org.kde.necessitas.ministro;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IMinistroCallback extends IInterface {
    void loaderReady(Bundle bundle) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMinistroCallback {
        private static final String DESCRIPTOR = "org.kde.necessitas.ministro.IMinistroCallback";
        static final int TRANSACTION_loaderReady = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMinistroCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMinistroCallback)) {
                return (IMinistroCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg0;
            if (code != 1) {
                if (code == 1598968902) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            }
            data.enforceInterface(DESCRIPTOR);
            if (data.readInt() != 0) {
                _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
            } else {
                _arg0 = null;
            }
            loaderReady(_arg0);
            return true;
        }

        /* loaded from: classes2.dex */
        public static class Proxy implements IMinistroCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // org.kde.necessitas.ministro.IMinistroCallback
            public void loaderReady(Bundle loaderParams) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (loaderParams != null) {
                        _data.writeInt(1);
                        loaderParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
