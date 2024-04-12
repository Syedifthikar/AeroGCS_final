package org.qtproject.qt5.android;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes.dex */
public class ExtractStyle {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    final String[] DisableDrawableStatesLabels;
    final int[] DrawableStates;
    final String[] DrawableStatesLabels;
    final int[] ENABLED_FOCUSED_SELECTED_STATE_SET;
    final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    final int[] ENABLED_FOCUSED_STATE_SET;
    final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    final int[] ENABLED_SELECTED_STATE_SET;
    final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    final int[] ENABLED_STATE_SET;
    final int[] ENABLED_WINDOW_FOCUSED_STATE_SET;
    final int[] FOCUSED_SELECTED_STATE_SET;
    final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    final int[] FOCUSED_STATE_SET;
    final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET;
    final int ImageView_adjustViewBounds;
    final int ImageView_baselineAlignBottom;
    final int ImageView_cropToPadding;
    final int ImageView_maxHeight;
    final int ImageView_maxWidth;
    final int ImageView_scaleType;
    final int ImageView_src;
    final int ImageView_tint;
    final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET;
    final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    final int[] PRESSED_ENABLED_FOCUSED_STATE_SET;
    final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    final int[] PRESSED_ENABLED_SELECTED_STATE_SET;
    final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    final int[] PRESSED_ENABLED_STATE_SET;
    final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET;
    final int[] PRESSED_FOCUSED_SELECTED_STATE_SET;
    final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    final int[] PRESSED_FOCUSED_STATE_SET;
    final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    final int[] PRESSED_SELECTED_STATE_SET;
    final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    final int TextAppearance_textAllCaps;
    final int TextAppearance_textColor;
    final int TextAppearance_textColorHighlight;
    final int TextAppearance_textColorHint;
    final int TextAppearance_textColorLink;
    final int TextAppearance_textSize;
    final int TextAppearance_textStyle;
    final int TextAppearance_typeface;
    final int TextView_autoLink;
    final int TextView_autoText;
    final int TextView_bufferType;
    final int TextView_capitalize;
    final int TextView_cursorVisible;
    final int TextView_digits;
    final int TextView_drawableBottom;
    final int TextView_drawableEnd;
    final int TextView_drawableLeft;
    final int TextView_drawablePadding;
    final int TextView_drawableRight;
    final int TextView_drawableStart;
    final int TextView_drawableTop;
    final int TextView_editable;
    final int TextView_ellipsize;
    final int TextView_ems;
    final int TextView_enabled;
    final int TextView_freezesText;
    final int TextView_gravity;
    final int TextView_height;
    final int TextView_hint;
    final int TextView_imeActionId;
    final int TextView_imeActionLabel;
    final int TextView_imeOptions;
    final int TextView_includeFontPadding;
    final int TextView_inputMethod;
    final int TextView_inputType;
    final int TextView_lineSpacingExtra;
    final int TextView_lineSpacingMultiplier;
    final int TextView_lines;
    final int TextView_linksClickable;
    final int TextView_marqueeRepeatLimit;
    final int TextView_maxEms;
    final int TextView_maxHeight;
    final int TextView_maxLength;
    final int TextView_maxLines;
    final int TextView_maxWidth;
    final int TextView_minEms;
    final int TextView_minHeight;
    final int TextView_minLines;
    final int TextView_minWidth;
    final int TextView_numeric;
    final int TextView_password;
    final int TextView_phoneNumber;
    final int TextView_privateImeOptions;
    final int TextView_scrollHorizontally;
    final int TextView_selectAllOnFocus;
    final int TextView_shadowColor;
    final int TextView_shadowDx;
    final int TextView_shadowDy;
    final int TextView_shadowRadius;
    final int TextView_singleLine;
    final int TextView_text;
    final int TextView_textAllCaps;
    final int TextView_textColor;
    final int TextView_textColorHighlight;
    final int TextView_textColorHint;
    final int TextView_textColorLink;
    final int TextView_textCursorDrawable;
    final int TextView_textIsSelectable;
    final int TextView_textScaleX;
    final int TextView_textSelectHandle;
    final int TextView_textSelectHandleLeft;
    final int TextView_textSelectHandleRight;
    final int TextView_textSize;
    final int TextView_textStyle;
    final int TextView_typeface;
    final int TextView_width;
    final int View_background;
    final int View_clickable;
    final int View_contentDescription;
    final int View_drawingCacheQuality;
    final int View_duplicateParentState;
    final int View_fadingEdge;
    final int View_filterTouchesWhenObscured;
    final int View_fitsSystemWindows;
    final int View_focusable;
    final int View_focusableInTouchMode;
    final int View_hapticFeedbackEnabled;
    final int View_id;
    final int View_isScrollContainer;
    final int View_keepScreenOn;
    final int View_longClickable;
    final int View_minHeight;
    final int View_minWidth;
    final int View_nextFocusDown;
    final int View_nextFocusLeft;
    final int View_nextFocusRight;
    final int View_nextFocusUp;
    final int View_onClick;
    final int View_overScrollMode;
    final int View_padding;
    final int View_paddingBottom;
    final int View_paddingEnd;
    final int View_paddingLeft;
    final int View_paddingRight;
    final int View_paddingStart;
    final int View_paddingTop;
    final int View_saveEnabled;
    final int View_scrollX;
    final int View_scrollY;
    final int View_scrollbarDefaultDelayBeforeFade;
    final int View_scrollbarFadeDuration;
    final int View_scrollbarSize;
    final int View_scrollbarStyle;
    final int View_scrollbarThumbHorizontal;
    final int View_scrollbarThumbVertical;
    final int View_scrollbarTrackHorizontal;
    final int View_scrollbarTrackVertical;
    final int View_scrollbars;
    final int View_soundEffectsEnabled;
    final int View_tag;
    final int View_visibility;
    final int defaultBackgroundColor;
    final int defaultTextColor;
    Context m_context;
    private HashMap<String, DrawableCache> m_drawableCache;
    final String m_extractPath;
    final boolean m_minimal;
    final Resources.Theme m_theme;
    final String[] sScaleTypeArray;
    Class<?> styleableClass = getClass("android.R$styleable");
    Class<?> rippleDrawableClass = getClass("android.graphics.drawable.RippleDrawable");
    Class<?> animatedStateListDrawableClass = getClass("android.graphics.drawable.AnimatedStateListDrawable");
    Class<?> vectorDrawableClass = getClass("android.graphics.drawable.VectorDrawable");
    final int[] EMPTY_STATE_SET = new int[0];
    final int[] SELECTED_STATE_SET = {16842913};
    final int[] PRESSED_STATE_SET = {16842919};
    final int[] WINDOW_FOCUSED_STATE_SET = {16842909};
    final int[] SELECTED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(this.SELECTED_STATE_SET, this.WINDOW_FOCUSED_STATE_SET);
    final int[] PRESSED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(this.PRESSED_STATE_SET, this.WINDOW_FOCUSED_STATE_SET);

    static native int[] extractChunkInfo20(byte[] bArr);

    static native int[] extractNativeChunkInfo20(long j);

    /* loaded from: classes.dex */
    public class SimpleJsonWriter {
        private boolean m_addComma = false;
        private int m_indentLevel = 0;
        private OutputStreamWriter m_writer;

        public SimpleJsonWriter(String str) throws FileNotFoundException {
            ExtractStyle.this = r2;
            this.m_writer = new OutputStreamWriter(new FileOutputStream(str));
        }

        public void close() throws IOException {
            this.m_writer.close();
        }

        private void writeIndent() throws IOException {
            this.m_writer.write(" ", 0, this.m_indentLevel);
        }

        SimpleJsonWriter beginObject() throws IOException {
            writeIndent();
            this.m_writer.write("{\n");
            this.m_indentLevel++;
            this.m_addComma = false;
            return this;
        }

        SimpleJsonWriter endObject() throws IOException {
            this.m_writer.write("\n");
            writeIndent();
            this.m_writer.write("}\n");
            this.m_indentLevel--;
            this.m_addComma = false;
            return this;
        }

        SimpleJsonWriter name(String str) throws IOException {
            if (this.m_addComma) {
                this.m_writer.write(",\n");
            }
            writeIndent();
            OutputStreamWriter outputStreamWriter = this.m_writer;
            outputStreamWriter.write(JSONObject.quote(str) + ": ");
            this.m_addComma = true;
            return this;
        }

        SimpleJsonWriter value(JSONObject jSONObject) throws IOException {
            this.m_writer.write(jSONObject.toString());
            return this;
        }
    }

    /* loaded from: classes.dex */
    class FakeCanvas extends Canvas {
        int[] chunkData = null;

        FakeCanvas() {
            ExtractStyle.this = r1;
        }

        /* loaded from: classes.dex */
        class Size {
            public int e;
            public int s;

            Size(int i, int i2) {
                FakeCanvas.this = r1;
                this.s = i;
                this.e = i2;
            }
        }

        @Override // android.graphics.Canvas
        public boolean isHardwareAccelerated() {
            return true;
        }

        public void drawPatch(Bitmap bitmap, byte[] bArr, RectF rectF, Paint paint) {
            this.chunkData = ExtractStyle.extractChunkInfo20(bArr);
        }
    }

