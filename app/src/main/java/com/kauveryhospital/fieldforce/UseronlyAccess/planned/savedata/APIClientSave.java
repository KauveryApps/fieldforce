package com.kauveryhospital.fieldforce.UseronlyAccess.planned.savedata;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClientSave {
   private static Retrofit retrofit = null;

   public static Retrofit getClient() {

       HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
       interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


       retrofit = new Retrofit.Builder()
               .baseUrl("https://kforce.kauveryhospital.com/fieldforcescripts/ASBTStructRest.dll/datasnap/rest/TASBTStruct/")
               .addConverterFactory(GsonConverterFactory.create())
               .client(client)
               .build();



       return retrofit;
   }
}
