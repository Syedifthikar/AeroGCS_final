package org.kde.necessitas.ministro;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import org.kde.necessitas.ministro.IMinistroCallback;

/* loaded from: classes2.dex */
public interface IMinistro extends IInterface {
    void requestLoader(IMinistroCallback iMinistroCallback, Bundle bundle) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMinistro {
        private static final String DESCRIPTOR = "org.kde.necessitas.ministro.IMinistro";
        static final int TRANSACTION_requestLoader = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMinistro asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMinistro)) {
                return (IMinistro) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg1;
            if (code != 1) {
                if (code == 1598968902) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            }
            data.enforceInterface(DESCRIPTOR);
            IMinistroCallback _arg0 = IMinistroCallback.Stub.asInterface(data.readStrongBinder());
            if (data.readInt() != 0) {
                _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
            } else {
                _arg1 = null;
            }
            requestLoader(_arg0, _arg1);
            reply.writeNoException();
            return true;
        }

        /* loaded from: classes2.dex */
        public static class Proxy implements IMinistro {
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

            @Override // org.kde.necessitas.ministro.IMinistro
            public void requestLoader(IMinistroCallback callback, Bundle parameters) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (parameters != null) {
                        _data.writeInt(1);
                        parameters.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
