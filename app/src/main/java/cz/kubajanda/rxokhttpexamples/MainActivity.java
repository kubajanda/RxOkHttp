package cz.kubajanda.rxokhttpexamples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import cz.kubajanda.rxokhttp.RxOkHttp;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		RxOkHttp.get("http://www.kubajanda.cz/mysongbook/beta/search?query=nohavica")
				.map(RxOkHttp.asJSONObject())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<JSONObject>(){

			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onSuccess(JSONObject object) {
				((TextView)findViewById(R.id.text)).setText(object.toString());
			}

			@Override
			public void onError(Throwable e) {
				((TextView)findViewById(R.id.text)).setText(e.toString());
			}
		});
	}
}
