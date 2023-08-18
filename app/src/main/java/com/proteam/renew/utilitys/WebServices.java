package com.proteam.renew.utilitys;


import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proteam.renew.requestModels.ApprovalRequest;
import com.proteam.renew.requestModels.AttendanceFilterRequest;
import com.proteam.renew.requestModels.CompletionRequest;
import com.proteam.renew.requestModels.FileUploadrequest;
import com.proteam.renew.requestModels.OnBoardingUpdate;
import com.proteam.renew.requestModels.RejectRequest;
import com.proteam.renew.requestModels.AttendancApproveRequest;
import com.proteam.renew.requestModels.AttendanceRequest;
import com.proteam.renew.requestModels.Loginmodel;
import com.proteam.renew.requestModels.OnBoarding;
import com.proteam.renew.requestModels.TrainingAllocationRequest;
import com.proteam.renew.requestModels.TrainingListResponsce;
import com.proteam.renew.requestModels.TrainingWorkersrequest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WebServices<T> {
    T t;
    Call<T> call=null;
    public T getT() {
        return t;
    }

    public void setT(T t) {

        this.t = t;
    }

    ApiType apiTypeVariable;
    Context context;
    OnResponseListener<T> onResponseListner;
    private static OkHttpClient.Builder builder;

    public enum ApiType {
       login,states,location,workers,training,skills,projects,activitys,onBoarding,trainingAllocate,
        contractors,supervisors,update,fileupdate,empReopen, trainingworkers,aadharvalidate,attendanceCount,
        complete,viewworker,approve,reject,attendence, attendancelist,empdetails,attendanceapprove, traininglist, attendancefilter
    }
    String BaseUrl = "https://gp.proteam.co.in/api/Workeronboard_api/";
    //String BaseUrl = "https://wom.proteam.co.in/api/Workeronboard_api/";


    public WebServices(OnResponseListener<T> onResponseListner) {
        this.onResponseListner = onResponseListner;

        if (onResponseListner instanceof Activity) {
            this.context = (Context) onResponseListner;
        } else if (onResponseListner instanceof IntentService) {
            this.context = (Context) onResponseListner;
        } else if (onResponseListner instanceof android.app.DialogFragment) {
            android.app.DialogFragment dialogFragment = (android.app.DialogFragment) onResponseListner;
            this.context = dialogFragment.getActivity();
        }else if (onResponseListner instanceof android.app.Fragment) {
            android.app.Fragment fragment = (android.app.Fragment) onResponseListner;
            this.context = fragment.getActivity();
        }
        else if (onResponseListner instanceof Adapter) {

            this.context = (Context) onResponseListner;
        }
        else if (onResponseListner instanceof Adapter) {
            this.context = (Context) onResponseListner;
        }
        else {
            //android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) onResponseListner;
            //this.context = fragment.getActivity();
        }
        builder = getHttpClient();
    }

    public WebServices(Context context, OnResponseListener<T> onResponseListner) {
        this.onResponseListner = onResponseListner;
        this.context = context;
        builder = getHttpClient();
    }

    public OkHttpClient.Builder getHttpClient() {

        if (builder == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(10000, TimeUnit.SECONDS);
            client.readTimeout(10000, TimeUnit.SECONDS).build();
            client.addInterceptor(loggingInterceptor);
            /*to pass header information with request*/
            client.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder().addHeader("Content-Type", "application/json").build();
                    return chain.proceed(request);
                }
            });

            return client;
        }
        return builder;
    }

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private Retrofit getRetrofitClient(String api)
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(api)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    public void login( ApiType apiTypes, Loginmodel loginmodel)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.validatelogin(loginmodel);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("usercompany===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void states( ApiType apiTypes)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.stateslist();

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void location( ApiType apiTypes)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.locationlist();

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void workers( ApiType apiTypes)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.workerslist();

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void viewWorker( ApiType apiTypes, String id)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.viewWorker(id);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void projects( ApiType apiTypes, String userid)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.projectslist(userid);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void skills( ApiType apiTypes)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.skillslist();

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void activity( ApiType apiTypes)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.activityList();

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void training( ApiType apiTypes)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.trininglist();

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void Conctractors(ApiType apiTypes, String user_id)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.contractors(user_id);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void Supervisor(ApiType apiTypes, String user_id)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.supervisor(user_id);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void onBoarding2(ApiType apiTypes, OnBoarding onBording)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.onBoarding(onBording);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void workerUpdate(ApiType apiTypes, OnBoardingUpdate onBording, String id)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.update(onBording, id);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void aadharvalidate(ApiType apiTypes, String a_number)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.aadharval(a_number);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void onBoarding(ApiType apiTypes, OnBoarding onBording)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        Log.d("testaadhaarpic",onBording.getMedical_certificate()+"----"+onBording.getAadhaar_card()+"----"+onBording.getProfilepic()+"----"+onBording.getDriving_lisence_docs());

        File file = new File(onBording.getProfilepic());//create path from uri
        Log.d("testinguri",onBording.getProfilepic());
        Log.d("aadharruritest",file.getAbsolutePath());

        RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("profilepic", file.getName(), fbody);

        File file1 = new File(onBording.getAadhaar_card());//create path from uri
        Log.d("aadharruritest",file1.getAbsolutePath());
        RequestBody fbody1 = RequestBody.create(MediaType.parse("image/*"), file1);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("aadhaar_card", file.getName(), fbody1);

        File file2 = new File(onBording.getMedical_certificate());
        RequestBody fbody2 = RequestBody.create(MediaType.parse("image/*"), file2);
        MultipartBody.Part fileToUpload2 = MultipartBody.Part.createFormData("medical_certificate", file.getName(), fbody2);

        File file3 = new File(onBording.getDriving_lisence_docs());
        RequestBody fbody3 = RequestBody.create(MediaType.parse("image/*"), file3);
        MultipartBody.Part fileToUpload3 = MultipartBody.Part.createFormData("driving_lisence_docs", file.getName(), fbody3);

        //RequestBody id1 = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody id1 = RequestBody.create(MediaType.parse("text/plain"), onBording.getAadhaar_no());
        RequestBody id2 = RequestBody.create(MediaType.parse("text/plain"), onBording.getAddress_line_1());
        RequestBody id3 = RequestBody.create(MediaType.parse("text/plain"), onBording.getBlood_group());
        RequestBody id4 = RequestBody.create(MediaType.parse("text/plain"), onBording.getCity());
        RequestBody id5 = RequestBody.create(MediaType.parse("text/plain"), onBording.getDob());
        RequestBody id6 = RequestBody.create(MediaType.parse("text/plain"), onBording.getDoj());
        RequestBody id7 = RequestBody.create(MediaType.parse("text/plain"), onBording.getDriving_license_no());
        RequestBody id8 = RequestBody.create(MediaType.parse("text/plain"), onBording.getDriving_license_expiry_date());
        RequestBody id9 = RequestBody.create(MediaType.parse("text/plain"), onBording.getEmergency_contact_name());
        RequestBody id10 = RequestBody.create(MediaType.parse("text/plain"), onBording.getEmergency_contact_no());
        RequestBody id11 = RequestBody.create(MediaType.parse("text/plain"), onBording.getEmployer_contractor_name());
        RequestBody id12 = RequestBody.create(MediaType.parse("text/plain"), onBording.getExp_month());
        RequestBody id13 = RequestBody.create(MediaType.parse("text/plain"), onBording.getExp_year());
        RequestBody id14 = RequestBody.create(MediaType.parse("text/plain"), onBording.getFather_name());
        RequestBody id15 = RequestBody.create(MediaType.parse("text/plain"), onBording.getFull_name());
        RequestBody id16 = RequestBody.create(MediaType.parse("text/plain"), onBording.getGender());
        RequestBody id17 = RequestBody.create(MediaType.parse("text/plain"), onBording.getInduction_date());
        RequestBody id18 = RequestBody.create(MediaType.parse("text/plain"), onBording.getInduction_status());
        RequestBody id19 = RequestBody.create(MediaType.parse("text/plain"), onBording.getMedical_test_date());
        RequestBody id20 = RequestBody.create(MediaType.parse("text/plain"), onBording.getMobile_no());
        RequestBody id21 = RequestBody.create(MediaType.parse("text/plain"), onBording.getNationality());
        RequestBody id22 = RequestBody.create(MediaType.parse("text/plain"), onBording.getPin_code());
        RequestBody id30 = RequestBody.create(MediaType.parse("text/plain"), onBording.getProject());
        RequestBody id23 = RequestBody.create(MediaType.parse("text/plain"), onBording.getSkill_set());
        RequestBody id24 = RequestBody.create(MediaType.parse("text/plain"), onBording.getSkill_type());
        RequestBody id25 = RequestBody.create(MediaType.parse("text/plain"), onBording.getState());
        RequestBody id26 = RequestBody.create(MediaType.parse("text/plain"), onBording.getSubcontract_contract_no());
        RequestBody id27 = RequestBody.create(MediaType.parse("text/plain"), onBording.getSubcontractor_name());
        RequestBody id28 = RequestBody.create(MediaType.parse("text/plain"), onBording.getUser_id());
        RequestBody id29 = RequestBody.create(MediaType.parse("text/plain"), onBording.getWork_employee_designation());
        RequestBody id31 = RequestBody.create(MediaType.parse("text/plain"), "0");
        RequestBody id32 = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody id33 = RequestBody.create(MediaType.parse("text/plain"), onBording.getMedical_test_status());

        if(onBording.getDriving_lisence_docs() != ""){
            call=(Call<T>)proRenew.onBoarding2(fileToUpload,fileToUpload1,fileToUpload2,fileToUpload3,id1,
                    id2,id3,id4,id5,id6,id7,id8,id9,id10,id11,id12,id13,id14,id15,id16,id17,id18,id19,id20,id21,id22,id30,id23,id24,
                    id25,id26,id27,id28,id29,id31,id32,id33,id32);
        } else {
            call=(Call<T>)proRenew.onBoarding3(fileToUpload,fileToUpload1,fileToUpload2,id1,
                    id2,id3,id4,id5,id6,id7,id8,id9,id10,id11,id12,id13,id14,id15,id16,id17,id18,id19,id20,id21,id22,id30,id23,id24,
                    id25,id26,id27,id28,id29,id31,id32,id33,id32);
        }


        //call=(Call<T>)proRenew.onBoarding2(fileToUpload,fileToUpload1,fileToUpload2,fileToUpload3, id1,id2);


        //call=(Call<T>)proRenew.update(onBording, id);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }
    public void Approve(ApiType apiTypes, ApprovalRequest approvalRequest)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.approve(approvalRequest);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void Reject(ApiType apiTypes, RejectRequest rejectRequest)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.reject(rejectRequest);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void attendance(ApiType apiTypes, AttendanceRequest attendanceRequest)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        call=(Call<T>)proRenew.attendencepass(attendanceRequest);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    //// attendance apis

    public void attendance_list(ApiType apiTypes, String user_id)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.attendance_list(user_id);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void cont_attendance_list(ApiType apiTypes, String user_id)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.contractor_attendance_list(user_id);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void cont_training_list(ApiType apiTypes, TrainingWorkersrequest training)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.allocation_worker_list(training);
        Log.d("workerlistcheck","getting inside");


        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void attendance_list2(ApiType apiTypes, AttendanceFilterRequest attendanceFilterRequest)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.attendance_list2(attendanceFilterRequest);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }
    public void empdetails(ApiType apiTypes, String id, String user_id, String project_id)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.empdetails(id, user_id, project_id);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void attendanceCount(ApiType apiTypes, String a_id, String user_id, String project_id)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.attendancecount(a_id,project_id,user_id);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }


    public void AttendanceApprove(ApiType apiTypes, AttendancApproveRequest request)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.attendanceapprove(request);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void Traininglist(ApiType apiTypes, String id) {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.training_list(id);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void Trainingcompletion(ApiType apiTypes, CompletionRequest completionRequest) {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.completion(completionRequest);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void trainingAllocation(ApiType apiTypes, TrainingAllocationRequest request)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.trainingaloocate(request);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void employeeresubmit(ApiType apiTypes, String request)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);
        call=(Call<T>)proRenew.empResubmit(request);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }

    public void file_update(ApiType apiTypes, String id, String path, String filename)
    {
        apiTypeVariable = apiTypes;
        Retrofit retrofit=getRetrofitClient(BaseUrl);
        renewNetwork proRenew=retrofit.create(renewNetwork.class);

        File file = new File(path);//create path from uri
        Log.d("testinguri",path);
        RequestBody fbody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData(filename, file.getName(), fbody);

        /*File file1 = new File(aadhar);//create path from uri
        RequestBody fbody1 = RequestBody.create(MediaType.parse("image/*"), file1);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("aadhaar_card", file.getName(), fbody1);

        File file2 = new File(medical);
        RequestBody fbody2 = RequestBody.create(MediaType.parse("image/*"), file2);
        MultipartBody.Part fileToUpload2 = MultipartBody.Part.createFormData("medical_certificate", file.getName(), fbody2);

        File file3 = new File(driving);
        RequestBody fbody3 = RequestBody.create(MediaType.parse("image/*"), file3);
        MultipartBody.Part fileToUpload3 = MultipartBody.Part.createFormData("driving_lisence_docs", file.getName(), fbody3);*/
        RequestBody id1 = RequestBody.create(MediaType.parse("text/plain"), id);
       // call=(Call<T>)proRenew.fileupload(fileToUpload,fileToUpload1,fileToUpload2,fileToUpload3,id1);

        call=(Call<T>)proRenew.fileupload(fileToUpload,id1);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println("stateslist===="+response.body());
                t=(T)response.body();
                onResponseListner.onResponse(t, apiTypeVariable, true,response.code());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onResponseListner.onResponse(null, apiTypeVariable, false,0);
            }
        });
    }
}

