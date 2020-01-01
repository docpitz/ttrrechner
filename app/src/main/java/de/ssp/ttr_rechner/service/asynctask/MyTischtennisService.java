package de.ssp.ttr_rechner.service.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MyTTClickTTParserImpl;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.caller.ServiceReady;

public class MyTischtennisService<T> extends AsyncTask<String, Void, Integer> {
    protected String errorMessage;
    protected Context context;
    protected ServiceReady<T> serviceReady;
    protected ParserEvaluator<T> parserEvaluation;
    protected T serviceReturnObject;
    protected boolean succcess;
    private ProgressDialog progressDialog;

    public MyTischtennisService(Context context, ParserEvaluator<T> parserEvaluation, ServiceReady<T> serviceReady) {
        if (context == null) throw new IllegalArgumentException("context must not be null");
        this.succcess = false;
        this.context = context;
        this.parserEvaluation = parserEvaluation;
        this.serviceReady = serviceReady;
    }

    @Override
    protected void onPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(parserEvaluation.getProgressDialogMessage() + ", bitte warten...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            long start = System.currentTimeMillis();
            serviceReturnObject = parserEvaluation.evaluateParser();
            errorMessage = parserEvaluation.getErrorMessageFromEvaluator();
            succcess = true;
            Log.i(Constants.LOG_TAG, "parser time " + (System.currentTimeMillis() - start) + " ms");
        } catch (ValidationException e) {
            errorMessage = e.getMessage();
            succcess = true;
            Log.i(Constants.LOG_TAG, e.getMessage());
        } catch (LoginExpiredException e) {
            try {
                new LoginManager().relogin();
                serviceReturnObject = parserEvaluation.evaluateParser();
                succcess = true;
            } catch (Exception e2) {
                errorMessage = "Das erneute Anmelden war nicht erfolgreich";
                Log.e(Constants.LOG_TAG, "", e2);
                succcess = false;
            }
        } catch (NoClickTTException e) {
            Log.d(Constants.LOG_TAG, "second try after no data msg");
            succcess = false;
            try {
                serviceReturnObject = parserEvaluation.evaluateParser();
                Log.d(Constants.LOG_TAG, "success");
                succcess = true;
            } catch (NoClickTTException e2) {
                logError(e);
                succcess = false;
                errorMessage = new MyTTClickTTParserImpl().parseError(Client.lastHtml);
            }
            catch (Exception e2) {
                logError(e);
                succcess = false;
                errorMessage = "Fehler beim Lesen der Webseite \n" + Client.shortenUrl();
            }
        } catch (NetworkException e) {
            succcess = false;
            errorMessage = "Das Netzwerk antwortet zu langsam oder ist ausgeschaltet";
        }
        catch (NoDataException ed) {
            logError(ed);
            succcess = false;
            errorMessage = "Mytischtennis.de meldet: " + ed.getMessage();
        }
        catch (NiceGuysException e) {
            succcess = false;
        }catch (Exception e) {
//            catch all others
            succcess = false;
            logError(e);
            errorMessage = "Fehler beim Lesen der Webseite \n" + Client.shortenUrl();
        }
        return null;
    }

    private void logError(Exception e) {
        Log.e(Constants.LOG_TAG, "Error reading " + Client.lastUrl(), e);
        Log.e(Constants.LOG_TAG, getLastH1());

    }

    private String getLastH1() {
        if (Client.lastHtml != null)
            try {
                int start = Client.lastHtml.indexOf("<p class=\"alert alert-danger\"");
                return Client.lastHtml.substring(Client.lastHtml.indexOf("<p class=\"alert alert-danger\""),
                        Client.lastHtml.indexOf("</h1>"));
            } catch (Exception e) {
                return "no h1 found";
            }
        return "--";
    }

    @Override
    protected void onPostExecute(Integer integer) {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            //see myttr-62
        }
        if (errorMessage == null && serviceReturnObject == null) {
            Log.d(Constants.LOG_TAG, "couldn't load data in class " + getClass());
            errorMessage = "Konnte die Daten nicht laden (Grund unbekannt)";
        }
        serviceReady.serviceReady(succcess, serviceReturnObject, errorMessage);

    }
}
