package com.example.timad.poznavacka;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DB {
    /* ZDE bude kod pro spojeni s databazi
     * potřeba připojit k Firebase v Tools > Firebase
     * DO main activity vyhlásit proměnný: StorageReference a možná i DatabaseReference
     * v OnCreate StorageReference = FirebaseStorage.getInstance().getReference(s:"nazev_slozky"); */

    /**
     * Sem přijde zjištění typu souboru
     * Nvm jestli to bude potřeba
     **/
    public String getFileExtension() {
        /* Sem přijde zjištění typu souboru
         * Nvm jestli to bude potřeba */
        return null;
    }

    /**
     * Tato metoda nahraje soubor do úložiště
     **/
    public void uploadFile(Uri imgUri, StorageReference storRef) {
        if (imgUri != null) {
            /* Asi se obrázky budou jmenovat jinak */
            StorageReference fileRef = storRef.child(UUID.randomUUID().toString() + "." + getFileExtension());
            fileRef.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            /* Když se upload podaří */
                            // Upload upload = new Upload("name".trim(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()); možná nebude použito

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            /* Když se nepodaří uploadnout */
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            /* Možná progress bar */
                        }
                    });
        } else {
            /* Napíše zprávu uživateli */
        }
    }

    public void downloadFile(StorageReference storageRef, String name){
        StorageReference islandRef = storageRef.child("images/island.jpg");

        File localFile = null;
        try {
            localFile = File.createTempFile(name, "jpg"); /* Nevim jestli to bude jpg */
        } catch (IOException e) {
            e.printStackTrace();
        }

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void downloadPoznavacka(){
        /* zjisti kolik je obrazku v poznavacce a stahne je ve for cyklu*/
    }
}
