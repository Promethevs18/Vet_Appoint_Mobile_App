package com.example.vetclinicapp.interfaces.third_layer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.interfaces.first_layer.Home;
import com.example.vetclinicapp.models.pets_and_users_details_model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

public class Appoint_Window extends AppCompatActivity {

    //EditTexts
    EditText date, time;

    //ChipGroup
    ChipGroup pets, services;

    //Button
    Button set;

    //Firebase Database Reference
    DatabaseReference pets_ref, booking_ref, services_ref, ownerRef;

    //Firebase User
    FirebaseUser current_user;

    //ArrayLists
    ArrayList<String> pets_selected;
    ArrayList<String> services_selected;

    //RadioGroup
    RadioGroup categories;

    //DatePicker
    DatePickerDialog datePickerDialog;
    //Calendar
    Calendar calendar = Calendar.getInstance();

    //Strings
    String alaga = "", serbisyo = "", contact_num = "", address = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint_window);

        //UI instantiation

        //ChipGroup
        pets = findViewById(R.id.pets_group);
        services = findViewById(R.id.services_group);

        //EditTexts
        date = findViewById(R.id.select_date);
        time = findViewById(R.id.select_time);

        //Button
        set = findViewById(R.id.book_now);

        //Database References
        pets_ref = FirebaseDatabase.getInstance().getReference("Owners and Pets");
        services_ref = FirebaseDatabase.getInstance().getReference("Services");
        booking_ref = FirebaseDatabase.getInstance().getReference("Bookings");
        ownerRef = FirebaseDatabase.getInstance().getReference("Owners");

        //Firebase User reference
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        //ArrayLists
        pets_selected = new ArrayList<>();
        services_selected = new ArrayList<>();

        //RadioGroup
        categories = findViewById(R.id.services_radio);


        //BELOW IS WHERE ALL THE MAGIC HAPPENS

        //this is for displaying all the pets
        pets_ref.child(Objects.requireNonNull(Objects.requireNonNull(current_user).getDisplayName())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    //una, nagtawag muna tayo ng data using our model
                    pets_and_users_details_model pets_natin = snap.getValue(pets_and_users_details_model.class);
                    //nag-initialize tayo ng Chip na gagamitin
                    Chip chippy = new Chip(Appoint_Window.this);
                    //then, we gave our chips some descriptions
                    chippy.setText(Objects.requireNonNull(pets_natin).getPetName());

                    //this is just to get the user's phone number and address
                    contact_num = pets_natin.getOwnerContact();
                    address = pets_natin.getPetAddress();


                    //dito naman, nag set tayo ng parang size (yung wrap_content lang gagamitin para fit)
                    ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //then we set margins para hindi dikit dikit kada isang chip
                    params.setMargins(2, 2, 2, 2);
                    //sinet natin ang layout params ng chips using the params
                    chippy.setLayoutParams(params);
                    //letting the system generate an id
                    chippy.setId(View.generateViewId());
                    //this part is the one writing the blueprint
                    ChipDrawable drawChip = ChipDrawable.createFromAttributes(Appoint_Window.this, null, com.google.android.material.R.attr.checkedChip, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter);
                    //we are giving the blueprint to our chips
                    chippy.setChipDrawable(drawChip);
                    //giving the chips to our chip group
                    pets.addView(chippy);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Appoint_Window.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        /* Now that we have drawn our chips, we are going to assign the selections in an array
        para mailagay natin sa database kung sino - sinong pet yun. Dynamic eto, which means kapag
        magdedeselect si user, mawawala rin sa list ang data na iyon
         */
        pets.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                int bilang = group.getChildCount();
                for (int i = 0; i < bilang; i++) {
                    Chip pinili = (Chip) group.getChildAt(i);
                    if (pinili.isChecked()) {
                        if (!pets_selected.contains(String.valueOf(pinili.getText()))) {
                            pets_selected.add(pinili.getText().toString());
                        }
                    } else {
                        pets_selected.remove(pinili.getText().toString());
                    }
                }
                //new - gumamit tayo ng StringJoiner para ipagsama ang mga laman ng ArrayList sa iisang string
                //initerate muna natin ang laman ng ArrayList using for, then used the joiner para ipagsama
                StringJoiner join = new StringJoiner(", ");
                for (int x = 0; x < pets_selected.size(); x++) {
                    join.add(pets_selected.get(x));
                }
                alaga = join.toString();

            }
        });

        //This is the code for choosing the category ng services
        categories.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.veterinary) {
                    Show_Service("Veterinary");
                } else if (i == R.id.other_services) {
                    Show_Service("Others");
                } else if (i == R.id.groom) {
                    Show_Service("Grooming");
                }
            }
        });

        //Now, it's the services turn
        services.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    Chip chosen = (Chip) group.getChildAt(i);
                    if (chosen.isChecked()) {
                        if (!services_selected.contains(chosen.getText().toString())) {
                            services_selected.add(String.valueOf(chosen.getText().toString()));
                        }
                    } else {
                        services_selected.remove(String.valueOf(chosen.getText().toString()));
                    }
                }

                //new - gumamit tayo ng StringJoiner para ipagsama ang mga laman ng ArrayList sa iisang string
                //initerate muna natin ang laman ng ArrayList using for, then used the joiner para ipagsama
                StringJoiner join = new StringJoiner(", ");
                for (int x = 0; x < services_selected.size(); x++) {
                    join.add(services_selected.get(x));
                }
                serbisyo = join.toString();
            }
        });

        //eto naman kapag pumili na si user ng Date
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mga variables para sa date eme
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                //calling the datePickerDialog
                datePickerDialog = new DatePickerDialog(Appoint_Window.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        //Assigning yung pinili ni user na date from the prompt, to each values
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);

                        //using a formatter para maipakita natin sa desired format ang date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        //setting the editText content to the result
                        date.setText(dateFormat.format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        //eto naman sa Time
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //using the timepickerdialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(Appoint_Window.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        String hourString = String.format(Locale.getDefault(), "%02d", i);
                        String hourOnly = hourString.substring(0, 2);
                        int finalHour = Integer.parseInt(hourOnly);
                        if (finalHour < 8) {
                            Toast.makeText(Appoint_Window.this, "Chosen hour is too early", Toast.LENGTH_SHORT).show();
                        } else if (finalHour > 18) {
                            Toast.makeText(Appoint_Window.this, "Chosen hour is too late", Toast.LENGTH_SHORT).show();
                        } else {
                            time.setText(String.format("%02d", i) + ":" + String.format("%02d", i1));
                        }
                    }
                }, 0, 0, false);
                timePickerDialog.setTitle("Select your desired time");
                timePickerDialog.show();


            }
        });

        //when the user clicks on the Set Appointment
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pd = new ProgressDialog(Appoint_Window.this);
                pd.setTitle("Booking");
                pd.setMessage("Creating your appointment, please wait...");
                pd.show();

                HashMap<String, Object> booking_map = new HashMap<>();
                booking_map.put("patients", alaga);
                booking_map.put("services", serbisyo);
                booking_map.put("sched_date", date.getText().toString());
                booking_map.put("sched_time", time.getText().toString());
                booking_map.put("owner", current_user.getDisplayName());
                booking_map.put("imageUrl", Objects.requireNonNull(current_user.getPhotoUrl()).toString());
                booking_map.put("address", address);
                booking_map.put("contact_num", contact_num);
                booking_map.put("status", "appointed");

                HashMap<String, Object> bookDeets = new HashMap<>();
                bookDeets.put("sched_date", date.getText().toString());
                bookDeets.put("sched_time", time.getText().toString());
                ownerRef.child(Objects.requireNonNull(current_user.getDisplayName())).child("Booking").updateChildren(bookDeets).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //this is to book an appointment sa booking node
                            booking_ref.child(date.getText().toString()).child(Objects.requireNonNull(current_user.getDisplayName())).updateChildren(booking_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //this is to book into the previous bookings section
                                        createCalendarEvent();
                                        ownerRef.child(Objects.requireNonNull(current_user.getDisplayName())).child("Previous Bookings").child(date.getText().toString()).updateChildren(booking_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    pd.dismiss();
                                                    AlertDialog.Builder build = new AlertDialog.Builder(Appoint_Window.this);
                                                    build.setTitle("Appointment Confirmed!");
                                                    build.setMessage("Your appointment has been confirmed, and details have also been posted on your device's calendar\n. Click proceed to return to main menu");
                                                    build.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Intent goHome = new Intent(Appoint_Window.this, Home.class);
                                                            startActivity(goHome);
                                                            Appoint_Window.this.finish();
                                                        }
                                                    });
                                                    build.show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void createCalendarEvent() {
        ContentValues values = new ContentValues();
        String dateAndTime = date.getText().toString() + " " + time.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateAndTime);
            values.put(CalendarContract.Events.DTSTART, Objects.requireNonNull(date).getTime());
            values.put(CalendarContract.Events.TITLE, "Pets appointment at AniCare Vet Clinic");
            values.put(CalendarContract.Events.DESCRIPTION, "You have an upcoming appointment with AniCare Vet Clinic on this particular date");
            values.put(CalendarContract.Events.CALENDAR_ID, getDefaultCalendar());
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Manila");

            Calendar kalendaryo = Calendar.getInstance();
            kalendaryo.setTime(date);
            kalendaryo.add(Calendar.HOUR, 1);
            Date end = kalendaryo.getTime();

            values.put(CalendarContract.Events.DTEND, end.getTime());


            Uri calUri = CalendarContract.Events.CONTENT_URI;
            Uri eventUri = getContentResolver().insert(calUri, values);

        } catch (ParseException e) {
            Toast.makeText(this, "Error occurred due to: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Long getDefaultCalendar() {
        long calendarId = -1;
        String[] project = {CalendarContract.Calendars._ID};
        String selection = CalendarContract.Calendars.IS_PRIMARY + "= 1";
        Cursor cursor = getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                project,
                selection,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(CalendarContract.Calendars._ID);
            calendarId = cursor.getLong(index);
            cursor.close();
        }
        return calendarId;
    }

    public void Show_Service(String chosen) {
        //dito naman yung para sa services
        services_ref.child(chosen).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                services.removeAllViews();
                for (DataSnapshot snappy : snapshot.getChildren()) {
                    //nag-initialize tayo ng Chip na gagamitin
                    Chip chippy = new Chip(Appoint_Window.this);
                    //then, we gave our chips some descriptions
                    chippy.setText(Objects.requireNonNull(snappy.getKey()));

                    //dito naman, nag set tayo ng parang size (yung wrap_content lang gagamitin para fit)
                    ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //then we set margins para hindi dikit dikit kada isang chip
                    params.setMargins(2, 2, 2, 2);
                    //sinet natin ang layout params ng chips using the params
                    chippy.setLayoutParams(params);
                    //letting the system generate an id
                    chippy.setId(View.generateViewId());
                    //this part is the one writing the blueprint
                    ChipDrawable drawChip = ChipDrawable.createFromAttributes(Appoint_Window.this, null, com.google.android.material.R.attr.checkedChip, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter);
                    //we are giving the blueprint to our chips
                    chippy.setChipDrawable(drawChip);
                    //giving the chips to our chip group
                    services.addView(chippy);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Appoint_Window.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}