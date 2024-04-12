package org.qtproject.qt5.android.bindings;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ComponentInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import dalvik.system.DexClassLoader;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.kde.necessitas.ministro.IMinistro;
import org.kde.necessitas.ministro.IMinistroCallback;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes2.dex */
public abstract class QtLoader {
    public static final String ANDROID_THEMES_KEY = "android.themes";
    public static final String APPLICATION_PARAMETERS_KEY = "application.parameters";
    public static final String APPLICATION_TITLE_KEY = "application.title";
    public static final String BUNDLED_LIBRARIES_KEY = "bundled.libraries";
    public static final String DEX_PATH_KEY = "dex.path";
    public static final String ENVIRONMENT_VARIABLES_KEY = "environment.variables";
    public static final String ERROR_CODE_KEY = "error.code";
    public static final String ERROR_MESSAGE_KEY = "error.message";
    public static final String EXTRACT_STYLE_KEY = "extract.android.style";
    private static final String EXTRACT_STYLE_MINIMAL_KEY = "extract.android.style.option";
    public static final int INCOMPATIBLE_MINISTRO_VERSION = 1;
    public static final String LIB_PATH_KEY = "lib.path";
    public static final String LOADER_CLASS_NAME_KEY = "loader.class.name";
    public static final String MAIN_LIBRARY_KEY = "main.library";
    public static final String MINIMUM_MINISTRO_API_KEY = "minimum.ministro.api";
    public static final String MINIMUM_QT_VERSION_KEY = "minimum.qt.version";
    public static final int MINISTRO_API_LEVEL = 5;
    public static final int MINISTRO_INSTALL_REQUEST_CODE = 62446;
    public static final String NATIVE_LIBRARIES_KEY = "native.libraries";
    public static final int NECESSITAS_API_LEVEL = 2;
    public static final String NECESSITAS_API_LEVEL_KEY = "necessitas.api.level";
    public static final int QT_VERSION = 329472;
    public static final String REPOSITORY_KEY = "repository";
    public static final String REQUIRED_MODULES_KEY = "required.modules";
    public static final String SOURCES_KEY = "sources";
    public static final String STATIC_INIT_CLASSES_KEY = "static.init.classes";
    public static final String SYSTEM_LIB_PATH = "/system/lib/";
    private static ArrayList<FileOutputStream> m_fileOutputStreams = new ArrayList<>();
    private ContextWrapper m_context;
    protected ComponentInfo m_contextInfo;
    private Class<?> m_delegateClass;
    public String[] SYSTEM_APP_PATHS = {"/system/priv-app/", "/system/app/"};
    public String APPLICATION_PARAMETERS = null;
    public String ENVIRONMENT_VARIABLES = "QT_USE_ANDROID_NATIVE_DIALOGS=1";
    public String[] QT_ANDROID_THEMES = null;
    public String QT_ANDROID_DEFAULT_THEME = null;
    public String[] m_sources = {"https://download.qt-project.org/ministro/android/qt5/qt-5.7"};
    public String m_repository = "default";
    public ArrayList<String> m_qtLibs = null;
    public int m_displayDensity = -1;
    private final List<String> supportedAbis = Arrays.asList(Build.SUPPORTED_ABIS);
    private String preferredAbi = null;
    private ServiceConnection m_ministroConnection = new AnonymousClass3();

    protected abstract Class<?> contextClassName();

    protected abstract String loaderClassName();

    public QtLoader(ContextWrapper context, Class<?> clazz) {
        this.m_context = context;
        this.m_delegateClass = clazz;
    }

    protected void finish() {
    }

    protected String getTitle() {
        return QtApplication.QtTAG;
    }

    protected void runOnUiThread(Runnable run) {
        run.run();
    }

    protected void downloadUpgradeMinistro(String msg) {
        Log.e(QtApplication.QtTAG, msg);
    }

    Intent getIntent() {
        return null;
    }

    private ArrayList<String> prefferedAbiLibs(String[] libs) {
        HashMap<String, ArrayList<String>> abisLibs = new HashMap<>();
        for (String lib : libs) {
            String[] archLib = lib.split(";", 2);
            String str = this.preferredAbi;
            if (str == null || archLib[0].equals(str)) {
                if (!abisLibs.containsKey(archLib[0])) {
                    abisLibs.put(archLib[0], new ArrayList<>());
                }
                abisLibs.get(archLib[0]).add(archLib[1]);
            }
        }
        String str2 = this.preferredAbi;
        if (str2 != null) {
            if (abisLibs.containsKey(str2)) {
                return abisLibs.get(this.preferredAbi);
            }
            return new ArrayList<>();
        }
        for (String abi : this.supportedAbis) {
            if (abisLibs.containsKey(abi)) {
                this.preferredAbi = abi;
                return abisLibs.get(abi);
            }
        }
        return new ArrayList<>();
    }

