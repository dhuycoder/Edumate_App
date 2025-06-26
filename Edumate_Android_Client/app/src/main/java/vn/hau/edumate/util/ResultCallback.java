package vn.hau.edumate.util;

public interface ResultCallback<T> {
    void onSuccess(T result);
    void onError(Throwable throwable);
}