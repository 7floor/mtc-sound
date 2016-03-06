package com.sevenfloor.mtcsound.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevenfloor.mtcsound.R;
import com.sevenfloor.mtcsound.ui.controls.BalanceCross;

public class BalanceFragment
        extends BaseFragment
        implements View.OnClickListener, BalanceCross.OnBalanceChangeListener {

    private BalanceCross balance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_balance, container, false);

        balance = (BalanceCross) view.findViewById(R.id.balanceCross);

        balance.setOnBalanceChangeListener(this);
        view.findViewById(R.id.balanceReset).setOnClickListener(this);
        view.findViewById(R.id.balanceFront).setOnClickListener(this);
        view.findViewById(R.id.balanceLeft).setOnClickListener(this);
        view.findViewById(R.id.balanceRight).setOnClickListener(this);
        view.findViewById(R.id.balanceRear).setOnClickListener(this);

        return view;
    }

    @Override
    protected void update() {
        updateBalance();
    }

    public void updateBalance() {
        int[] parts = parseList(audioManager.getParameters("av_balance="));
        if (parts.length == 2)
        {
            balance.setBalance(parts[0], 28 - parts[1]);
        }
        else
        {
            balance.setBalance(14, 14);
        }
    }

    @Override
    public void onBalanceChange(int balanceX, int balanceY, int byUser) {
        audioManager.setParameters(String.format("av_balance=%d,%d", balanceX, 28 - balanceY));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balanceReset:
                balance.setBalance(14, 14);
                break;
            case R.id.balanceFront:
                balance.balanceYdown();
                break;
            case R.id.balanceLeft:
                balance.balanceXdown();
                break;
            case R.id.balanceRight:
                balance.balanceXup();
                break;
            case R.id.balanceRear:
                balance.balanceYup();
                break;
        }
    }
}

