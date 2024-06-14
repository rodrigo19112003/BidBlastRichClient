package gRPC;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.Base64;
import javafx.application.Platform;
import model.HypermediaFile;
import proto.video.Video.VideoChunkResponse;
import proto.video.Video.VideoRequest;
import proto.video.Video.VideoUploadRequest;
import proto.video.Video.VideoUploadResponse;
import proto.video.VideoServiceGrpc;

public class Client {
    private static ManagedChannel channel = null;
    private final VideoServiceGrpc.VideoServiceStub videoServiceStub;
    private static final String GRPC_URL = "localhost";
    private static final int GRPC_PORT = 3001;
    private final VideoStreamListener streamListener;

    public Client(VideoStreamListener streamListener) {
        this.streamListener = streamListener;
        channel = ManagedChannelBuilder.forAddress(GRPC_URL, GRPC_PORT)
                .usePlaintext()
                .build();
        videoServiceStub = VideoServiceGrpc.newStub(channel);
    }

    public Client() {
        channel = ManagedChannelBuilder.forAddress(GRPC_URL, GRPC_PORT)
                .usePlaintext()
                .build();
        videoServiceStub = VideoServiceGrpc.newStub(channel);
        this.streamListener = null;
    }

    public void streamVideo(int videoId) {
        VideoRequest request = VideoRequest.newBuilder().setVideoId(videoId).build();

        videoServiceStub.streamVideo(request, new StreamObserver<VideoChunkResponse>() {
            @Override
            public void onNext(VideoChunkResponse value) {
                byte[] videoChunk = value.getData().toByteArray();
                if (streamListener != null) {
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

    public void uploadVideo(HypermediaFile videoFile, int auctionId, GrpcClientCallback callback) {
    System.out.println("Preparando para subir el video con nombre: " + videoFile.getName());
    StreamObserver<VideoUploadResponse> responseObserver = new StreamObserver<VideoUploadResponse>() {
        @Override
        public void onNext(VideoUploadResponse value) {
            System.out.println("Respuesta de carga de video recibida: " + value.getMessage());
            callback.onSuccess();
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("Error en la carga gRPC: " + t.getMessage());
            callback.onError(t);
        }

        @Override
        public void onCompleted() {
            System.out.println("Carga gRPC completada.");
            channel.shutdown();
        }
    };

    StreamObserver<VideoUploadRequest> requestObserver = videoServiceStub.uploadVideo(responseObserver);
    try {
        byte[] decodedContent = Base64.getDecoder().decode(videoFile.getContent());
        System.out.println("Enviando solicitud de carga de video para: " + videoFile.getName());
        VideoUploadRequest request = VideoUploadRequest.newBuilder()
                .setAuctionId(auctionId)
                .setMimeType(videoFile.getMimeType())
                .setContent(ByteString.copyFrom(decodedContent))
                .setName(videoFile.getName())
                .build();
        requestObserver.onNext(request);
        requestObserver.onCompleted();
    } catch (RuntimeException e) {
        System.err.println("Error en la solicitud gRPC: " + e.getMessage());
        requestObserver.onError(e);
        throw e;
    }
}


    public static boolean getChannelStatus() {
        return channel != null;
    }

    public void shutdown() {
        channel.shutdownNow();
    }
}
