package run;

import views.ClientFrm;
import views.LoginFrm;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) {
        // phải thêm chức năng chọn server
        LoginFrm frm = new LoginFrm();
        frm.setVisible(true);
    }
}
