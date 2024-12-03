package com.example.memorina;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView applicationName;
    private Button restartButton;
    private int[] cards = {
            R.drawable.riki, R.drawable.razor, R.drawable.tiny, R.drawable.phantom_lancer,
            R.drawable.bounty_hunter, R.drawable.tidehunter, R.drawable.vengeful_spirit, R.drawable.tuskar
    };
    private List<Integer> Deck = new ArrayList<>();
    private ImageView firstCard, secondCard;
    private boolean isFlipping = false;
    private int pairsFound = 0;

    private void flipCard(ImageView card, int imageResId, Runnable onAnimationEnd) {
        card.animate()
                .rotationY(90)
                .setDuration(150)
                .withEndAction(() -> {
                    card.setImageResource(imageResId);
                    card.setRotationY(-90);
                    card.animate()
                            .rotationY(0)
                            .setDuration(150)
                            .withEndAction(onAnimationEnd)
                            .start();
                })
                .start();
    }

    private void onCardClick(View view) {
        if (isFlipping) return;

        ImageView clickedCard = (ImageView) view;

        if (clickedCard.equals(firstCard) || clickedCard.getTag() == null) return;

        flipCard(clickedCard, (int) clickedCard.getTag(), () -> {
            if (firstCard == null) {
                firstCard = clickedCard;
            } else {
                secondCard = clickedCard;
                isFlipping = true;

                new Handler().postDelayed(() -> {
                    if (firstCard.getTag().equals(secondCard.getTag())) {
                        firstCard.setVisibility(View.INVISIBLE);
                        secondCard.setVisibility(View.INVISIBLE);
                        pairsFound++;
                        if (pairsFound == cards.length) {
                            Toast.makeText(this, "RADIANT VICTORY!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        flipCard(firstCard, R.drawable.card_background, null);
                        flipCard(secondCard, R.drawable.card_background, null);
                    }
                    firstCard = null;
                    secondCard = null;
                    isFlipping = false;
                }, 500);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        applicationName = findViewById(R.id.applicationName);
        restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(v -> startNewGame());
        startNewGame();
    }

    private void startNewGame() {
        gridLayout.removeAllViews();
        Deck.clear();
        pairsFound = 0;
        applicationName.setText("DOTA2 x HearthStone MEMORINA");
        firstCard = null;
        secondCard = null;
        isFlipping = false;

        for (int cardImage : cards) {
            Deck.add(cardImage);
            Deck.add(cardImage);
        }
        Collections.shuffle(Deck);

        for (int i = 0; i < Deck.size(); i++) {
            ImageView card = new ImageView(this);
            card.setImageResource(R.drawable.card_background);
            card.setTag(Deck.get(i));
            card.setScaleType(ImageView.ScaleType.CENTER_CROP);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.rowSpec = GridLayout.spec(i / 4, 1, 1f);
            params.columnSpec = GridLayout.spec(i % 4, 1, 1f);
            card.setLayoutParams(params);

            card.setOnClickListener(this::onCardClick);

            gridLayout.addView(card);
        }
    }
}
