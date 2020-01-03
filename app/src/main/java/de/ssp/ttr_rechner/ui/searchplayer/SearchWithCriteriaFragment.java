package de.ssp.ttr_rechner.ui.searchplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.TTRClubParser;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ssp.ttr_rechner.FoundedPlayerActivity;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.TTRechnerActivity;
import de.ssp.ttr_rechner.service.ServiceErrorAlertDialogHelper;
import de.ssp.ttr_rechner.service.caller.ServiceCallerSearchPlayer;
import de.ssp.ttr_rechner.service.caller.ServiceReady;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchWithCriteriaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchWithCriteriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchWithCriteriaFragment extends Fragment implements FloatingButtonAction, ServiceReady<List<Player>>
{
    enum Gender
    {
        ALL(null),
        FEMALE("female"),
        MALE("male");

        String gender;
        Gender(String gender)
        {
            this.gender = gender;
        }
    }

    private OnFragmentInteractionListener mListener;
    protected TTRClubParser clubParser;

    protected @BindView(R.id.txtClubSearch) AutoCompleteTextView txtClubSearch;
    protected @BindView(R.id.btnAlleGeschlechter) ToggleButton btnAlleGeschlechter;
    protected @BindView(R.id.btnMaennlich) ToggleButton btnMaennlich;
    protected @BindView(R.id.btnWeiblich) ToggleButton btnWeiblich;
    protected @BindView(R.id.btnGruoupGeschlecht) RadioGroup radioGroup;
    protected @BindView(R.id.txtGeburtsjahrVon) EditText txtGeburtsjahrVon;
    protected @BindView(R.id.txtGeburtsjahrBis) EditText txtGeburtsjahrBis;
    protected @BindView(R.id.txtTTRVon) EditText txtTTRVon;
    protected @BindView(R.id.txtTTRBis) EditText txtTTRBis;

    final RadioGroup.OnCheckedChangeListener toggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int position = 0; position < radioGroup.getChildCount(); position++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(position);
                view.setChecked(view.getId() == i);
            }
        }
    };


    public SearchWithCriteriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchWithPlayerFragment.
     */
    public static SearchWithCriteriaFragment newInstance() {
        SearchWithCriteriaFragment fragment = new SearchWithCriteriaFragment();
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
        View view = inflater.inflate(R.layout.player_search_fragment_criteria, container, false);
        ButterKnife.bind(this, view);
        radioGroup.setOnCheckedChangeListener(toggleListener);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @OnClick({R.id.btnAlleGeschlechter, R.id.btnWeiblich, R.id.btnMaennlich})
    protected void pressGeschlecht(ToggleButton view)
    {
        if(view.isChecked())
        {
            ((RadioGroup)view.getParent()).check(view.getId());
        }
        else
        {
            view.setChecked(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
            foundedClub = ttrClubParser.getClubNameBestMatch(vereinsname);
            txtClubSearch.setText(foundedClub != null ? foundedClub.getName() : "");
        }

        SearchPlayer searchPlayer = new SearchPlayer();
        searchPlayer.setTtrFrom(ttrFrom);
        searchPlayer.setTtrTo(ttrTo);
        searchPlayer.setYearFrom(yearFrom);
        searchPlayer.setYearTo(yearTo);
        searchPlayer.setClub(foundedClub);
        searchPlayer.setGender(getGenderFromUI().gender);

        ServiceCallerSearchPlayer serviceCallerSearchPlayer = new ServiceCallerSearchPlayer(getContext(),this, searchPlayer);
        serviceCallerSearchPlayer.callService();

    }

    private Gender getGenderFromUI()
    {
        Gender gender = Gender.ALL;
        if(btnMaennlich.isChecked())
        {
            gender = Gender.MALE;
        }
        else if(btnWeiblich.isChecked())
        {
            gender = Gender.FEMALE;
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

    @Override
    public void serviceReady(boolean success, List<Player> playerList, String errorMessage)
    {
        if(! ServiceErrorAlertDialogHelper.showErrorDialog(this.getContext(), success, errorMessage))
        {
            Intent intent = new Intent(getActivity(), FoundedPlayerActivity.class);
            intent.putExtra(FoundedPlayerActivity.PUT_EXTRA_PLAYER_LIST,(ArrayList)playerList);
            getActivity().startActivityForResult(intent, TTRechnerActivity.REQUEST_CODE_SEARCH);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
