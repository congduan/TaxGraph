package com.dc.tax;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class AboutDialog extends DialogFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        final View view = inflater.inflate(R.layout.layout_about, null);
        view.setOnClickListener(this);

        TextView text = view.findViewById(R.id.about_text);
        text.setText("收入计算器：目前仅支持上海，后续逐渐支持更多城市，尽请期待.\n版本：v1.0\n联系方式: congduan@yeah.net");

        return view;
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
    }
}
