package cz.kubajanda.rxokhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.Callable;

/**
 * @author Jakub Janda
 */

public class RequestCallable implements Callable<Response> {
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
