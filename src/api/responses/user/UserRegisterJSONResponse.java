package api.responses.user;

import model.Account;

public class UserRegisterJSONResponse {
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public UserRegisterJSONResponse(Account account) {
        this.account = account;
    }
}
