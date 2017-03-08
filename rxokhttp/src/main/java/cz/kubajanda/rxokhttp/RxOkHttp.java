package cz.kubajanda.rxokhttp;

import io.reactivex.Single;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;

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
		return Single.fromCallable(new RequestCallable(client, request));
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
					return response.body()
								   .string();
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
					return new JSONObject(response.body()
												  .string());
				} catch (IOException | JSONException e) {
					throw Exceptions.propagate(e);
				}
			}
		};
	}

	private static class RequestCallable implements Callable<Response> {
		private final OkHttpClient mClient;
		private final Request      mRequest;

		public RequestCallable(OkHttpClient client,
							   Request request) {
			mClient = client;
			mRequest = request;
		}

		@Override
		public Response call()
		  throws
		  Exception {
			return mClient.newCall(mRequest)
						  .execute();
		}
	}
}
