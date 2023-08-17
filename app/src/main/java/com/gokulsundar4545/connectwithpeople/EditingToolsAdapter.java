package com.gokulsundar4545.connectwithpeople;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;

public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {
    private final OnItemSelected mOnItemSelected;
    private final List<ToolModel> mToolList = new ArrayList<>();

    public interface OnItemSelected {
        void onToolSelected(ToolType toolType);
    }

    public static class ToolModel {
        private final String mToolName;
        private final int mToolIcon;
        private final ToolType mToolType;

        public ToolModel(String toolName, int toolIcon, ToolType toolType) {
            this.mToolName = toolName;
            this.mToolIcon = toolIcon;
            this.mToolType = toolType;
        }

        public String getToolName() {
            return mToolName;
        }

        public int getToolIcon() {
            return mToolIcon;
        }

        public ToolType getToolType() {
            return mToolType;
        }
    }

    public EditingToolsAdapter(OnItemSelected onItemSelected) {
        this.mOnItemSelected = onItemSelected;
        setupTools();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.getToolName());
        holder.imgToolIcon.setImageResource(item.getToolIcon());
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;

        ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).getToolType());
                }
            });
        }
    }

    private void setupTools() {
        mToolList.add(new ToolModel("Brush", R.drawable.ic_brush, ToolType.BRUSH));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI));
        mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER));
    }
}
