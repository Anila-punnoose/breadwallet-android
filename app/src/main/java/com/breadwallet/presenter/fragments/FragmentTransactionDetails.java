package com.breadwallet.presenter.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.breadwallet.R;
import com.breadwallet.presenter.entities.TransactionListItem;
import com.breadwallet.tools.adapter.TransactionPagerAdapter;

import java.util.List;


/**
 * BreadWallet
 * <p>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 6/29/15.
 * Copyright (c) 2016 breadwallet LLC
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class FragmentTransactionDetails extends Fragment {
    private static final String TAG = FragmentTransactionDetails.class.getName();

    public TextView mTitle;
    public LinearLayout backgroundLayout;
    public static final int ANIMATION_DURATION = 300;
    private ViewPager txViewPager;
    private TransactionPagerAdapter txPagerAdapter;
    private List<TransactionListItem> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.

        View rootView = inflater.inflate(R.layout.fragment_transaction_details, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        backgroundLayout = (LinearLayout) rootView.findViewById(R.id.background_layout);
        txViewPager = (ViewPager) rootView.findViewById(R.id.tx_list_pager);
        txViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
            }
        });
        txPagerAdapter = new TransactionPagerAdapter(getChildFragmentManager(), items);
        txViewPager.setAdapter(txPagerAdapter);
        int pos = getArguments().getInt("pos");
        txViewPager.setCurrentItem(pos, false);

        return rootView;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewTreeObserver observer = txViewPager.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                observer.removeGlobalOnLayoutListener(this);
                animateBackgroundDim(false);
                animateSignalSlide(false);
            }
        });

    }

    private void animateSignalSlide(final boolean reverse) {
        float translationY = txViewPager.getTranslationY();
        float signalHeight = txViewPager.getHeight();
        txViewPager.setTranslationY(reverse ? translationY : translationY + signalHeight);
        txViewPager.animate().translationY(reverse ? 2000 : translationY).setDuration(ANIMATION_DURATION).setInterpolator(new OvershootInterpolator(0.7f)).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (reverse && getActivity() != null)
                    getActivity().getFragmentManager().popBackStack();
            }
        });

    }

    private void animateBackgroundDim(boolean reverse) {
        int transColor = reverse ? R.color.black_trans : android.R.color.transparent;
        int blackTransColor = reverse ? android.R.color.transparent : R.color.black_trans;

        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(transColor, blackTransColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                backgroundLayout.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });

        anim.setDuration(ANIMATION_DURATION);
        anim.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        animateBackgroundDim(true);
        animateSignalSlide(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void setItems(List<TransactionListItem> items) {
        this.items = items;
    }
}