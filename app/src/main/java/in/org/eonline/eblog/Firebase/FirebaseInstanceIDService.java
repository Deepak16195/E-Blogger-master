package in.org.eonline.eblog.Firebase;

/**
 * Created by Dignity on 04-12-2017.
 */

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;


/**
 * Created by filipp on 5/23/2016.
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

    }

}