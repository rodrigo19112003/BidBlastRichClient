package gRPC;

public interface VideoStreamListener {
    void onVideoChunkReceived(byte[] videoChunk);
    void onVideoFetchError(Throwable error);
    void onVideoFetchComplete();
}