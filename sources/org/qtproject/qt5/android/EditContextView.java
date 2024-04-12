package org.qtproject.qt5.android;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;

/* loaded from: classes.dex */
public class EditContextView extends LinearLayout implements View.OnClickListener {
    public static final int COPY_BUTTON = 2;
    public static final int CUT_BUTTON = 1;
    public static final int PASTE_BUTTON = 4;
    public static final int SALL_BUTTON = 8;
    HashMap<Integer, ContextButton> m_buttons;
    OnClickListener m_onClickListener;

    /* loaded from: classes.dex */
    public interface OnClickListener {
        void contextButtonClicked(int i);
    }

    /* loaded from: classes.dex */
    public class ContextButton extends TextView {
        public int m_buttonId;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ContextButton(Context context, int i) {
            super(context);
            EditContextView.this = r3;
            this.m_buttonId = i;
            setText(i);
            setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0f));
            setGravity(17);
            setTextColor(getResources().getColor(17170442));
            r3.setBackground(getResources().getDrawable(17301529));
            float f = getResources().getDisplayMetrics().density;
            int i2 = (int) ((16.0f * f) + 0.5f);
            int i3 = (int) ((f * 8.0f) + 0.5f);
            setPadding(i2, i3, i2, i3);
            setSingleLine();
            setEllipsize(TextUtils.TruncateAt.END);
            setOnClickListener(r3);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.m_onClickListener.contextButtonClicked(((ContextButton) view).m_buttonId);
    }

    void addButton(int i) {
        ContextButton contextButton = new ContextButton(getContext(), i);
        this.m_buttons.put(Integer.valueOf(i), contextButton);
        addView(contextButton);
    }

    public void updateButtons(int i) {
        this.m_buttons.get(17039363).setVisibility((i & 1) != 0 ? 0 : 8);
        this.m_buttons.get(17039361).setVisibility((i & 2) != 0 ? 0 : 8);
        this.m_buttons.get(17039371).setVisibility((i & 4) != 0 ? 0 : 8);
        this.m_buttons.get(17039373).setVisibility((i & 8) == 0 ? 8 : 0);
    }

    public EditContextView(Context context, OnClickListener onClickListener) {
        super(context);
        this.m_buttons = new HashMap<>(4);
        this.m_onClickListener = onClickListener;
        setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        addButton(17039363);
        addButton(17039361);
        addButton(17039371);
        addButton(17039373);
    }
}
