package dialogs;

/*
 * Created by Vj on 10-Apr-17.
 */

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sspl.org.tp.R;

import app_utility.OnFragmentInteractionListener;


public class SortPopupWindow {
    private Activity aActivity;

    public PopupWindow mPopupWindowSort;

    private String[] sortNamesArray;
    private RadioGroup rgDialogSort;

    private int nCheckBoxFlag = 0;

    private OnFragmentInteractionListener onFragmentInteractionListener;

    private RadioButton[] radioButton;


    public SortPopupWindow(Activity activity, int nCheckBoxFlag) {
        this.aActivity = activity;
        this.nCheckBoxFlag = nCheckBoxFlag;
        this.onFragmentInteractionListener = (OnFragmentInteractionListener) activity;
        onCreateView();
    }

    private void onCreateView() {
        View mFilterMenuLayout = View.inflate(aActivity, R.layout.dialog_sort, null);

        mPopupWindowSort = new PopupWindow(mFilterMenuLayout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        rgDialogSort = (RadioGroup) mFilterMenuLayout.findViewById(R.id.rg_dialog_sort);
        radioButton = new RadioButton[2];

        sortNamesArray = aActivity.getApplicationContext().getResources().getStringArray(R.array.sort_names);

        for (int i = 0; i <= radioButton.length - 1; i++) {
            addDynamicContents(i);
        }
        if (nCheckBoxFlag == 1) {
            radioButton[0].setChecked(true);
            radioButton[1].setChecked(false);
        } else if (nCheckBoxFlag == 2) {
            radioButton[0].setChecked(false);
            radioButton[1].setChecked(true);
        } else {
            radioButton[0].setChecked(false);
            radioButton[1].setChecked(false);
        }
        rgDialogSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case 1:
                        onFragmentInteractionListener.onFragmentMessage("SORT_VALUE", 1, false);
                        mPopupWindowSort.dismiss();
                        break;
                    case 2:
                        onFragmentInteractionListener.onFragmentMessage("SORT_VALUE", 2, false);
                        mPopupWindowSort.dismiss();
                        break;
                }
            }
        });
    }

    private void addDynamicContents(int i) {
        radioButton[i] = new RadioButton(aActivity.getApplicationContext());
        radioButton[i].setTag(sortNamesArray[i]);
        radioButton[i].setScaleX(1.1f);
        radioButton[i].setScaleY(1.1f);
        radioButton[i].setText(sortNamesArray[i]);
        radioButton[i].setId(i + 1);
        radioButton[i].setTextColor(ResourcesCompat.getColor(aActivity.getResources(), R.color.grey, null));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButton[i].setButtonTintList(ResourcesCompat.getColorStateList(aActivity.getResources(), R.color.darkGray, null));
        } else {
            //below 2 line of code adds a gray border to pre-lollipop devices checkbox
            int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
            radioButton[i].setButtonDrawable(id);
        }
        //RadioGroup.LayoutParams rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        rgDialogSort.addView(radioButton[i], new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT));
    }
}
