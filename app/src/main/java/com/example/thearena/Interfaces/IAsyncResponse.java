package com.example.thearena.Interfaces;

//----------------------------------  Functional InterFace ------------------------
//---------------------------------- Used to make sure commands being processed Asynchronously-------------------------------------
public interface IAsyncResponse {

     <T> void processFinished(T questionList);
}
