package grpc;

public interface IVideoStreamListener {
    void onVideoChunkReceived(byte[] videoChunk);
    void onVideoFetchError(Throwable error);
    void onVideoFetchComplete();
}