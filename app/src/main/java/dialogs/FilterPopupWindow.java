package dialogs;

/*
 * Created by Vj on 06-Apr-17.
 */

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import com.sspl.org.tp.R;

import java.util.ArrayList;

public class FilterPopupWindow {
    private Activity aActivity;

    public PopupWindow mPopUpWindowFilter;

    private LinearLayout llDynamicParent;

    private CheckBox[] checkBoxes;

    private String[] filterNamesArray;

    private ArrayList<String> alSavedCheckBoxTag;

    private boolean isAllUnCheckedProgramatically = false;

    public FilterPopupWindow(Activity activity) {
        this.aActivity = activity;
        onCreateView();
    }

    private void onCreateView() {
        View mFilterMenuLayout = View.inflate(aActivity, R.layout.dialog_filter, null);
        //mFilterMenuLayout.setAnimation(AnimationUtils.loadAnimation(aActivity, R.anim.popup_animation));

        mPopUpWindowFilter = new PopupWindow(mFilterMenuLayout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        llDynamicParent = (LinearLayout) mFilterMenuLayout.findViewById(R.id.ll_filter_dynamic_parent);
        checkBoxes = new CheckBox[5];
        filterNamesArray = aActivity.getApplicationContext().getResources().getStringArray(R.array.filter_names);
        alSavedCheckBoxTag = new ArrayList<>();
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
                                checkAll();
                            }
                            if (!checkBoxes[0].isChecked() && !isAllUnCheckedProgramatically) {
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
                            break;
                    }
                }
            });
        }

    }

    private void addDynamicContents(int i) {
        //creating dynamic lLayoutTVCB and adding it to parent llDynamicParent which is already in XML
        LinearLayout llCheckBoxTextView = new LinearLayout(aActivity);
        llCheckBoxTextView.setOrientation(LinearLayout.HORIZONTAL);
        llCheckBoxTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llCBTVParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llCBTVParams.setMargins(0, 10, 0, 0);
        llCheckBoxTextView.setLayoutParams(llCBTVParams);
        llDynamicParent.addView(llCheckBoxTextView);

        TextView tvDynamicText = new TextView(aActivity);
        tvDynamicText.setGravity(Gravity.START);
        //adding weight to textView to look appropriately
        LinearLayout.LayoutParams params = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .7f);
        tvDynamicText.setLayoutParams(params);
        //setting textSize according to display
        if (Build.VERSION.SDK_INT < 23) {
            //noinspection deprecation
            tvDynamicText.setTextAppearance(aActivity, R.style.TextAppearance_AppCompat_Medium);
        } else {
            tvDynamicText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        }
        tvDynamicText.setText(filterNamesArray[i]);
        tvDynamicText.setTextColor(ContextCompat.getColor(aActivity, R.color.grey));

        LinearLayout.LayoutParams params1 = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .3f);

        checkBoxes[i] = new CheckBox(aActivity.getApplicationContext());
        checkBoxes[i].setTag(filterNamesArray[i]);
        checkBoxes[i].setScaleX(1.1f);
        checkBoxes[i].setScaleY(1.1f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkBoxes[i].setButtonTintList(ResourcesCompat.getColorStateList(aActivity.getResources(), R.color.darkGray, null));
        } else {
            //below 2 line of code adds a gray border to pre-lollipop devices checkbox
            int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
            checkBoxes[i].setButtonDrawable(id);
        }
        checkBoxes[i].setLayoutParams(params1);

        llCheckBoxTextView.addView(checkBoxes[i]);
        llCheckBoxTextView.addView(tvDynamicText);
    }

    private void checkAll() {
        alSavedCheckBoxTag.clear();
        for (int j = 0; j < filterNamesArray.length; j++) {
            checkBoxes[j].setChecked(true);
            if (!alSavedCheckBoxTag.contains(filterNamesArray[j])) {
                alSavedCheckBoxTag.add(filterNamesArray[j]);
            }
        }
    }

    private void unCheckAll() {
        for (int j = 0; j < filterNamesArray.length; j++) {
            checkBoxes[j].setChecked(false);
        }
        alSavedCheckBoxTag.clear();
    }
}
