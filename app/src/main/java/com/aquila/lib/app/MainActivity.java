package com.aquila.lib.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aquila.lib.album.AlbumFileEntity;
import com.aquila.lib.album.AlbumGridAdapter;
import com.aquila.lib.album.AlbumSelectOption;
import com.aquila.lib.album.OnAlbumSelectCallbackImpl;
import com.aquila.lib.album.utils.AlbumTypeDefine;
import com.aquila.lib.album.utils.ImageLoadUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button singleButton;
    private Button multipleButton;
    private EditText maxEditText;
    private TextView resultTitleTextView;
    private ImageView singleResultImageView;
    private ExpandGridView gridView;
    private CheckBox cropCheckBox;

    private RadioGroup typeRadioGroup;

    private AlbumGridAdapter gridAdapter;

    private int selectType = AlbumTypeDefine.TYPE_PICTURE_AND_VIDEO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewFromXML();
        initUI();
        setViewListeners();
    }

    private void initUI() {
        gridAdapter = new AlbumGridAdapter();
        gridAdapter.setMultipleSelectMode(false);
        gridView.setAdapter(gridAdapter);
    }

    private void setViewListeners() {
        singleButton.setOnClickListener(this);
        multipleButton.setOnClickListener(this);
        typeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_type_picture_RadioButton:
                        selectType = AlbumTypeDefine.TYPE_PICTURE;
                        break;
                    case R.id.main_type_picture_and_video_RadioButton:
                        selectType = AlbumTypeDefine.TYPE_PICTURE_AND_VIDEO;
                        break;
                    case R.id.main_type_video_RadioButton:
                        selectType = AlbumTypeDefine.TYPE_VIDEO;
                        break;
                }
            }
        });
    }

    private void initViewFromXML() {
        singleButton = findViewById(R.id.main_single_mode_Button);
        multipleButton = findViewById(R.id.main_multiple_mode_Button);
        maxEditText = findViewById(R.id.main_max_count_EditText);
        typeRadioGroup = findViewById(R.id.main_select_type_RadioGroup);
        singleResultImageView = findViewById(R.id.main_single_select_result_ImageView);
        gridView = findViewById(R.id.main_multiple_select_result_GridView);
        resultTitleTextView = findViewById(R.id.main_select_result_TextView);
        cropCheckBox = findViewById(R.id.main_crop_CheckBox);
    }

    @Override
    public void onClick(final View v) {
        if (v == singleButton) {
            AlbumSelectOption.with(this)
                    .setTitle("单选模式").setSelectType(selectType).setMultipleSelectMode(false)
                    .setCrop(cropCheckBox.isChecked())
                    .doAlbumSelect(new OnAlbumSelectCallbackImpl(){
                        @Override
                        public void onSingleSelect(AlbumFileEntity albumFileEntity) {
                            singleResultImageView.setVisibility(View.VISIBLE);
                            gridView.setVisibility(View.GONE);
                            ImageLoadUtil.loadImage(singleResultImageView, albumFileEntity.getPath());
                            resultTitleTextView.setText("选择的图片路径为:" + albumFileEntity.getPath());
                        }
                    });

        } else if (v == multipleButton) {
            int max = 9;
            if (maxEditText.getText().length() > 0) {
                max = Integer.parseInt(maxEditText.getText().toString());
            }
            AlbumSelectOption.with(this)
                    .setMax(max).setTitle("多选模式")
                    .setSelectType(selectType).setMultipleSelectMode(true)
                    .doAlbumSelect(new OnAlbumSelectCallbackImpl(){
                        @Override
                        public void onMultipleSelect(List<AlbumFileEntity> selectList) {
                            singleResultImageView.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);

                            resultTitleTextView.setText("多选的结果为:" + formatPath(selectList));
                            gridAdapter.setDataList(selectList);
                        }
                    });
        }
    }

    private String formatPath(List<AlbumFileEntity> selectList) {
        if (selectList == null || selectList.size() == 0) {
            return "[空]";
        }

        StringBuilder sb = new StringBuilder();
        int i = 0, len = selectList.size();
        for (AlbumFileEntity entity : selectList) {
            sb.append(entity.getPath());
            if (i < len - 1) {
                sb.append("\n");
            }
            i++;
        }
        return sb.toString();

    }
}
