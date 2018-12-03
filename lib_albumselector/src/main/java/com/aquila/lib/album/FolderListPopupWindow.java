package com.aquila.lib.album;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.aquila.lib.album.interfaces.OnFolderSelectListener;
import com.aquila.lib.album.widget.CustomRecyclerView;

import java.util.List;

/***
 * @date 创建时间 2018/11/17 15:04
 * @author 作者: W.YuLong
 * @description 文件夹选择的PopupWindow
 */
public class FolderListPopupWindow extends PopupWindow {
    private CustomRecyclerView dataRecyclerView;
    private View maskView;
    private AlbumFolderAdapter folderAdapter;
    //动画的时长
    private final int animDuration = 300 ;

    public FolderListPopupWindow(Context context, OnFolderSelectListener onFolderSelectListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_window_folder_list_layout, null);
        dataRecyclerView = view.findViewById(R.id.popup_window_data_RecyclerView);
        maskView = view.findViewById(R.id.popup_window_mask_View);
        maskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
//        setAnimationStyle(R.style.bottomDialogWindowAnim);
        setBackgroundDrawable(new ColorDrawable(0));
//        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setTouchable(true);

        folderAdapter = new AlbumFolderAdapter();
        folderAdapter.setOnFolderSelectListener(onFolderSelectListener);
        dataRecyclerView.setAdapter(folderAdapter);
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        dataRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(maskView, "alpha", 0, 1);
                alphaAnim.setDuration(animDuration).start();
                ObjectAnimator.ofFloat(dataRecyclerView, "translationY", dataRecyclerView.getHeight(), 0)
                        .setDuration(animDuration).start();

            }
        });
    }

    public void setDefaultFolder(int position) {
        folderAdapter.setSelectPosition(position);
    }

    @Override
    public void dismiss() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(dataRecyclerView, "translationY", 0, dataRecyclerView.getHeight())
                .setDuration(animDuration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                FolderListPopupWindow.super.dismiss();
            }
        });
        animator.start();

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(maskView, "alpha", 1, 0);
        alphaAnim.setDuration(animDuration).start();
    }

    public void setDataList(List<AlbumFolderEntity> dataList) {
        folderAdapter.setDataList(dataList);
    }
}
