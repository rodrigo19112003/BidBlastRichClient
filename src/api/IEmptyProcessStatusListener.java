package api;

import repositories.ProcessErrorCodes;

public interface IEmptyProcessStatusListener {
    void onSuccess();
    void onError(ProcessErrorCodes errorCode);
}
