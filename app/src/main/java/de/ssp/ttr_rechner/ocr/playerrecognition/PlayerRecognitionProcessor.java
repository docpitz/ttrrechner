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
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.jmelzer.myttr.logic.TTRClubParser;
import com.jmelzer.myttr.model.SearchPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import de.ssp.ttr_rechner.ocr.VisionProcessorBase;

/**
 * Processor for the text recognition demo.
 */
public class PlayerRecognitionProcessor extends VisionProcessorBase<FirebaseVisionText> {

    private static final String TAG = "TextRecProc";

    private final FirebaseVisionTextRecognizer detector;
    private Context context;
    private PlayerRecognitionProcessorFinisher finisher;
    private List<PlayerFinder> playerFinderList = new ArrayList<>();

    public PlayerRecognitionProcessor(Context context, PlayerRecognitionProcessorFinisher finisher) {
        this.context = context;
        this.finisher = finisher;
        detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        playerFinderList.add(new PlayerInGroupFinder(new TTRClubParser(context)));
        playerFinderList.add(new PlayerInGroupFinder(new TTRClubParser(context)));
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
    protected void onSuccess(@NonNull FirebaseVisionText results)
    {
        ArrayList<SearchPlayer> searchPlayers = new ArrayList<>();
        for (PlayerFinder playerFinder: playerFinderList) {
            ArrayList<SearchPlayer> tmpSearchPlayers = playerFinder.search(results);
            if(tmpSearchPlayers != null && tmpSearchPlayers.size() > 0)
            {
                searchPlayers = tmpSearchPlayers;
                break;
            }
        }
        finisher.ocrFinished(searchPlayers);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Text detection failed." + e);
    }
}
