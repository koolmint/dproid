package org.koolmint.dproid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewArticleFragment extends Fragment {
	public static final String ARG_WR_ID="org.koolmint.dproid.wr_id";
	public static final String PAGE_URL="http://dvdprime.donga.com/g5/bbs/board.php?bo_table=comm&wr_id=";
	public static final String TAG="org.koolmint.dproid";
	private TextView testTextView;
	private Handler hndl=new Handler();
	private StringBuilder buff;
	private ProgressDialog progressDlg;
	private String content;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fragment_view_article, parent, false);
		testTextView=(TextView)v.findViewById(R.id.test_text_view);
		return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args=getArguments();
		final String targetURL=PAGE_URL + args.getInt(ARG_WR_ID);

		buff=new StringBuilder();
		
		progressDlg=ProgressDialog.show(getActivity(), null, "wait...");
		progressDlg.show();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpURLConnection conn=(HttpURLConnection)new URL(targetURL).openConnection();
					conn.setUseCaches(false);;
					conn.setReadTimeout(10*1000);
					conn.setRequestProperty("User-Agent", "Mozilla/5.0");
					
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
						String line="";
						while((line=reader.readLine()) != null) 
							buff.append(line);
					} else {
						// not HTTP_OK
					}
					
					conn.disconnect();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Document doc=Jsoup.parse(buff.toString());
				final Element title=doc.select("h1#bo_v_title").first();
				Element author=doc.select("a.sv_member").first();
				
				Pattern p=Pattern.compile("<!-- 본문 내용 시작 \\{ -->(.*?)<!-- \\} 본문 내용 끝 -->");
				Matcher m=p.matcher(buff.toString());
				if(m.find()) {
					content=m.group(1).replaceAll("<div id=\"bo_v_con\"></div>", "").trim();
					content=content.replaceAll("<p>",  "").replaceAll("</p>",  "\\\n");
				}
				
				hndl.post(new Runnable() {
					@Override
					public void run() {
						testTextView.setText(content);
						
						if(progressDlg.isShowing())
							progressDlg.dismiss();
					}
				});
			}
		}).start();
	}

	public static ViewArticleFragment newInstance(int wr_id) {
		Bundle args=new Bundle();
		args.putInt(ARG_WR_ID, wr_id);
		
		ViewArticleFragment fragment=new ViewArticleFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
}
