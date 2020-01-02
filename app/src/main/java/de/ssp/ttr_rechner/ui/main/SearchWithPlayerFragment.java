package de.ssp.ttr_rechner.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.logic.ClubParser;
import com.jmelzer.myttr.logic.TTRClubParser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import de.ssp.ttr_rechner.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchWithPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchWithPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchWithPlayerFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    protected TTRClubParser clubParser;
    protected Club foundedClub;
    protected @BindView(R.id.txtClubSearch) AutoCompleteTextView txtClubSearch;


    public SearchWithPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchWithPlayerFragment.
     */
    public static SearchWithPlayerFragment newInstance() {
        SearchWithPlayerFragment fragment = new SearchWithPlayerFragment();
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
        View view = inflater.inflate(R.layout.fragment_search_with_player, container, false);
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
