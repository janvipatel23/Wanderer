package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.PinUpdate;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class PinUpdateService {
    private final static String DOCUMENT_ID = "0dZ4madili5WE9g9iIqn";
    private final static String COLLECTION_NAME = "pin_updates";

    // Sends a pin update to the firestore database
    public String sendPinUpdate() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        PinUpdate pinUpdate = new PinUpdate(DOCUMENT_ID, new Date());
        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(COLLECTION_NAME).document(DOCUMENT_ID).set(pinUpdate);
        return collectionApiFuture.get().getUpdateTime().toString();
    }
}
