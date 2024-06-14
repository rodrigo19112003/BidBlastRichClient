package grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import proto.video.VideoServiceGrpc;
import proto.video.Video.VideoUploadRequest;
import proto.video.Video.VideoUploadResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import static lib.Configuration.GRPC_BASE_URL;
import static lib.Configuration.GRPC_PORT;

public class Server {

    private final ManagedChannel channel;
    private final VideoServiceGrpc.VideoServiceStub asyncStub;

    public Server() {
        this.channel = ManagedChannelBuilder.forAddress(GRPC_BASE_URL, GRPC_PORT)
                .usePlaintext()
                .build();
        this.asyncStub = VideoServiceGrpc.newStub(channel);
    }

    public void uploadVideo(File videoFile, int auctionId, String mimeType) throws Exception {
        StreamObserver<VideoUploadRequest> requestObserver = asyncStub.uploadVideo(new StreamObserver<VideoUploadResponse>() {
            @Override
            public void onNext(VideoUploadResponse response) {
                System.out.println("Upload response: " + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Upload completed.");
            }
        });

        try (InputStream inputStream = new FileInputStream(videoFile)) {
            byte[] buffer = new byte[256 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                VideoUploadRequest request = VideoUploadRequest.newBuilder()
                        .setAuctionId(auctionId)
                        .setMimeType(mimeType)
                        .setContent(com.google.protobuf.ByteString.copyFrom(buffer, 0, bytesRead))
                        .setName(videoFile.getName())
                        .build();
                requestObserver.onNext(request);
            }
        } catch (Exception e) {
            requestObserver.onError(e);
            throw e;
        }

        requestObserver.onCompleted();
    }

    public void shutdown() {
        channel.shutdown();
    }
}