// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package de.ssp.ttr_rechner.ocr.playerrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.logic.TTRClubParser;
import com.jmelzer.myttr.model.SearchPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.ssp.ttr_rechner.StillImageActivity;
import de.ssp.ttr_rechner.ocr.CameraImageGraphic;
import de.ssp.ttr_rechner.ocr.FrameMetadata;
import de.ssp.ttr_rechner.ocr.GraphicOverlay;
import de.ssp.ttr_rechner.ocr.VisionProcessorBase;

/**
 * Processor for the text recognition demo.
 */
public class PlayerRecognitionProcessor extends VisionProcessorBase<FirebaseVisionText> {

    private static final String TAG = "TextRecProc";

    private final FirebaseVisionTextRecognizer detector;
    private Context context;
    private StillImageActivity activity;

    private ArrayList<SearchPlayer> searchPlayers;
    private ArrayList<String> foundedLines;

    private FirebaseVisionText result;

    public PlayerRecognitionProcessor(StillImageActivity context) {
        this.context = context;
        this.activity = context;
        detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
        }
    }

    @Override
    protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
        return detector.processImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull FirebaseVisionText results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay,
                    originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }
        result = results;
        foundedLines = new ArrayList<>();
        searchPlayers = new ArrayList<>();
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
                TTRClubParser clubParser = new TTRClubParser(context);
                Club verein = clubParser.getClubExact(lineText);
                searchPlayer.setClub(verein);
                searchPlayers.add(searchPlayer);
                searchPlayer = new SearchPlayer();
                isPlayerFound = false;
            }
        }

        activity.playerInPictureSearchSuccess(searchPlayers);

        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Text detection failed." + e);
    }
}
