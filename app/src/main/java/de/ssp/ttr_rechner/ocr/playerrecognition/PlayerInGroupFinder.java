package de.ssp.ttr_rechner.ocr.playerrecognition;

import android.util.Log;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.logic.TTRClubParser;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

public class PlayerInGroupFinder implements PlayerFinder
{
    TTRClubParser clubParser;
    public PlayerInGroupFinder(TTRClubParser clubParser)
    {
        this.clubParser = clubParser;
    }

    public ArrayList<SearchPlayer> search(@NonNull FirebaseVisionText results)
    {
        ArrayList<String> foundedLines = findLines(results);
        return findPlayers(foundedLines, clubParser);
    }

    protected ArrayList<String>findLines(@NonNull FirebaseVisionText results)
    {
        ArrayList<String> foundedLines = new ArrayList<>();

        Log.d(this.getClass().toString(), results.toString());
        List<FirebaseVisionText.TextBlock> blocks = results.getTextBlocks();
        Pattern patternIsWord = Pattern.compile("([A-Z])\\w+");
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (FirebaseVisionText.Line line: lines) {
                String lineText = line.getText();
                Matcher matcherWord = patternIsWord.matcher(lineText);
                if(matcherWord.find())
                {
                    foundedLines.add(lineText);
                }
            }
        }
        return foundedLines;
    }

    protected  ArrayList<SearchPlayer>findPlayers(ArrayList<String> foundedLines, TTRClubParser clubParser)
    {
        ArrayList<SearchPlayer> searchPlayers = new ArrayList<>();

        Pattern patternName = Pattern.compile("([A-Z])\\w+, ([A-Z])\\w+");
        Pattern patternVerein = Pattern.compile("([A-Z])\\w+");
        SearchPlayer searchPlayer = null;
        boolean isPlayerFound = false;
        for (String lineText: foundedLines) {
            Matcher matcherName = patternName.matcher(lineText);
            Matcher matcherVerein = patternVerein.matcher(lineText);
            if (matcherName.find()) {
                if(searchPlayer != null && searchPlayer.getFirstname() != null && searchPlayer.getLastname() != null) {
                    searchPlayers.add(searchPlayer);
                }
                String player = lineText.substring(matcherName.start(), matcherName.end());
                if(player != null && !player.isEmpty())
                {
                    searchPlayer = new SearchPlayer();
                    String[] name = player.split(", ");

                    searchPlayer.setLastname(name[0]);
                    searchPlayer.setFirstname(name[1]);
                    isPlayerFound = true;

                }
            }
            else if (matcherVerein.find() && isPlayerFound) {
                Club verein = clubParser.getClubExact(lineText);
                searchPlayer.setClub(verein);
                searchPlayers.add(searchPlayer);
                searchPlayer = new SearchPlayer();
                isPlayerFound = false;
            }
        }
        return searchPlayers;
    }
}
