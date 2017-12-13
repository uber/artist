package com.uber.artist.mylibrary;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.SuppressWarnings;

public class MyLinearLayout extends LinearLayout implements MyView {
  private Drawable foreground;

  private final Rect selfBounds = new Rect();

  private final Rect overlayBounds = new Rect();

  private boolean foregroundInPadding = true;

  private boolean foregroundBoundsChanged = false;

  private int foregroundGravity = Gravity.FILL;

  private boolean clicksIsInitting;

  @Nullable private PublishRelay<Signal> clicks;

  @Nullable private Disposable clicksDisposable;

  private boolean longClicksIsInitting;

  @Nullable private PublishRelay<Signal> longClicks;

  @Nullable private Disposable longClicksDisposable;

  public MyLinearLayout(Context context) {
    this(context, null);
  }

  public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyLinearLayout(Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
  }

  public View sampleMethodFromCustomTrait() {
    return this;
  }

  public boolean isVisible() {
    return getVisibility() == View.VISIBLE;
  }

  public boolean isInvisible() {
    return getVisibility() == View.INVISIBLE;
  }

  public boolean isGone() {
    return getVisibility() == View.GONE;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    foregroundBoundsChanged = true;
  }

  /**
   * Describes how the foreground is positioned.
   *
   * @return foreground gravity.
   * @see #setForegroundGravity(int)
   */
  @SuppressWarnings("MissingOverride")
  public int getForegroundGravity() {
    return foregroundGravity;
  }

