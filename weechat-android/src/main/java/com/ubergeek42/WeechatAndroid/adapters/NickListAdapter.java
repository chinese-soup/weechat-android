package com.ubergeek42.WeechatAndroid.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ubergeek42.WeechatAndroid.BuildConfig;
import com.ubergeek42.WeechatAndroid.R;
import com.ubergeek42.WeechatAndroid.WeechatActivity;
import com.ubergeek42.WeechatAndroid.relay.Buffer;
import com.ubergeek42.WeechatAndroid.relay.BufferNicklistEye;
import com.ubergeek42.WeechatAndroid.relay.Nick;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NickListAdapter extends BaseAdapter implements BufferNicklistEye,
        DialogInterface.OnDismissListener, DialogInterface.OnShowListener {
    private static Logger logger = LoggerFactory.getLogger("NickListAdapter");
    final private static boolean DEBUG = BuildConfig.DEBUG;

    private final @NonNull
    WeechatActivity activity;
    private final @NonNull LayoutInflater inflater;
    private final @NonNull Buffer buffer;
    private @NonNull Nick[] nicks = new Nick[0];
    private int defaultNickTextColor = 0;
    private AlertDialog dialog;

    public NickListAdapter(@NonNull WeechatActivity activity, @NonNull Buffer buffer) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.buffer = buffer;
    }

    @Override
    public int getCount() {
        return nicks.length;
    }

    @Override
    public Nick getItem(int position) {
        return nicks[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.select_dialog_item_material_2_lines, parent, false);

        final TextView textView = (TextView) convertView;
        if (defaultNickTextColor == 0)
            defaultNickTextColor = textView.getTextColors().getDefaultColor();

        Nick nick = getItem(position);
        textView.setText(nick.prefix + nick.name);
        int color = nick.away ? ContextCompat.getColor(convertView.getContext(), R.color.away_nick)
                              : defaultNickTextColor;
        textView.setTextColor(color);
        return convertView;
    }

    public void onNicklistChanged() {
        if (DEBUG) logger.debug("onNicklistChanged()");
        final Nick[] tmp = buffer.getNicksCopy();
        final String nicklistCount = activity.getResources().getQuantityString(
                R.plurals.nick_list_count, tmp.length, tmp.length);
        final String title = activity.getString(R.string.nick_list_title,
                buffer.shortName, nicklistCount);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nicks = tmp;
                notifyDataSetChanged();
                dialog.setTitle(title);
            }
        });
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (DEBUG) logger.debug("onShow()");
        this.dialog = (AlertDialog) dialog;
        buffer.setBufferNicklistEye(this);
        onNicklistChanged();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (DEBUG) logger.debug("onDismiss()");
        buffer.setBufferNicklistEye(null);
    }
}