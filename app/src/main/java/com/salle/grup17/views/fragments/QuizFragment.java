package com.salle.grup17.views.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.salle.grup17.R;
import com.salle.grup17.models.QuizData;
import com.salle.grup17.models.QuizOption;
import com.salle.grup17.models.QuizQuestion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizFragment extends Fragment {

    private static final int TOTAL_QUESTIONS = 10;

    private static final int COLOR_BACKGROUND_BUTTON = Color.rgb(32, 34, 54);
    private static final int COLOR_CORRECT = Color.rgb(168, 204, 106);
    private static final int COLOR_WRONG = Color.rgb(190, 60, 60);
    private static final int COLOR_WHITE = Color.WHITE;

    private static Integer lastQuizScore = null;

    private LinearLayout homeContainer;
    private LinearLayout questionContainer;
    private LinearLayout resultContainer;
    private LinearLayout episodeCard;

    private TextView tvLastScore;
    private TextView tvLastResult;
    private TextView tvProgress;
    private TextView tvQuestion;
    private TextView tvEpisodeTitle;
    private TextView tvEpisodeCode;
    private TextView tvFinalScore;
    private TextView tvFinalResult;

    private ImageView ivCharacter;

    private Button btnStartQuiz;
    private Button btnPlayAgain;
    private Button btnAnswer1;
    private Button btnAnswer2;
    private Button btnAnswer3;
    private Button btnAnswer4;

    private final List<QuizQuestion> allQuestions = new ArrayList<>();
    private final List<QuizQuestion> currentQuiz = new ArrayList<>();

    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    private Button[] answerButtons;

    public QuizFragment() {
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        homeContainer = view.findViewById(R.id.homeContainer);
        questionContainer = view.findViewById(R.id.questionContainer);
        resultContainer = view.findViewById(R.id.resultContainer);
        episodeCard = view.findViewById(R.id.episodeCard);

        tvLastScore = view.findViewById(R.id.tvLastScore);
        tvLastResult = view.findViewById(R.id.tvLastResult);
        tvProgress = view.findViewById(R.id.tvProgress);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        tvEpisodeTitle = view.findViewById(R.id.tvEpisodeTitle);
        tvEpisodeCode = view.findViewById(R.id.tvEpisodeCode);
        tvFinalScore = view.findViewById(R.id.tvFinalScore);
        tvFinalResult = view.findViewById(R.id.tvFinalResult);

        ivCharacter = view.findViewById(R.id.ivCharacter);

        btnStartQuiz = view.findViewById(R.id.btnStartQuiz);
        btnPlayAgain = view.findViewById(R.id.btnPlayAgain);
        btnAnswer1 = view.findViewById(R.id.btnAnswer1);
        btnAnswer2 = view.findViewById(R.id.btnAnswer2);
        btnAnswer3 = view.findViewById(R.id.btnAnswer3);
        btnAnswer4 = view.findViewById(R.id.btnAnswer4);

        answerButtons = new Button[]{
                btnAnswer1,
                btnAnswer2,
                btnAnswer3,
                btnAnswer4
        };

        loadQuizFromAssets();
        showHome();

        btnStartQuiz.setOnClickListener(v -> startQuiz());
        btnPlayAgain.setOnClickListener(v -> showHome());
    }

    private void showHome() {
        homeContainer.setVisibility(View.VISIBLE);
        questionContainer.setVisibility(View.GONE);
        resultContainer.setVisibility(View.GONE);

        if (lastQuizScore == null) {
            tvLastScore.setText("-");
            tvLastResult.setText("No quiz completed yet");
            tvLastResult.setTextSize(13);
        } else {
            tvLastScore.setText(String.valueOf(lastQuizScore));
            tvLastResult.setText("out of " + TOTAL_QUESTIONS);
            tvLastResult.setTextSize(16);
        }
    }

    private void startQuiz() {
        if (allQuestions.size() < TOTAL_QUESTIONS) {
            Toast.makeText(
                    requireContext(),
                    "Not enough quiz questions in JSON",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        List<QuizQuestion> shuffled = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffled);

        currentQuiz.clear();
        currentQuiz.addAll(shuffled.subList(0, TOTAL_QUESTIONS));

        currentQuestionIndex = 0;
        correctAnswers = 0;

        showQuestion();
    }

    private void showQuestion() {
        resetButtons();

        homeContainer.setVisibility(View.GONE);
        questionContainer.setVisibility(View.VISIBLE);
        resultContainer.setVisibility(View.GONE);

        QuizQuestion question = currentQuiz.get(currentQuestionIndex);

        tvProgress.setText("Question " + (currentQuestionIndex + 1) + " / " + TOTAL_QUESTIONS);

        if (question.isTypeA()) {
            showCharacterQuestion(question);
        } else {
            showEpisodeQuestion(question);
        }

        List<QuizOption> options = question.getOptions();

        if (options == null || options.size() < 4) {
            Toast.makeText(requireContext(), "Invalid question options", Toast.LENGTH_SHORT).show();
            showHome();
            return;
        }

        List<QuizOption> shuffledOptions = new ArrayList<>(options);
        Collections.shuffle(shuffledOptions);

        btnAnswer1.setText(shuffledOptions.get(0).getName());
        btnAnswer2.setText(shuffledOptions.get(1).getName());
        btnAnswer3.setText(shuffledOptions.get(2).getName());
        btnAnswer4.setText(shuffledOptions.get(3).getName());

        btnAnswer1.setTag(shuffledOptions.get(0).isCorrect());
        btnAnswer2.setTag(shuffledOptions.get(1).isCorrect());
        btnAnswer3.setTag(shuffledOptions.get(2).isCorrect());
        btnAnswer4.setTag(shuffledOptions.get(3).isCorrect());

        btnAnswer1.setOnClickListener(v -> checkAnswer(shuffledOptions.get(0), btnAnswer1));
        btnAnswer2.setOnClickListener(v -> checkAnswer(shuffledOptions.get(1), btnAnswer2));
        btnAnswer3.setOnClickListener(v -> checkAnswer(shuffledOptions.get(2), btnAnswer3));
        btnAnswer4.setOnClickListener(v -> checkAnswer(shuffledOptions.get(3), btnAnswer4));
    }

    private void showCharacterQuestion(QuizQuestion question) {
        ivCharacter.setVisibility(View.VISIBLE);
        episodeCard.setVisibility(View.GONE);
        tvEpisodeCode.setVisibility(View.GONE);

        tvQuestion.setText("Who is this character?");

        Glide.with(requireContext())
                .load(question.getImage())
                .into(ivCharacter);
    }

    private void showEpisodeQuestion(QuizQuestion question) {
        ivCharacter.setVisibility(View.GONE);
        episodeCard.setVisibility(View.VISIBLE);
        tvEpisodeCode.setVisibility(View.VISIBLE);

        tvQuestion.setText("Which character appears in this episode?");
        tvEpisodeTitle.setText(question.getEpisodeTitle());
        tvEpisodeCode.setText(question.getEpisodeCode());
    }

    private void checkAnswer(QuizOption selectedOption, Button selectedButton) {
        disableButtons();

        if (selectedOption.isCorrect()) {
            correctAnswers++;
            setButtonColor(selectedButton, COLOR_CORRECT);
            selectedButton.setTextColor(Color.rgb(17, 17, 17));
            Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            setButtonColor(selectedButton, COLOR_WRONG);
            selectedButton.setTextColor(COLOR_WHITE);
            highlightCorrectAnswer();
            Toast.makeText(requireContext(), "Wrong!", Toast.LENGTH_SHORT).show();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            currentQuestionIndex++;

            if (currentQuestionIndex < TOTAL_QUESTIONS) {
                showQuestion();
            } else {
                showFinalResult();
            }
        }, 1200);
    }

    private void showFinalResult() {
        lastQuizScore = correctAnswers;

        homeContainer.setVisibility(View.GONE);
        questionContainer.setVisibility(View.GONE);
        resultContainer.setVisibility(View.VISIBLE);

        tvFinalScore.setText(String.valueOf(correctAnswers));
        tvFinalResult.setText("out of " + TOTAL_QUESTIONS);
    }

    private void highlightCorrectAnswer() {
        for (Button button : answerButtons) {
            Object tag = button.getTag();

            if (tag instanceof Boolean && (Boolean) tag) {
                setButtonColor(button, COLOR_CORRECT);
                button.setTextColor(Color.rgb(17, 17, 17));
            }
        }
    }

    private void resetButtons() {
        for (Button button : answerButtons) {
            button.setEnabled(true);
            button.setTextColor(COLOR_WHITE);
            button.setTag(false);
            setButtonColor(button, COLOR_BACKGROUND_BUTTON);
        }
    }

    private void disableButtons() {
        for (Button button : answerButtons) {
            button.setEnabled(false);
        }
    }

    private void setButtonColor(Button button, int color) {
        button.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void loadQuizFromAssets() {
        try {
            String json = loadJSONFromAssets("quiz_data.json");

            Gson gson = new Gson();
            QuizData quizData = gson.fromJson(json, QuizData.class);

            allQuestions.clear();

            if (quizData != null && quizData.getQuestions() != null) {
                allQuestions.addAll(quizData.getQuestions());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error loading quiz JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private String loadJSONFromAssets(String filename) throws IOException {
        InputStream inputStream = requireContext().getAssets().open(filename);
        int size = inputStream.available();

        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();

        return new String(buffer, StandardCharsets.UTF_8);
    }
}