  /**
   * Describes how the foreground is positioned. Defaults to START and TOP.
   *
   * @param foregroundGravity See {@link android.view.Gravity}
   * @see #getForegroundGravity()
   */
  @SuppressWarnings("MissingOverride")
  public void setForegroundGravity(int foregroundGravity) {
    if (this.foregroundGravity != foregroundGravity) {
      if ((foregroundGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
        foregroundGravity |= GravityCompat.START;
      }
      if ((foregroundGravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
        foregroundGravity |= Gravity.TOP;
      }
      this.foregroundGravity = foregroundGravity;
      if (this.foregroundGravity == Gravity.FILL && foreground != null) {
        Rect padding = new Rect();
        foreground.getPadding(padding);
      }
      requestLayout();
    }
  }

  @Override
  protected boolean verifyDrawable(Drawable who) {
    return super.verifyDrawable(who) || (who == foreground);
  }

  @Override
  public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    if (foreground != null) {
      foreground.jumpToCurrentState();
    }
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    if (foreground != null && foreground.isStateful()) {
      foreground.setState(getDrawableState());
    }
  }

  /**
   * Returns the drawable used as the foreground of this view. The foreground drawable, if non-null,
   * is always drawn on top of the children.
   *
   * @return A Drawable or null if no foreground was set.
   */
  @SuppressWarnings("MissingOverride")
  public Drawable getForeground() {
    return foreground;
  }

  /**
   * Supply a Drawable that is to be rendered on top of all of the child views in this layout. Any
   * padding in the Drawable will be taken into account by ensuring that the children are inset to
   * be placed inside of the padding area.
   *
   * @param drawable The Drawable to be drawn on top of the children.
   */
  @SuppressWarnings("MissingOverride")
  @SuppressLint("NewApi")
  public void setForeground(Drawable drawable) {
    if (foreground != drawable) {
      if (foreground != null) {
        foreground.setCallback(null);
        unscheduleDrawable(foreground);
      }
      foreground = drawable;
      if (drawable != null) {
        setWillNotDraw(false);
        drawable.setCallback(this);
        if (drawable.isStateful()) {
          drawable.setState(getDrawableState());
        }
        if (foregroundGravity == Gravity.FILL) {
          Rect padding = new Rect();
          drawable.getPadding(padding);
        }
      } else {
        setWillNotDraw(true);
      }
      requestLayout();
      invalidate();
    }
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (changed) {
      foregroundBoundsChanged = true;
    }
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    if (foreground != null) {
      final Drawable localForeground = foreground;
      if (foregroundBoundsChanged) {
        foregroundBoundsChanged = false;
        final Rect localSelfBounds = selfBounds;
        final Rect localOverlayBounds = overlayBounds;
        final int w = getRight() - getLeft();
        final int h = getBottom() - getTop();
        if (foregroundInPadding) {
          localSelfBounds.set(0, 0, w, h);
        } else {
          localSelfBounds.set(
              getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
        }
        Gravity.apply(
            foregroundGravity,
            localForeground.getIntrinsicWidth(),
            localForeground.getIntrinsicHeight(),
            localSelfBounds,
            localOverlayBounds);
        localForeground.setBounds(localOverlayBounds);
      }
      localForeground.draw(canvas);
    }
  }

  @TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void drawableHotspotChanged(float x, float y) {
    super.drawableHotspotChanged(x, y);
    if (foreground != null) {
      foreground.setHotspot(x, y);
    }
  }

  /** @deprecated Use {@link #clicks()} */
  @Override
  @Deprecated
  public final void setOnClickListener(final OnClickListener l) {
    if (clicksIsInitting) {
      clicksIsInitting = false;
      super.setOnClickListener(l);
    } else {
      if (clicksDisposable != null) {
        clicksDisposable.dispose();
        clicksDisposable = null;
      }
      if (l != null) {
        clicksDisposable =
            clicks()
                .subscribe(
                    new Consumer<Signal>() {
                      @Override
                      public void accept(Signal ignored) {
                        l.onClick(MyLinearLayout.this);
                      }
                    });
      }
    }
  }

  /**
   * @return an Observable of click events. The emitted value is unspecified and should only be used
   *     as notification.
   */
  public Observable<Signal> clicks() {
    if (clicks == null) {
      clicksIsInitting = true;
      clicks = PublishRelay.create();
      RxView.clicks(this)
          .map(MyUtils.createRxBindingSignalMapper())
          .doOnNext(MyUtils.createTapProcessor())
          .subscribe(clicks);
    }
    return clicks.hide();
  }

  /** @deprecated Use {@link #longClicks()} */
  @Override
  @Deprecated
  public final void setOnLongClickListener(final OnLongClickListener l) {
    if (longClicksIsInitting) {
      longClicksIsInitting = false;
      super.setOnLongClickListener(l);
    } else {
      if (longClicksDisposable != null) {
        longClicksDisposable.dispose();
        longClicksDisposable = null;
      }
      if (l != null) {
        longClicksDisposable =
            longClicks()
                .subscribe(
                    new Consumer<Signal>() {
                      @Override
                      public void accept(Signal ignored) {
                        l.onLongClick(MyLinearLayout.this);
                      }
                    });
      }
    }
  }

  /**
   * @return an Observable of longclick events. The emitted value is unspecified and should only be
   *     used as notification.
   */
  public Observable<Signal> longClicks() {
    if (longClicks == null) {
      longClicksIsInitting = true;
      longClicks = PublishRelay.create();
      RxView.longClicks(this)
          .map(MyUtils.createRxBindingSignalMapper())
          .doOnNext(MyUtils.createTapProcessor())
          .subscribe(longClicks);
    }
    return longClicks.hide();
  }

  /**
   * @return an observable which emits on layout changes. The emitted value is unspecified and
   *     should only be used as notification.
   */
  public Observable<Signal> layoutChanges() {
    return RxView.layoutChanges(this).map(MyUtils.createRxBindingSignalMapper());
  }

  @CallSuper
  @SuppressWarnings("CheckNullabilityTypes")
  protected void init(
      Context context,
      @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr,
      @StyleRes int defStyleRes) {
    TypedArray foregroundTA = context.obtainStyledAttributes(attrs, R.styleable.ForegroundView);
    final Drawable localForeground =
        foregroundTA.getDrawable(R.styleable.ForegroundView_android_foreground);
    if (localForeground != null) {
      // noinspection AndroidLintNewApi
      setForeground(localForeground);
    }
    foregroundGravity =
        foregroundTA.getInt(
            R.styleable.ForegroundView_android_foregroundGravity, foregroundGravity);
    foregroundInPadding =
        foregroundTA.getBoolean(R.styleable.ForegroundView_foregroundInsidePadding, true);
    foregroundTA.recycle();
  }
}
