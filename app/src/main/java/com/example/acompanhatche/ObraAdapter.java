package com.example.acompanhatche;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ObraAdapter extends RecyclerView.Adapter<ObraAdapter.ObraViewHolder> {

    private List<Obra> lista;

    public ObraAdapter(List<Obra> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ObraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_obra, parent, false);
        return new ObraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObraViewHolder holder, int position) {
        Obra obra = lista.get(position);
        holder.nome.setText(obra.getNome());
        holder.status.setText(obra.getStatus());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ObraViewHolder extends RecyclerView.ViewHolder {

        TextView nome, status;

        public ObraViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.txtNomeObra);
            status = itemView.findViewById(R.id.txtStatusObra);
        }
    }
}
