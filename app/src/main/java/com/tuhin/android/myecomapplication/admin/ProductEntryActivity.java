package com.tuhin.android.myecomapplication.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.common.NodeNames;

import java.util.HashMap;
import java.util.Map;

public class ProductEntryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE_SELECTOR = 100;
    private static final int REQUEST_CODE_PERMISSION_GRANTED = 10;
    private EditText pdtNAmeEdt, pdtPriceEdt, pdtAvillabilityEdt, pdtQuntityEdt;
    private Button addPDtBtn, exitBtn;
    private Spinner pdtCateSpinner;
    private LinearProgressIndicator linearProgressIndicator;

    private ImageView productIv;
    private Uri imageUri;
    private String pdtCategory;
    private String photoUrl;

    private DatabaseReference root_reference, productRef;
    private StorageReference storageReference;

    String[] tittles = {
            "Grocery",
            "Fresh",
            "Home Care",
            "Baby Products",
            "Electronics",
            "Beauty Products"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_entry);

        pdtNAmeEdt = findViewById(R.id.productNameEdt);
        pdtPriceEdt = findViewById(R.id.productPriceEdt);
        pdtAvillabilityEdt = findViewById(R.id.productAvillabilityEdt);
        pdtQuntityEdt = findViewById(R.id.productQuantityEdt);
        linearProgressIndicator = findViewById(R.id.uploadIndicator);
        addPDtBtn = findViewById(R.id.addProductBtn);
        exitBtn = findViewById(R.id.exitProductBtn);

        pdtCateSpinner = findViewById(R.id.spinnerCategory);

        productIv = findViewById(R.id.ivProduct);



        root_reference = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL).getReference();
        root_reference.keepSynced(true);

        storageReference = FirebaseStorage.getInstance(Constants.FIREBASE_STORAGE_REFERENCE_URL).getReference();


        ArrayAdapter categoryArrayAdater = new ArrayAdapter(getApplicationContext(),
                R.layout.support_simple_spinner_dropdown_item,
                tittles);

        pdtCateSpinner.setAdapter(categoryArrayAdater);

        pdtCateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pdtCategory = tittles[i];
                //Toast.makeText(getApplicationContext(), tittles[i], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        productIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){

                    ActivityCompat.requestPermissions(ProductEntryActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSION_GRANTED);
                }else{

                    selectImage();
                }
            }
        });

        addPDtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pdtNAmeEdt.getText().toString().equals("")){
                    pdtNAmeEdt.setError(getString(R.string.field_error));
                }
                else if(pdtPriceEdt.getText().toString().equals("")){
                    pdtPriceEdt.setError(getString(R.string.field_error));
                }else if(pdtQuntityEdt.getText().toString().equals("")){
                    pdtQuntityEdt.setError(getString(R.string.field_error));
                }else if(pdtAvillabilityEdt.getText().toString().equals("")){
                    pdtAvillabilityEdt.setError(getString(R.string.field_error));
                }else{
                    String id=addProductToServer();

                    uploadImagetoServer(imageUri,id);
                }
            }
        });


    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityIfNeeded(intent, REQUEST_CODE_IMAGE_SELECTOR);

    }

    private String addProductToServer(){

        productRef = root_reference.child(NodeNames.PRODUCT).push();
        String pushId = productRef.getKey();
        addPDtBtn.setEnabled(false);

        Map<String,String> product = new HashMap<>();
        product.put(NodeNames.PRODUCT_ID,pushId);
        product.put(NodeNames.PRODUCT_NAME,pdtNAmeEdt.getText().toString());
        product.put(NodeNames.PRODUCT_PRICE,pdtPriceEdt.getText().toString());
        product.put(NodeNames.PRODUCT_QUANTITY,pdtQuntityEdt.getText().toString());
        product.put(NodeNames.PRODUCT_CATEGORY,pdtCategory);
        product.put(NodeNames.PRODUCT_AVAILABILITY,pdtAvillabilityEdt.getText().toString());
        product.put(NodeNames.PRODUCT_PHOTO,"");
        productRef.setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    //addPDtBtn.setEnabled(true);
                    pdtNAmeEdt.setText("");
                    pdtQuntityEdt.setText("");
                    pdtAvillabilityEdt.setText("");
                    pdtPriceEdt.setText("");

                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.failed_add_product,task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return pushId;
    }

    private void uploadImagetoServer(Uri uri,String productId){



        String filePath = productId+".jpg";
        StorageReference prStrREf = storageReference.child(NodeNames.PRODUCT_IMAGES+"/"+filePath);
        Toast.makeText(getApplicationContext(), prStrREf.getName(), Toast.LENGTH_SHORT).show();
        UploadTask uploadTask = prStrREf.putFile(uri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                linearProgressIndicator.setVisibility(View.VISIBLE);
                double progressCount = (100* snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();
                linearProgressIndicator.setProgress((int) progressCount);

            }
        });

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show();
                    linearProgressIndicator.setVisibility(View.GONE);
                    addPDtBtn.setEnabled(true);

                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.failed_add_product,task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_GRANTED && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMAGE_SELECTOR && resultCode == RESULT_OK && data!= null){
            imageUri = data.getData();
            if(imageUri!=null)
            productIv.setImageURI(imageUri);
        }
    }
}
