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

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.TTRClubParser;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.ssp.ttr_rechner.FoundedPlayerActivity;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.TTRechnerActivity;
import de.ssp.ttr_rechner.service.ServiceErrorAlertDialogHelper;
import de.ssp.ttr_rechner.service.caller.ServiceCallerSearchPlayer;
import de.ssp.ttr_rechner.service.caller.ServiceReady;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchWithNameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchWithNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchWithNameFragment extends Fragment implements FloatingButtonAction, ServiceReady<List<Player>>
{
    private OnFragmentInteractionListener mListener;
    protected TTRClubParser clubParser;
    protected @BindView(R.id.txtClubSearch) AutoCompleteTextView txtClubSearch;
    protected @BindView(R.id.txtVorname) EditText txtVorname;
    protected @BindView(R.id.txtNachname) EditText txtNachname;


    public SearchWithNameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchWithNameFragment.
     */
    public static SearchWithNameFragment newInstance() {
        SearchWithNameFragment fragment = new SearchWithNameFragment();
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
        View view = inflater.inflate(R.layout.player_search_fragment_name, container, false);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPressedFloatingButton(View view)
    {
        TTRClubParser ttrClubParser = new TTRClubParser(getContext());
        String vorname = txtVorname.getText().toString();
        String nachname = txtNachname.getText().toString();
        String vereinsname = txtClubSearch.getText().toString();

        if(! validateInput(vorname, nachname))
        {
            return;
        }


        Club foundedClub = null;
        if(vereinsname != null && !vereinsname.isEmpty())
        {
            foundedClub = ttrClubParser.getClubNameBestMatch(vereinsname);
            txtClubSearch.setText(foundedClub != null ? foundedClub.getName() : "");
        }

        SearchPlayer searchPlayer = new SearchPlayer();
        searchPlayer.setFirstname(vorname);
        searchPlayer.setLastname(nachname);
        searchPlayer.setClub(foundedClub);

        ServiceCallerSearchPlayer serviceCallerSearchPlayer = new ServiceCallerSearchPlayer(getContext(),this,searchPlayer);
        serviceCallerSearchPlayer.callService();
    }

    @Override
    public void serviceReady(boolean success, List<Player> playerList, String errorMessage)
    {
        if(! ServiceErrorAlertDialogHelper.showErrorDialog(this.getContext(), success, errorMessage))
        {
            Intent intent = new Intent(getActivity(), FoundedPlayerActivity.class);
            intent.putExtra(FoundedPlayerActivity.PUT_EXTRA_PLAYER_LIST, (ArrayList) playerList);
            getActivity().startActivityForResult(intent, TTRechnerActivity.REQUEST_CODE_SEARCH);
        }
    }

    private boolean validateInput(String vorname, String nachname)
    {
        if(vorname.isEmpty() && !nachname.isEmpty() || !vorname.isEmpty() && nachname.isEmpty())
        {
            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
            dialogBuilder.setTitle("Fehler bei der Eingabe")
                    .setMessage("Bitte Vorname und Nachname gemeinsam eingeben oder beide frei lassen.")
                    .setPositiveButton("Ok", null);
            dialogBuilder.create().show();
            return false;
        }
        return true;
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
