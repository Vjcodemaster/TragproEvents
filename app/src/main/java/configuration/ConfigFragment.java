package configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.sspl.org.tp.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import app_utility.CircularProgressBar;
import app_utility.NetworkState;
import app_utility.OnToastInteractionInterface;
import app_utility.SharedPreferencesClass;
import app_utility.TagProAsyncTask;

import static app_utility.StaticReferenceClass.sAPP_PREFERENCES;
import static app_utility.StaticReferenceClass.sConfigureUrl;


public class ConfigFragment extends Fragment implements ExpandableLayout.OnExpansionUpdateListener {

    SharedPreferencesClass sPreferenceClass;

    Button btnConfigure;
    Button btnExpand;

    ImageView ivExpand;

    AppCompatCheckBox cbDeviceScan, cbPublic, cbPrivate, cbReceiveInfo;
    LinearLayout llDynamicParent;
    CoordinatorLayout clSnackBar;

    ArrayList<String> alSavedCheckBoxTag;
    ArrayList<String> alCategory, alID;
    ArrayList<Integer> ID;

    boolean isDeviceScanChecked = false, isReceiveInfoChecked = false;

    int nVisibility = 0;
    int nUserType = 0;

    NetworkState networkState;

    private Snackbar snackbar;

    public String snackbarMessage = "";

    private View snackBarView;

    ArrayList<CheckBox> arrayOfCheckBox;

    LinearLayout lLayoutTVCB;


    boolean isAllUnCheckedProgramatically = false;

    private ExpandableLayout expandableLayout;

    OnToastInteractionInterface mToastInterface;

    CheckBox[] checkBoxes;

    public ConfigFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        sPreferenceClass = new SharedPreferencesClass(getActivity());

        alID = new ArrayList<>();
        alCategory = new ArrayList<>();
        arrayOfCheckBox = new ArrayList<>();
        ID = new ArrayList<>();

        alSavedCheckBoxTag = new ArrayList<>();
        alID = getArguments().getStringArrayList("IDArray");
        alCategory = getArguments().getStringArrayList("CategoryArray");
        nUserType = getArguments().getInt("userType");
        alCategory.add(0, "All");
        alID.add(0,"All");

        expandableLayout = (ExpandableLayout) view.findViewById(R.id.expandable_layout);
        expandableLayout.collapse();
        expandableLayout.setOnExpansionUpdateListener(this);

        llDynamicParent = (LinearLayout) expandableLayout.findViewById(R.id.llDynamicParent);

        //setting the size of checkboxes
        checkBoxes = new CheckBox[alID.size()];

