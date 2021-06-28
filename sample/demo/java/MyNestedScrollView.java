package com.uber.artist.mylibrary;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.AttrRes;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.widget.NestedScrollView;
import com.jakewharton.rxbinding3.core.RxNestedScrollView;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.view.ViewScrollChangeEvent;
import com.jakewharton.rxrelay2.PublishRelay;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.SuppressWarnings;

public class MyNestedScrollView extends NestedScrollView implements MyView {
  private Drawable foreground;

  private boolean clicksIsInitting;

  @Nullable private PublishRelay<Signal> clicks;

  @Nullable private Disposable clicksDisposable;

  private boolean longClicksIsInitting;

  @Nullable private PublishRelay<Signal> longClicks;

  @Nullable private Disposable longClicksDisposable;

  private boolean scrollChangeEventsIsInitting;

  @Nullable private PublishRelay<ViewScrollChangeEvent> scrollChangeEvents;

  @Nullable private Disposable scrollChangeEventsDisposable;

  public MyNestedScrollView(Context context) {
    this(context, null);
  }

  public MyNestedScrollView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyNestedScrollView(
      Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
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
    if (foreground != null) {
      foreground.setBounds(0, 0, w, h);
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
        foreground.setBounds(0, 0, getWidth(), getHeight());
        setWillNotDraw(false);
        drawable.setCallback(this);
        if (drawable.isStateful()) {
          drawable.setState(getDrawableState());
        }
      } else {
        setWillNotDraw(true);
      }
      invalidate();
    }
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    if (foreground != null) {
      foreground.draw(canvas);
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
  public final void setOnClickListener(@Nullable final OnClickListener l) {
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
                        l.onClick(MyNestedScrollView.this);
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
  public final void setOnLongClickListener(@Nullable final OnLongClickListener l) {
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
                        l.onLongClick(MyNestedScrollView.this);
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

  /** @deprecated Use {@link #scrollChangeEvents()} */
  @Override
  @Deprecated
  public final void setOnScrollChangeListener(final OnScrollChangeListener l) {
    if (scrollChangeEventsIsInitting) {
      scrollChangeEventsIsInitting = false;
      super.setOnScrollChangeListener(l);
    } else {
      if (scrollChangeEventsDisposable != null) {
        scrollChangeEventsDisposable.dispose();
        scrollChangeEventsDisposable = null;
      }
      if (l != null) {
        scrollChangeEventsDisposable =
            scrollChangeEvents()
                .subscribe(
                    new Consumer<ViewScrollChangeEvent>() {
                      @Override
                      public void accept(ViewScrollChangeEvent event) {
                        l.onScrollChange(
                            MyNestedScrollView.this,
                            event.scrollX(),
                            event.scrollY(),
                            event.oldScrollX(),
                            event.oldScrollY());
                      }
                    });
      }
    }
  }

  /** @return an observable of scroll-change events for this NestedScrollView. */
  public Observable<ViewScrollChangeEvent> scrollChangeEvents() {
    if (scrollChangeEvents == null) {
      scrollChangeEventsIsInitting = true;
      scrollChangeEvents = PublishRelay.create();
      RxNestedScrollView.scrollChangeEvents(this).subscribe(scrollChangeEvents);
    }
    return scrollChangeEvents.hide();
  }

  @CallSuper
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
    foregroundTA.recycle();
  }
}
