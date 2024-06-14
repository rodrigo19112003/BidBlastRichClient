package gRPC;

public interface GrpcClientCallback {
    void onSuccess();
    void onError(Throwable error);
}
