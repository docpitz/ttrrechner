package de.ssp.ttr_rechner.ui.searchplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jmelzer.myttr.Player;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.ssp.ttr_rechner.FoundedPlayerActivity;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.TTRechnerActivity;
import de.ssp.ttr_rechner.model.NextGame;
import de.ssp.ttr_rechner.service.ServiceErrorAlertDialogHelper;
import de.ssp.ttr_rechner.service.caller.ServiceCallerFindCompletePlayer;
import de.ssp.ttr_rechner.service.caller.ServiceCallerFindPlayersByTeam;
import de.ssp.ttr_rechner.service.caller.ServiceCallerNextGames;
import de.ssp.ttr_rechner.service.caller.ServiceReady;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchWithNextGamesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchWithNextGamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchWithNextGamesFragment extends ListFragment implements ServiceReady<NextGame[]>, FloatingButtonAction
{


    private OnFragmentInteractionListener mListener;
    protected NextGame[] nextGames;
    protected @BindView(android.R.id.list) ListView listMannschaften;

    public SearchWithNextGamesFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onPressedFloatingButton(View view)
    {
    }



    public class ServiceReadySearchPlayer implements ServiceReady<List<Player>>
    {
        @Override
        public void serviceReady(boolean success, List<Player> playerList, String errorMessage)
        {
            if(! ServiceErrorAlertDialogHelper.showErrorDialog(SearchWithNextGamesFragment.this.getContext(), success, errorMessage))
            {
                ServiceCallerFindCompletePlayer serviceCallerFindCompletePlayer = new ServiceCallerFindCompletePlayer(getContext(), new ServiceReadyFindPlayer(), playerList);
                serviceCallerFindCompletePlayer.callService();
            }
        }
    }

    public class ServiceReadyFindPlayer implements  ServiceReady<List<Player>>
    {
        @Override
        public void serviceReady(boolean success, List<Player> playerList, String errorMessage)
        {
            if(! ServiceErrorAlertDialogHelper.showErrorDialog(SearchWithNextGamesFragment.this.getContext(), success, errorMessage))
            {
                Intent intentForTTRKonstanteActivity = new Intent(getActivity(), FoundedPlayerActivity.class);
                intentForTTRKonstanteActivity.putExtra(FoundedPlayerActivity.PUT_EXTRA_PLAYER_LIST, (ArrayList)playerList);
                getActivity().startActivityForResult(intentForTTRKonstanteActivity, TTRechnerActivity.REQUEST_CODE_SEARCH);
            }
        }
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        NextGame nextGame = nextGames[position];
        ServiceCallerFindPlayersByTeam serviceCallerFindPlayersByTeam = new ServiceCallerFindPlayersByTeam(getContext(), new ServiceReadySearchPlayer(), nextGame.getGegnerId());
        serviceCallerFindPlayersByTeam.callService();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchWithPlayerFragment.
     */
    public static SearchWithNextGamesFragment newInstance()
    {
        return new SearchWithNextGamesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
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

        if(nextGames == null)
        {
            ServiceCallerNextGames serviceCallerNextGames = new ServiceCallerNextGames(this.getContext(), this);
            serviceCallerNextGames.callService();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void serviceReady(boolean success, NextGame[] nextGames, String errorMessage)
    {
        if(! ServiceErrorAlertDialogHelper.showErrorDialog(this.getContext(), success, errorMessage))
        {
            this.nextGames = nextGames;
            listMannschaften.setAdapter(new NextGamesListAdapter(this.getContext(), nextGames));
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
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
