package com.eitan.shopik.CompanyFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Company.Company;
import com.eitan.shopik.Database;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewItemFragment extends Fragment {

    private String type, color, size, brand, price, name, stock, company_id, company_name, company_logo,link;
    private Button mConfirm;
    private String strap_type, opening_type, classification, material;
    private EditText mName, mInStock,mPrice,mLink;
    private Switch mAnimal, mAccessories, mBrandLogo;
    private ImageView mImage, mImage1, mImage2, mImage3;
    private Uri resultUri, image1Uri, image2Uri, image3Uri;
    private TextView mCompanyName;
    private DatabaseReference itemsDB, companyDB;
    private FirebaseAuth mAuth;
    private boolean animal, accessories, brand_logo;
    private BottomNavigationView bottomNav;
    private static int TIME_OUT = 10000; //Time to launch the another activity
    CircleImageView mCompany_logo;
    private String key;
    Spinner types, sizes, colors, brands, strap_types, opening_types, classifications, materials;
    ArrayAdapter<CharSequence> types_adapter, colors_adapter, brands_adapter, sizes_adapter, classification_adapter,
            opening_adapter, strap_adapter, material_adapter;


    public NewItemFragment() {
        // Required empty public constructor
    }

    public static NewItemFragment newInstance() {
        NewItemFragment fragment = new NewItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company_add_item, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == -1) {
            assert data != null;
            resultUri = data.getData();
            mImage.setImageURI(resultUri);
        } else if (requestCode == 2 && resultCode == -1) {
            assert data != null;
            image1Uri = data.getData();
            mImage1.setImageURI(image1Uri);
        } else if (requestCode == 3 && resultCode == -1) {
            assert data != null;
            image2Uri = data.getData();
            mImage2.setImageURI(image2Uri);
        } else if (requestCode == 4 && resultCode == -1) {
            assert data != null;
            image3Uri = data.getData();
            mImage3.setImageURI(image3Uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        company_id = mAuth.getCurrentUser().getUid();
        mCompanyName = getActivity().findViewById(R.id.company_name);
        company_name = mCompanyName.getText().toString();
        itemsDB = FirebaseDatabase.getInstance().getReference().child("Items");
        companyDB = FirebaseDatabase.getInstance().getReference().child("Companies").child(company_id);
        mConfirm = Objects.requireNonNull(getView()).findViewById(R.id.confirm);
        bottomNav = Objects.requireNonNull(getActivity()).findViewById(R.id.company_bottom_nav);
        bottomNav.setVisibility(View.INVISIBLE);
        bottomNav.setSelected(false);

        final RelativeLayout classification_layout = getView().findViewById(R.id.choose_item_classification);
        final RelativeLayout accessories_layout = getView().findViewById(R.id.choose_item_accessories);
        final RelativeLayout material_layout = getView().findViewById(R.id.choose_item_material);
        final RelativeLayout opening_layout = getView().findViewById(R.id.choose_item_opening);
        final RelativeLayout shape_layout = getView().findViewById(R.id.choose_item_shape);
        final RelativeLayout strap_layout = getView().findViewById(R.id.choose_item_strap_type);

        companyDB.child("logo_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                company_logo = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        types = Objects.requireNonNull(getView()).findViewById(R.id.type_spinner);
        types_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.types, R.layout.support_simple_spinner_dropdown_item);
        types_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        types.setAdapter(types_adapter);

        sizes = Objects.requireNonNull(getView()).findViewById(R.id.size_spinner);
        sizes_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.sizes, R.layout.support_simple_spinner_dropdown_item);
        sizes_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        sizes.setAdapter(sizes_adapter);

        brands = Objects.requireNonNull(getView()).findViewById(R.id.brand_spinner);
        brands_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.brands, R.layout.support_simple_spinner_dropdown_item);
        brands_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        brands.setAdapter(brands_adapter);

        colors = Objects.requireNonNull(getView()).findViewById(R.id.color_spinner);
        colors_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.colors, R.layout.support_simple_spinner_dropdown_item);
        colors_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        colors.setAdapter(colors_adapter);

        classifications = Objects.requireNonNull(getView()).findViewById(R.id.classification_spinner);
        classification_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.classification, R.layout.support_simple_spinner_dropdown_item);
        classification_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        classifications.setAdapter(classification_adapter);

        opening_types = Objects.requireNonNull(getView()).findViewById(R.id.item_opening);
        opening_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.opening_type, R.layout.support_simple_spinner_dropdown_item);
        opening_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        opening_types.setAdapter(opening_adapter);

        strap_types = Objects.requireNonNull(getView()).findViewById(R.id.item_strap);
        strap_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.strap_type, R.layout.support_simple_spinner_dropdown_item);
        strap_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        strap_types.setAdapter(strap_adapter);

        materials = Objects.requireNonNull(getView()).findViewById(R.id.item_material);
        material_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.material, R.layout.support_simple_spinner_dropdown_item);
        material_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        materials.setAdapter(material_adapter);

        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
                    if(type.equals(Macros.Items.SHOES)){
                        classification_layout.setVisibility(View.GONE);
                        accessories_layout.setVisibility(View.GONE);
                        material_layout.setVisibility(View.GONE);
                        opening_layout.setVisibility(View.GONE);
                        shape_layout.setVisibility(View.GONE);
                        strap_layout.setVisibility(View.GONE);

                    }
                    else{
                        classification_layout.setVisibility(View.VISIBLE);
                        accessories_layout.setVisibility(View.VISIBLE);
                        material_layout.setVisibility(View.VISIBLE);
                        opening_layout.setVisibility(View.VISIBLE);
                        shape_layout.setVisibility(View.VISIBLE);
                        strap_layout.setVisibility(View.VISIBLE);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        colors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        brands.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                brand = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                size = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        materials.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                material = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        opening_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                opening_type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        classifications.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classification = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        strap_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strap_type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mName = getView().findViewById(R.id.item_name);
        mInStock = getView().findViewById(R.id.item_in_stock);
        mAccessories = getView().findViewById(R.id.item_accessories);
        mBrandLogo = getView().findViewById(R.id.item_brand_logo);
        mPrice = Objects.requireNonNull(getView()).findViewById(R.id.edit_price);
        mLink = getView().findViewById(R.id.item_site_link);

        mAnimal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                animal = isChecked;
            }
        });

        mAccessories.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accessories = isChecked;
            }
        });

        mBrandLogo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                brand_logo = isChecked;
            }
        });

        mImage = getView().findViewById(R.id.item_image1);
        mImage1 = getView().findViewById(R.id.item_image2);
        mImage2 = getView().findViewById(R.id.item_image3);
        mImage3 = getView().findViewById(R.id.item_image4);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        mImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });

        mImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 4);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Database connection = new Database();
                name = mName.getText().toString();
                stock = mInStock.getText().toString();
                price = mPrice.getText().toString();
                link = mLink.getText().toString();

                key = companyDB.child("items").child(type).push().getKey();
                final Map newImage = new HashMap();

                newImage.put("id", key);
                newImage.put("color", color);
                newImage.put("type", type);
                newImage.put("size", size);
                newImage.put("brand", brand);
                newImage.put("name", name);
                newImage.put("onSale",false);
                newImage.put("numSold", "0");
                newImage.put("inStock", stock);
                if(type.equals(Macros.Items.BAG)){
                    newImage.put("animalFriendly", animal);
                    newImage.put("strapType", strap_type);
                    newImage.put("openingType", opening_type);
                    newImage.put("classification", classification);
                    newImage.put("material", material);
                    newImage.put("accessories", accessories);
                }
                newImage.put("price", price);
                newImage.put("seller", company_name);
                newImage.put("seller_id",company_id);
                newImage.put("brandLogo", brand_logo);
                newImage.put("seller_logo",company_logo);
                newImage.put("site_link",link);

                assert key != null;
                mAuth = FirebaseAuth.getInstance();

                if (resultUri != null) {
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ItemsImages").child(type).child("img1").child(key);

                    Bitmap bitmap1 = null;

                    try {
                        bitmap1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getApplication().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();

                    assert bitmap1 != null;

                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 80, baos1);

                    Glide.with(getActivity().getApplication()).load(bitmap1).into(mImage);

                    byte[] data = baos1.toByteArray();

                    UploadTask uploadTask1 = filePath.putBytes(data);
                    uploadTask1.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newImage.put("imageUrl",uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                if (image1Uri != null) {
                    assert key != null;
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ItemsImages").child(type).child("img2").child(key);

                    Bitmap bitmap2 = null;

                    try {
                        bitmap2 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getApplication().getContentResolver(), image1Uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

                    assert bitmap2 != null;

                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 50, baos2);

                    Glide.with(getActivity().getApplication()).load(bitmap2).into(mImage1);

                    byte[] data = baos2.toByteArray();

                    UploadTask uploadTask2 = filePath.putBytes(data);
                    uploadTask2.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newImage.put("imageUrl2",uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                if (image2Uri != null) {
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ItemsImages").child(type).child("img3").child(key);

                    Bitmap bitmap3 = null;

                    try {
                        bitmap3 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getApplication().getContentResolver(), image2Uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos3 = new ByteArrayOutputStream();

                    assert bitmap3 != null;

                    bitmap3.compress(Bitmap.CompressFormat.JPEG, 50, baos3);

                    Glide.with(getActivity().getApplication()).load(bitmap3).into(mImage3);

                    byte[] data = baos3.toByteArray();

                    UploadTask uploadTask3 = filePath.putBytes(data);
                    uploadTask3.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    uploadTask3.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newImage.put("imageUrl3",uri.toString());

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                if (image3Uri != null) {

                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ItemsImages").child(type).child("img4").child(key);

                    Bitmap bitmap4 = null;

                    try {
                        bitmap4 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getApplication().getContentResolver(), image3Uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos4 = new ByteArrayOutputStream();

                    assert bitmap4 != null;

                    bitmap4.compress(Bitmap.CompressFormat.JPEG, 50, baos4);

                    Glide.with(getActivity().getApplication()).load(bitmap4).into(mImage3);

                    byte[] data = baos4.toByteArray();

                    UploadTask uploadTask4 = filePath.putBytes(data);
                    uploadTask4.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    uploadTask4.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newImage.put("imageUrl4",uri.toString());

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

                bottomNav.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // connection.addItem(newImage);
                    }
                }, TIME_OUT);
            }
        });
    }
}