package com.rockchipme.app.fragments;

import android.support.design.widget.BottomSheetDialogFragment;


/**
 * Created by Android on 2/15/2017.
 */
public class BasketDialog extends BottomSheetDialogFragment {
//
//    String mString;
//
//    ApiCallServiceTask okHttpCommon;
//    List<Cart> cartList;
//
//    BasketListAdapter basketListAdapter;
//
//    TextView tv_totalAmount,tv_totalPrice,tv_taxPrice,tv_deliveryPrice,tv_grandTotal;
//    EditText et_voucherCode;
//    Button btn_placeOrder,btn_addVoucher;
//    RecyclerView rv_Vart;
//
//    public static BasketDialog newInstance(String string) {
//        BasketDialog f = new BasketDialog();
//        Bundle args = new Bundle();
//        args.putString("string", string);
//        f.setArguments(args);
//        return f;
//    }
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mString = getArguments().getString("string");
//        okHttpCommon = new ApiCallServiceTask(getActivity());
//
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.layout_basket, container, false);
//        //TextView tv = (TextView) v.findViewById(R.id.text);
////        initialize(v);
//
////        Log.e("jabbaba","jgl;ko");
//
////        getBasketItems();
//
//        final LinearLayoutManager layoutManager
//                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        // use a linear layout manager
//        rv_Vart.setLayoutManager(layoutManager);
//
//
//
//
//
//
//
//
//
//        return v;
//    }
//
//    //request to server
//    private void getBasketItems() {
//        SharedPreferences pref = getActivity().getSharedPreferences(Constants.PREF_LOGIN,getActivity().MODE_PRIVATE);
//        RequestBody body=new FormBody.Builder()
//                .add("rest_key", Constants.REST_KEY)
//                .add("api_key",pref.getString("api_key",null))
//                .build();
//        okHttpCommon.requestApi(true,body, Constants.SERVER_URL+Constants.getCart_api,"GET CATEGORIES...");
//    }//end requset
//
//    @Subscribe (threadMode = ThreadMode.MAIN)
//    public void response(ApiResponseBus responseBus){
//        String response = responseBus.response();
//        String resp_code = null;
//        String response2 = null;
//        Log.e(Constants.APP_TAG, "BasketDialog: response: " + response);
//        try {
//            JSONObject object = new JSONObject(response);
//            JSONObject jsonObject = object.optJSONObject("response_text");
//            resp_code = object.getString("response_code");
//            response2= jsonObject.optJSONArray("cart").toString();
//        } catch (JSONException e) { e.printStackTrace(); }
//
//        if (resp_code.trim().equalsIgnoreCase("200")) {
//            Type type = new TypeToken<ArrayList<Cart>>(){}.getType();
//            cartList = new Gson().fromJson(response2,type);
//
//            // create an Object for Adapter
//            basketListAdapter = new BasketListAdapter(getActivity(), cartList, rv_Vart);
//            // set the adapter object to the Recyclerview
//            rv_Vart.setAdapter(basketListAdapter);
//        }
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(getActivity());
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(getActivity());
//    }
//
//    void initialize(View v){
//        tv_totalAmount = (TextView)v.findViewById(R.id.tv_totalAmount);
//        tv_deliveryPrice = (TextView)v.findViewById(R.id.tv_deliveryFeePrice);
//        tv_grandTotal = (TextView)v.findViewById(R.id.tv_grandTotalPrice);
//        tv_taxPrice = (TextView) v.findViewById(R.id.tv_stateTaxprice);
//        tv_totalPrice = (TextView) v.findViewById(R.id.tv_totalPrice);
//        btn_placeOrder = (Button)v.findViewById(R.id.btn_placeOreder);
//        btn_addVoucher = (Button)v.findViewById(R.id.btn_addVoucher);
//        et_voucherCode = (EditText) v.findViewById(R.id.et_VoucherCode);
//        rv_Vart = (RecyclerView) v.findViewById(R.id.rv_cart);
//    }

}