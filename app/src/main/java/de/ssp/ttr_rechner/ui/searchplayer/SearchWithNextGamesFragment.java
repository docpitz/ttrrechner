package de.ssp.ttr_rechner.ui.searchplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.jmelzer.myttr.Player;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.ssp.service.mytischtennis.ServiceErrorAlertDialogHelper;
import de.ssp.service.mytischtennis.caller.ServiceCallerFindCompletePlayer;
import de.ssp.service.mytischtennis.caller.ServiceCallerFindPlayersByTeam;
import de.ssp.service.mytischtennis.caller.ServiceCallerNextGames;
import de.ssp.service.mytischtennis.caller.ServiceFinish;
import de.ssp.service.mytischtennis.model.NextGame;
import de.ssp.ttr_rechner.FoundedPlayerActivity;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.TTRCalculatorActivity;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SearchWithNextGamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchWithNextGamesFragment extends ListFragment implements FloatingButtonAction
{
    protected NextGame[] nextGames;
    protected @BindView(android.R.id.list) ListView listMannschaften;
    protected boolean isSingleChooseActive;

    public class ServiceFinishSearchPlayer implements ServiceFinish<String, List<Player>>
    {
        @Override
        public void serviceFinished(String requestValue, boolean success, List<Player> playerList, String errorMessage)
        {
            if(! ServiceErrorAlertDialogHelper.showErrorDialog(SearchWithNextGamesFragment.this.getContext(), success, errorMessage))
            {
                ServiceCallerFindCompletePlayer serviceCallerFindCompletePlayer = new ServiceCallerFindCompletePlayer(getContext(), new ServiceFinishFindPlayer(), playerList);
                serviceCallerFindCompletePlayer.callService();
            }
        }
    }

    public class ServiceFinishFindPlayer implements ServiceFinish<List<Player>, List<Player>>
    {
        @Override
        public void serviceFinished(List<Player> returnValue, boolean success, List<Player> playerList, String errorMessage)
        {
            if(! ServiceErrorAlertDialogHelper.showErrorDialog(SearchWithNextGamesFragment.this.getContext(), success, errorMessage))
            {
                Intent intentForTTRKonstanteActivity = new Intent(getActivity(), FoundedPlayerActivity.class);
                intentForTTRKonstanteActivity.putExtra(FoundedPlayerActivity.PUT_EXTRA_PLAYER_LIST, (ArrayList)playerList);
                intentForTTRKonstanteActivity.putExtra(FoundedPlayerActivity.PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV, isSingleChooseActive);
                getActivity().startActivityForResult(intentForTTRKonstanteActivity, TTRCalculatorActivity.REQUEST_CODE_SEARCH);
            }
        }
    }

    public class ServiceFinishNextGames implements ServiceFinish<Void, NextGame[]>
    {
        @Override
        public void serviceFinished(Void returnValue, boolean success, NextGame[] nextGames, String errorMessage)
        {
            if(! ServiceErrorAlertDialogHelper.showErrorDialog(SearchWithNextGamesFragment.this.getContext(), success, errorMessage))
            {
                SearchWithNextGamesFragment.this.nextGames = nextGames;
                listMannschaften.setAdapter(new NextGamesListAdapter(SearchWithNextGamesFragment.this.getContext(), nextGames));
            }
        }
    }

    public SearchWithNextGamesFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchWithNextGamesFragment.
     */
    public static SearchWithNextGamesFragment newInstance(boolean isSingleChooseActive)
    {
        SearchWithNextGamesFragment fragment = new SearchWithNextGamesFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.player_search_fragment_next_games, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(nextGames != null)
        {
            listMannschaften.setAdapter(new NextGamesListAdapter(this.getContext(), nextGames));
        }
        if (!getUserVisibleHint())
        {
            return;
        }
        hideSoftKeyBoard();
        if(nextGames == null)
        {
            ServiceCallerNextGames serviceCallerNextGames = new ServiceCallerNextGames(this.getContext(), new ServiceFinishNextGames());
            serviceCallerNextGames.callService();
        }
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        NextGame nextGame = nextGames[position];
        ServiceCallerFindPlayersByTeam serviceCallerFindPlayersByTeam = new ServiceCallerFindPlayersByTeam(getContext(), new ServiceFinishSearchPlayer(), nextGame.getGegnerId());
        serviceCallerFindPlayersByTeam.callService();
    }


    @Override
    public void onPressedFloatingButton(View view)
    {
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
