package de.ssp.ttr_rechner.ui.searchplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.logic.TTRClubParser;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import de.ssp.ttr_rechner.FoundedPlayerActivity;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.service.caller.ServiceCallerSearchPlayer;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SearchWithCriteriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchWithCriteriaFragment extends Fragment implements FloatingButtonAction
{
    enum Gender
    {
        ALL(null),
        FEMALE("female"),
        MALE("male");

        String stringValue;
        Gender(String gender)
        {
            this.stringValue = gender;
        }
    }

    protected TTRClubParser clubParser;
    protected boolean isSingleChooseActive;

    protected @BindView(R.id.txtClubSearch) AutoCompleteTextView txtClubSearch;
    protected @BindView(R.id.btnGroupGender) RadioRealButtonGroup btnGroupGender;
    protected @BindView(R.id.txtGeburtsjahrVon) EditText txtGeburtsjahrVon;
    protected @BindView(R.id.txtGeburtsjahrBis) EditText txtGeburtsjahrBis;
    protected @BindView(R.id.txtTTRVon) EditText txtTTRVon;
    protected @BindView(R.id.txtTTRBis) EditText txtTTRBis;

    public SearchWithCriteriaFragment() {
        // Required empty public constructor
    }

    public static SearchWithCriteriaFragment newInstance(boolean isSingleChooseActive) {
        SearchWithCriteriaFragment fragment = new SearchWithCriteriaFragment();
        Bundle args = new Bundle();
        args.putBoolean(FoundedPlayerActivity.PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV, isSingleChooseActive);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isSingleChooseActive = getArguments().getBoolean(FoundedPlayerActivity.PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.player_search_fragment_criteria, container, false);
        ButterKnife.bind(this, view);
        initializeClubParsing();
        return view;
    }

    private void initializeClubParsing()
    {
        clubParser = new TTRClubParser(this.getContext());
        List<String> clubList = clubParser.getClubs();
        String[] clubs = clubList.toArray(new String[clubList.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.select_dialog_item, clubs);
        txtClubSearch.setThreshold(3);//will start working from first character
        txtClubSearch.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }

    @Override
    public void onPressedFloatingButton(View view)
    {
        int ttrFrom = convertEditTextViewToInt(txtTTRVon);
        int ttrTo = convertEditTextViewToInt(txtTTRBis);
        int yearFrom = convertEditTextViewToInt(txtGeburtsjahrVon);
        int yearTo = convertEditTextViewToInt(txtGeburtsjahrBis);
        TTRClubParser ttrClubParser = new TTRClubParser(getContext());
        String vereinsname = txtClubSearch.getText().toString();

        Club foundedClub = null;
        if (vereinsname != null && !vereinsname.isEmpty())
        {
            vereinsname.trim();
            foundedClub = ttrClubParser.getClubNameBestMatch(vereinsname);
            txtClubSearch.setText(foundedClub != null ? foundedClub.getName() : "");
        }

        SearchPlayer searchPlayer = new SearchPlayer();
        searchPlayer.setTtrFrom(ttrFrom);
        searchPlayer.setTtrTo(ttrTo);
        searchPlayer.setYearFrom(yearFrom);
        searchPlayer.setYearTo(yearTo);
        searchPlayer.setClub(foundedClub);
        searchPlayer.setGender(getGenderFromUI().stringValue);

        ServiceCallerSearchPlayer serviceCallerSearchPlayer = new ServiceCallerSearchPlayer(getContext(),new ServiceReadySearchPlayer(getActivity(), isSingleChooseActive), searchPlayer);
        serviceCallerSearchPlayer.callService();

    }

    private Gender getGenderFromUI()
    {
        Gender gender = Gender.ALL;
        switch (btnGroupGender.getPosition())
        {
            case 0:
                gender = Gender.ALL;
                break;
            case 1:
                gender = Gender.MALE;
                break;
            case 2:
                gender = Gender.FEMALE;
                break;
        }
        return gender;
    }

    private int convertEditTextViewToInt(EditText editText)
    {
        String str = editText.getText().toString();
        if(str.isEmpty())
        {
            return -1;
        }
        return Integer.valueOf(str).intValue();
    }
}
