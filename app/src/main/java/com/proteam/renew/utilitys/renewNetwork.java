package com.proteam.renew.utilitys;

import com.proteam.renew.requestModels.ApprovalRequest;
import com.proteam.renew.requestModels.AttendanceFilterRequest;
import com.proteam.renew.requestModels.CompletionRequest;
import com.proteam.renew.requestModels.OnBoardingUpdate;
import com.proteam.renew.requestModels.RejectRequest;
import com.proteam.renew.requestModels.AttendancApproveRequest;
import com.proteam.renew.requestModels.AttendanceRequest;
import com.proteam.renew.requestModels.Loginmodel;
import com.proteam.renew.requestModels.OnBoarding;
import com.proteam.renew.requestModels.TrainingAllocationRequest;
import com.proteam.renew.requestModels.TrainingListResponsce;
import com.proteam.renew.requestModels.TrainingWorkersrequest;
import com.proteam.renew.responseModel.AttendanceCount1Responsce;
import com.proteam.renew.responseModel.AttendanceCountResponsce;
import com.proteam.renew.responseModel.Attendance_new_list;
import com.proteam.renew.responseModel.ContractorListResponsce;
import com.proteam.renew.responseModel.EmployeedetailResponsce;
import com.proteam.renew.responseModel.Generalresponsce;
import com.proteam.renew.responseModel.LocationResponse;
import com.proteam.renew.responseModel.LoginResponse;
import com.proteam.renew.responseModel.SupervisorListResponsce;
import com.proteam.renew.responseModel.TraininWorkersResponsce;
import com.proteam.renew.responseModel.ViewActivityMasterResponsce;
import com.proteam.renew.responseModel.ViewProjectMaster;
import com.proteam.renew.responseModel.ViewSkillsetMaster;
import com.proteam.renew.responseModel.ViewTrainingMaster;
import com.proteam.renew.responseModel.generalGesponce;
import com.proteam.renew.responseModel.statesResponse;
import com.proteam.renew.responseModel.workersListResponsce;
import com.proteam.renew.responseModel.workersListResponsceItem;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface renewNetwork {
    @POST("login")
    Call<LoginResponse> validatelogin(@Body Loginmodel loginmodel);

    @GET("viewStateMaster")
    Call<statesResponse> stateslist();

    @GET("viewLocationMaster")
    Call<LocationResponse> locationlist();

    @GET("workers")
    Call<workersListResponsce> workerslist();

    @GET("viewWorker/")
    Call<workersListResponsce> viewWorker(@Query("id") String id);

   /* @GET("viewProjectMaster")
    Call<ViewProjectMaster> projectslist();*/

    @GET("user_wise_project_list/")
    Call<ViewProjectMaster> projectslist(@Query("user_id") String user_id);

    @GET("viewSkillSetMaster")
    Call<ViewSkillsetMaster> skillslist();

    @GET("viewTrainingMaster")
    Call<ViewTrainingMaster> trininglist();

    @GET("contractor_list/")
    Call<ContractorListResponsce> contractors(@Query("user_id") String user_id);

    @GET("project_wise_supervisor/")
    Call<SupervisorListResponsce> supervisor(@Query("project_id") String pro_id);

    @POST("create_employee")
    Call<Boolean> onBoarding(@Body OnBoarding onBording);

    @PUT("updateWorker/{id}")
    Call<Generalresponsce> update(@Body OnBoardingUpdate onBording, @Path("id") String id);

    @POST("approve_employee")
    Call<Generalresponsce> approve(@Body ApprovalRequest onapprove);

    @POST("reject_employee")
    Call<Generalresponsce> reject(@Body RejectRequest onapprove);

    @GET("viewActivityMaster")
    Call<ViewActivityMasterResponsce> activityList();

    @POST("attendance_insert")
    Call<AttendanceCountResponsce> attendencepass(@Body AttendanceRequest attendanceRequest);

    @GET("attendance_count")
    Call<AttendanceCount1Responsce> attendancecount(@Query("activity_id")  String id, @Query("project_id") String user_id, @Query("user_id") String project_id);

    @GET("list_attendance/")
    Call<Attendance_new_list> attendance_list(@Query("user_id") String user_id);

    @GET("contractor_wise_attendance_list/")
    Call<Attendance_new_list> contractor_attendance_list(@Query("user_id") String user_id);
    @GET("employeeDetails")
    Call<EmployeedetailResponsce> empdetails(@Query("employee")  String id, @Query("user_id") String user_id, @Query("project_id") String project_id);

    @PUT("employee_resubmit/{id}")
    Call<Generalresponsce> empResubmit(@Path("id") String id);
    @PUT("approve_attendance")
    Call<generalGesponce> attendanceapprove(@Body AttendancApproveRequest request);

    @GET("training_allocation_list/")
    Call<TrainingListResponsce> training_list(@Query("user_id") String user_id);

    @GET("aadhaar_validation/")
    Call<Generalresponsce> aadharval(@Query("aadhaar_no") String a_no);

    @POST("allocate_training")
    Call<generalGesponce> trainingaloocate(@Body TrainingAllocationRequest tainingallocate);

    @POST("update_training_allocation")
    Call<Generalresponsce> completion(@Body CompletionRequest tainingallocate);

    @POST("list_workers_attendance")
    Call<Attendance_new_list> attendance_list2(@Body AttendanceFilterRequest filterRequest);

    @POST("viewAllocationList")
    Call<TraininWorkersResponsce> allocation_worker_list(@Body TrainingWorkersrequest workers);

    @Multipart
    @POST("file_upload")
    Call<generalGesponce> fileupload(@Part MultipartBody.Part file,
                                     @Part MultipartBody.Part file1,
                                     @Part MultipartBody.Part file2,
                                     @Part MultipartBody.Part file3,
                                     @Part("id") RequestBody parms2 );
    @Multipart
    @POST("file_upload")
    Call<generalGesponce> fileupload(@Part MultipartBody.Part file,
                                     @Part("id") RequestBody parms2 );
    @Multipart
    @POST("create_employee")
    Call<Generalresponsce> onBoarding2(@Part MultipartBody.Part file,
                              @Part MultipartBody.Part file1,
                              @Part MultipartBody.Part file2,
                              @Part MultipartBody.Part file3,
                              @Part("aadhaar_no") RequestBody parms1,
                              @Part("address_line_1") RequestBody parms2,
                              @Part("blood_group") RequestBody parms3,
                              @Part("city") RequestBody parms4,
                              @Part("dob") RequestBody parms5,
                              @Part("doj") RequestBody parms6,
                              @Part("driving_license_no") RequestBody parms7,
                              @Part("driving_license_expiry_date") RequestBody parms8,
                              @Part("emergency_contact_name") RequestBody parms9,
                              @Part("emergency_contact_no") RequestBody parms10,
                              @Part("employer_contractor_name") RequestBody parms11,
                              @Part("exp_month") RequestBody parms12,
                              @Part("exp_year") RequestBody parms13,
                              @Part("father_name") RequestBody parms14,
                              @Part("full_name") RequestBody parms15,
                              @Part("gender") RequestBody parms16,
                              @Part("induction_date") RequestBody parms17,
                              @Part("induction_status") RequestBody parms18,
                              @Part("medical_test_date") RequestBody parms20,
                              @Part("mobile_no") RequestBody parms21,
                              @Part("nationality") RequestBody parms22,
                              @Part("pin_code") RequestBody parms23,
                              @Part("project") RequestBody parms24,
                              @Part("skill_set") RequestBody parms25,
                              @Part("skill_type") RequestBody parms26,
                              @Part("state") RequestBody parms27,
                              @Part("subcontract_contract_no") RequestBody parms28,
                              @Part("subcontractor_name") RequestBody parms29,
                              @Part("user_id") RequestBody parms30,
                              @Part("work_employee_designation") RequestBody parms31,
                              @Part("status") RequestBody parms32,
                              @Part("feed") RequestBody parms33,
                              @Part("medical_test_status") RequestBody parms19,
                              @Part("report_is_ok") RequestBody parms34
                              );

    @Multipart
    @POST("create_employee")
    Call<Generalresponsce> onBoarding3(@Part MultipartBody.Part file,
                              @Part MultipartBody.Part file1,
                              @Part MultipartBody.Part file2,
                              @Part("aadhaar_no") RequestBody parms1,
                              @Part("address_line_1") RequestBody parms2,
                              @Part("blood_group") RequestBody parms3,
                              @Part("city") RequestBody parms4,
                              @Part("dob") RequestBody parms5,
                              @Part("doj") RequestBody parms6,
                              @Part("driving_license_no") RequestBody parms7,
                              @Part("driving_license_expiry_date") RequestBody parms8,
                              @Part("emergency_contact_name") RequestBody parms9,
                              @Part("emergency_contact_no") RequestBody parms10,
                              @Part("employer_contractor_name") RequestBody parms11,
                              @Part("exp_month") RequestBody parms12,
                              @Part("exp_year") RequestBody parms13,
                              @Part("father_name") RequestBody parms14,
                              @Part("full_name") RequestBody parms15,
                              @Part("gender") RequestBody parms16,
                              @Part("induction_date") RequestBody parms17,
                              @Part("induction_status") RequestBody parms18,
                              @Part("medical_test_date") RequestBody parms20,
                              @Part("mobile_no") RequestBody parms21,
                              @Part("nationality") RequestBody parms22,
                              @Part("pin_code") RequestBody parms23,
                              @Part("project") RequestBody parms24,
                              @Part("skill_set") RequestBody parms25,
                              @Part("skill_type") RequestBody parms26,
                              @Part("state") RequestBody parms27,
                              @Part("subcontract_contract_no") RequestBody parms28,
                              @Part("subcontractor_name") RequestBody parms29,
                              @Part("user_id") RequestBody parms30,
                              @Part("work_employee_designation") RequestBody parms31,
                              @Part("status") RequestBody parms32,
                              @Part("feed") RequestBody parms33,
                              @Part("medical_test_status") RequestBody parms19,
                              @Part("report_is_ok") RequestBody parms34
    );

}
