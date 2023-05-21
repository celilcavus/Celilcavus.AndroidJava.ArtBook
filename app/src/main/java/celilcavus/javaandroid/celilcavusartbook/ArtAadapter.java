package celilcavus.javaandroid.celilcavusartbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import celilcavus.javaandroid.celilcavusartbook.databinding.RecylerrowBinding;

public class ArtAadapter extends RecyclerView.Adapter<ArtAadapter.ArtHolder> {
    public ArrayList<Art> artArrayList;

    public ArtAadapter(ArrayList<Art> artArrayList) {
        this.artArrayList = artArrayList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecylerrowBinding recylerrowBinding = RecylerrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ArtHolder(recylerrowBinding);
    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, @SuppressLint("RecyclerView") int position) {
        holder._binding.recylViewTextView.setText(artArrayList.get(position).name + artArrayList.get(position).id);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(holder.itemView.getContext(), ArtActivity.class);
                System.out.println("id ================== "+ artArrayList.get(position).id);
                intent.putExtra("value",artArrayList.get(position).id);
                intent.putExtra("key","old");
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    public class ArtHolder extends RecyclerView.ViewHolder{
        private RecylerrowBinding _binding;
        public ArtHolder(RecylerrowBinding binding) {
            super(binding.getRoot());
            _binding = binding;
        }
    }
}
