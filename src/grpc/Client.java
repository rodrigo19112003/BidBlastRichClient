package grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import static lib.Configuration.GRPC_BASE_URL;
import static lib.Configuration.GRPC_PORT;
import proto.video.Video.VideoChunkResponse;
import proto.video.Video.VideoRequest;
import proto.video.VideoServiceGrpc;

public class Client {
    private static ManagedChannel channel = null;
    private final VideoServiceGrpc.VideoServiceStub videoServiceStub;
    private final IVideoStreamListener streamListener;

    public Client(IVideoStreamListener streamListener) {
        this.streamListener = streamListener;
        channel = ManagedChannelBuilder.forAddress(GRPC_BASE_URL, GRPC_PORT)
                .usePlaintext()
                .build();
        videoServiceStub = VideoServiceGrpc.newStub(channel);
    }

    public void streamVideo(int videoId) {
        VideoRequest request = VideoRequest.newBuilder().setVideoId(videoId).build();

        videoServiceStub.streamVideo(request, new StreamObserver<VideoChunkResponse>() {
            @Override
            public void onNext(VideoChunkResponse value) {
                byte[] videoChunk = value.getData().toByteArray();
                if(streamListener != null){
                    Platform.runLater(() -> streamListener.onVideoChunkReceived(videoChunk));
                }
            }

            @Override
            public void onError(Throwable t) {
                Platform.runLater(() -> streamListener.onVideoFetchError(t));
                channel.shutdownNow();
            }

            @Override
            public void onCompleted() {
                Platform.runLater(streamListener::onVideoFetchComplete);
            }
        });
    }

    public static boolean getChannelStatus() {
        return channel != null;
    }

    public void shutdown() {
        channel.shutdownNow();
    }
}