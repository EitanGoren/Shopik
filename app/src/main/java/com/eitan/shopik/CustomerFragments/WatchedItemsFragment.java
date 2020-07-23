package com.eitan.shopik.CustomerFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.Adapters.RecyclerAdapter;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.material.textfield.TextInputEditText;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class WatchedItemsFragment extends Fragment {

    private static final String USER_NAME = "meytalgo123@gmail.com";
    private static final String PASSWORD = "Lgqazwsx";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Mobile Safari/537.36";
    Button mLoginBtn, mSearchBtn;
    TextInputEditText email,pass,search;
    String mEmail,mPass,mSearch;
    Map<String,String> cookie;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    ArrayList<RecyclerItem> list = new ArrayList<>();
    ImageView check_circle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_watched_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        check_circle.setVisibility(View.INVISIBLE);
        mLoginBtn.setOnClickListener(v -> {
            mEmail = Objects.requireNonNull(email.getText()).toString();
            mPass = Objects.requireNonNull(pass.getText()).toString();
            try {
                loginToShufersal login = new loginToShufersal(mEmail,mPass);
                login.execute();
            }
            catch (Exception e) {
                System.out.println("Error in HttpUrlConnectionExample " + e.getMessage());
            }
        });
        mSearchBtn.setOnClickListener(v -> {
            mSearch = Objects.requireNonNull(search.getText()).toString();

            searchProduct search = new searchProduct(mSearch);
            search.execute();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView = requireView().findViewById(R.id.recycler_market);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setScrollbarFadingEnabled(true);

        recyclerAdapter = new RecyclerAdapter(list,"Market");
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void init() {
        mLoginBtn = requireView().findViewById(R.id.login_btn);
        mSearchBtn = requireView().findViewById(R.id.search_btn);
        email = requireView().findViewById(R.id.enter_email);
        pass = requireView().findViewById(R.id.enter_password);
        search = requireView().findViewById(R.id.search);
        check_circle = requireView().findViewById(R.id.check_circle);
    }

    private class loginToShufersal extends AsyncTask<Void,Void,String> {

        private String user_name,password;

        public loginToShufersal(String user_name,String password){
            this.password = password;
            this.user_name = user_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            check_circle.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                Document doc = Jsoup.connect("https://www.shufersal.co.il/online/he/login").get();
                Elements koko = doc.select("meta");
                String Token = "";
                for( Element element : koko){
                    if(element.attr("name").equals("_csrf")){
                        Token = element.attr("content");
                        break;
                    }
                }

                Connection.Response loginForm = Jsoup.connect("https://www.shufersal.co.il/online/he/j_spring_security_check")
                        .data(
                                "fail_url","/login/?error=true",
                                "j_username",user_name,
                                "j_password",password,
                                "remember-me","False",
                                "CSRFToken",Token,
                                "submit","כניסה"
                        ).method(Connection.Method.POST)
                        //.ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .userAgent(USER_AGENT)
                        .execute();

                cookie = loginForm.cookies();

                return loginForm.statusMessage();
            }
            catch (IOException e) {
                Log.e(Macros.TAG,"Error : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
            if(status.equals("OK"))
                check_circle.setVisibility(View.VISIBLE);
        }
    }

    private class searchProduct extends AsyncTask<Void,Void,String> {

        private String search_prod;

        searchProduct(String search){
            this.search_prod = search;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                if(cookie == null) return "error";

                Document document = Jsoup.connect("https://www.shufersal.co.il/online/he/search?text=" + search_prod).cookies(cookie).get();
                Elements elements = document.getElementsByAttributeValueContaining("class","SEARCH tileBlock miglog-prod miglog-sellingmethod-by_unit");
                for (Element elem : elements){
                    RecyclerItem recyclerItem = new RecyclerItem(elem.attributes().get("data-product-name"),null);
                    String img = elem.childNode(1).childNode(5).childNode(1).attributes().get("src");
                    recyclerItem.setImage_resource(img);
                    list.add(recyclerItem);
                    publishProgress();
                }
            }
            catch (IOException e) {
                Log.e(Macros.TAG,"Error : " + e.getMessage());
            }
            return "ok";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}
