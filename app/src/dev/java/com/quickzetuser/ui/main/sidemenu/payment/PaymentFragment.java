package com.quickzetuser.ui.main.sidemenu.payment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.ui.main.sidemenu.payment.cash.CashFragment;
import com.quickzetuser.ui.main.sidemenu.payment.remainingPayment.PaymentRemainingFragment;
import com.quickzetuser.ui.main.sidemenu.payment.wallet.WalletFragment;


/**
 * Created by Sunil kumar yadav on 14/3/18.
 */

public class PaymentFragment extends AppBaseFragment {

    private LinearLayout ll_cash;
    private LinearLayout ll_wallet;
    private TextView tv_payment_remaining;

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_payment;
    }

    @Override
    public void initializeComponent() {
        ll_cash = getView().findViewById(R.id.ll_cash);
        ll_wallet = getView().findViewById(R.id.ll_wallet);
        tv_payment_remaining = getView().findViewById(R.id.tv_payment_remaining);

        ll_cash.setOnClickListener(this);
        ll_wallet.setOnClickListener(this);
        tv_payment_remaining.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_cash:
                addCashFragment();
                break;

            case R.id.ll_wallet:
                addMoneyFragment();
                break;

            case R.id.tv_payment_remaining:
                addPaymentRemainingFragment();
                break;
        }
    }

    private void addCashFragment() {
        CashFragment fragment = new CashFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.right_out;
        try {
            getMainActivity().changeFragment(fragment, true, false, 0,
                    enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private void addPaymentRemainingFragment() {
        PaymentRemainingFragment fragment = new PaymentRemainingFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.right_out;
        try {
            getMainActivity().changeFragment(fragment, true, false, 0,
                    enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void addMoneyFragment() {
        WalletFragment fragment = new WalletFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.right_out;
        try {
            getMainActivity().changeFragment(fragment, true, false, 0,
                    enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
