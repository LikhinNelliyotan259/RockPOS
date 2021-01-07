package com.rockchipme.app.activities;

//public class NotificationDetailsActivity extends BaseActivity {
//
//    String title = "", description = "", imageUrl = "";
//
//    @Override
//    protected int getLayoutResourceId() {
//        return R.layout.activity_notification_details;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setActionBarTitle(getResources().getString(R.string.notifications));
//        initViews();
//        if(getIntent().getStringExtra("title") != null){
//            title = getIntent().getStringExtra("title");
//        }
//        if(getIntent().getStringExtra("description") != null){
//            description = getIntent().getStringExtra("description");
//        }
//        if(getIntent().getStringExtra("imageUrl") != null){
//            imageUrl = getIntent().getStringExtra("imageUrl");
//        }
//        tvTitle.setText(title);
//        tvDescription.setText(description);
//        Glide.with(getApplicationContext()).load(Constants.NOTIFICATION_BIG_URL + imageUrl).fitCenter().into(iv);
//    }
//
//    ImageView iv;
//    TextView tvTitle, tvDescription;
//    public void initViews(){
//        iv = (ImageView) findViewById(R.id.iv);
//        tvTitle = (TextView) findViewById(R.id.tvTitle);
//        tvDescription = (TextView) findViewById(R.id.tvDescription);
//    }
//
//}
