package com.example.homelesspersontracker;

import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.LatLngOrBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private static String uid = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public static String requestID;

    public static ArrayList<Request> requestArrayList;

    public FirebaseHelper(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }
    public String getUid(){
        return uid;
    }

    public void updateUid(String uid) {
        FirebaseHelper.uid = uid;
    }

    public void addUserToFirestore(String name, String userType, String username, String phoneNum, String address, int age, String newUID) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("userType", userType);
        user.put("username", username);
        user.put("phoneNum", phoneNum);
        user.put("address", address);
        user.put("age", age);


        db.collection(newUID).document(newUID).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(MainActivity.TAG, name + "'s user account added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(MainActivity.TAG, "error adding user account", e);
                    }
                });
    }

    public void addUserToFirestore(String name, String userType, String location, String region, String services, String newUID) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("userType", userType);
        user.put("location", location);
        user.put("region", region);
        user.put("services", services);


        db.collection(newUID).document(newUID).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(MainActivity.TAG, name + "'s user account added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(MainActivity.TAG, "error adding user account", e);
                    }
                });
    }

    public void addUserToFirestore(String name, String userType, String agency, String employeeId, String newUID) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("userType", userType);
        user.put("agency", agency);
        user.put("employeeId", employeeId);
        user.put("latitude", 0);
        user.put("longitude", 0);


        db.collection(newUID).document(newUID).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(MainActivity.TAG, name + "'s user account added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(MainActivity.TAG, "error adding user account", e);
                    }
                });
    }

    public void uploadToFirebase(Uri imageUri) {
        if (imageUri != null){

            final StorageReference fileRef  = FirebaseStorage.getInstance().getReference("Images" );

            fileRef.putFile(imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("LFRA", "Inside onSuccess");
                                    Uri url = uri;

                                    Map<String , Object> image = new HashMap<>();
                                    image.put("Image", url.toString());

                                    db.collection(uid).document(mAuth.getCurrentUser().getDisplayName()).
                                            collection("images").document("Image").
                                            set(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("LFRA", "Inside onComplete of weird file thing i hate my life: " + url.toString());

                                        }
                                    });

                                }
                            });
                        }
                    });
        }
    }

    public void initiateRequest(boolean isHelpRequest, Request r) {
        if(isHelpRequest) {
            db.collection("HelpRequests").add(r)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        requestID = documentReference.getId();
                        Map<String, Object> requestId = new HashMap<>();
                        requestId.put("requestId", documentReference.getId());
                        db.collection(mAuth.getUid()).document(mAuth.getUid())
                                .collection("requests").
                                document(documentReference.getId()).set(requestId)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("LFRA", "Success adding request to user");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("LFRA", "Failure to add request to user");
                                        }
                                    });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LFRA", "Failure to add request: ", e);
                    }
                });
        } else {
            db.collection("DonationRequests").add(r)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            requestID = documentReference.getId();
                            Map<String, Object> requestId = new HashMap<>();
                            requestId.put("requestId", documentReference.getId());
                            db.collection(mAuth.getUid()).document(mAuth.getUid())
                                    .collection("requests").
                                    document(documentReference.getId()).set(requestId)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("LFRA", "Success adding request to user");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("LFRA", "Failure to add request to user");
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("LFRA", "Failure to add request: ", e);
                        }
                    });
        }
    }

    public void updateEmployeeLocation(Location location) {
        db.collection(mAuth.getUid()).document(mAuth.getUid()).
                update("latitude", location.getLatitude())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection(mAuth.getUid()).document(mAuth.getUid())
                                .update("longitude", location.getLongitude())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Map<String, Double> latlong = new HashMap<>();
                                        latlong.put("latitude", location.getLatitude());
                                        latlong.put("longitude", location.getLongitude());

                                        db.collection("EmployeeLocations").document(mAuth.getUid())
                                                .set(latlong).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("LFRA", "Success updating employee location");
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("LFRA", "Error updating employee location", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("LFRA", "Error updating employee location", e);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LFRA", "Error updating employee location", e);
                    }
                });
    }

    public void updateRequestStatus(Request r) {
        db.collection("HelpRequests").document(r.getId())
                .update("status", r.getStatus())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("LFRA", "Successfully updated request status to: " + r.getStatus());
                    }
                });
    }

    public void addRequestToWorker(String workerID) {
        // add the worker id to the request
            // user requests
        db.collection(mAuth.getUid()).document(mAuth.getUid())
                .collection("requests").
                document(requestID)
                .update("workerID", workerID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("LFRA", "Success adding worker ID to user's request");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LFRA", "Failure adding worker ID to user's request");
                    }
                });
            // TODO: help request/donation request

        // add the request to the worker
        Map<String, String> requestInfoMap = new HashMap<>();
        requestInfoMap.put("requesterId", mAuth.getUid());
        requestInfoMap.put("requestId", requestID);
        db.collection(workerID).document(workerID).collection("requests")
                .document(requestID).set(requestInfoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("LFRA", "Success adding request to worker");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LFRA", "Failure adding request to worker");
                    }
                });;


    }

    public void getUserType(FirestoreCallback firestoreCallback) {

        db.collection(mAuth.getUid()).document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String userType = task.getResult().getString("userType");
                            firestoreCallback.onCallback(userType);
                        } else {
                            Log.d("LFRA", "Error getting user type", task.getException());
                        }
                    }
                });
    }

    public void getEmployeeLocations(FirestoreCallback firestoreCallback) {
        db.collection("EmployeeLocations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, com.google.android.gms.maps.model.LatLng> locationsMap = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                locationsMap.put(document.getId(), new LatLng
                                        (document.getDouble("latitude"),
                                                document.getDouble("longitude")));
                            }
                            firestoreCallback.onCallback(locationsMap);
                        } else {
                            Log.d("LFRA", "Error getting locations", task.getException());
                        }
                    }
                });

    }

    public void getRequests(FirestoreCallback firestoreCallback) {
        db.collection(mAuth.getUid()).document(mAuth.getUid()).collection("requests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            requestArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                getRequestsHelper(doc, new FirestoreCallback() {
                                    @Override
                                    public void onCallback(String userType) {

                                    }

                                    @Override
                                    public void onCallback(Map<String, LatLng> locationsMap) {

                                    }

                                    @Override
                                    public void onCallback(ArrayList<Request> requestAL) {

                                    }

                                    @Override
                                    public void onCallback(Request r) {
                                        requestArrayList.add(r);
                                    }
                                });
                            }

                            firestoreCallback.onCallback(new ArrayList<>());
                        } else {
                            Log.d("LFRA", "Error getting requests", task.getException());
                        }
                    }
                });
    }

    private void getRequestsHelper(QueryDocumentSnapshot doc, FirestoreCallback firestoreCallback) {
        db.collection("HelpRequests")
                .document(doc.getString("requestId")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> taskTwo) {
                        Request request = new HelpRequest(taskTwo.getResult().getString("status"),
                                taskTwo.getResult().getString("date"),
                                taskTwo.getResult().getDouble("requestLat"),
                                taskTwo.getResult().getDouble("requestLong"));

                        request.setId(doc.getString("requestId"));

                        firestoreCallback.onCallback(request);
                    }
                });
    }

    //https://stackoverflow.com/questions/48499310/how-to-return-a-documentsnapshot-as-a-result-of-a-method/48500679#48500679
    public interface FirestoreCallback {
        // we use the ArrayList of the data type we are working with in Firestore
        void onCallback(String userType);

        void onCallback(Map<String, com.google.android.gms.maps.model.LatLng> locationsMap);

        void onCallback(ArrayList<Request> requestAL);

        void onCallback(Request r);
    }
}
