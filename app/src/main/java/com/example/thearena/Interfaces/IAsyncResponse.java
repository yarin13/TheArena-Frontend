package com.example.thearena.Interfaces;

import androidx.annotation.Nullable;

//----------------------------------  Functional InterFace ------------------------
//---------------------------------- Used to make sure commands being processed Asynchronously-------------------------------------
public interface IAsyncResponse {

     <T> void processFinished(T questionList, @Nullable String mail,@Nullable String pass);

     <T> void processFinished(T response);
}