    public void loadApplication(Bundle loaderParams) {
        try {
            int errorCode = loaderParams.getInt(ERROR_CODE_KEY);
            if (errorCode != 0) {
                if (errorCode == 1) {
                    downloadUpgradeMinistro(loaderParams.getString(ERROR_MESSAGE_KEY));
                    return;
                }
                AlertDialog errorDialog = new AlertDialog.Builder(this.m_context).create();
                errorDialog.setMessage(loaderParams.getString(ERROR_MESSAGE_KEY));
                errorDialog.setButton(this.m_context.getResources().getString(17039370), new DialogInterface.OnClickListener() { // from class: org.qtproject.qt5.android.bindings.QtLoader.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        QtLoader.this.finish();
                    }
                });
                errorDialog.show();
                return;
            }
            ArrayList<String> libs = new ArrayList<>();
            if (this.m_contextInfo.metaData.containsKey("android.app.bundled_libs_resource_id")) {
                int resourceId = this.m_contextInfo.metaData.getInt("android.app.bundled_libs_resource_id");
                libs.addAll(prefferedAbiLibs(this.m_context.getResources().getStringArray(resourceId)));
            }
            if (this.m_contextInfo.metaData.containsKey("android.app.lib_name")) {
                String libName = this.m_contextInfo.metaData.getString("android.app.lib_name") + "_" + this.preferredAbi;
                loaderParams.putString(MAIN_LIBRARY_KEY, libName);
            }
            loaderParams.putStringArrayList(BUNDLED_LIBRARIES_KEY, libs);
            loaderParams.putInt(NECESSITAS_API_LEVEL_KEY, 2);
            DexClassLoader classLoader = new DexClassLoader(loaderParams.getString(DEX_PATH_KEY), this.m_context.getDir("outdex", 0).getAbsolutePath(), loaderParams.containsKey(LIB_PATH_KEY) ? loaderParams.getString(LIB_PATH_KEY) : null, this.m_context.getClassLoader());
            Class<?> loaderClass = classLoader.loadClass(loaderParams.getString(LOADER_CLASS_NAME_KEY));
            Object qtLoader = loaderClass.newInstance();
            Method prepareAppMethod = qtLoader.getClass().getMethod("loadApplication", contextClassName(), ClassLoader.class, Bundle.class);
            if (!((Boolean) prepareAppMethod.invoke(qtLoader, this.m_context, classLoader, loaderParams)).booleanValue()) {
                throw new Exception(BuildConfig.FLAVOR);
            }
            QtApplication.setQtContextDelegate(this.m_delegateClass, qtLoader);
            Method startAppMethod = qtLoader.getClass().getMethod("startApplication", new Class[0]);
            if (!((Boolean) startAppMethod.invoke(qtLoader, new Object[0])).booleanValue()) {
                throw new Exception(BuildConfig.FLAVOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog errorDialog2 = new AlertDialog.Builder(this.m_context).create();
            if (this.m_contextInfo.metaData.containsKey("android.app.fatal_error_msg")) {
                errorDialog2.setMessage(this.m_contextInfo.metaData.getString("android.app.fatal_error_msg"));
            } else {
                errorDialog2.setMessage("Fatal error, your application can't be started.");
            }
            errorDialog2.setButton(this.m_context.getResources().getString(17039370), new DialogInterface.OnClickListener() { // from class: org.qtproject.qt5.android.bindings.QtLoader.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    QtLoader.this.finish();
                }
            });
            errorDialog2.show();
        }
    }

    /* renamed from: org.qtproject.qt5.android.bindings.QtLoader$3 */
    /* loaded from: classes2.dex */
    public class AnonymousClass3 implements ServiceConnection {
        private IMinistro m_service = null;
        private IMinistroCallback m_ministroCallback = new IMinistroCallback.Stub() { // from class: org.qtproject.qt5.android.bindings.QtLoader.3.1
            @Override // org.kde.necessitas.ministro.IMinistroCallback
            public void loaderReady(final Bundle loaderParams) throws RemoteException {
                QtLoader.this.runOnUiThread(new Runnable() { // from class: org.qtproject.qt5.android.bindings.QtLoader.3.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        QtLoader.this.m_context.unbindService(QtLoader.this.m_ministroConnection);
                        QtLoader.this.loadApplication(loaderParams);
                    }
                });
            }
        };

