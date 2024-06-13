import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.64.0)",
    comments = "Source: src/proto/video.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class VideoServiceGrpc {

  private VideoServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "VideoService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Video.VideoRequest,
      Video.VideoChunkResponse> getStreamVideoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "streamVideo",
      requestType = Video.VideoRequest.class,
      responseType = Video.VideoChunkResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<Video.VideoRequest,
      Video.VideoChunkResponse> getStreamVideoMethod() {
    io.grpc.MethodDescriptor<Video.VideoRequest, Video.VideoChunkResponse> getStreamVideoMethod;
    if ((getStreamVideoMethod = VideoServiceGrpc.getStreamVideoMethod) == null) {
      synchronized (VideoServiceGrpc.class) {
        if ((getStreamVideoMethod = VideoServiceGrpc.getStreamVideoMethod) == null) {
          VideoServiceGrpc.getStreamVideoMethod = getStreamVideoMethod =
              io.grpc.MethodDescriptor.<Video.VideoRequest, Video.VideoChunkResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "streamVideo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Video.VideoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Video.VideoChunkResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VideoServiceMethodDescriptorSupplier("streamVideo"))
              .build();
        }
      }
    }
    return getStreamVideoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<Video.VideoUploadRequest,
      Video.VideoUploadResponse> getUploadVideoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "uploadVideo",
      requestType = Video.VideoUploadRequest.class,
      responseType = Video.VideoUploadResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<Video.VideoUploadRequest,
      Video.VideoUploadResponse> getUploadVideoMethod() {
    io.grpc.MethodDescriptor<Video.VideoUploadRequest, Video.VideoUploadResponse> getUploadVideoMethod;
    if ((getUploadVideoMethod = VideoServiceGrpc.getUploadVideoMethod) == null) {
      synchronized (VideoServiceGrpc.class) {
        if ((getUploadVideoMethod = VideoServiceGrpc.getUploadVideoMethod) == null) {
          VideoServiceGrpc.getUploadVideoMethod = getUploadVideoMethod =
              io.grpc.MethodDescriptor.<Video.VideoUploadRequest, Video.VideoUploadResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "uploadVideo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Video.VideoUploadRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Video.VideoUploadResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VideoServiceMethodDescriptorSupplier("uploadVideo"))
              .build();
        }
      }
    }
    return getUploadVideoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VideoServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VideoServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VideoServiceStub>() {
        @java.lang.Override
        public VideoServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VideoServiceStub(channel, callOptions);
        }
      };
    return VideoServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VideoServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VideoServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VideoServiceBlockingStub>() {
        @java.lang.Override
        public VideoServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VideoServiceBlockingStub(channel, callOptions);
        }
      };
    return VideoServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VideoServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VideoServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VideoServiceFutureStub>() {
        @java.lang.Override
        public VideoServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VideoServiceFutureStub(channel, callOptions);
        }
      };
    return VideoServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void streamVideo(Video.VideoRequest request,
        io.grpc.stub.StreamObserver<Video.VideoChunkResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStreamVideoMethod(), responseObserver);
    }

    /**
     */
    default io.grpc.stub.StreamObserver<Video.VideoUploadRequest> uploadVideo(
        io.grpc.stub.StreamObserver<Video.VideoUploadResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getUploadVideoMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service VideoService.
   */
  public static abstract class VideoServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return VideoServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service VideoService.
   */
  public static final class VideoServiceStub
      extends io.grpc.stub.AbstractAsyncStub<VideoServiceStub> {
    private VideoServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VideoServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VideoServiceStub(channel, callOptions);
    }

    /**
     */
    public void streamVideo(Video.VideoRequest request,
        io.grpc.stub.StreamObserver<Video.VideoChunkResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getStreamVideoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<Video.VideoUploadRequest> uploadVideo(
        io.grpc.stub.StreamObserver<Video.VideoUploadResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getUploadVideoMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service VideoService.
   */
  public static final class VideoServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<VideoServiceBlockingStub> {
    private VideoServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VideoServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VideoServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<Video.VideoChunkResponse> streamVideo(
        Video.VideoRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getStreamVideoMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service VideoService.
   */
  public static final class VideoServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<VideoServiceFutureStub> {
    private VideoServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VideoServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VideoServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_STREAM_VIDEO = 0;
  private static final int METHODID_UPLOAD_VIDEO = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAM_VIDEO:
          serviceImpl.streamVideo((Video.VideoRequest) request,
              (io.grpc.stub.StreamObserver<Video.VideoChunkResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_UPLOAD_VIDEO:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.uploadVideo(
              (io.grpc.stub.StreamObserver<Video.VideoUploadResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getStreamVideoMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              Video.VideoRequest,
              Video.VideoChunkResponse>(
                service, METHODID_STREAM_VIDEO)))
        .addMethod(
          getUploadVideoMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              Video.VideoUploadRequest,
              Video.VideoUploadResponse>(
                service, METHODID_UPLOAD_VIDEO)))
        .build();
  }

  private static abstract class VideoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    VideoServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Video.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("VideoService");
    }
  }

  private static final class VideoServiceFileDescriptorSupplier
      extends VideoServiceBaseDescriptorSupplier {
    VideoServiceFileDescriptorSupplier() {}
  }

  private static final class VideoServiceMethodDescriptorSupplier
      extends VideoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    VideoServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (VideoServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VideoServiceFileDescriptorSupplier())
              .addMethod(getStreamVideoMethod())
              .addMethod(getUploadVideoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
