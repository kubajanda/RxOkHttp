package cz.kubajanda.rxokhttp;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/** Helper class for creating and working with OkHttp in Rx style.
 *
 *
 * @author Jakub Janda
 */

public class RxOkHttp {
	private static OkHttpClient sDefaultClient;

	/** Returns default client which is used by methods without client specified.
	 *
	 * @return default client
	 */
	public static synchronized OkHttpClient getDefaultClient() {
		if (sDefaultClient == null) {
			sDefaultClient = new OkHttpClient();
		}

		return sDefaultClient;
	}

	/** Sets default OkHttpClient which is used by methods without client specified.
	 *
	 * @param client
	 */
	public static synchronized void setDefaultClient(OkHttpClient client) {
		sDefaultClient = client;
	}

	/** Creates Response observable from request object using default client.
	 *
	 * @param request
	 *
	 * @return response observable
	 */
	public static Single<Response> from(final Request request) {
		return from(getDefaultClient(), request);
	}

	/** Creates Response observable from request object using specified client.
	 *
	 * @param request
	 *
	 * @return response observable
	 */
	public static Single<Response> from(final OkHttpClient client,
										final Request request) {
		return Single.create(new ResponseOnSubscribe(client, request));
	}

	/** Creates Response observable by getting specified url.
	 *
	 * @param client
	 * @param url
	 *
	 * @return response observable
	 */
	public static Single<Response> get(final OkHttpClient client,
									   String url) {
		return from(client,
					new Request.Builder().url(url)
										 .get()
										 .build());
	}

	/** Creates Response observable by getting specified url, using default client.
	 *
	 * @param url
	 *
	 * @return response observable
	 */
	public static Single<Response> get(String url) {
		return get(getDefaultClient(), url);
	}

	/** Helper for mapping response to string.
	 *
	 * @return function mapping response to string
	 */
	public static Function<Response, String> asString() {
		return new Function<Response, String>() {
			@Override
			public String apply(Response response) {
				try {
					final String data = response.body()
												.string();
					response.close();
					return data;
				} catch (IOException e) {
					throw Exceptions.propagate(e);
				}
			}
		};
	}

	/** Helper for mapping response to JSONObject.
	 *
	 * @return function mapping response to JSONObject
	 */
	public static Function<Response, JSONObject> asJSONObject() {
		return new Function<Response, JSONObject>() {
			@Override
			public JSONObject apply(Response response) {
				try {
					final JSONObject jsonObject = new JSONObject(response.body()
																		 .string());
					response.close();
					return jsonObject;
				} catch (IOException | JSONException e) {
					throw Exceptions.propagate(e);
				}
			}
		};
	}

	private static class ResponseOnSubscribe implements SingleOnSubscribe<Response>, Cancellable {
		private final OkHttpClient mClient;
		private final Request      mRequest;
		private       Call         mCall;

		private ResponseOnSubscribe(OkHttpClient client,
									Request request) {
			mClient = client;
			mRequest = request;
		}

		@Override
		public void subscribe(SingleEmitter<Response> e)
		  throws
		  Exception {
			e.setCancellable(this);

			if (!e.isDisposed()) {
				mCall = mClient.newCall(mRequest);
				final Response response = mCall.execute();

				if (!e.isDisposed()) {
					e.onSuccess(response);
				}
			}
		}

		@Override
		public void cancel()
		  throws
		  Exception {
			if (mCall != null) {
				mCall.cancel();
			}
		}
	}
}