        AnonymousClass3() {
            QtLoader.this = this$0;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMinistro asInterface = IMinistro.Stub.asInterface(service);
            this.m_service = asInterface;
            if (asInterface != null) {
                try {
                    Bundle parameters = new Bundle();
                    parameters.putStringArray(QtLoader.REQUIRED_MODULES_KEY, (String[]) QtLoader.this.m_qtLibs.toArray());
                    parameters.putString(QtLoader.APPLICATION_TITLE_KEY, QtLoader.this.getTitle());
                    parameters.putInt(QtLoader.MINIMUM_MINISTRO_API_KEY, 5);
                    parameters.putInt(QtLoader.MINIMUM_QT_VERSION_KEY, QtLoader.QT_VERSION);
                    parameters.putString(QtLoader.ENVIRONMENT_VARIABLES_KEY, QtLoader.this.ENVIRONMENT_VARIABLES);
                    if (QtLoader.this.APPLICATION_PARAMETERS != null) {
                        parameters.putString(QtLoader.APPLICATION_PARAMETERS_KEY, QtLoader.this.APPLICATION_PARAMETERS);
                    }
                    parameters.putStringArray(QtLoader.SOURCES_KEY, QtLoader.this.m_sources);
                    parameters.putString(QtLoader.REPOSITORY_KEY, QtLoader.this.m_repository);
                    if (QtLoader.this.QT_ANDROID_THEMES != null) {
                        parameters.putStringArray(QtLoader.ANDROID_THEMES_KEY, QtLoader.this.QT_ANDROID_THEMES);
                    }
                    this.m_service.requestLoader(this.m_ministroCallback, parameters);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            this.m_service = null;
        }
    }

    public void ministroNotFound() {
        AlertDialog errorDialog = new AlertDialog.Builder(this.m_context).create();
        if (this.m_contextInfo.metaData.containsKey("android.app.ministro_not_found_msg")) {
            errorDialog.setMessage(this.m_contextInfo.metaData.getString("android.app.ministro_not_found_msg"));
        } else {
            errorDialog.setMessage("Can't find Ministro service.\nThe application can't start.");
        }
        errorDialog.setButton(this.m_context.getResources().getString(17039370), new DialogInterface.OnClickListener() { // from class: org.qtproject.qt5.android.bindings.QtLoader.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                QtLoader.this.finish();
            }
        });
        errorDialog.show();
    }

    /* JADX WARN: Removed duplicated region for block: B:501:0x00cf A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:515:0x0115 A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:530:0x015c A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:535:0x01a4  */
    /* JADX WARN: Removed duplicated region for block: B:544:0x01df A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:554:0x022a A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:557:0x026a A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:561:0x02c0 A[Catch: Exception -> 0x0420, TRY_ENTER, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:572:0x0301 A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:584:0x033c A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:587:0x037b A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:593:0x0390  */
    /* JADX WARN: Removed duplicated region for block: B:594:0x0392 A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:597:0x03af A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:602:0x03d0 A[Catch: Exception -> 0x0420, TryCatch #1 {Exception -> 0x0420, blocks: (B:477:0x001a, B:479:0x0024, B:480:0x0038, B:482:0x0042, B:483:0x004c, B:485:0x0056, B:486:0x006e, B:489:0x007b, B:491:0x0085, B:494:0x00a0, B:501:0x00cf, B:503:0x00dd, B:505:0x00f4, B:507:0x00ff, B:509:0x0105, B:526:0x0152, B:527:0x0157, B:528:0x0158, B:530:0x015c, B:531:0x0173, B:533:0x0179, B:536:0x01a6, B:539:0x01b2, B:541:0x01bd, B:542:0x01d9, B:544:0x01df, B:546:0x01f1, B:548:0x01fb, B:550:0x021a, B:554:0x022a, B:555:0x0242, B:557:0x026a, B:558:0x027b, B:561:0x02c0, B:563:0x02cf, B:565:0x02d5, B:567:0x02db, B:569:0x02e1, B:570:0x02fb, B:572:0x0301, B:574:0x030d, B:576:0x0311, B:577:0x0317, B:579:0x0322, B:581:0x0328, B:582:0x0336, B:584:0x033c, B:585:0x0351, B:587:0x037b, B:588:0x037e, B:590:0x0386, B:594:0x0392, B:595:0x03a5, B:597:0x03af, B:600:0x03bb, B:602:0x03d0, B:603:0x03df, B:504:0x00e8, B:515:0x0115, B:517:0x013b, B:519:0x0141, B:612:0x0403, B:614:0x040f, B:615:0x0418, B:616:0x041c, B:605:0x03e3, B:608:0x03fa, B:609:0x03ff), top: B:623:0x001a, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void startApp(boolean r25) {
        /*
            Method dump skipped, instructions count: 1063
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.qtproject.qt5.android.bindings.QtLoader.startApp(boolean):void");
    }
}
