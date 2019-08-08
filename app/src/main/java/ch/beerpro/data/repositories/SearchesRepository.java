package ch.beerpro.data.repositories;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import ch.beerpro.domain.models.Search;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;
import ch.beerpro.presentation.utils.EntityClassSnapshotParser;

public class SearchesRepository {

    private final static EntityClassSnapshotParser<Search> parser = new EntityClassSnapshotParser<>(Search.class);

    public static LiveData<List<Search>> getLatestSearchesByUser(String userId) {
        return new FirestoreQueryLiveDataArray<>(FirebaseFirestore.getInstance().collection(Search.COLLECTION)
                .orderBy(Search.FIELD_CREATION_DATE, Query.Direction.DESCENDING)
                .whereEqualTo(Search.FIELD_USER_ID, userId).limit(3), Search.class);
    }

    public void addSearchTerm(String term) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Search search = new Search(currentUser.getUid(), term);
        db.collection(Search.COLLECTION).add(search);
    }
}
