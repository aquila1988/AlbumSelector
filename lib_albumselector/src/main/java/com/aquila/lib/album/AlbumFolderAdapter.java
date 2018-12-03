package com.aquila.lib.album;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aquila.lib.album.base.BaseRecycleAdapter;
import com.aquila.lib.album.base.BaseViewHolder;
import com.aquila.lib.album.interfaces.OnFolderSelectListener;
import com.aquila.lib.album.utils.ImageLoadUtil;
import com.aquila.lib.album.utils.SPSingleton;
import com.aquila.lib.album.widget.SquaredImageView;

import java.util.List;

/***
 * @date 创建时间 2018/11/17 14:40
 * @author 作者: W.YuLong
 * @description
 */
public class AlbumFolderAdapter extends BaseRecycleAdapter<AlbumFolderEntity, AlbumFolderAdapter.AlbumFolderViewHolder> {
    private OnFolderSelectListener onFolderSelectListener;

    public static final String SP_selectFolderNameAndPath = "selectFolderNameAndPath";

    @Override
    public void setDataList(List<AlbumFolderEntity> list) {
        String selectFolder = SPSingleton.get().getString(SP_selectFolderNameAndPath, "");
        if (list != null && list.size() > 0){
            int index = 0;
            for (AlbumFolderEntity entity: list){
                if (TextUtils.equals(selectFolder, entity.getFolderInfo())){
                    selectPosition = index;
                    break;
                }
                index++;
            }
        }
        super.setDataList(list);

    }

    public void setOnFolderSelectListener(OnFolderSelectListener onFolderSelectListener) {
        this.onFolderSelectListener = onFolderSelectListener;
    }

    @Override
    public AlbumFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumFolderViewHolder(parent);
    }


    @Override
    public void onBindViewHolder(AlbumFolderViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        AlbumFolderEntity entity = dataList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;

                if (onFolderSelectListener != null) {
                    SPSingleton.get().putString(SP_selectFolderNameAndPath, entity.getFolderInfo());
                    onFolderSelectListener.onFolderSelect(entity);
                }
                notifyDataSetChanged();
            }
        });

    }


    /***
     *@date 创建时间 2018/11/17 14:59
     *@author 作者: W.YuLong
     *@description
     */
    public static class AlbumFolderViewHolder extends BaseViewHolder {
        private SquaredImageView coverImageView;
        private TextView nameTextView;
        private TextView pathTextView;
        private TextView countTextView;
        private ImageView checkImageView;

        public AlbumFolderViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.holder_album_folder_info_layout);

            countTextView = itemView.findViewById(R.id.holder_folder_item_count_TextView);
            coverImageView = itemView.findViewById(R.id.holder_folder_cover_ImageView);
            nameTextView = itemView.findViewById(R.id.holder_folder_name_TextView);
            checkImageView = itemView.findViewById(R.id.holder_folder_check_ImageView);
            pathTextView = itemView.findViewById(R.id.holder_folder_path_TextView);
        }

        @Override
        public <T> void initUIData(T t) {
            AlbumFolderEntity folderEntity = (AlbumFolderEntity) t;

            ImageLoadUtil.loadImage(coverImageView, folderEntity.getCoverEntity().getPath());
            nameTextView.setText(folderEntity.getFolderName());
            pathTextView.setText(folderEntity.getFolderPath());
            countTextView.setText(String.format("共有%d张", folderEntity.getFileEntityList().size()));
        }

        @Override
        public void onSelectPosition(boolean isSelected) {
            checkImageView.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
