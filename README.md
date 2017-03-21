# RxOkHttp
RxOkHttp is small Android library with helpers for working with OkHttp in Rx style. It is primarily developed for my own projects, so the API is small currently and it will probably expand as I need. But everyone can help with expanding and improving this library:)

## Used libraries
Library is using these libraries:

    com.squareup.okhttp3:okhttp:3.4.2
    io.reactivex.rxjava2:rxandroid:2.0.1
    io.reactivex.rxjava2:rxjava:2.0.7
    
## Example
The simplest example coudle be something like this:
~~~~~
RxOkHttp.get("http://headers.jsontest.com/")
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
~~~~~

## Default OkHttpClient
You can set default OkHttpClient instance which will be used by default by methods without client instance as parameter:
~~~~
RxOkHttp.setDefaultClient(OkHttpClient client)
~~~~
If you don't specify one, default instance will be created when needed.

## Obtaining Single\<Response\>
RxOkHttp uses Single as observable. The main entry points for obtaining Response observables are:
~~~~
Single<Response> observable = RxOkHttp.from(Request request)
Single<Response> observale = RxOkHttp.from(OkHttpClient client, Request request)
~~~~

As get is probably the most used http method, there is also helper for this:
~~~~
Single<Response> observable = RxOkHttp.get(String url)
~~~~

## Mapping helpers
Currently there are there helpers which can be used to map Single<Response> to String and JSONObject:
~~~~
Function<Response, String> mapResponseToString = RxOkHttp.asString()
Function<Response, JSONObject> mapresponseAsJson = RxOkHttp.asJSONObject()
~~~~
