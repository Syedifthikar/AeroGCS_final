package org.qtproject.qt5.android;

import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputMethodManager;

/* loaded from: classes.dex */
public class QtInputConnection extends BaseInputConnection {
    private static final int ID_ADD_TO_DICTIONARY = 16908330;
    private static final int ID_COPY = 16908321;
    private static final int ID_COPY_URL = 16908323;
    private static final int ID_CUT = 16908320;
    private static final int ID_PASTE = 16908322;
    private static final int ID_SELECT_ALL = 16908319;
    private static final int ID_SWITCH_INPUT_METHOD = 16908324;
    private QtEditText m_view;

    private void setClosing(boolean z) {
        if (z) {
            this.m_view.postDelayed(new HideKeyboardRunnable(), 100L);
        } else {
            QtNative.activityDelegate().setKeyboardVisibility(true, System.nanoTime());
        }
    }

    public QtInputConnection(QtEditText qtEditText) {
        super(qtEditText, true);
        this.m_view = null;
        this.m_view = qtEditText;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean beginBatchEdit() {
        setClosing(false);
        return QtNativeInputConnection.beginBatchEdit();
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean endBatchEdit() {
        setClosing(false);
        return QtNativeInputConnection.endBatchEdit();
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitCompletion(CompletionInfo completionInfo) {
        setClosing(false);
        return QtNativeInputConnection.commitCompletion(completionInfo.getText().toString(), completionInfo.getPosition());
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence charSequence, int i) {
        setClosing(false);
        return QtNativeInputConnection.commitText(charSequence.toString(), i);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int i, int i2) {
        setClosing(false);
        return QtNativeInputConnection.deleteSurroundingText(i, i2);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean finishComposingText() {
        setClosing(true);
        return QtNativeInputConnection.finishComposingText();
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public int getCursorCapsMode(int i) {
        return QtNativeInputConnection.getCursorCapsMode(i);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
        QtExtractedText extractedText = QtNativeInputConnection.getExtractedText(extractedTextRequest.hintMaxChars, extractedTextRequest.hintMaxLines, i);
        if (extractedText == null) {
            return null;
        }
        ExtractedText extractedText2 = new ExtractedText();
        extractedText2.partialEndOffset = extractedText.partialEndOffset;
        extractedText2.partialStartOffset = extractedText.partialStartOffset;
        extractedText2.selectionEnd = extractedText.selectionEnd;
        extractedText2.selectionStart = extractedText.selectionStart;
        extractedText2.startOffset = extractedText.startOffset;
        extractedText2.text = extractedText.text;
        return extractedText2;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public CharSequence getSelectedText(int i) {
        return QtNativeInputConnection.getSelectedText(i);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public CharSequence getTextAfterCursor(int i, int i2) {
        return QtNativeInputConnection.getTextAfterCursor(i, i2);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public CharSequence getTextBeforeCursor(int i, int i2) {
        return QtNativeInputConnection.getTextBeforeCursor(i, i2);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean performContextMenuAction(int i) {
        if (i != ID_ADD_TO_DICTIONARY) {
            switch (i) {
                case ID_SELECT_ALL /* 16908319 */:
                    return QtNativeInputConnection.selectAll();
                case ID_CUT /* 16908320 */:
                    return QtNativeInputConnection.cut();
                case ID_COPY /* 16908321 */:
                    return QtNativeInputConnection.copy();
                case ID_PASTE /* 16908322 */:
                    return QtNativeInputConnection.paste();
                case ID_COPY_URL /* 16908323 */:
                    return QtNativeInputConnection.copyURL();
                case ID_SWITCH_INPUT_METHOD /* 16908324 */:
                    InputMethodManager inputMethodManager = (InputMethodManager) this.m_view.getContext().getSystemService("input_method");
                    if (inputMethodManager != null) {
                        inputMethodManager.showInputMethodPicker();
                    }
                    return true;
                default:
                    return super.performContextMenuAction(i);
            }
        }
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence charSequence, int i) {
        setClosing(false);
        return QtNativeInputConnection.setComposingText(charSequence.toString(), i);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean setComposingRegion(int i, int i2) {
        setClosing(false);
        return QtNativeInputConnection.setComposingRegion(i, i2);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean setSelection(int i, int i2) {
        setClosing(false);
        return QtNativeInputConnection.setSelection(i, i2);
    }
}
