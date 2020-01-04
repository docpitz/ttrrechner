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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.ssp.ttr_rechner.FoundedPlayerActivity;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.service.caller.ServiceCallerSearchPlayer;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SearchWithNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchWithNameFragment extends Fragment implements FloatingButtonAction
{
    protected TTRClubParser clubParser;
    protected boolean isSingleChooseActive;
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
    public static SearchWithNameFragment newInstance(boolean isSingleChooseActive) {
        SearchWithNameFragment fragment = new SearchWithNameFragment();
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

    @Override
    public void onDetach() {
        super.onDetach();
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
            vereinsname.trim();
            foundedClub = ttrClubParser.getClubNameBestMatch(vereinsname);
            txtClubSearch.setText(foundedClub != null ? foundedClub.getName() : "");
        }

        SearchPlayer searchPlayer = new SearchPlayer();
        searchPlayer.setFirstname(vorname);
        searchPlayer.setLastname(nachname);
        searchPlayer.setClub(foundedClub);

        ServiceCallerSearchPlayer serviceCallerSearchPlayer = new ServiceCallerSearchPlayer(getContext(), new ServiceReadySearchPlayer(getActivity(), isSingleChooseActive), searchPlayer);
        serviceCallerSearchPlayer.callService();
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
}
