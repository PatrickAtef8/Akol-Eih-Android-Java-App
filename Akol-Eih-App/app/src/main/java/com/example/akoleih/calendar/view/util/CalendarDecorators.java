package com.example.akoleih.calendar.view.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.fragment.app.Fragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

public class CalendarDecorators {

    public static DayViewDecorator createPastDatesDecorator(CalendarDay minDate) {
        return new PastDatesDecorator(minDate);
    }

    public static DayViewDecorator createMealThumbnailDecorator(
            Set<CalendarDay> plannedDates,
            Map<CalendarDay, String> mealThumbnails,
            Map<CalendarDay, Integer> mealCounts,
            Fragment fragment) {
        return new MealThumbnailDecorator(plannedDates, mealThumbnails, mealCounts, fragment);
    }

    private static class PastDatesDecorator implements DayViewDecorator {
        private final CalendarDay min;

        PastDatesDecorator(CalendarDay minDate) {
            this.min = minDate;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.isBefore(min);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.addSpan(new android.text.style.ForegroundColorSpan(
                    Color.argb(127, 128, 128, 128)));
        }
    }

    private static class MealThumbnailDecorator implements DayViewDecorator {
        private final Set<CalendarDay> plannedDates;
        private final Map<CalendarDay, String> mealThumbnails;
        private final Map<CalendarDay, Integer> mealCounts;
        private final WeakReference<Fragment> fragmentRef;

        MealThumbnailDecorator(Set<CalendarDay> plannedDates,
                               Map<CalendarDay, String> mealThumbnails,
                               Map<CalendarDay, Integer> mealCounts,
                               Fragment fragment) {
            this.plannedDates = plannedDates;
            this.mealThumbnails = mealThumbnails;
            this.mealCounts = mealCounts;
            this.fragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return plannedDates.contains(day) && mealThumbnails.containsKey(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            for (var e : mealThumbnails.entrySet()) {
                CalendarDay d = e.getKey();
                if (!shouldDecorate(d)) continue;
                String url = e.getValue();
                Fragment fragment = fragmentRef.get();
                if (fragment == null || !fragment.isAdded()) return;
                Picasso.get().load(url)
                        .resize(80, 80).centerCrop()
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bmp, Picasso.LoadedFrom from) {
                                Fragment f = fragmentRef.get();
                                if (f != null && f.isAdded()) {
                                    Bitmap circ = makeCircular(bmp);
                                    Drawable dr = new BitmapDrawable(f.getResources(), circ);
                                    view.setBackgroundDrawable(dr);
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception ex, Drawable err) {
                                view.addSpan(new android.text.style.BackgroundColorSpan(Color.LTGRAY));
                            }

                            @Override
                            public void onPrepareLoad(Drawable ph) {
                            }
                        });
                int count = mealCounts.getOrDefault(d, 1);
                if (count > 1) view.addSpan(new DotSpan(5, Color.RED));
                break;
            }
        }

        private Bitmap makeCircular(Bitmap bmp) {
            int s = Math.min(bmp.getWidth(), bmp.getHeight());
            Bitmap out = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(out);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setAlpha(100);
            c.drawCircle(s / 2f, s / 2f, s / 2f, p);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            c.drawBitmap(bmp, 0, 0, p);
            return out;
        }
    }
}