    private int[] stateSetUnion(int[] iArr, int[] iArr2) {
        int[] iArr3;
        try {
            int length = iArr.length;
            int length2 = iArr2.length;
            int[] iArr4 = new int[length + length2];
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            for (int i4 : (int[]) this.styleableClass.getDeclaredField("ViewDrawableStates").get(null)) {
                if (i < length && iArr[i] == i4) {
                    iArr4[i3] = i4;
                    i++;
                    i3++;
                } else if (i2 < length2 && iArr2[i2] == i4) {
                    int i5 = i3 + 1;
                    iArr4[i3] = i4;
                    i2++;
                    i3 = i5;
                }
                if (i3 > 1) {
                }
            }
            return iArr4;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Class<?> getClass(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    Field getAccessibleField(Class<?> cls, String str) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Field tryGetAccessibleField(Class<?> cls, String str) {
        if (cls == null) {
            return null;
        }
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (Exception e) {
            for (Class<?> cls2 : cls.getInterfaces()) {
                Field tryGetAccessibleField = tryGetAccessibleField(cls2, str);
                if (tryGetAccessibleField != null) {
                    return tryGetAccessibleField;
                }
            }
            return tryGetAccessibleField(cls.getSuperclass(), str);
        }
    }

    int getField(Class<?> cls, String str) {
        try {
            return cls.getDeclaredField(str).getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    JSONObject getColorStateList(ColorStateList colorStateList) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("EMPTY_STATE_SET", colorStateList.getColorForState(this.EMPTY_STATE_SET, 0));
            jSONObject.put("WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("SELECTED_STATE_SET", colorStateList.getColorForState(this.SELECTED_STATE_SET, 0));
            jSONObject.put("SELECTED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.SELECTED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("FOCUSED_STATE_SET", colorStateList.getColorForState(this.FOCUSED_STATE_SET, 0));
            jSONObject.put("FOCUSED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.FOCUSED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("FOCUSED_SELECTED_STATE_SET", colorStateList.getColorForState(this.FOCUSED_SELECTED_STATE_SET, 0));
            jSONObject.put("FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("ENABLED_STATE_SET", colorStateList.getColorForState(this.ENABLED_STATE_SET, 0));
            jSONObject.put("ENABLED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.ENABLED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("ENABLED_SELECTED_STATE_SET", colorStateList.getColorForState(this.ENABLED_SELECTED_STATE_SET, 0));
            jSONObject.put("ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("ENABLED_FOCUSED_STATE_SET", colorStateList.getColorForState(this.ENABLED_FOCUSED_STATE_SET, 0));
            jSONObject.put("ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("ENABLED_FOCUSED_SELECTED_STATE_SET", colorStateList.getColorForState(this.ENABLED_FOCUSED_SELECTED_STATE_SET, 0));
            jSONObject.put("ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_STATE_SET, 0));
            jSONObject.put("PRESSED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_SELECTED_STATE_SET", colorStateList.getColorForState(this.PRESSED_SELECTED_STATE_SET, 0));
            jSONObject.put("PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_FOCUSED_SELECTED_STATE_SET", colorStateList.getColorForState(this.PRESSED_FOCUSED_SELECTED_STATE_SET, 0));
            jSONObject.put("PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_ENABLED_STATE_SET", colorStateList.getColorForState(this.PRESSED_ENABLED_STATE_SET, 0));
            jSONObject.put("PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_ENABLED_SELECTED_STATE_SET", colorStateList.getColorForState(this.PRESSED_ENABLED_SELECTED_STATE_SET, 0));
            jSONObject.put("PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_ENABLED_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_ENABLED_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET, 0));
            jSONObject.put("PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET", colorStateList.getColorForState(this.PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET, 0));
            jSONObject.put("PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET", colorStateList.getColorForState(this.PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET, 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    String getFileName(String str, String[] strArr) {
        for (String str2 : strArr) {
            str = str + "__" + str2;
        }
        return str;
    }

    String getStatesName(String[] strArr) {
        String str = BuildConfig.FLAVOR;
        for (String str2 : strArr) {
            if (str.length() > 0) {
                str = str + "__";
            }
            str = str + str2;
        }
        return str;
    }

    void addDrawableItemIfNotExists(JSONObject jSONObject, ArrayList<Integer> arrayList, Drawable drawable, String[] strArr, String str) {
        Iterator<Integer> it = arrayList.iterator();
        while (it.hasNext()) {
            if (it.next().equals(Integer.valueOf(drawable.hashCode()))) {
                return;
            }
        }
        arrayList.add(Integer.valueOf(drawable.hashCode()));
        try {
            jSONObject.put(getStatesName(strArr), getDrawable(drawable, getFileName(str, strArr), null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void addSolution(String str, JSONObject jSONObject, int i, Drawable drawable, ArrayList<Integer> arrayList, int i2) {
        int[] iArr = new int[i];
        String[] strArr = new String[i];
        int i3 = 0;
        int i4 = 0;
        for (int i5 = i2; i5 > 0; i5 >>= 1) {
            if ((i5 & 1) > 0) {
                strArr[i3] = this.DrawableStatesLabels[i4];
                iArr[i3] = this.DrawableStates[i4];
                i3++;
            }
            i4++;
        }
        drawable.setState(iArr);
        addDrawableItemIfNotExists(jSONObject, arrayList, drawable.getCurrent(), strArr, str);
    }

    int bitCount(int i) {
        int i2 = 0;
        while (i > 0) {
            i2++;
            i &= i - 1;
        }
        return i2;
    }

    JSONObject getStatesList(int[] iArr) throws JSONException {
        boolean z;
        JSONObject jSONObject = new JSONObject();
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            int i2 = iArr[i];
            int i3 = 0;
            while (true) {
                int[] iArr2 = this.DrawableStates;
                if (i3 < iArr2.length) {
                    if (i2 == iArr2[i3]) {
                        jSONObject.put(this.DrawableStatesLabels[i3], true);
                        z = true;
                        break;
                    } else if (i2 != (-iArr2[i3])) {
                        i3++;
                    } else {
                        jSONObject.put(this.DrawableStatesLabels[i3], false);
                        z = true;
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            if (!z) {
                jSONObject.put("unhandled_state_" + i2, i2 > 0);
            }
        }
        return jSONObject;
    }

    String getStatesName(int[] iArr) {
        boolean z;
        String str = BuildConfig.FLAVOR;
        for (int i : iArr) {
            int i2 = 0;
            while (true) {
                int[] iArr2 = this.DrawableStates;
                z = true;
                if (i2 >= iArr2.length) {
                    z = false;
                    break;
                } else if (i == iArr2[i2]) {
                    if (str.length() > 0) {
                        str = str + "__";
                    }
                    str = str + this.DrawableStatesLabels[i2];
                } else if (i != (-iArr2[i2])) {
                    i2++;
                } else {
                    if (str.length() > 0) {
                        str = str + "__";
                    }
                    str = str + this.DisableDrawableStatesLabels[i2];
                }
            }
            if (!z) {
                if (str.length() > 0) {
                    str = str + ";";
                }
                str = str + i;
            }
        }
        if (str.length() > 0) {
            return str;
        }
        return "empty";
    }

    private JSONObject getLayerDrawable(Object obj, String str) {
        JSONObject jSONObject = new JSONObject();
        LayerDrawable layerDrawable = (LayerDrawable) obj;
        int numberOfLayers = layerDrawable.getNumberOfLayers();
        try {
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < numberOfLayers; i++) {
                int id = layerDrawable.getId(i);
                if (id == -1) {
                    id = i;
                }
                Drawable drawable = layerDrawable.getDrawable(i);
                JSONObject drawable2 = getDrawable(drawable, str + "__" + id, null);
                drawable2.put("id", id);
                jSONArray.put(drawable2);
            }
            jSONObject.put("type", "layer");
            Rect rect = new Rect();
            if (layerDrawable.getPadding(rect)) {
                jSONObject.put("padding", getJsonRect(rect));
            }
            jSONObject.put("layers", jSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    private JSONObject getStateListDrawable(Object obj, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            StateListDrawable stateListDrawable = (StateListDrawable) obj;
            int intValue = ((Integer) StateListDrawable.class.getMethod("getStateCount", new Class[0]).invoke(stateListDrawable, new Object[0])).intValue();
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < intValue; i++) {
                JSONObject jSONObject2 = new JSONObject();
                Drawable drawable = (Drawable) StateListDrawable.class.getMethod("getStateDrawable", Integer.TYPE).invoke(stateListDrawable, Integer.valueOf(i));
                int[] iArr = (int[]) StateListDrawable.class.getMethod("getStateSet", Integer.TYPE).invoke(stateListDrawable, Integer.valueOf(i));
                if (iArr != null) {
                    jSONObject2.put("states", getStatesList(iArr));
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("__");
                sb.append(iArr != null ? getStatesName(iArr) : "state_pos_" + i);
                jSONObject2.put("drawable", getDrawable(drawable, sb.toString(), null));
                jSONArray.put(jSONObject2);
            }
            jSONObject.put("type", "stateslist");
            Rect rect = new Rect();
            if (stateListDrawable.getPadding(rect)) {
                jSONObject.put("padding", getJsonRect(rect));
            }
            jSONObject.put("stateslist", jSONArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    private JSONObject getGradientDrawable(GradientDrawable gradientDrawable) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("type", "gradient");
            Drawable.ConstantState constantState = gradientDrawable.getConstantState();
            Class<?> cls = constantState.getClass();
            jSONObject.put("shape", cls.getField("mShape").getInt(constantState));
            jSONObject.put("gradient", cls.getField("mGradient").getInt(constantState));
            jSONObject.put("orientation", ((GradientDrawable.Orientation) cls.getField("mOrientation").get(constantState)).name());
            int[] iArr = (int[]) cls.getField(Build.VERSION.SDK_INT < 23 ? "mColors" : "mGradientColors").get(constantState);
            if (iArr != null) {
                jSONObject.put("colors", getJsonArray(iArr, 0, iArr.length));
            }
            jSONObject.put("positions", getJsonArray((float[]) cls.getField("mPositions").get(constantState)));
            jSONObject.put("strokeWidth", cls.getField("mStrokeWidth").getInt(constantState));
            jSONObject.put("strokeDashWidth", cls.getField("mStrokeDashWidth").getFloat(constantState));
            jSONObject.put("strokeDashGap", cls.getField("mStrokeDashGap").getFloat(constantState));
            jSONObject.put("radius", cls.getField("mRadius").getFloat(constantState));
            float[] fArr = (float[]) cls.getField("mRadiusArray").get(constantState);
            if (fArr != null) {
                jSONObject.put("radiusArray", getJsonArray(fArr));
            }
            Rect rect = (Rect) cls.getField("mPadding").get(constantState);
            if (rect != null) {
                jSONObject.put("padding", getJsonRect(rect));
            }
            jSONObject.put("width", cls.getField("mWidth").getInt(constantState));
            jSONObject.put("height", cls.getField("mHeight").getInt(constantState));
            jSONObject.put("innerRadiusRatio", cls.getField("mInnerRadiusRatio").getFloat(constantState));
            jSONObject.put("thicknessRatio", cls.getField("mThicknessRatio").getFloat(constantState));
            jSONObject.put("innerRadius", cls.getField("mInnerRadius").getInt(constantState));
            jSONObject.put("thickness", cls.getField("mThickness").getInt(constantState));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    private JSONObject getRotateDrawable(RotateDrawable rotateDrawable, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("type", "rotate");
            Drawable.ConstantState constantState = rotateDrawable.getConstantState();
            Class<?> cls = constantState.getClass();
            if (Build.VERSION.SDK_INT < 23) {
                jSONObject.put("drawable", getDrawable(getAccessibleField(cls, "mDrawable").get(constantState), str, null));
            } else {
                jSONObject.put("drawable", getDrawable(rotateDrawable.getClass().getMethod("getDrawable", new Class[0]).invoke(rotateDrawable, new Object[0]), str, null));
            }
            jSONObject.put("pivotX", getAccessibleField(cls, "mPivotX").getFloat(constantState));
            jSONObject.put("pivotXRel", getAccessibleField(cls, "mPivotXRel").getBoolean(constantState));
            jSONObject.put("pivotY", getAccessibleField(cls, "mPivotY").getFloat(constantState));
            jSONObject.put("pivotYRel", getAccessibleField(cls, "mPivotYRel").getBoolean(constantState));
            jSONObject.put("fromDegrees", getAccessibleField(cls, "mFromDegrees").getFloat(constantState));
            jSONObject.put("toDegrees", getAccessibleField(cls, "mToDegrees").getFloat(constantState));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    private JSONObject getAnimationDrawable(AnimationDrawable animationDrawable, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("type", "animation");
            jSONObject.put("oneshot", animationDrawable.isOneShot());
            int numberOfFrames = animationDrawable.getNumberOfFrames();
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < numberOfFrames; i++) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("duration", animationDrawable.getDuration(i));
                Drawable frame = animationDrawable.getFrame(i);
                jSONObject2.put("drawable", getDrawable(frame, str + "__" + i, null));
                jSONArray.put(jSONObject2);
            }
            jSONObject.put("frames", jSONArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    private JSONObject getJsonRect(Rect rect) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("left", rect.left);
        jSONObject.put("top", rect.top);
        jSONObject.put("right", rect.right);
        jSONObject.put("bottom", rect.bottom);
        return jSONObject;
    }

    private JSONArray getJsonArray(int[] iArr, int i, int i2) {
        JSONArray jSONArray = new JSONArray();
        int i3 = i2 + i;
        while (i < i3) {
            jSONArray.put(iArr[i]);
            i++;
        }
        return jSONArray;
    }

    private JSONArray getJsonArray(float[] fArr) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        if (fArr != null) {
            for (float f : fArr) {
                jSONArray.put(f);
            }
        }
        return jSONArray;
    }

    private JSONObject getJsonChunkInfo(int[] iArr) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        if (iArr == null) {
            return jSONObject;
        }
        jSONObject.put("xdivs", getJsonArray(iArr, 3, iArr[0]));
        jSONObject.put("ydivs", getJsonArray(iArr, iArr[0] + 3, iArr[1]));
        jSONObject.put("colors", getJsonArray(iArr, iArr[0] + 3 + iArr[1], iArr[2]));
        return jSONObject;
    }

    private JSONObject findPatchesMarings(Drawable drawable) throws JSONException, NoSuchFieldException, IllegalAccessException {
        NinePatch ninePatch;
        Field tryGetAccessibleField = tryGetAccessibleField(NinePatchDrawable.class, "mNinePatch");
        if (tryGetAccessibleField != null) {
            ninePatch = (NinePatch) tryGetAccessibleField.get(drawable);
        } else {
            Object obj = getAccessibleField(NinePatchDrawable.class, "mNinePatchState").get(drawable);
            ninePatch = (NinePatch) getAccessibleField(obj.getClass(), "mNinePatch").get(obj);
        }
        return getJsonChunkInfo(extractNativeChunkInfo20(getAccessibleField(ninePatch.getClass(), "mNativeChunk").getLong(ninePatch)));
    }

    /* loaded from: classes.dex */
    public class DrawableCache {
        Object drawable;
        JSONObject object;

        public DrawableCache(JSONObject jSONObject, Object obj) {
            ExtractStyle.this = r1;
            this.object = jSONObject;
            this.drawable = obj;
        }
    }

    private JSONObject getRippleDrawable(Object obj, String str, Rect rect) {
        JSONObject layerDrawable = getLayerDrawable(obj, str);
        JSONObject jSONObject = new JSONObject();
        try {
            Object obj2 = getAccessibleField(this.rippleDrawableClass, "mState").get(obj);
            jSONObject.put("mask", getDrawable((Drawable) getAccessibleField(this.rippleDrawableClass, "mMask").get(obj), str, rect));
            jSONObject.put("maxRadius", getAccessibleField(obj2.getClass(), "mMaxRadius").getInt(obj2));
            jSONObject.put("color", getColorStateList((ColorStateList) getAccessibleField(obj2.getClass(), "mColor").get(obj2)));
            layerDrawable.put("ripple", jSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return layerDrawable;
    }

    private HashMap<Long, Long> getStateTransitions(Object obj) throws Exception {
        HashMap<Long, Long> hashMap = new HashMap<>();
        int i = getAccessibleField(obj.getClass(), "mSize").getInt(obj);
        long[] jArr = (long[]) getAccessibleField(obj.getClass(), "mKeys").get(obj);
        long[] jArr2 = (long[]) getAccessibleField(obj.getClass(), "mValues").get(obj);
        for (int i2 = 0; i2 < i; i2++) {
            hashMap.put(Long.valueOf(jArr[i2]), Long.valueOf(jArr2[i2]));
        }
        return hashMap;
    }

    private HashMap<Integer, Integer> getStateIds(Object obj) throws Exception {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int i = getAccessibleField(obj.getClass(), "mSize").getInt(obj);
        int[] iArr = (int[]) getAccessibleField(obj.getClass(), "mKeys").get(obj);
        int[] iArr2 = (int[]) getAccessibleField(obj.getClass(), "mValues").get(obj);
        for (int i2 = 0; i2 < i; i2++) {
            hashMap.put(Integer.valueOf(iArr[i2]), Integer.valueOf(iArr2[i2]));
        }
        return hashMap;
    }

    private int findStateIndex(int i, HashMap<Integer, Integer> hashMap) {
        for (Map.Entry<Integer, Integer> entry : hashMap.entrySet()) {
            if (i == entry.getValue().intValue()) {
                return entry.getKey().intValue();
            }
        }
        return -1;
    }

    private JSONObject getAnimatedStateListDrawable(Object obj, String str) {
        JSONObject stateListDrawable = getStateListDrawable(obj, str);
        try {
            Object obj2 = getAccessibleField(this.animatedStateListDrawableClass, "mState").get(obj);
            HashMap<Integer, Integer> stateIds = getStateIds(getAccessibleField(obj2.getClass(), "mStateIds").get(obj2));
            for (Map.Entry<Long, Long> entry : getStateTransitions(getAccessibleField(obj2.getClass(), "mTransitions").get(obj2)).entrySet()) {
                int findStateIndex = findStateIndex(entry.getKey().intValue(), stateIds);
                int findStateIndex2 = findStateIndex((int) (entry.getKey().longValue() >> 32), stateIds);
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("from", findStateIndex2);
                jSONObject.put("to", findStateIndex);
                jSONObject.put("reverse", (entry.getValue().longValue() >> 32) != 0);
                stateListDrawable.getJSONArray("stateslist").getJSONObject(entry.getValue().intValue()).put("transition", jSONObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stateListDrawable;
    }

    private JSONObject getVPath(Object obj) throws Exception {
        JSONObject jSONObject = new JSONObject();
        Class<?> cls = obj.getClass();
        jSONObject.put("type", "path");
        jSONObject.put("name", tryGetAccessibleField(cls, "mPathName").get(obj));
        Object[] objArr = (Object[]) tryGetAccessibleField(cls, "mNodes").get(obj);
        JSONArray jSONArray = new JSONArray();
        for (Object obj2 : objArr) {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("type", String.valueOf(getAccessibleField(obj2.getClass(), "mType").getChar(obj2)));
            jSONObject2.put("params", getJsonArray((float[]) getAccessibleField(obj2.getClass(), "mParams").get(obj2)));
            jSONArray.put(jSONObject2);
        }
        jSONObject.put("nodes", jSONArray);
        jSONObject.put("isClip", (Boolean) cls.getMethod("isClipPath", new Class[0]).invoke(obj, new Object[0]));
        if (tryGetAccessibleField(cls, "mStrokeColor") == null) {
            return jSONObject;
        }
        jSONObject.put("strokeColor", getAccessibleField(cls, "mStrokeColor").getInt(obj));
        jSONObject.put("strokeWidth", getAccessibleField(cls, "mStrokeWidth").getFloat(obj));
        jSONObject.put("fillColor", getAccessibleField(cls, "mFillColor").getInt(obj));
        jSONObject.put("strokeAlpha", getAccessibleField(cls, "mStrokeAlpha").getFloat(obj));
        jSONObject.put("fillRule", getAccessibleField(cls, "mFillRule").getInt(obj));
        jSONObject.put("fillAlpha", getAccessibleField(cls, "mFillAlpha").getFloat(obj));
        jSONObject.put("trimPathStart", getAccessibleField(cls, "mTrimPathStart").getFloat(obj));
        jSONObject.put("trimPathEnd", getAccessibleField(cls, "mTrimPathEnd").getFloat(obj));
        jSONObject.put("trimPathOffset", getAccessibleField(cls, "mTrimPathOffset").getFloat(obj));
        jSONObject.put("strokeLineCap", (Paint.Cap) getAccessibleField(cls, "mStrokeLineCap").get(obj));
        jSONObject.put("strokeLineJoin", (Paint.Join) getAccessibleField(cls, "mStrokeLineJoin").get(obj));
        jSONObject.put("strokeMiterlimit", getAccessibleField(cls, "mStrokeMiterlimit").getFloat(obj));
        return jSONObject;
    }

    private JSONObject getVGroup(Object obj) throws Exception {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("type", "group");
        Class<?> cls = obj.getClass();
        jSONObject.put("name", getAccessibleField(cls, "mGroupName").get(obj));
        jSONObject.put("rotate", getAccessibleField(cls, "mRotate").getFloat(obj));
        jSONObject.put("pivotX", getAccessibleField(cls, "mPivotX").getFloat(obj));
        jSONObject.put("pivotY", getAccessibleField(cls, "mPivotY").getFloat(obj));
        jSONObject.put("scaleX", getAccessibleField(cls, "mScaleX").getFloat(obj));
        jSONObject.put("scaleY", getAccessibleField(cls, "mScaleY").getFloat(obj));
        jSONObject.put("translateX", getAccessibleField(cls, "mTranslateX").getFloat(obj));
        jSONObject.put("translateY", getAccessibleField(cls, "mTranslateY").getFloat(obj));
        JSONArray jSONArray = new JSONArray();
        Iterator it = ((ArrayList) getAccessibleField(cls, "mChildren").get(obj)).iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (cls.isInstance(next)) {
                jSONArray.put(getVGroup(next));
            } else {
                jSONArray.put(getVPath(next));
            }
        }
        jSONObject.put("children", jSONArray);
        return jSONObject;
    }

    private JSONObject getVectorDrawable(Object obj, String str, Rect rect) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("type", "vector");
            Object obj2 = getAccessibleField(this.vectorDrawableClass, "mVectorState").get(obj);
            Class<?> cls = obj2.getClass();
            ColorStateList colorStateList = (ColorStateList) getAccessibleField(cls, "mTint").get(obj2);
            if (colorStateList != null) {
                jSONObject.put("tintList", getColorStateList(colorStateList));
                jSONObject.put("tintMode", (PorterDuff.Mode) getAccessibleField(cls, "mTintMode").get(obj2));
            }
            Object obj3 = getAccessibleField(cls, "mVPathRenderer").get(obj2);
            Class<?> cls2 = obj3.getClass();
            jSONObject.put("baseWidth", getAccessibleField(cls2, "mBaseWidth").getFloat(obj3));
            jSONObject.put("baseHeight", getAccessibleField(cls2, "mBaseHeight").getFloat(obj3));
            jSONObject.put("viewportWidth", getAccessibleField(cls2, "mViewportWidth").getFloat(obj3));
            jSONObject.put("viewportHeight", getAccessibleField(cls2, "mViewportHeight").getFloat(obj3));
            jSONObject.put("rootAlpha", getAccessibleField(cls2, "mRootAlpha").getInt(obj3));
            jSONObject.put("rootName", getAccessibleField(cls2, "mRootName").get(obj3));
            jSONObject.put("rootGroup", getVGroup(getAccessibleField(obj3.getClass(), "mRootGroup").get(obj3)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public JSONObject getDrawable(Object obj, String str, Rect rect) {
        Bitmap bitmap = null;
        if (obj == null || this.m_minimal) {
            return null;
        }
        DrawableCache drawableCache = this.m_drawableCache.get(str);
        if (drawableCache != null) {
            if (drawableCache.drawable.equals(obj)) {
                return drawableCache.object;
            }
            Log.e(QtNative.QtTAG, "Different drawable objects points to the same file name \"" + str + "\"");
        }
        JSONObject jSONObject = new JSONObject();
        if (obj instanceof Bitmap) {
            bitmap = (Bitmap) obj;
        } else if (obj instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) obj;
            bitmap = bitmapDrawable.getBitmap();
            try {
                jSONObject.put("gravity", bitmapDrawable.getGravity());
                jSONObject.put("tileModeX", bitmapDrawable.getTileModeX());
                jSONObject.put("tileModeY", bitmapDrawable.getTileModeY());
                jSONObject.put("antialias", (Boolean) BitmapDrawable.class.getMethod("hasAntiAlias", new Class[0]).invoke(bitmapDrawable, new Object[0]));
                jSONObject.put("mipMap", (Boolean) BitmapDrawable.class.getMethod("hasMipMap", new Class[0]).invoke(bitmapDrawable, new Object[0]));
                jSONObject.put("tintMode", (PorterDuff.Mode) BitmapDrawable.class.getMethod("getTintMode", new Class[0]).invoke(bitmapDrawable, new Object[0]));
                ColorStateList colorStateList = (ColorStateList) BitmapDrawable.class.getMethod("getTint", new Class[0]).invoke(bitmapDrawable, new Object[0]);
                if (colorStateList != null) {
                    jSONObject.put("tintList", getColorStateList(colorStateList));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Class<?> cls = this.rippleDrawableClass;
            if (cls != null && cls.isInstance(obj)) {
                return getRippleDrawable(obj, str, rect);
            }
            Class<?> cls2 = this.animatedStateListDrawableClass;
            if (cls2 != null && cls2.isInstance(obj)) {
                return getAnimatedStateListDrawable(obj, str);
            }
            Class<?> cls3 = this.vectorDrawableClass;
            if (cls3 != null && cls3.isInstance(obj)) {
                return getVectorDrawable(obj, str, rect);
            }
            if (obj instanceof ScaleDrawable) {
                return getDrawable(((ScaleDrawable) obj).getDrawable(), str, null);
            }
            if (obj instanceof LayerDrawable) {
                return getLayerDrawable(obj, str);
            }
            if (obj instanceof StateListDrawable) {
                return getStateListDrawable(obj, str);
            }
            if (obj instanceof GradientDrawable) {
                return getGradientDrawable((GradientDrawable) obj);
            }
            if (obj instanceof RotateDrawable) {
                return getRotateDrawable((RotateDrawable) obj, str);
            }
            if (obj instanceof AnimationDrawable) {
                return getAnimationDrawable((AnimationDrawable) obj, str);
            }
            if (obj instanceof ClipDrawable) {
                try {
                    jSONObject.put("type", "clipDrawable");
                    Object constantState = ((ClipDrawable) obj).getConstantState();
                    jSONObject.put("drawable", getDrawable(getAccessibleField(constantState.getClass(), "mDrawable").get(constantState), str, null));
                    if (rect != null) {
                        jSONObject.put("padding", getJsonRect(rect));
                    } else {
                        Rect rect2 = new Rect();
                        if (((Drawable) obj).getPadding(rect2)) {
                            jSONObject.put("padding", getJsonRect(rect2));
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                return jSONObject;
            } else if (obj instanceof ColorDrawable) {
                Bitmap createBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                Drawable drawable = (Drawable) obj;
                drawable.setBounds(0, 0, 1, 1);
                drawable.draw(new Canvas(createBitmap));
                try {
                    jSONObject.put("type", "color");
                    jSONObject.put("color", createBitmap.getPixel(0, 0));
                    if (rect != null) {
                        jSONObject.put("padding", getJsonRect(rect));
                    } else {
                        Rect rect3 = new Rect();
                        if (drawable.getPadding(rect3)) {
                            jSONObject.put("padding", getJsonRect(rect3));
                        }
                    }
                } catch (JSONException e3) {
                    e3.printStackTrace();
                }
                return jSONObject;
            } else if (obj instanceof InsetDrawable) {
                try {
                    InsetDrawable insetDrawable = (InsetDrawable) obj;
                    Object obj2 = getAccessibleField(InsetDrawable.class, Build.VERSION.SDK_INT > 21 ? "mState" : "mInsetState").get(insetDrawable);
                    Rect rect4 = new Rect();
                    boolean padding = insetDrawable.getPadding(rect4);
                    Object obj3 = getAccessibleField(obj2.getClass(), "mDrawable").get(obj2);
                    if (!padding) {
                        rect4 = null;
                    }
                    return getDrawable(obj3, str, rect4);
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            } else {
                Drawable drawable2 = (Drawable) obj;
                int intrinsicWidth = drawable2.getIntrinsicWidth();
                int intrinsicHeight = drawable2.getIntrinsicHeight();
                drawable2.setLevel(10000);
                if (intrinsicWidth < 1 || intrinsicHeight < 1) {
                    intrinsicWidth = 100;
                    intrinsicHeight = 100;
                }
                Bitmap createBitmap2 = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
                drawable2.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                drawable2.draw(new Canvas(createBitmap2));
                if (obj instanceof NinePatchDrawable) {
                    NinePatchDrawable ninePatchDrawable = (NinePatchDrawable) obj;
                    try {
                        jSONObject.put("type", "9patch");
                        jSONObject.put("drawable", getDrawable(createBitmap2, str, null));
                        if (rect != null) {
                            jSONObject.put("padding", getJsonRect(rect));
                        } else {
                            Rect rect5 = new Rect();
                            if (ninePatchDrawable.getPadding(rect5)) {
                                jSONObject.put("padding", getJsonRect(rect5));
                            }
                        }
                        jSONObject.put("chunkInfo", findPatchesMarings(drawable2));
                        return jSONObject;
                    } catch (Exception e5) {
                        e5.printStackTrace();
                    }
                }
                bitmap = createBitmap2;
            }
        }
        try {
            str = this.m_extractPath + str + ".png";
            FileOutputStream fileOutputStream = new FileOutputStream(str);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e6) {
            e6.printStackTrace();
        } catch (IOException e7) {
            e7.printStackTrace();
        }
        try {
            jSONObject.put("type", "image");
            jSONObject.put("path", str);
            jSONObject.put("width", bitmap.getWidth());
            jSONObject.put("height", bitmap.getHeight());
            this.m_drawableCache.put(str, new DrawableCache(jSONObject, obj));
        } catch (JSONException e8) {
            e8.printStackTrace();
        }
        return jSONObject;
    }

    public void extractViewInformations(String str, int i, JSONObject jSONObject, String str2, AttributeSet attributeSet) {
        try {
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(attributeSet, (int[]) this.styleableClass.getDeclaredField("View").get(null), i, 0);
            if (str2 != null) {
                jSONObject.put("qtClass", str2);
            }
            jSONObject.put("defaultBackgroundColor", this.defaultBackgroundColor);
            jSONObject.put("defaultTextColorPrimary", this.defaultTextColor);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i2 = 0; i2 < indexCount; i2++) {
                int index = obtainStyledAttributes.getIndex(i2);
                if (index == this.View_background) {
                    Drawable drawable = obtainStyledAttributes.getDrawable(index);
                    jSONObject.put("View_background", getDrawable(drawable, str + "_View_background", null));
                } else if (index == this.View_padding) {
                    jSONObject.put("View_padding", obtainStyledAttributes.getDimensionPixelSize(index, -1));
                } else if (index == this.View_paddingLeft) {
                    jSONObject.put("View_paddingLeft", obtainStyledAttributes.getDimensionPixelSize(index, -1));
                } else if (index == this.View_paddingTop) {
                    jSONObject.put("View_paddingTop", obtainStyledAttributes.getDimensionPixelSize(index, -1));
                } else if (index == this.View_paddingRight) {
                    jSONObject.put("View_paddingRight", obtainStyledAttributes.getDimensionPixelSize(index, -1));
                } else if (index == this.View_paddingBottom) {
                    jSONObject.put("View_paddingBottom", obtainStyledAttributes.getDimensionPixelSize(index, -1));
                } else if (index == this.View_scrollX) {
                    jSONObject.put("View_paddingBottom", obtainStyledAttributes.getDimensionPixelOffset(index, 0));
                } else if (index == this.View_scrollY) {
                    jSONObject.put("View_scrollY", obtainStyledAttributes.getDimensionPixelOffset(index, 0));
                } else if (index == this.View_id) {
                    jSONObject.put("View_id", obtainStyledAttributes.getResourceId(index, -1));
                } else if (index == this.View_tag) {
                    jSONObject.put("View_tag", obtainStyledAttributes.getText(index));
                } else if (index == this.View_fitsSystemWindows) {
                    jSONObject.put("View_fitsSystemWindows", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_focusable) {
                    jSONObject.put("View_focusable", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_focusableInTouchMode) {
                    jSONObject.put("View_focusableInTouchMode", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_clickable) {
                    jSONObject.put("View_clickable", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_longClickable) {
                    jSONObject.put("View_longClickable", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_saveEnabled) {
                    jSONObject.put("View_saveEnabled", obtainStyledAttributes.getBoolean(index, true));
                } else if (index == this.View_duplicateParentState) {
                    jSONObject.put("View_duplicateParentState", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_visibility) {
                    jSONObject.put("View_visibility", obtainStyledAttributes.getInt(index, 0));
                } else if (index == this.View_drawingCacheQuality) {
                    jSONObject.put("View_drawingCacheQuality", obtainStyledAttributes.getInt(index, 0));
                } else if (index == this.View_drawingCacheQuality) {
                    jSONObject.put("View_contentDescription", obtainStyledAttributes.getString(index));
                } else if (index == this.View_soundEffectsEnabled) {
                    jSONObject.put("View_soundEffectsEnabled", obtainStyledAttributes.getBoolean(index, true));
                } else if (index == this.View_hapticFeedbackEnabled) {
                    jSONObject.put("View_hapticFeedbackEnabled", obtainStyledAttributes.getBoolean(index, true));
                } else if (index == this.View_scrollbars) {
                    jSONObject.put("View_scrollbars", obtainStyledAttributes.getInt(index, 0));
                } else if (index == this.View_fadingEdge) {
                    jSONObject.put("View_fadingEdge", obtainStyledAttributes.getInt(index, 0));
                } else if (index == this.View_scrollbarStyle) {
                    jSONObject.put("View_scrollbarStyle", obtainStyledAttributes.getInt(index, 0));
                } else if (index == this.View_scrollbarFadeDuration) {
                    jSONObject.put("View_scrollbarFadeDuration", obtainStyledAttributes.getInt(index, 0));
                } else if (index == this.View_scrollbarDefaultDelayBeforeFade) {
                    jSONObject.put("View_scrollbarDefaultDelayBeforeFade", obtainStyledAttributes.getInt(index, 0));
                } else if (index == this.View_scrollbarSize) {
                    jSONObject.put("View_scrollbarSize", obtainStyledAttributes.getDimensionPixelSize(index, -1));
                } else if (index == this.View_scrollbarThumbHorizontal) {
                    Drawable drawable2 = obtainStyledAttributes.getDrawable(index);
                    jSONObject.put("View_scrollbarThumbHorizontal", getDrawable(drawable2, str + "_View_scrollbarThumbHorizontal", null));
                } else if (index == this.View_scrollbarThumbVertical) {
                    Drawable drawable3 = obtainStyledAttributes.getDrawable(index);
                    jSONObject.put("View_scrollbarThumbVertical", getDrawable(drawable3, str + "_View_scrollbarThumbVertical", null));
                } else if (index == this.View_scrollbarTrackHorizontal) {
                    Drawable drawable4 = obtainStyledAttributes.getDrawable(index);
                    jSONObject.put("View_scrollbarTrackHorizontal", getDrawable(drawable4, str + "_View_scrollbarTrackHorizontal", null));
                } else if (index == this.View_scrollbarTrackVertical) {
                    Drawable drawable5 = obtainStyledAttributes.getDrawable(index);
                    jSONObject.put("View_scrollbarTrackVertical", getDrawable(drawable5, str + "_View_scrollbarTrackVertical", null));
                } else if (index == this.View_isScrollContainer) {
                    jSONObject.put("View_isScrollContainer", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_keepScreenOn) {
                    jSONObject.put("View_keepScreenOn", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_filterTouchesWhenObscured) {
                    jSONObject.put("View_filterTouchesWhenObscured", obtainStyledAttributes.getBoolean(index, false));
                } else if (index == this.View_nextFocusLeft) {
                    jSONObject.put("View_nextFocusLeft", obtainStyledAttributes.getResourceId(index, -1));
                } else if (index == this.View_nextFocusRight) {
                    jSONObject.put("View_nextFocusRight", obtainStyledAttributes.getResourceId(index, -1));
                } else if (index == this.View_nextFocusUp) {
                    jSONObject.put("View_nextFocusUp", obtainStyledAttributes.getResourceId(index, -1));
                } else if (index == this.View_nextFocusDown) {
                    jSONObject.put("View_nextFocusDown", obtainStyledAttributes.getResourceId(index, -1));
                } else if (index == this.View_minWidth) {
                    jSONObject.put("View_minWidth", obtainStyledAttributes.getDimensionPixelSize(index, 0));
                } else if (index == this.View_minHeight) {
                    jSONObject.put("View_minHeight", obtainStyledAttributes.getDimensionPixelSize(index, 0));
                } else if (index == this.View_onClick) {
                    jSONObject.put("View_onClick", obtainStyledAttributes.getString(index));
                } else if (index == this.View_overScrollMode) {
                    jSONObject.put("View_overScrollMode", obtainStyledAttributes.getInt(index, 1));
                } else if (index == this.View_paddingStart) {
                    jSONObject.put("View_paddingStart", obtainStyledAttributes.getDimensionPixelSize(index, 0));
                } else if (index == this.View_paddingEnd) {
                    jSONObject.put("View_paddingEnd", obtainStyledAttributes.getDimensionPixelSize(index, 0));
                }
            }
            obtainStyledAttributes.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject extractTextAppearance(int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(i, (int[]) this.styleableClass.getDeclaredField("TextAppearance").get(null));
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i2 = 0; i2 < indexCount; i2++) {
                int index = obtainStyledAttributes.getIndex(i2);
                if (index == this.TextAppearance_textColorHighlight) {
                    jSONObject.put("TextAppearance_textColorHighlight", obtainStyledAttributes.getColor(index, 0));
                } else if (index == this.TextAppearance_textColor) {
                    jSONObject.put("TextAppearance_textColor", getColorStateList(obtainStyledAttributes.getColorStateList(index)));
                } else if (index == this.TextAppearance_textColorHint) {
                    jSONObject.put("TextAppearance_textColorHint", getColorStateList(obtainStyledAttributes.getColorStateList(index)));
                } else if (index == this.TextAppearance_textColorLink) {
                    jSONObject.put("TextAppearance_textColorLink", getColorStateList(obtainStyledAttributes.getColorStateList(index)));
                } else if (index == this.TextAppearance_textSize) {
                    jSONObject.put("TextAppearance_textSize", obtainStyledAttributes.getDimensionPixelSize(index, 15));
                } else if (index == this.TextAppearance_typeface) {
                    jSONObject.put("TextAppearance_typeface", obtainStyledAttributes.getInt(index, -1));
                } else if (index == this.TextAppearance_textStyle) {
                    jSONObject.put("TextAppearance_textStyle", obtainStyledAttributes.getInt(index, -1));
                } else if (index == this.TextAppearance_textAllCaps) {
                    jSONObject.put("TextAppearance_textAllCaps", obtainStyledAttributes.getBoolean(index, false));
                }
            }
            obtainStyledAttributes.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public JSONObject extractTextAppearanceInformations(String str, String str2, AttributeSet attributeSet, int i) {
        int i2;
        String str3;
        JSONObject jSONObject;
        String str4;
        TypedArray typedArray;
        int i3;
        ColorStateList colorStateList;
        ColorStateList colorStateList2;
        int i4;
        ColorStateList colorStateList3;
        boolean z;
        int i5;
        int i6;
        int i7;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        String str10;
        String str11;
        Drawable drawable;
        StringBuilder sb;
        String str12 = "_TextView_textSelectHandle";
        String str13 = "_TextView_textSelectHandleRight";
        String str14 = "_TextView_textSelectHandleLeft";
        String str15 = "TextView_textSelectHandleRight";
        JSONObject jSONObject2 = new JSONObject();
        try {
            i2 = Class.forName("android.R$attr").getDeclaredField(str).getInt(null);
            str3 = "TextView_textSelectHandle";
            jSONObject = jSONObject2;
            str4 = "TextView_textCursorDrawable";
        } catch (Exception e) {
            e = e;
            e.printStackTrace();
            return jSONObject2;
        }
        try {
            extractViewInformations(str, i2, jSONObject2, str2, attributeSet);
            int i8 = 0;
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("TextView").get(null), i2, 0);
            int i9 = i;
            if (-1 == i9) {
                i9 = obtainStyledAttributes.getResourceId(this.styleableClass.getDeclaredField("TextView_textAppearance").getInt(null), -1);
            }
            if (i9 != -1) {
                typedArray = this.m_theme.obtainStyledAttributes(i9, (int[]) this.styleableClass.getDeclaredField("TextAppearance").get(null));
            } else {
                typedArray = null;
            }
            if (typedArray == null) {
                i3 = 0;
                colorStateList = null;
                colorStateList2 = null;
                i4 = 15;
                colorStateList3 = null;
                z = false;
                i5 = -1;
                i6 = -1;
            } else {
                int indexCount = typedArray.getIndexCount();
                ColorStateList colorStateList4 = null;
                ColorStateList colorStateList5 = null;
                colorStateList3 = null;
                int i10 = 15;
                int i11 = 0;
                z = false;
                i5 = -1;
                i6 = -1;
                while (i11 < indexCount) {
                    int index = typedArray.getIndex(i11);
                    int i12 = indexCount;
                    if (index == this.TextAppearance_textColorHighlight) {
                        i8 = typedArray.getColor(index, i8);
                    } else if (index == this.TextAppearance_textColor) {
                        colorStateList4 = typedArray.getColorStateList(index);
                    } else if (index == this.TextAppearance_textColorHint) {
                        colorStateList5 = typedArray.getColorStateList(index);
                    } else if (index == this.TextAppearance_textColorLink) {
                        colorStateList3 = typedArray.getColorStateList(index);
                    } else if (index == this.TextAppearance_textSize) {
                        i10 = typedArray.getDimensionPixelSize(index, i10);
                    } else if (index == this.TextAppearance_typeface) {
                        i5 = typedArray.getInt(index, -1);
                        i8 = i8;
                    } else {
                        int i13 = i8;
                        if (index == this.TextAppearance_textStyle) {
                            i6 = typedArray.getInt(index, -1);
                            i8 = i13;
                        } else if (index != this.TextAppearance_textAllCaps) {
                            i8 = i13;
                        } else {
                            i8 = i13;
                            z = typedArray.getBoolean(index, false);
                        }
                    }
                    i11++;
                    indexCount = i12;
                }
                i3 = i8;
                typedArray.recycle();
                colorStateList2 = colorStateList4;
                colorStateList = colorStateList5;
                i4 = i10;
            }
            int indexCount2 = obtainStyledAttributes.getIndexCount();
            int i14 = i4;
            ColorStateList colorStateList6 = colorStateList3;
            boolean z2 = z;
            int i15 = i6;
            int i16 = 0;
            ColorStateList colorStateList7 = colorStateList;
            ColorStateList colorStateList8 = colorStateList2;
            int i17 = i5;
            int i18 = i3;
            while (i16 < indexCount2) {
                int i19 = indexCount2;
                int index2 = obtainStyledAttributes.getIndex(i16);
                if (index2 == this.TextView_editable) {
                    i7 = i16;
                    jSONObject2 = jSONObject;
                    jSONObject2.put("TextView_editable", obtainStyledAttributes.getBoolean(index2, false));
                    str7 = str13;
                    str10 = str14;
                    str6 = str4;
                    str9 = str12;
                    str8 = str3;
                } else {
                    i7 = i16;
                    jSONObject2 = jSONObject;
                    if (index2 == this.TextView_inputMethod) {
                        jSONObject2.put("TextView_inputMethod", obtainStyledAttributes.getText(index2));
                        str7 = str13;
                        str10 = str14;
                        str6 = str4;
                        str9 = str12;
                        str8 = str3;
                    } else if (index2 == this.TextView_numeric) {
                        String str16 = str12;
                        jSONObject2.put("TextView_numeric", obtainStyledAttributes.getInt(index2, 0));
                        str7 = str13;
                        str8 = str3;
                        str9 = str16;
                        str6 = str4;
                        str10 = str14;
                    } else {
                        String str17 = str12;
                        if (index2 == this.TextView_digits) {
                            jSONObject2.put("TextView_digits", obtainStyledAttributes.getText(index2));
                            str7 = str13;
                            str8 = str3;
                            str9 = str17;
                            str6 = str4;
                            str10 = str14;
                        } else if (index2 == this.TextView_phoneNumber) {
                            jSONObject2.put("TextView_phoneNumber", obtainStyledAttributes.getBoolean(index2, false));
                            str7 = str13;
                            str8 = str3;
                            str9 = str17;
                            str6 = str4;
                            str10 = str14;
                        } else if (index2 == this.TextView_autoText) {
                            jSONObject2.put("TextView_autoText", obtainStyledAttributes.getBoolean(index2, false));
                            str7 = str13;
                            str8 = str3;
                            str9 = str17;
                            str6 = str4;
                            str10 = str14;
                        } else if (index2 == this.TextView_capitalize) {
                            jSONObject2.put("TextView_capitalize", obtainStyledAttributes.getInt(index2, -1));
                            str7 = str13;
                            str8 = str3;
                            str9 = str17;
                            str6 = str4;
                            str10 = str14;
                        } else if (index2 == this.TextView_bufferType) {
                            jSONObject2.put("TextView_bufferType", obtainStyledAttributes.getInt(index2, 0));
                            str7 = str13;
                            str8 = str3;
                            str9 = str17;
                            str6 = str4;
                            str10 = str14;
                        } else if (index2 == this.TextView_selectAllOnFocus) {
                            jSONObject2.put("TextView_selectAllOnFocus", obtainStyledAttributes.getBoolean(index2, false));
                            str7 = str13;
                            str8 = str3;
                            str9 = str17;
                            str6 = str4;
                            str10 = str14;
                        } else if (index2 == this.TextView_autoLink) {
                            jSONObject2.put("TextView_autoLink", obtainStyledAttributes.getInt(index2, 0));
                            str7 = str13;
                            str8 = str3;
                            str9 = str17;
                            str6 = str4;
                            str10 = str14;
                        } else {
                            String str18 = str15;
                            if (index2 == this.TextView_linksClickable) {
                                jSONObject2.put("TextView_linksClickable", obtainStyledAttributes.getBoolean(index2, true));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_linksClickable) {
                                jSONObject2.put("TextView_linksClickable", obtainStyledAttributes.getBoolean(index2, true));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_drawableLeft) {
                                jSONObject2.put("TextView_drawableLeft", getDrawable(obtainStyledAttributes.getDrawable(index2), str + "_TextView_drawableLeft", null));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_drawableTop) {
                                jSONObject2.put("TextView_drawableTop", getDrawable(obtainStyledAttributes.getDrawable(index2), str + "_TextView_drawableTop", null));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_drawableRight) {
                                jSONObject2.put("TextView_drawableRight", getDrawable(obtainStyledAttributes.getDrawable(index2), str + "_TextView_drawableRight", null));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_drawableBottom) {
                                jSONObject2.put("TextView_drawableBottom", getDrawable(obtainStyledAttributes.getDrawable(index2), str + "_TextView_drawableBottom", null));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_drawableStart) {
                                jSONObject2.put("TextView_drawableStart", getDrawable(obtainStyledAttributes.getDrawable(index2), str + "_TextView_drawableStart", null));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_drawableEnd) {
                                jSONObject2.put("TextView_drawableEnd", getDrawable(obtainStyledAttributes.getDrawable(index2), str + "_TextView_drawableEnd", null));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_drawablePadding) {
                                jSONObject2.put("TextView_drawablePadding", obtainStyledAttributes.getDimensionPixelSize(index2, 0));
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str6 = str4;
                                str15 = str18;
                                str10 = str14;
                            } else if (index2 == this.TextView_textCursorDrawable) {
                                try {
                                    str5 = str4;
                                    try {
                                        jSONObject2.put(str5, getDrawable(obtainStyledAttributes.getDrawable(index2), str + "_TextView_textCursorDrawable", null));
                                    } catch (Exception e2) {
                                        try {
                                            jSONObject2.put(str5, getDrawable(this.m_context.getResources().getDrawable(obtainStyledAttributes.getResourceId(index2, 0)), str + "_TextView_textCursorDrawable", null));
                                        } catch (Exception e3) {
                                            e3.printStackTrace();
                                        }
                                        str6 = str5;
                                        str7 = str13;
                                        str8 = str3;
                                        str9 = str17;
                                        str15 = str18;
                                        str10 = str14;
                                        indexCount2 = i19;
                                        str3 = str8;
                                        str12 = str9;
                                        str14 = str10;
                                        str13 = str7;
                                        str4 = str6;
                                        jSONObject = jSONObject2;
                                        i16 = i7 + 1;
                                    }
                                } catch (Exception e4) {
                                    str5 = str4;
                                }
                                str6 = str5;
                                str7 = str13;
                                str8 = str3;
                                str9 = str17;
                                str15 = str18;
                                str10 = str14;
                            } else {
                                String str19 = str4;
                                if (index2 == this.TextView_maxLines) {
                                    jSONObject2.put("TextView_maxLines", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_maxHeight) {
                                    jSONObject2.put("TextView_maxHeight", obtainStyledAttributes.getDimensionPixelSize(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_lines) {
                                    jSONObject2.put("TextView_lines", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_height) {
                                    jSONObject2.put("TextView_height", obtainStyledAttributes.getDimensionPixelSize(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_minLines) {
                                    jSONObject2.put("TextView_minLines", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_minHeight) {
                                    jSONObject2.put("TextView_minHeight", obtainStyledAttributes.getDimensionPixelSize(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_maxEms) {
                                    jSONObject2.put("TextView_maxEms", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_maxWidth) {
                                    jSONObject2.put("TextView_maxWidth", obtainStyledAttributes.getDimensionPixelSize(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_ems) {
                                    jSONObject2.put("TextView_ems", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_width) {
                                    jSONObject2.put("TextView_width", obtainStyledAttributes.getDimensionPixelSize(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_minEms) {
                                    jSONObject2.put("TextView_minEms", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_minWidth) {
                                    jSONObject2.put("TextView_minWidth", obtainStyledAttributes.getDimensionPixelSize(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_gravity) {
                                    jSONObject2.put("TextView_gravity", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_hint) {
                                    jSONObject2.put("TextView_hint", obtainStyledAttributes.getText(index2));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_text) {
                                    jSONObject2.put("TextView_text", obtainStyledAttributes.getText(index2));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_scrollHorizontally) {
                                    jSONObject2.put("TextView_scrollHorizontally", obtainStyledAttributes.getBoolean(index2, false));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_singleLine) {
                                    jSONObject2.put("TextView_singleLine", obtainStyledAttributes.getBoolean(index2, false));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_ellipsize) {
                                    jSONObject2.put("TextView_ellipsize", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_marqueeRepeatLimit) {
                                    jSONObject2.put("TextView_marqueeRepeatLimit", obtainStyledAttributes.getInt(index2, 3));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_includeFontPadding) {
                                    jSONObject2.put("TextView_includeFontPadding", obtainStyledAttributes.getBoolean(index2, true));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 == this.TextView_cursorVisible) {
                                    jSONObject2.put("TextView_cursorVisible", obtainStyledAttributes.getBoolean(index2, true));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                } else if (index2 != this.TextView_maxLength) {
                                    if (index2 == this.TextView_textScaleX) {
                                        str6 = str19;
                                        str7 = str13;
                                        jSONObject2.put("TextView_textScaleX", obtainStyledAttributes.getFloat(index2, 1.0f));
                                        str8 = str3;
                                        str9 = str17;
                                        str15 = str18;
                                        str10 = str14;
                                    } else {
                                        str6 = str19;
                                        str7 = str13;
                                        if (index2 == this.TextView_freezesText) {
                                            jSONObject2.put("TextView_freezesText", obtainStyledAttributes.getBoolean(index2, false));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_shadowColor) {
                                            jSONObject2.put("TextView_shadowColor", obtainStyledAttributes.getInt(index2, 0));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_shadowDx) {
                                            jSONObject2.put("TextView_shadowDx", obtainStyledAttributes.getFloat(index2, 0.0f));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_shadowDy) {
                                            jSONObject2.put("TextView_shadowDy", obtainStyledAttributes.getFloat(index2, 0.0f));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_shadowRadius) {
                                            jSONObject2.put("TextView_shadowRadius", obtainStyledAttributes.getFloat(index2, 0.0f));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_enabled) {
                                            jSONObject2.put("TextView_enabled", obtainStyledAttributes.getBoolean(index2, true));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_textColorHighlight) {
                                            i18 = obtainStyledAttributes.getColor(index2, i18);
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_textColor) {
                                            colorStateList8 = obtainStyledAttributes.getColorStateList(index2);
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_textColorHint) {
                                            colorStateList7 = obtainStyledAttributes.getColorStateList(index2);
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_textColorLink) {
                                            colorStateList6 = obtainStyledAttributes.getColorStateList(index2);
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_textSize) {
                                            i14 = obtainStyledAttributes.getDimensionPixelSize(index2, i14);
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_typeface) {
                                            i17 = obtainStyledAttributes.getInt(index2, i17);
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_textStyle) {
                                            i15 = obtainStyledAttributes.getInt(index2, i15);
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_password) {
                                            jSONObject2.put("TextView_password", obtainStyledAttributes.getBoolean(index2, false));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_lineSpacingExtra) {
                                            jSONObject2.put("TextView_lineSpacingExtra", obtainStyledAttributes.getDimensionPixelSize(index2, 0));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_lineSpacingMultiplier) {
                                            jSONObject2.put("TextView_lineSpacingMultiplier", obtainStyledAttributes.getFloat(index2, 1.0f));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_inputType) {
                                            jSONObject2.put("TextView_inputType", obtainStyledAttributes.getInt(index2, 0));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_imeOptions) {
                                            jSONObject2.put("TextView_imeOptions", obtainStyledAttributes.getInt(index2, 0));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_imeActionLabel) {
                                            jSONObject2.put("TextView_imeActionLabel", obtainStyledAttributes.getText(index2));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_imeActionId) {
                                            jSONObject2.put("TextView_imeActionId", obtainStyledAttributes.getInt(index2, 0));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_privateImeOptions) {
                                            jSONObject2.put("TextView_privateImeOptions", obtainStyledAttributes.getString(index2));
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 == this.TextView_textSelectHandleLeft && str.equals("textViewStyle")) {
                                            try {
                                                jSONObject2.put("TextView_textSelectHandleLeft", getDrawable(obtainStyledAttributes.getDrawable(index2), str + str14, null));
                                            } catch (Exception e5) {
                                                try {
                                                    jSONObject2.put("TextView_textSelectHandleLeft", getDrawable(this.m_context.getResources().getDrawable(obtainStyledAttributes.getResourceId(index2, 0)), str + str14, null));
                                                } catch (Exception e6) {
                                                    e6.printStackTrace();
                                                }
                                            }
                                            str8 = str3;
                                            str9 = str17;
                                            str15 = str18;
                                            str10 = str14;
                                        } else if (index2 != this.TextView_textSelectHandleRight || !str.equals("textViewStyle")) {
                                            str15 = str18;
                                            if (index2 != this.TextView_textSelectHandle || !str.equals("textViewStyle")) {
                                                str7 = str7;
                                                str8 = str3;
                                                str9 = str17;
                                                str10 = str14;
                                                if (index2 == this.TextView_textIsSelectable) {
                                                    jSONObject2.put("TextView_textIsSelectable", obtainStyledAttributes.getBoolean(index2, false));
                                                } else if (index2 == this.TextView_textAllCaps) {
                                                    z2 = obtainStyledAttributes.getBoolean(index2, false);
                                                }
                                            } else {
                                                try {
                                                    Drawable drawable2 = obtainStyledAttributes.getDrawable(index2);
                                                    StringBuilder sb2 = new StringBuilder();
                                                    sb2.append(str);
                                                    str7 = str7;
                                                    str9 = str17;
                                                    try {
                                                        sb2.append(str9);
                                                        str10 = str14;
                                                        try {
                                                            JSONObject drawable3 = getDrawable(drawable2, sb2.toString(), null);
                                                            str8 = str3;
                                                            try {
                                                                jSONObject2.put(str8, drawable3);
                                                            } catch (Exception e7) {
                                                                try {
                                                                    try {
                                                                        jSONObject2.put(str8, getDrawable(this.m_context.getResources().getDrawable(obtainStyledAttributes.getResourceId(index2, 0)), str + str9, null));
                                                                    } catch (Exception e8) {
                                                                        e = e8;
                                                                        e.printStackTrace();
                                                                        indexCount2 = i19;
                                                                        str3 = str8;
                                                                        str12 = str9;
                                                                        str14 = str10;
                                                                        str13 = str7;
                                                                        str4 = str6;
                                                                        jSONObject = jSONObject2;
                                                                        i16 = i7 + 1;
                                                                    }
                                                                } catch (Exception e9) {
                                                                    e = e9;
                                                                }
                                                                indexCount2 = i19;
                                                                str3 = str8;
                                                                str12 = str9;
                                                                str14 = str10;
                                                                str13 = str7;
                                                                str4 = str6;
                                                                jSONObject = jSONObject2;
                                                                i16 = i7 + 1;
                                                            }
                                                        } catch (Exception e10) {
                                                            str8 = str3;
                                                            jSONObject2.put(str8, getDrawable(this.m_context.getResources().getDrawable(obtainStyledAttributes.getResourceId(index2, 0)), str + str9, null));
                                                            indexCount2 = i19;
                                                            str3 = str8;
                                                            str12 = str9;
                                                            str14 = str10;
                                                            str13 = str7;
                                                            str4 = str6;
                                                            jSONObject = jSONObject2;
                                                            i16 = i7 + 1;
                                                        }
                                                    } catch (Exception e11) {
                                                        str10 = str14;
                                                    }
                                                } catch (Exception e12) {
                                                    str7 = str7;
                                                    str8 = str3;
                                                    str9 = str17;
                                                    str10 = str14;
                                                }
                                            }
                                        } else {
                                            try {
                                                drawable = obtainStyledAttributes.getDrawable(index2);
                                                sb = new StringBuilder();
                                                sb.append(str);
                                                str11 = str7;
                                                try {
                                                    sb.append(str11);
                                                    str15 = str18;
                                                } catch (Exception e13) {
                                                    str15 = str18;
                                                }
                                            } catch (Exception e14) {
                                                str15 = str18;
                                                str11 = str7;
                                            }
                                            try {
                                                jSONObject2.put(str15, getDrawable(drawable, sb.toString(), null));
                                            } catch (Exception e15) {
                                                try {
                                                    jSONObject2.put(str15, getDrawable(this.m_context.getResources().getDrawable(obtainStyledAttributes.getResourceId(index2, 0)), str + str11, null));
                                                } catch (Exception e16) {
                                                    e16.printStackTrace();
                                                }
                                                str7 = str11;
                                                str8 = str3;
                                                str9 = str17;
                                                str10 = str14;
                                                indexCount2 = i19;
                                                str3 = str8;
                                                str12 = str9;
                                                str14 = str10;
                                                str13 = str7;
                                                str4 = str6;
                                                jSONObject = jSONObject2;
                                                i16 = i7 + 1;
                                            }
                                            str7 = str11;
                                            str8 = str3;
                                            str9 = str17;
                                            str10 = str14;
                                        }
                                    }
                                } else {
                                    jSONObject2.put("TextView_maxLength", obtainStyledAttributes.getInt(index2, -1));
                                    str6 = str19;
                                    str7 = str13;
                                    str8 = str3;
                                    str9 = str17;
                                    str15 = str18;
                                    str10 = str14;
                                }
                            }
                        }
                    }
                }
                indexCount2 = i19;
                str3 = str8;
                str12 = str9;
                str14 = str10;
                str13 = str7;
                str4 = str6;
                jSONObject = jSONObject2;
                i16 = i7 + 1;
            }
            jSONObject2 = jSONObject;
            obtainStyledAttributes.recycle();
            jSONObject2.put("TextAppearance_textColorHighlight", i18);
            jSONObject2.put("TextAppearance_textColor", getColorStateList(colorStateList8));
            jSONObject2.put("TextAppearance_textColorHint", getColorStateList(colorStateList7));
            jSONObject2.put("TextAppearance_textColorLink", getColorStateList(colorStateList6));
            jSONObject2.put("TextAppearance_textSize", i14);
            jSONObject2.put("TextAppearance_typeface", i17);
            jSONObject2.put("TextAppearance_textStyle", i15);
            jSONObject2.put("TextAppearance_textAllCaps", z2);
            return jSONObject2;
        } catch (Exception e17) {
            e = e17;
            jSONObject2 = jSONObject;
            e.printStackTrace();
            return jSONObject2;
        }
    }

    public JSONObject extractImageViewInformations(String str, String str2) {
        JSONObject jSONObject = new JSONObject();
        try {
            int i = Class.forName("android.R$attr").getDeclaredField(str).getInt(null);
            extractViewInformations(str, i, jSONObject, str2, null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("ImageView").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(this.ImageView_src);
            if (drawable != null) {
                jSONObject.put("ImageView_src", getDrawable(drawable, str + "_ImageView_src", null));
            }
            jSONObject.put("ImageView_baselineAlignBottom", obtainStyledAttributes.getBoolean(this.ImageView_baselineAlignBottom, false));
            jSONObject.put("ImageView_adjustViewBounds", obtainStyledAttributes.getBoolean(this.ImageView_adjustViewBounds, false));
            jSONObject.put("ImageView_maxWidth", obtainStyledAttributes.getDimensionPixelSize(this.ImageView_maxWidth, Integer.MAX_VALUE));
            jSONObject.put("ImageView_maxHeight", obtainStyledAttributes.getDimensionPixelSize(this.ImageView_maxHeight, Integer.MAX_VALUE));
            int i2 = obtainStyledAttributes.getInt(this.ImageView_scaleType, -1);
            if (i2 >= 0) {
                jSONObject.put("ImageView_scaleType", this.sScaleTypeArray[i2]);
            }
            int i3 = obtainStyledAttributes.getInt(this.ImageView_tint, 0);
            if (i3 != 0) {
                jSONObject.put("ImageView_tint", i3);
            }
            jSONObject.put("ImageView_cropToPadding", obtainStyledAttributes.getBoolean(this.ImageView_cropToPadding, false));
            obtainStyledAttributes.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    void extractCompoundButton(SimpleJsonWriter simpleJsonWriter, String str, String str2) {
        JSONObject extractTextAppearanceInformations = extractTextAppearanceInformations(str, str2, null, -1);
        try {
            int i = Class.forName("android.R$attr").getDeclaredField(str).getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("CompoundButton").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "CompoundButton_button"));
            if (drawable != null) {
                extractTextAppearanceInformations.put("CompoundButton_button", getDrawable(drawable, str + "_CompoundButton_button", null));
            }
            obtainStyledAttributes.recycle();
            simpleJsonWriter.name(str).value(extractTextAppearanceInformations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void extractProgressBarInfo(JSONObject jSONObject, String str) {
        try {
            int i = Class.forName("android.R$attr").getDeclaredField(str).getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("ProgressBar").get(null), i, 0);
            int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "ProgressBar_minWidth"), 24);
            int dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "ProgressBar_maxWidth"), 48);
            int dimensionPixelSize3 = obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "ProgressBar_minHeight"), 24);
            int dimensionPixelSize4 = obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "ProgressBar_maxHeight"), 48);
            jSONObject.put("ProgressBar_indeterminateDuration", obtainStyledAttributes.getInt(getField(this.styleableClass, "ProgressBar_indeterminateDuration"), 4000));
            jSONObject.put("ProgressBar_minWidth", dimensionPixelSize);
            jSONObject.put("ProgressBar_maxWidth", dimensionPixelSize2);
            jSONObject.put("ProgressBar_minHeight", dimensionPixelSize3);
            jSONObject.put("ProgressBar_maxHeight", dimensionPixelSize4);
            jSONObject.put("ProgressBar_progress_id", 16908301);
            jSONObject.put("ProgressBar_secondaryProgress_id", 16908303);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "ProgressBar_progressDrawable"));
            if (drawable != null) {
                jSONObject.put("ProgressBar_progressDrawable", getDrawable(drawable, str + "_ProgressBar_progressDrawable", null));
            }
            Drawable drawable2 = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "ProgressBar_indeterminateDrawable"));
            if (drawable2 != null) {
                jSONObject.put("ProgressBar_indeterminateDrawable", getDrawable(drawable2, str + "_ProgressBar_indeterminateDrawable", null));
            }
            obtainStyledAttributes.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void extractProgressBar(SimpleJsonWriter simpleJsonWriter, String str, String str2) {
        JSONObject extractTextAppearanceInformations = extractTextAppearanceInformations(str, str2, null, -1);
        try {
            extractProgressBarInfo(extractTextAppearanceInformations, str);
            simpleJsonWriter.name(str).value(extractTextAppearanceInformations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void extractAbsSeekBar(SimpleJsonWriter simpleJsonWriter, String str, String str2) {
        JSONObject extractTextAppearanceInformations = extractTextAppearanceInformations(str, str2, null, -1);
        extractProgressBarInfo(extractTextAppearanceInformations, str);
        try {
            int i = Class.forName("android.R$attr").getDeclaredField(str).getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("SeekBar").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "SeekBar_thumb"));
            if (drawable != null) {
                extractTextAppearanceInformations.put("SeekBar_thumb", getDrawable(drawable, str + "_SeekBar_thumb", null));
            }
            try {
                extractTextAppearanceInformations.put("SeekBar_thumbOffset", this.styleableClass.getDeclaredField("SeekBar_thumbOffset").getInt(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
            obtainStyledAttributes.recycle();
            simpleJsonWriter.name(str).value(extractTextAppearanceInformations);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    void extractSwitch(SimpleJsonWriter simpleJsonWriter, String str, String str2) {
        JSONObject jSONObject = new JSONObject();
        try {
            int i = Class.forName("com.android.internal.R$attr").getDeclaredField(str).getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("Switch").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "Switch_thumb"));
            if (drawable != null) {
                jSONObject.put("Switch_thumb", getDrawable(drawable, str + "_Switch_thumb", null));
            }
            Drawable drawable2 = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "Switch_track"));
            if (drawable2 != null) {
                jSONObject.put("Switch_track", getDrawable(drawable2, str + "_Switch_track", null));
            }
            jSONObject.put("Switch_switchTextAppearance", extractTextAppearance(obtainStyledAttributes.getResourceId(this.styleableClass.getDeclaredField("Switch_switchTextAppearance").getInt(null), -1)));
            jSONObject.put("Switch_textOn", obtainStyledAttributes.getText(getField(this.styleableClass, "Switch_textOn")));
            jSONObject.put("Switch_textOff", obtainStyledAttributes.getText(getField(this.styleableClass, "Switch_textOff")));
            jSONObject.put("Switch_switchMinWidth", obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "Switch_switchMinWidth"), 0));
            jSONObject.put("Switch_switchPadding", obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "Switch_switchPadding"), 0));
            jSONObject.put("Switch_thumbTextPadding", obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "Switch_thumbTextPadding"), 0));
            jSONObject.put("Switch_showText", obtainStyledAttributes.getBoolean(getField(this.styleableClass, "Switch_showText"), true));
            jSONObject.put("Switch_splitTrack", obtainStyledAttributes.getBoolean(getField(this.styleableClass, "Switch_splitTrack"), false));
            obtainStyledAttributes.recycle();
            simpleJsonWriter.name(str).value(jSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JSONObject extractCheckedTextView(AttributeSet attributeSet, String str) {
        JSONObject extractTextAppearanceInformations = extractTextAppearanceInformations("textViewStyle", str, attributeSet, -1);
        try {
            int i = Class.forName("android.R$attr").getDeclaredField("textViewStyle").getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(attributeSet, (int[]) this.styleableClass.getDeclaredField("CheckedTextView").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "CheckedTextView_checkMark"));
            if (drawable != null) {
                extractTextAppearanceInformations.put("CheckedTextView_checkMark", getDrawable(drawable, str + "_CheckedTextView_checkMark", null));
            }
            obtainStyledAttributes.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extractTextAppearanceInformations;
    }

    private JSONObject extractItemStyle(int i, String str, int i2) {
        XmlResourceParser layout;
        int next;
        try {
            layout = this.m_context.getResources().getLayout(i);
            while (true) {
                next = layout.next();
                if (next == 2 || next == 1) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (next != 2) {
            return null;
        }
        AttributeSet asAttributeSet = Xml.asAttributeSet(layout);
        String name = layout.getName();
        if (name.equals("TextView")) {
            return extractTextAppearanceInformations("textViewStyle", str, asAttributeSet, i2);
        }
        if (name.equals("CheckedTextView")) {
            return extractCheckedTextView(asAttributeSet, str);
        }
        return null;
    }

    private void extractItemsStyle(SimpleJsonWriter simpleJsonWriter) {
        try {
            simpleJsonWriter.name("simple_list_item").value(extractItemStyle(17367043, "simple_list_item", 16973890));
            simpleJsonWriter.name("simple_list_item_checked").value(extractItemStyle(17367045, "simple_list_item_checked", 16973890));
            simpleJsonWriter.name("simple_list_item_multiple_choice").value(extractItemStyle(17367056, "simple_list_item_multiple_choice", 16973890));
            simpleJsonWriter.name("simple_list_item_single_choice").value(extractItemStyle(17367055, "simple_list_item_single_choice", 16973890));
            simpleJsonWriter.name("simple_spinner_item").value(extractItemStyle(17367048, "simple_spinner_item", -1));
            simpleJsonWriter.name("simple_spinner_dropdown_item").value(extractItemStyle(17367049, "simple_spinner_dropdown_item", 16973890));
            simpleJsonWriter.name("simple_dropdown_item_1line").value(extractItemStyle(17367050, "simple_dropdown_item_1line", 16973890));
            simpleJsonWriter.name("simple_selectable_list_item").value(extractItemStyle(17367061, "simple_selectable_list_item", 16973890));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void extractListView(SimpleJsonWriter simpleJsonWriter, String str, String str2) {
        JSONObject extractTextAppearanceInformations = extractTextAppearanceInformations(str, str2, null, -1);
        try {
            int i = Class.forName("android.R$attr").getDeclaredField(str).getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("ListView").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "ListView_divider"));
            if (drawable != null) {
                extractTextAppearanceInformations.put("ListView_divider", getDrawable(drawable, str + "_ListView_divider", null));
            }
            extractTextAppearanceInformations.put("ListView_dividerHeight", obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "ListView_dividerHeight"), 0));
            obtainStyledAttributes.recycle();
            simpleJsonWriter.name(str).value(extractTextAppearanceInformations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void extractCalendar(SimpleJsonWriter simpleJsonWriter, String str, String str2) {
        JSONObject extractTextAppearanceInformations = extractTextAppearanceInformations(str, str2, null, -1);
        try {
            int i = Class.forName("android.R$attr").getDeclaredField(str).getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("CalendarView").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "CalendarView_selectedDateVerticalBar"));
            if (drawable != null) {
                extractTextAppearanceInformations.put("CalendarView_selectedDateVerticalBar", getDrawable(drawable, str + "_CalendarView_selectedDateVerticalBar", null));
            }
            extractTextAppearanceInformations.put("CalendarView_dateTextAppearance", extractTextAppearance(obtainStyledAttributes.getResourceId(this.styleableClass.getDeclaredField("CalendarView_dateTextAppearance").getInt(null), -1)));
            extractTextAppearanceInformations.put("CalendarView_weekDayTextAppearance", extractTextAppearance(obtainStyledAttributes.getResourceId(this.styleableClass.getDeclaredField("CalendarView_weekDayTextAppearance").getInt(null), -1)));
            extractTextAppearanceInformations.put("CalendarView_firstDayOfWeek", obtainStyledAttributes.getInt(getField(this.styleableClass, "CalendarView_firstDayOfWeek"), 0));
            extractTextAppearanceInformations.put("CalendarView_focusedMonthDateColor", obtainStyledAttributes.getColor(getField(this.styleableClass, "CalendarView_focusedMonthDateColor"), 0));
            extractTextAppearanceInformations.put("CalendarView_selectedWeekBackgroundColor", obtainStyledAttributes.getColor(getField(this.styleableClass, "CalendarView_selectedWeekBackgroundColor"), 0));
            extractTextAppearanceInformations.put("CalendarView_showWeekNumber", obtainStyledAttributes.getBoolean(getField(this.styleableClass, "CalendarView_showWeekNumber"), true));
            extractTextAppearanceInformations.put("CalendarView_shownWeekCount", obtainStyledAttributes.getInt(getField(this.styleableClass, "CalendarView_shownWeekCount"), 6));
            extractTextAppearanceInformations.put("CalendarView_unfocusedMonthDateColor", obtainStyledAttributes.getColor(getField(this.styleableClass, "CalendarView_unfocusedMonthDateColor"), 0));
            extractTextAppearanceInformations.put("CalendarView_weekNumberColor", obtainStyledAttributes.getColor(getField(this.styleableClass, "CalendarView_weekNumberColor"), 0));
            extractTextAppearanceInformations.put("CalendarView_weekSeparatorLineColor", obtainStyledAttributes.getColor(getField(this.styleableClass, "CalendarView_weekSeparatorLineColor"), 0));
            obtainStyledAttributes.recycle();
            simpleJsonWriter.name(str).value(extractTextAppearanceInformations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void extractToolBar(SimpleJsonWriter simpleJsonWriter, String str, String str2) {
        JSONObject extractTextAppearanceInformations = extractTextAppearanceInformations(str, str2, null, -1);
        try {
            int i = Class.forName("com.android.internal.R$attr").getDeclaredField(str).getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("ActionBar").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "ActionBar_background"));
            if (drawable != null) {
                extractTextAppearanceInformations.put("ActionBar_background", getDrawable(drawable, str + "_ActionBar_background", null));
            }
            Drawable drawable2 = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "ActionBar_backgroundStacked"));
            if (drawable2 != null) {
                extractTextAppearanceInformations.put("ActionBar_backgroundStacked", getDrawable(drawable2, str + "_ActionBar_backgroundStacked", null));
            }
            Drawable drawable3 = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "ActionBar_backgroundSplit"));
            if (drawable3 != null) {
                extractTextAppearanceInformations.put("ActionBar_backgroundSplit", getDrawable(drawable3, str + "_ActionBar_backgroundSplit", null));
            }
            Drawable drawable4 = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "ActionBar_divider"));
            if (drawable4 != null) {
                extractTextAppearanceInformations.put("ActionBar_divider", getDrawable(drawable4, str + "_ActionBar_divider", null));
            }
            extractTextAppearanceInformations.put("ActionBar_itemPadding", obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "ActionBar_itemPadding"), 0));
            obtainStyledAttributes.recycle();
            simpleJsonWriter.name(str).value(extractTextAppearanceInformations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void extractTabBar(SimpleJsonWriter simpleJsonWriter, String str, String str2) {
        JSONObject extractTextAppearanceInformations = extractTextAppearanceInformations(str, str2, null, -1);
        try {
            int i = Class.forName("android.R$attr").getDeclaredField(str).getInt(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, (int[]) this.styleableClass.getDeclaredField("LinearLayout").get(null), i, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "LinearLayout_divider"));
            if (drawable != null) {
                extractTextAppearanceInformations.put("LinearLayout_divider", getDrawable(drawable, str + "_LinearLayout_divider", null));
            }
            extractTextAppearanceInformations.put("LinearLayout_showDividers", obtainStyledAttributes.getInt(getField(this.styleableClass, "LinearLayout_showDividers"), 0));
            extractTextAppearanceInformations.put("LinearLayout_dividerPadding", obtainStyledAttributes.getDimensionPixelSize(getField(this.styleableClass, "LinearLayout_dividerPadding"), 0));
            obtainStyledAttributes.recycle();
            simpleJsonWriter.name(str).value(extractTextAppearanceInformations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractWindow(SimpleJsonWriter simpleJsonWriter, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            Class<?> cls = Class.forName("android.R$attr");
            int[] iArr = (int[]) this.styleableClass.getDeclaredField("Window").get(null);
            TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(null, iArr, cls.getDeclaredField("windowBackground").getInt(null), 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(getField(this.styleableClass, "Window_windowBackground"));
            if (drawable != null) {
                jSONObject.put("Window_windowBackground", getDrawable(drawable, str + "_Window_windowBackground", null));
            }
            obtainStyledAttributes.recycle();
            TypedArray obtainStyledAttributes2 = this.m_theme.obtainStyledAttributes(null, iArr, cls.getDeclaredField("windowFrame").getInt(null), 0);
            Drawable drawable2 = obtainStyledAttributes2.getDrawable(getField(this.styleableClass, "Window_windowFrame"));
            if (drawable2 != null) {
                jSONObject.put("Window_windowFrame", getDrawable(drawable2, str + "_Window_windowFrame", null));
            }
            obtainStyledAttributes2.recycle();
            simpleJsonWriter.name(str).value(jSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject extractDefaultPalette() {
        TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(new int[]{16842804});
        JSONObject extractTextAppearance = extractTextAppearance(obtainStyledAttributes.getResourceId(0, -1));
        try {
            extractTextAppearance.put("defaultBackgroundColor", this.defaultBackgroundColor);
            extractTextAppearance.put("defaultTextColorPrimary", this.defaultTextColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        obtainStyledAttributes.recycle();
        return extractTextAppearance;
    }

    public ExtractStyle(Context context, String str, boolean z) {
        int[] iArr = {16842910};
        this.ENABLED_STATE_SET = iArr;
        int[] iArr2 = {16842908};
        this.FOCUSED_STATE_SET = iArr2;
        this.ENABLED_FOCUSED_STATE_SET = stateSetUnion(iArr, iArr2);
        this.ENABLED_SELECTED_STATE_SET = stateSetUnion(this.ENABLED_STATE_SET, this.SELECTED_STATE_SET);
        this.ENABLED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(this.ENABLED_STATE_SET, this.WINDOW_FOCUSED_STATE_SET);
        this.FOCUSED_SELECTED_STATE_SET = stateSetUnion(this.FOCUSED_STATE_SET, this.SELECTED_STATE_SET);
        this.FOCUSED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(this.FOCUSED_STATE_SET, this.WINDOW_FOCUSED_STATE_SET);
        this.ENABLED_FOCUSED_SELECTED_STATE_SET = stateSetUnion(this.ENABLED_FOCUSED_STATE_SET, this.SELECTED_STATE_SET);
        this.ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(this.ENABLED_FOCUSED_STATE_SET, this.WINDOW_FOCUSED_STATE_SET);
        this.ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(this.ENABLED_SELECTED_STATE_SET, this.WINDOW_FOCUSED_STATE_SET);
        this.FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(this.FOCUSED_SELECTED_STATE_SET, this.WINDOW_FOCUSED_STATE_SET);
        this.ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(this.ENABLED_FOCUSED_SELECTED_STATE_SET, this.WINDOW_FOCUSED_STATE_SET);
        int[] stateSetUnion = stateSetUnion(this.PRESSED_STATE_SET, this.SELECTED_STATE_SET);
        this.PRESSED_SELECTED_STATE_SET = stateSetUnion;
        this.PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(stateSetUnion, this.WINDOW_FOCUSED_STATE_SET);
        int[] stateSetUnion2 = stateSetUnion(this.PRESSED_STATE_SET, this.FOCUSED_STATE_SET);
        this.PRESSED_FOCUSED_STATE_SET = stateSetUnion2;
        this.PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(stateSetUnion2, this.WINDOW_FOCUSED_STATE_SET);
        int[] stateSetUnion3 = stateSetUnion(this.PRESSED_FOCUSED_STATE_SET, this.SELECTED_STATE_SET);
        this.PRESSED_FOCUSED_SELECTED_STATE_SET = stateSetUnion3;
        this.PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(stateSetUnion3, this.WINDOW_FOCUSED_STATE_SET);
        int[] stateSetUnion4 = stateSetUnion(this.PRESSED_STATE_SET, this.ENABLED_STATE_SET);
        this.PRESSED_ENABLED_STATE_SET = stateSetUnion4;
        this.PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(stateSetUnion4, this.WINDOW_FOCUSED_STATE_SET);
        int[] stateSetUnion5 = stateSetUnion(this.PRESSED_ENABLED_STATE_SET, this.SELECTED_STATE_SET);
        this.PRESSED_ENABLED_SELECTED_STATE_SET = stateSetUnion5;
        this.PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(stateSetUnion5, this.WINDOW_FOCUSED_STATE_SET);
        int[] stateSetUnion6 = stateSetUnion(this.PRESSED_ENABLED_STATE_SET, this.FOCUSED_STATE_SET);
        this.PRESSED_ENABLED_FOCUSED_STATE_SET = stateSetUnion6;
        this.PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(stateSetUnion6, this.WINDOW_FOCUSED_STATE_SET);
        int[] stateSetUnion7 = stateSetUnion(this.PRESSED_ENABLED_FOCUSED_STATE_SET, this.SELECTED_STATE_SET);
        this.PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = stateSetUnion7;
        this.PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = stateSetUnion(stateSetUnion7, this.WINDOW_FOCUSED_STATE_SET);
        this.View_background = getField(this.styleableClass, "View_background");
        this.View_padding = getField(this.styleableClass, "View_padding");
        this.View_paddingLeft = getField(this.styleableClass, "View_paddingLeft");
        this.View_paddingTop = getField(this.styleableClass, "View_paddingTop");
        this.View_paddingRight = getField(this.styleableClass, "View_paddingRight");
        this.View_paddingBottom = getField(this.styleableClass, "View_paddingBottom");
        this.View_scrollX = getField(this.styleableClass, "View_scrollX");
        this.View_scrollY = getField(this.styleableClass, "View_scrollY");
        this.View_id = getField(this.styleableClass, "View_id");
        this.View_tag = getField(this.styleableClass, "View_tag");
        this.View_fitsSystemWindows = getField(this.styleableClass, "View_fitsSystemWindows");
        this.View_focusable = getField(this.styleableClass, "View_focusable");
        this.View_focusableInTouchMode = getField(this.styleableClass, "View_focusableInTouchMode");
        this.View_clickable = getField(this.styleableClass, "View_clickable");
        this.View_longClickable = getField(this.styleableClass, "View_longClickable");
        this.View_saveEnabled = getField(this.styleableClass, "View_saveEnabled");
        this.View_duplicateParentState = getField(this.styleableClass, "View_duplicateParentState");
        this.View_visibility = getField(this.styleableClass, "View_visibility");
        this.View_drawingCacheQuality = getField(this.styleableClass, "View_drawingCacheQuality");
        this.View_contentDescription = getField(this.styleableClass, "View_contentDescription");
        this.View_soundEffectsEnabled = getField(this.styleableClass, "View_soundEffectsEnabled");
        this.View_hapticFeedbackEnabled = getField(this.styleableClass, "View_hapticFeedbackEnabled");
        this.View_scrollbars = getField(this.styleableClass, "View_scrollbars");
        this.View_fadingEdge = getField(this.styleableClass, "View_fadingEdge");
        this.View_scrollbarStyle = getField(this.styleableClass, "View_scrollbarStyle");
        this.View_scrollbarFadeDuration = getField(this.styleableClass, "View_scrollbarFadeDuration");
        this.View_scrollbarDefaultDelayBeforeFade = getField(this.styleableClass, "View_scrollbarDefaultDelayBeforeFade");
        this.View_scrollbarSize = getField(this.styleableClass, "View_scrollbarSize");
        this.View_scrollbarThumbHorizontal = getField(this.styleableClass, "View_scrollbarThumbHorizontal");
        this.View_scrollbarThumbVertical = getField(this.styleableClass, "View_scrollbarThumbVertical");
        this.View_scrollbarTrackHorizontal = getField(this.styleableClass, "View_scrollbarTrackHorizontal");
        this.View_scrollbarTrackVertical = getField(this.styleableClass, "View_scrollbarTrackVertical");
        this.View_isScrollContainer = getField(this.styleableClass, "View_isScrollContainer");
        this.View_keepScreenOn = getField(this.styleableClass, "View_keepScreenOn");
        this.View_filterTouchesWhenObscured = getField(this.styleableClass, "View_filterTouchesWhenObscured");
        this.View_nextFocusLeft = getField(this.styleableClass, "View_nextFocusLeft");
        this.View_nextFocusRight = getField(this.styleableClass, "View_nextFocusRight");
        this.View_nextFocusUp = getField(this.styleableClass, "View_nextFocusUp");
        this.View_nextFocusDown = getField(this.styleableClass, "View_nextFocusDown");
        this.View_minWidth = getField(this.styleableClass, "View_minWidth");
        this.View_minHeight = getField(this.styleableClass, "View_minHeight");
        this.View_onClick = getField(this.styleableClass, "View_onClick");
        this.View_overScrollMode = getField(this.styleableClass, "View_overScrollMode");
        this.View_paddingStart = getField(this.styleableClass, "View_paddingStart");
        this.View_paddingEnd = getField(this.styleableClass, "View_paddingEnd");
        this.TextAppearance_textColorHighlight = getField(this.styleableClass, "TextAppearance_textColorHighlight");
        this.TextAppearance_textColor = getField(this.styleableClass, "TextAppearance_textColor");
        this.TextAppearance_textColorHint = getField(this.styleableClass, "TextAppearance_textColorHint");
        this.TextAppearance_textColorLink = getField(this.styleableClass, "TextAppearance_textColorLink");
        this.TextAppearance_textSize = getField(this.styleableClass, "TextAppearance_textSize");
        this.TextAppearance_typeface = getField(this.styleableClass, "TextAppearance_typeface");
        this.TextAppearance_textStyle = getField(this.styleableClass, "TextAppearance_textStyle");
        this.TextAppearance_textAllCaps = getField(this.styleableClass, "TextAppearance_textAllCaps");
        this.TextView_editable = getField(this.styleableClass, "TextView_editable");
        this.TextView_inputMethod = getField(this.styleableClass, "TextView_inputMethod");
        this.TextView_numeric = getField(this.styleableClass, "TextView_numeric");
        this.TextView_digits = getField(this.styleableClass, "TextView_digits");
        this.TextView_phoneNumber = getField(this.styleableClass, "TextView_phoneNumber");
        this.TextView_autoText = getField(this.styleableClass, "TextView_autoText");
        this.TextView_capitalize = getField(this.styleableClass, "TextView_capitalize");
        this.TextView_bufferType = getField(this.styleableClass, "TextView_bufferType");
        this.TextView_selectAllOnFocus = getField(this.styleableClass, "TextView_selectAllOnFocus");
        this.TextView_autoLink = getField(this.styleableClass, "TextView_autoLink");
        this.TextView_linksClickable = getField(this.styleableClass, "TextView_linksClickable");
        this.TextView_drawableLeft = getField(this.styleableClass, "TextView_drawableLeft");
        this.TextView_drawableTop = getField(this.styleableClass, "TextView_drawableTop");
        this.TextView_drawableRight = getField(this.styleableClass, "TextView_drawableRight");
        this.TextView_drawableBottom = getField(this.styleableClass, "TextView_drawableBottom");
        this.TextView_drawableStart = getField(this.styleableClass, "TextView_drawableStart");
        this.TextView_drawableEnd = getField(this.styleableClass, "TextView_drawableEnd");
        this.TextView_drawablePadding = getField(this.styleableClass, "TextView_drawablePadding");
        this.TextView_textCursorDrawable = getField(this.styleableClass, "TextView_textCursorDrawable");
        this.TextView_maxLines = getField(this.styleableClass, "TextView_maxLines");
        this.TextView_maxHeight = getField(this.styleableClass, "TextView_maxHeight");
        this.TextView_lines = getField(this.styleableClass, "TextView_lines");
        this.TextView_height = getField(this.styleableClass, "TextView_height");
        this.TextView_minLines = getField(this.styleableClass, "TextView_minLines");
        this.TextView_minHeight = getField(this.styleableClass, "TextView_minHeight");
        this.TextView_maxEms = getField(this.styleableClass, "TextView_maxEms");
        this.TextView_maxWidth = getField(this.styleableClass, "TextView_maxWidth");
        this.TextView_ems = getField(this.styleableClass, "TextView_ems");
        this.TextView_width = getField(this.styleableClass, "TextView_width");
        this.TextView_minEms = getField(this.styleableClass, "TextView_minEms");
        this.TextView_minWidth = getField(this.styleableClass, "TextView_minWidth");
        this.TextView_gravity = getField(this.styleableClass, "TextView_gravity");
        this.TextView_hint = getField(this.styleableClass, "TextView_hint");
        this.TextView_text = getField(this.styleableClass, "TextView_text");
        this.TextView_scrollHorizontally = getField(this.styleableClass, "TextView_scrollHorizontally");
        this.TextView_singleLine = getField(this.styleableClass, "TextView_singleLine");
        this.TextView_ellipsize = getField(this.styleableClass, "TextView_ellipsize");
        this.TextView_marqueeRepeatLimit = getField(this.styleableClass, "TextView_marqueeRepeatLimit");
        this.TextView_includeFontPadding = getField(this.styleableClass, "TextView_includeFontPadding");
        this.TextView_cursorVisible = getField(this.styleableClass, "TextView_cursorVisible");
        this.TextView_maxLength = getField(this.styleableClass, "TextView_maxLength");
        this.TextView_textScaleX = getField(this.styleableClass, "TextView_textScaleX");
        this.TextView_freezesText = getField(this.styleableClass, "TextView_freezesText");
        this.TextView_shadowColor = getField(this.styleableClass, "TextView_shadowColor");
        this.TextView_shadowDx = getField(this.styleableClass, "TextView_shadowDx");
        this.TextView_shadowDy = getField(this.styleableClass, "TextView_shadowDy");
        this.TextView_shadowRadius = getField(this.styleableClass, "TextView_shadowRadius");
        this.TextView_enabled = getField(this.styleableClass, "TextView_enabled");
        this.TextView_textColorHighlight = getField(this.styleableClass, "TextView_textColorHighlight");
        this.TextView_textColor = getField(this.styleableClass, "TextView_textColor");
        this.TextView_textColorHint = getField(this.styleableClass, "TextView_textColorHint");
        this.TextView_textColorLink = getField(this.styleableClass, "TextView_textColorLink");
        this.TextView_textSize = getField(this.styleableClass, "TextView_textSize");
        this.TextView_typeface = getField(this.styleableClass, "TextView_typeface");
        this.TextView_textStyle = getField(this.styleableClass, "TextView_textStyle");
        this.TextView_password = getField(this.styleableClass, "TextView_password");
        this.TextView_lineSpacingExtra = getField(this.styleableClass, "TextView_lineSpacingExtra");
        this.TextView_lineSpacingMultiplier = getField(this.styleableClass, "TextView_lineSpacingMultiplier");
        this.TextView_inputType = getField(this.styleableClass, "TextView_inputType");
        this.TextView_imeOptions = getField(this.styleableClass, "TextView_imeOptions");
        this.TextView_imeActionLabel = getField(this.styleableClass, "TextView_imeActionLabel");
        this.TextView_imeActionId = getField(this.styleableClass, "TextView_imeActionId");
        this.TextView_privateImeOptions = getField(this.styleableClass, "TextView_privateImeOptions");
        this.TextView_textSelectHandleLeft = getField(this.styleableClass, "TextView_textSelectHandleLeft");
        this.TextView_textSelectHandleRight = getField(this.styleableClass, "TextView_textSelectHandleRight");
        this.TextView_textSelectHandle = getField(this.styleableClass, "TextView_textSelectHandle");
        this.TextView_textIsSelectable = getField(this.styleableClass, "TextView_textIsSelectable");
        this.TextView_textAllCaps = getField(this.styleableClass, "TextView_textAllCaps");
        this.ImageView_src = getField(this.styleableClass, "ImageView_src");
        this.ImageView_baselineAlignBottom = getField(this.styleableClass, "ImageView_baselineAlignBottom");
        this.ImageView_adjustViewBounds = getField(this.styleableClass, "ImageView_adjustViewBounds");
        this.ImageView_maxWidth = getField(this.styleableClass, "ImageView_maxWidth");
        this.ImageView_maxHeight = getField(this.styleableClass, "ImageView_maxHeight");
        this.ImageView_scaleType = getField(this.styleableClass, "ImageView_scaleType");
        this.ImageView_tint = getField(this.styleableClass, "ImageView_tint");
        this.ImageView_cropToPadding = getField(this.styleableClass, "ImageView_cropToPadding");
        this.DrawableStates = new int[]{16842914, 16842912, 16842910, 16842908, 16842919, 16842913, 16842909, 16908288, 16843597, 16843518, 16843547};
        this.DrawableStatesLabels = new String[]{"active", "checked", "enabled", "focused", "pressed", "selected", "window_focused", "background", "multiline", "activated", "accelerated"};
        this.DisableDrawableStatesLabels = new String[]{"inactive", "unchecked", "disabled", "not_focused", "no_pressed", "unselected", "window_not_focused", "background", "multiline", "activated", "accelerated"};
        this.m_drawableCache = new HashMap<>();
        this.sScaleTypeArray = new String[]{"MATRIX", "FIT_XY", "FIT_START", "FIT_CENTER", "FIT_END", "CENTER", "CENTER_CROP", "CENTER_INSIDE"};
        this.m_minimal = z;
        this.m_extractPath = str + "/";
        new File(this.m_extractPath).mkdirs();
        this.m_context = context;
        Resources.Theme theme = context.getTheme();
        this.m_theme = theme;
        TypedArray obtainStyledAttributes = theme.obtainStyledAttributes(new int[]{16842801, 16842806, 16842904});
        this.defaultBackgroundColor = obtainStyledAttributes.getColor(0, 0);
        int color = obtainStyledAttributes.getColor(1, 16777215);
        this.defaultTextColor = color == 16777215 ? obtainStyledAttributes.getColor(2, 16777215) : color;
        obtainStyledAttributes.recycle();
        try {
            SimpleJsonWriter simpleJsonWriter = new SimpleJsonWriter(this.m_extractPath + "style.json");
            simpleJsonWriter.beginObject();
            try {
                simpleJsonWriter.name("defaultStyle").value(extractDefaultPalette());
                extractWindow(simpleJsonWriter, "windowStyle");
                simpleJsonWriter.name("buttonStyle").value(extractTextAppearanceInformations("buttonStyle", "QPushButton", null, -1));
                simpleJsonWriter.name("spinnerStyle").value(extractTextAppearanceInformations("spinnerStyle", "QComboBox", null, -1));
                extractProgressBar(simpleJsonWriter, "progressBarStyleHorizontal", "QProgressBar");
                extractProgressBar(simpleJsonWriter, "progressBarStyleLarge", null);
                extractProgressBar(simpleJsonWriter, "progressBarStyleSmall", null);
                extractProgressBar(simpleJsonWriter, "progressBarStyle", null);
                extractAbsSeekBar(simpleJsonWriter, "seekBarStyle", "QSlider");
                extractSwitch(simpleJsonWriter, "switchStyle", null);
                extractCompoundButton(simpleJsonWriter, "checkboxStyle", "QCheckBox");
                simpleJsonWriter.name("editTextStyle").value(extractTextAppearanceInformations("editTextStyle", "QLineEdit", null, -1));
                extractCompoundButton(simpleJsonWriter, "radioButtonStyle", "QRadioButton");
                simpleJsonWriter.name("textViewStyle").value(extractTextAppearanceInformations("textViewStyle", "QWidget", null, -1));
                simpleJsonWriter.name("scrollViewStyle").value(extractTextAppearanceInformations("scrollViewStyle", "QAbstractScrollArea", null, -1));
                extractListView(simpleJsonWriter, "listViewStyle", "QListView");
                simpleJsonWriter.name("listSeparatorTextViewStyle").value(extractTextAppearanceInformations("listSeparatorTextViewStyle", null, null, -1));
                extractItemsStyle(simpleJsonWriter);
                extractCompoundButton(simpleJsonWriter, "buttonStyleToggle", null);
                extractCalendar(simpleJsonWriter, "calendarViewStyle", "QCalendarWidget");
                extractToolBar(simpleJsonWriter, "actionBarStyle", "QToolBar");
                simpleJsonWriter.name("actionButtonStyle").value(extractTextAppearanceInformations("actionButtonStyle", "QToolButton", null, -1));
                simpleJsonWriter.name("actionBarTabTextStyle").value(extractTextAppearanceInformations("actionBarTabTextStyle", null, null, -1));
                simpleJsonWriter.name("actionBarTabStyle").value(extractTextAppearanceInformations("actionBarTabStyle", null, null, -1));
                simpleJsonWriter.name("actionOverflowButtonStyle").value(extractImageViewInformations("actionOverflowButtonStyle", null));
                extractTabBar(simpleJsonWriter, "actionBarTabBarStyle", "QTabBar");
            } catch (Exception e) {
                e.printStackTrace();
            }
            simpleJsonWriter.endObject();
            simpleJsonWriter.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
