package com.pigdroid.gameboard.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.turn.controller.TurnGameController;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.util.StringUtils;

import java.util.Iterator;

/**
 * Created by eduyayo on 07/07/2015.
 */
public class PlayerViewUtils {

    private PlayerViewUtils() {}

    public static void setPlayerStatus(final Activity context, final ViewGroup viewGroup, final TurnGameController<?> controller) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int [] colors = context.getResources().getIntArray(R.array.androidcolors);
                for (Iterator<Player> it = controller.getPlayers(); it.hasNext(); ) {
                    Player player = it.next();
                    View child = viewGroup.findViewWithTag(player.getName());
                    if (child == null) {
                        child = LayoutInflater.from(context).inflate(
                                R.layout.item_contact_simplest, null);
                        child.setTag(player.getName());
                        viewGroup.addView(child);
                    }
                    setPlayerStatus(player, child, controller);
                    int index = controller.getPlayerIndex(((HumanPlayer) player).getEmail());
                    child.findViewById(android.R.id.icon).setBackgroundColor(colors[index]);
                }
            }
        });
    }

    private static void setPlayerStatus(Player player, View view, TurnGameController<?> controller) {
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(StringUtils.shortenEmail(player.getName()));
        switch (player.getStatus()) {
            case PRESENT:
                view.setBackgroundResource(R.drawable.badge_count_grey);
                if (controller.getCurrentPlayerEmail().equals(((HumanPlayer) player).getEmail())) {
                    view.setBackgroundResource(R.drawable.badge_count_green);
                } else {
                    view.setBackgroundResource(R.drawable.badge_count_grey);
                }
                break;
            case INVITED:
                view.setBackgroundResource(R.drawable.badge_count_yellow);
                break;
            case GONE:
            default:
                view.setBackgroundResource(R.drawable.badge_count_red);
                break;
        }
    }

}
