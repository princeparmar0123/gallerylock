package com.calculator.vault.lock.hide.photo.video.custom;//package com.master.valultcalculator.Custom;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Rect;
//import android.os.Parcelable;
//import android.text.Editable;
//import android.text.TextPaint;
//import android.text.method.ScrollingMovementMethod;
//import android.util.AttributeSet;
//import android.view.ActionMode;
//import android.view.ActionMode.Callback;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.appcompat.widget.AppCompatEditText;
//
//import com.master.valultcalculator.R;
//
//import java.util.HashMap;
//
//import kotlin.jvm.JvmOverloads;
//import kotlin.jvm.internal.Intrinsics;
//
//public final class CalculatorEditText extends AppCompatEditText {
//    public static final C4589b Companion = new C4589b(null);
//    public static final C4588a NO_SELECTION_ACTION_MODE_CALLBACK = new C4588a();
//    public HashMap _$_findViewCache;
//    //public final float mMaximumTextSize;
//    //public final float mMinimumTextSize;
//    public C4590c mOnTextSizeChangeListener;
//    //public final float mStepTextSize;
//    public final TextPaint mTempPaint;
//    public final Rect mTempRect;
//    public int mWidthConstraint;
//
//    /* renamed from: com.privacy.common.widget.calculate.CalculatorEditText$a */
//    public static final class C4588a implements Callback {
//        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
//            return false;
//        }
//
//        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
//            return false;
//        }
//
//        public void onDestroyActionMode(ActionMode actionMode) {
//        }
//
//        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
//            return false;
//        }
//    }
//
//    /* renamed from: com.privacy.common.widget.calculate.CalculatorEditText$b */
//    public static final class C4589b {
//        public C4589b() {
//        }
//
//        public /* synthetic */ C4589b(DefaultConstructorMarker defaultConstructorMarker) {
//            this();
//        }
//    }
//
//    /* renamed from: com.privacy.common.widget.calculate.CalculatorEditText$c */
//    public interface C4590c {
//        /* renamed from: a */
//        void mo24693a(TextView textView, float f);
//    }
//
//    @JvmOverloads
//    public CalculatorEditText(Context context) {
//        this(context, null, 0, 6, null);
//    }
//
//    @JvmOverloads
//    public CalculatorEditText(Context context, AttributeSet attributeSet) {
//        this(context, attributeSet, 0, 4, null);
//    }
//
//    public /* synthetic */ CalculatorEditText(Context context, AttributeSet attributeSet, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
//        this(context, attributeSet, i);
//        if ((i2 & 2) != 0) {
//            attributeSet = null;
//        }
//        if ((i2 & 4) != 0) {
//            i = 0;
//        }
//
//    }
//
//    public void _$_clearFindViewByIdCache() {
//        HashMap hashMap = this._$_findViewCache;
//        if (hashMap != null) {
//            hashMap.clear();
//        }
//    }
//
//    public View _$_findCachedViewById(int i) {
//        if (this._$_findViewCache == null) {
//            this._$_findViewCache = new HashMap();
//        }
//        View view = (View) this._$_findViewCache.get(Integer.valueOf(i));
//        if (view != null) {
//            return view;
//        }
//        View findViewById = findViewById(i);
//        this._$_findViewCache.put(Integer.valueOf(i), findViewById);
//        return findViewById;
//    }
//
//    public int getCompoundPaddingBottom() {
//        TextPaint paint = getPaint();
//        Intrinsics.checkExpressionValueIsNotNull(paint, "paint");
//        return super.getCompoundPaddingBottom() - Math.min(getPaddingBottom(), paint.getFontMetricsInt().descent);
//    }
//
//    public int getCompoundPaddingTop() {
//        getPaint().getTextBounds("H", 0, 1, this.mTempRect);
//        TextPaint paint = getPaint();
//        Intrinsics.checkExpressionValueIsNotNull(paint, "paint");
//        return super.getCompoundPaddingTop() - Math.min(getPaddingTop(), -(paint.getFontMetricsInt().ascent + this.mTempRect.height()));
//    }
//
//    public final float getVariableTextSize(String str) {
//        if (this.mWidthConstraint < 0 || this.mMaximumTextSize <= this.mMinimumTextSize) {
//            return getTextSize();
//        }
//        this.mTempPaint.set(getPaint());
//        float f = this.mMinimumTextSize;
//        while (true) {
//            float f2 = this.mMaximumTextSize;
//            if (f >= f2) {
//                break;
//            }
//            float min = Math.min(this.mStepTextSize + f, f2);
//            this.mTempPaint.setTextSize(min);
//            if (this.mTempPaint.measureText(str) > ((float) this.mWidthConstraint)) {
//                break;
//            }
//            f = min;
//        }
//        return f;
//    }
//
//    public void onMeasure(int i, int i2) {
//        super.onMeasure(i, i2);
//        this.mWidthConstraint = (MeasureSpec.getSize(i) - getPaddingLeft()) - getPaddingRight();
//        Editable text = getText();
//        if (text == null) {
//            Intrinsics.throwNpe();
//        }
//        setTextSize(0, getVariableTextSize(text.toString()));
//    }
//
//    public Parcelable onSaveInstanceState() {
//        super.onSaveInstanceState();
//        return null;
//    }
//
//    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//        super.onTextChanged(charSequence, i, i2, i3);
//        int length = charSequence.length();
//        if (!(getSelectionStart() == length && getSelectionEnd() == length)) {
//            setSelection(length);
//        }
//        setTextSize(0, getVariableTextSize(charSequence.toString()));
//    }
//
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//        if (motionEvent.getActionMasked() == 1) {
//            cancelLongPress();
//        }
//        return super.onTouchEvent(motionEvent);
//    }
//
//    public final void setOnTextSizeChangeListener(C4590c cVar) {
//        this.mOnTextSizeChangeListener = cVar;
//    }
//
//    public void setTextSize(int i, float f) {
//        float textSize = getTextSize();
//        super.setTextSize(i, f);
//        if (this.mOnTextSizeChangeListener != null && getTextSize() != textSize) {
//            C4590c cVar = this.mOnTextSizeChangeListener;
//            if (cVar == null) {
//                Intrinsics.throwNpe();
//            }
//            cVar.mo24693a(this, textSize);
//        }
//    }
//
//    @SuppressLint("ResourceType")
//    @JvmOverloads
//    public CalculatorEditText(Context context, AttributeSet attributeSet, int i) {
//        super(context, attributeSet, i);
//        this.mTempPaint = new TextPaint();
//        this.mTempRect = new Rect();
//        this.mWidthConstraint = -1;
//       // TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CalculatorEditText, i, 0);
////        this.mMaximumTextSize = obtainStyledAttributes.getDimension(0, getTextSize());
////        this.mMinimumTextSize = obtainStyledAttributes.getDimension(1, getTextSize());
////        this.mStepTextSize = obtainStyledAttributes.getDimension(2, (this.mMaximumTextSize - this.mMinimumTextSize) / ((float) 3));
////        obtainStyledAttributes.recycle();
//        setCustomSelectionActionModeCallback(NO_SELECTION_ACTION_MODE_CALLBACK);
//        if (isFocusable()) {
//            setMovementMethod(ScrollingMovementMethod.getInstance());
//        }
//       // setTextSize(0, this.mMaximumTextSize);
//        setMinHeight(getLineHeight() + getCompoundPaddingBottom() + getCompoundPaddingTop());
//    }
//}
