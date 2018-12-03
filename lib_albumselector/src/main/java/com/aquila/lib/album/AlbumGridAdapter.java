package com.aquila.lib.album;

import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aquila.lib.album.base.BaseListAdapter;
import com.aquila.lib.album.base.BaseViewHolder;
import com.aquila.lib.album.interfaces.OnNotifySelectCountListener;
import com.aquila.lib.album.interfaces.OnSingleItemSelectCallback;
import com.aquila.lib.album.utils.ImageLoadUtil;
import com.aquila.lib.album.utils.ScreenUtil;
import com.aquila.lib.album.widget.SquaredImageView;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/***
 * @date 创建时间 2018/11/16 16:33
 * @author 作者: W.YuLong
 * @description
 */
public class AlbumGridAdapter extends BaseListAdapter<AlbumFileEntity> {
    private List<AlbumFileEntity> selectList = new LinkedList<>();
    private int max = 9;
    private OnNotifySelectCountListener onNotifySelectCountListener;
    private boolean isMultipleSelectMode = false;

    private OnSingleItemSelectCallback onSingleItemSelectCallback;

//    private int firstSelectMediaType = 0;

    public void setOnSingleItemSelectCallback(OnSingleItemSelectCallback onSingleItemSelectCallback) {
        this.onSingleItemSelectCallback = onSingleItemSelectCallback;
    }

    public void setOnNotifySelectCountListener(OnNotifySelectCountListener onNotifySelectCountListener) {
        this.onNotifySelectCountListener = onNotifySelectCountListener;
    }

    public void setMultipleSelectMode(boolean multipleSelectMode) {
        isMultipleSelectMode = multipleSelectMode;
        notifyDataSetChanged();
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumItemViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new AlbumItemViewHolder(parent);
            convertView = viewHolder.itemView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AlbumItemViewHolder) convertView.getTag();
        }
        AlbumFileEntity entity = dataList.get(position);
        viewHolder.initUIData(entity);

        viewHolder.setSelectIndexUI(isMultipleSelectMode, selectList.indexOf(entity));

//        viewHolder.setFirstSelectMediaType(firstSelectMediaType);

        viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                List<String> pathList = new ArrayList<>();
                pathList.add(entity.getPath());
                AlbumImageScanDialog.with(v.getContext())
                        .setCLickDismiss(true)
                        .setDataList(pathList).builder().show();
                return false;
            }
        });

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMultipleSelectMode) { //单选模式
                    if (onSingleItemSelectCallback != null) {
                        onSingleItemSelectCallback.onSingleItemSelect(entity);
                    }
                } else {//多选模式
                    if (viewHolder.checkImageView.isSelected()) {
                        if (selectList.contains(entity)) {
                            selectList.remove(entity);
                        }
                    } else {
                        if (!selectList.contains(entity)) {
                            if (selectList.size() >= max) {
                                Toast.makeText(v.getContext(), String.format("最多可以选择%d张", max), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            selectList.add(entity);
                        }
                    }
                    if (onNotifySelectCountListener != null) {
                        onNotifySelectCountListener.onSelectCount(selectList.size());
                    }
                    notifyDataSetChanged();

                }
            }
        });


        return convertView;
    }

    /*取消选择*/
    public void unSelectAll() {
        selectList.clear();
        if (onNotifySelectCountListener != null) {
            onNotifySelectCountListener.onSelectCount(selectList.size());
        }
        notifyDataSetChanged();
    }

    public List<AlbumFileEntity> getSelectList() {
        return selectList;
    }


/**********************************************************************************************************************/
    /***
     *@date 创建时间 2018/11/16 18:23
     *@author 作者: W.YuLong
     *@description
     */
    public static class AlbumItemViewHolder extends BaseViewHolder {

        private SquaredImageView imageView;
        private ImageView checkImageView;
        private LinearLayout videoTypeLayout;
        private TextView videoDurationTextView;
        private TextView indexTextView;
        private ImageView maskView;

        private AlbumFileEntity entity;

        public AlbumItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.holder_album_image_layout);

            imageView = itemView.findViewById(R.id.holder_album_ImageView);
            checkImageView = itemView.findViewById(R.id.holder_album_check_ImageView);
            videoTypeLayout = itemView.findViewById(R.id.holder_album_video_info_layout);
            videoDurationTextView = itemView.findViewById(R.id.holder_album_video_duration_TextView);
            indexTextView = itemView.findViewById(R.id.holder_album_index_TextView);
            maskView = itemView.findViewById(R.id.holder_album_mask_View);
        }

        @Override
        public void onSelectPosition(boolean isSelected) {
            checkImageView.setSelected(isSelected);
        }

        public void setSelectIndexUI(boolean isMultipleMode, int index) {
            if (index >= 0) {
                if (index >= 99) {
                    indexTextView.setText("···");
                } else {
                    indexTextView.setText(String.valueOf(index + 1));
                }
                indexTextView.setVisibility(View.VISIBLE);
                checkImageView.setVisibility(View.GONE);
                checkImageView.setSelected(true);
            } else {
                indexTextView.setVisibility(View.GONE);
                checkImageView.setSelected(false);
                if (isMultipleMode) {
                    checkImageView.setVisibility(View.VISIBLE);
                }

            }
        }

        public void setFirstSelectMediaType(int type) {
            if (type != 0 && entity.getMediaType() != type) {
                maskView.setImageResource(R.color.color_0000);
//                itemView.setEnabled(false);
            } else {
                maskView.setImageResource(R.color.color_99404040);
//                itemView.setEnabled(true);
            }
        }

        @Override
        public <T> void initUIData(T t) {
            entity = (AlbumFileEntity) t;
            RequestOptions options = new RequestOptions();
            options.override(ScreenUtil.getScreenWidth(itemView.getContext()) / 4, ScreenUtil.getScreenWidth(itemView.getContext()) / 4)
                    .centerCrop()
                    .error(R.drawable.album_ic_default);

            ImageLoadUtil.loadImageWithOption(imageView, entity.getPath(), options);

            if (entity.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                videoTypeLayout.setVisibility(View.VISIBLE);
                videoDurationTextView.setText(formatVideoDuration(entity.getDuration()));
            } else {
                videoTypeLayout.setVisibility(View.GONE);
            }
        }


        private String formatVideoDuration(long time) {
            time /= 1000;

            long seconds = time % 60;
            long minutes = time / 60 % 60;
            String result;
            if (time > 3600) {
                long hours = time / 3600;
                result = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                result = String.format("%02d:%02d", minutes, seconds);
            }

            return result;

        }
    }
}
