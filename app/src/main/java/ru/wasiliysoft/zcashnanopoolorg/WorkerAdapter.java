package ru.wasiliysoft.zcashnanopoolorg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.wasiliysoft.zcashnanopoolorg.Model.NpGeneral;


public class WorkerAdapter extends ArrayAdapter<NpGeneral.Worker> {
    public WorkerAdapter(@NonNull Context context, int resource, @NonNull List<NpGeneral.Worker> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return super.getView(position, convertView, parent);
    }
}
