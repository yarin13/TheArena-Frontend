package com.example.thearena.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thearena.Classes.PointsSummary;
import com.example.thearena.Classes.Question;
import com.example.thearena.R;

import java.util.List;



//------------------------------------- RecyclerView To Show the question in the RegisterQuestions Fragment------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Question> questionList;

    public RecyclerViewAdapter(Context context, List<Question> questionList){
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {
        Question question = questionList.get(position);
        holder.questionTitle.setText(question.getQuestion());
        holder.firstAnswer.setText(question.getFirstAnswer());
        holder.firstAnswer.setChecked(true);
        holder.secondAnswer.setText(question.getSecondAnswer());
        if(question.getThirdAnswer()!=null) {
            holder.thirdAnswer.setText(question.getThirdAnswer());
        }
        else {
            holder.thirdAnswer.setVisibility(View.GONE);
        }

        if(question.getFourthAnswer()!=null) {
            holder.fourthAnswer.setText(question.getFourthAnswer());
        }
        else {
            holder.fourthAnswer.setVisibility(View.GONE);
        }

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==holder.firstAnswer.getId()){
                    PointsSummary.arr[position]= 10;
                }
                else if(radioGroup.getCheckedRadioButtonId()==holder.secondAnswer.getId()){
                    PointsSummary.arr[position]= 20;
                }
                else if(radioGroup.getCheckedRadioButtonId()==holder.thirdAnswer.getId()){
                    PointsSummary.arr[position]= 30;
                }
                else if(radioGroup.getCheckedRadioButtonId()==holder.fourthAnswer.getId()){
                    PointsSummary.arr[position]= 40;
                }

             }
        });

    }



    @Override
    public int getItemCount() {
        //remove the if statement!!
        if(questionList!=null)
            return questionList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView questionTitle;
        public RadioButton firstAnswer;
        public RadioButton secondAnswer;
        public RadioButton thirdAnswer;
        public RadioButton fourthAnswer;


        public RadioGroup radioGroup;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            questionTitle = itemView.findViewById(R.id.question_row_questionTitle);
            firstAnswer = itemView.findViewById(R.id.question_row_firstAnswer);
            secondAnswer = itemView.findViewById(R.id.question_row_secondAnswer);
            thirdAnswer = itemView.findViewById(R.id.question_row_thirdAnswer);
            fourthAnswer = itemView.findViewById(R.id.question_row_fourthAnswer);


            radioGroup = itemView.findViewById(R.id.question_row_radioGroup);

        }

    }
}
