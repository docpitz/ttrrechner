package de.ssp.ttr_rechner.ui.ocr;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.ssp.ttr_rechner.R;

final class FoundedPlayerHeaderViewHolder extends RecyclerView.ViewHolder {

    final TextView tvTitle;

    FoundedPlayerHeaderViewHolder(@NonNull View view) {
        super(view);

        tvTitle = view.findViewById(R.id.tvTitle);
    }
}
