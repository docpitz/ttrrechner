package de.ssp.ttr_rechner.ui.ocr;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.ssp.ttr_rechner.R;

final class ErrorRowViewHolder extends RecyclerView.ViewHolder {

    final View rootView;
    final TextView txtError;

    ErrorRowViewHolder(@NonNull View view) {
        super(view);

        rootView = view;
        txtError = view.findViewById(R.id.txtError);
    }
}