        /*
        *for loop executes until the size of checkboxes.
        *addDynamicContents adds all the dynamic layouts, checkbox, textview into the xml.
        *then setting onCheckedChangeListener to all the checkboxes using switch case.
         */
        for (int i = 0; i < checkBoxes.length; i++) {
            addDynamicContents(i);
            final int finalI = i;
            checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String sChkBox = checkBoxes[finalI].getTag().toString();
                    String sAll = "All";
                    switch (finalI) {
                        case 0:
                            //if "All" checkbox is checked or unchecked
                            if (checkBoxes[0].isChecked()) {
                                /*for (int j = 1; j < checkBoxes.length; j++) {
                                    checkBoxes[j].setChecked(true);
                                    alSavedCheckBoxTag.add(alID.get(j));
                                }*/
                                checkAll();
                            }
                            if (!checkBoxes[0].isChecked() && !isAllUnCheckedProgramatically) {
                                /*for (int j = 1; j < checkBoxes.length; j++) {
                                    checkBoxes[j].setChecked(false);
                                }*/
                                unCheckAll();
                            }
                            break;
                        default:
                            /*
                            *first we will check if arraylist already contains checkbox tag, if not add it into arraylist.
                            * else if any checkbox is unchecked, it is removed from the arraylist.
                            *
                            * in next if condition if any checkbox is checked, if size of arraylist is = size of checkbox -1,
                            * (this condition is to confirm whether arraylist contains all the checkboxes except "All", that
                            * means we need to check All checkbox and add it to arraylist as well), if arraylist doesn't contain
                            * "All" string, we set the All checkbox to checked state.
                            *
                            * in next if condition if any checkbox is unchecked, if size of arraylist is = size of checkbox -1,
                            * (this condition is to confirm whether arraylist contains all the checkboxes including "All" but
                            * one of the check box is unchecked, that means we need to uncheck All checkbox and remove it to
                            * arraylist as well).
                            *
                            * isAllUnCheckedProgramatically is to check if we have unChecked the "All" checkbox programatically.
                            * by setting isAllUnCheckedProgramatically to true, unChecking of every checkbox present in the list
                            * will be avoided. Thus achieving the condition we want to put into it.
                            *
                            * once the "All" checkbox is set to false, we bring back the default boolean of isAllUnCheckedProgramatically
                            * to false. so that it wont affect if user unChecks "All" checkbox manually.
                             */
                            if (!alSavedCheckBoxTag.contains(sChkBox)) {
                                alSavedCheckBoxTag.add(sChkBox);
                            } else if (!isChecked) {
                                String sChkBox1 = checkBoxes[finalI].getTag().toString();
                                alSavedCheckBoxTag.remove(sChkBox1);
                            }
                            if (isChecked && alSavedCheckBoxTag.size() == (checkBoxes.length - 1) && !alSavedCheckBoxTag.contains(sAll)) {
                                checkBoxes[0].setChecked(true);
                            }
                            if (!isChecked && alSavedCheckBoxTag.size() == (checkBoxes.length - 1) && alSavedCheckBoxTag.contains(sAll)) {
                                isAllUnCheckedProgramatically = true;
                                checkBoxes[0].setChecked(false);
                                isAllUnCheckedProgramatically = false;
                                alSavedCheckBoxTag.remove(sAll);
                            }
                            Toast.makeText(getActivity(), String.valueOf(finalI), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }

        networkState = new NetworkState();


        clSnackBar = (CoordinatorLayout) view.findViewById(R.id.clSnackBar);

        btnExpand = (Button) view.findViewById(R.id.btn_expand);
        btnConfigure = (Button) view.findViewById(R.id.btnConfigure);

        ivExpand = (ImageView) view.findViewById(R.id.iv_expand);

        cbDeviceScan = (AppCompatCheckBox) view.findViewById(R.id.cbDeviceScan);
        cbPublic = (AppCompatCheckBox) view.findViewById(R.id.cbPublic);
        cbPrivate = (AppCompatCheckBox) view.findViewById(R.id.cbPrivate);
        cbReceiveInfo = (AppCompatCheckBox) view.findViewById(R.id.cbReceiveInfo);


        if (nUserType == 0) {
            cbPrivate.setChecked(true);
        } else {
            cbPublic.setChecked(true);
        }

        cbDeviceScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDeviceScanChecked = isChecked;
            }
        });

        cbReceiveInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isReceiveInfoChecked = isChecked;
            }
        });

        cbPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /*
            below code checked or unchecked only if the other button is checked or unchecked
            if user tries to remove the check of the public button without selecting private
            public button remains checked.
            same applies for private button too
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPrivate.setChecked(false);
                    nVisibility = 1;
                } else if (!cbPrivate.isChecked()) {
                    nVisibility = 0;
                    cbPublic.setChecked(true);
                }
            }
        });

        cbPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPublic.setChecked(false);
                    nVisibility = 0;
                } else if (!cbPublic.isChecked()) {
                    nVisibility = 1;
                    cbPrivate.setChecked(true);
                }
            }
        });

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout.isExpanded())
                    expandableLayout.collapse();
                else
                    expandableLayout.expand();

            }
        });

        ivExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout.isExpanded())
                    expandableLayout.collapse();
                else
                    expandableLayout.expand();
            }
        });

        btnConfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureUserSettings();
            }
        });
        snackbar = Snackbar.make(clSnackBar, snackbarMessage, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackBarView = snackbar.getView();

        return view;
    }

    public void checkAll() {
        alSavedCheckBoxTag.clear();
        for (int j = 0; j < alID.size(); j++) {
            checkBoxes[j].setChecked(true);
            if (!alSavedCheckBoxTag.contains(alID.get(j))) {
                alSavedCheckBoxTag.add(alID.get(j));
            }
        }
    }

    public void unCheckAll() {
        for (int j = 0; j < alID.size(); j++) {
            checkBoxes[j].setChecked(false);
        }
        alSavedCheckBoxTag.clear();
    }

    public void addDynamicContents(int i) {
        //creating dynamic lLayoutTVCB and adding it to parent llDynamicParent which is already in XML
        lLayoutTVCB = new LinearLayout(getActivity());
        lLayoutTVCB.setOrientation(LinearLayout.HORIZONTAL);
        llDynamicParent.addView(lLayoutTVCB);

        TextView tvDynamicText = new TextView(getActivity());
        //adding weight to textView to look appropriately
        LinearLayout.LayoutParams params = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .3f);
        tvDynamicText.setLayoutParams(params);
        //setting textSize according to display
        if (Build.VERSION.SDK_INT < 23) {
            //noinspection deprecation
            tvDynamicText.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Medium);
        } else {
            tvDynamicText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        }
        tvDynamicText.setText(alCategory.get(i));

        LinearLayout.LayoutParams params1 = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .1f);

        checkBoxes[i] = new CheckBox(getActivity().getApplicationContext());
        checkBoxes[i].setTag(alID.get(i));
        checkBoxes[i].setScaleX(1.1f);
        checkBoxes[i].setScaleY(1.1f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkBoxes[i].setButtonTintList(ResourcesCompat.getColorStateList(getResources(), R.color.rosePink, null));
        }else {
            //below 2 line of code adds a gray border to pre-lollipop devices checkbox
            int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
            checkBoxes[i].setButtonDrawable(id);
        }
        checkBoxes[i].setLayoutParams(params1);

        lLayoutTVCB.addView(tvDynamicText);
        lLayoutTVCB.addView(checkBoxes[i]);
    }

    //shows snackbar if there is no Internet connection
    public void noInternetConnectivity() {
        snackBarView.setBackgroundColor(Color.RED);
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        tv.setText(snackbarMessage);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }


    public void configureUserSettings() {
        if (!networkState.isOnline() && !networkState.isNetworkAvailable(getActivity())) {
            snackbarMessage = getResources().getString(R.string.no_internet_connection);
            noInternetConnectivity();
        } else {

            CircularProgressBar circularProgressBar = new CircularProgressBar(getActivity());
            circularProgressBar.setCanceledOnTouchOutside(false);
            circularProgressBar.setCancelable(false);
            circularProgressBar.show();
            //dialog.show();
            //creating object of editor
            SharedPreferences.Editor editor;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(sAPP_PREFERENCES, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            String sSessionID = sharedPreferences.getString("sessionID", null);

            if (alSavedCheckBoxTag.contains("All")) {
                int index = alSavedCheckBoxTag.indexOf("All");
                alSavedCheckBoxTag.remove(index);
            }
            //Set the values
            Set<String> setID = new HashSet<>();
            Set<String> setCategory = new HashSet<>();
            Set<String> setCheckedItems = new HashSet<>();
            //setID.addAll(alID);
            setCategory.addAll(alCategory);
            setCheckedItems.addAll(alSavedCheckBoxTag);

            editor.putInt("visibility", nVisibility);
            editor.putInt("userType", nUserType);
            editor.putBoolean("isReceiveInfoChecked", isReceiveInfoChecked);
            editor.putStringSet("IDArray", setID);
            editor.putStringSet("CategoryArray", setCategory);
            editor.putStringSet("CheckedItemsArray", setCheckedItems);
            editor.apply();
            TagProAsyncTask mTagProAsyncTask = new TagProAsyncTask(getActivity(), sSessionID, nVisibility, isReceiveInfoChecked, alSavedCheckBoxTag,
                    sPreferenceClass, circularProgressBar, mToastInterface);

            mTagProAsyncTask.execute("3", sConfigureUrl);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnToastInteractionInterface) {
            mToastInterface = (OnToastInteractionInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onExpansionUpdate(float expansionFraction) {
        ivExpand.setRotation(expansionFraction * 180);
    }
}
