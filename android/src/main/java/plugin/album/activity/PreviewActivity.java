package plugin.album.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.Map;

import plugin.album.AlbumPlugin;
import plugin.album.Constants;
import plugin.album.PickerMgr;
import plugin.album.PreviewMgr;
import plugin.album.R;
import plugin.album.adapter.PreviewListAdapter;
import plugin.album.adapter.PreviewPagerAdapter;
import plugin.album.data.MediaItem;
import plugin.album.data.PreviewItem;
import plugin.album.dialog.ProgressDialog;
import plugin.album.utils.MediaInfoExtract;
import plugin.album.view.ViewClickListener;
import plugin.album.view.ViewPagerFixed;

public class PreviewActivity extends FragmentActivity {
    private static final String TAG = "PreviewActivity";
    private ViewPagerFixed mViewPager;
    private PreviewPagerAdapter mViewPagerAdapter;
    private View mAreaAppbar;
    private View mBottomBar;
    private int mCurrentPosition = 0;
    private PreviewMgr sPreviewMgr;
    private PickerMgr sPickerMgr;
    private TextView mTitleCount;
    private CheckBox mCheckBoxSelect;
    private CheckBox mOriginalCheckBox;
    private PreviewListAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private View mAreaBack;
    private Button mBtnSend;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        initData();
        initView();
    }

    private void initData() {
        sPickerMgr = PickerMgr.getInstance();
        sPreviewMgr = PreviewMgr.getInstance();
        mCurrentPosition = sPreviewMgr.getInitialIndex();
        if (mCurrentPosition == PreviewMgr.INVALID_INDEX) {
            mCurrentPosition = 0;
        }
    }

    private void initView() {
        mViewPager = findViewById(R.id.viewpager);
        mViewPagerAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), -1, new ViewClickListener() {
            @Override
            public void onClickListener() {
                onImageSingleTap(mAreaAppbar.getVisibility() != View.VISIBLE);
            }

            @Override
            public void onClickPlay(boolean showBar) {
                onImageSingleTap(showBar);
            }
        });
        mViewPagerAdapter.setData(sPreviewMgr.getOptionalList(), false);
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.setCurrentItem(mCurrentPosition, false);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                onPagerSelect(position);
            }
        });

        mRecyclerAdapter = new PreviewListAdapter(sPreviewMgr.getSelectList(), new PreviewListAdapter.OnSelectListener() {
            @Override
            public void OnClickListener(int position) {
                onListSelect(position);
            }
        });
        mRecyclerView = findViewById(R.id.recycler_image_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        mAreaAppbar = findViewById(R.id.area_appbar);
        mBottomBar = findViewById(R.id.bottom_bar);
        mTitleCount = findViewById(R.id.tv_count);

        mCheckBoxSelect = findViewById(R.id.cb_select);
        mCheckBoxSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mCheckBoxSelect.isChecked();
                if (isChecked && sPickerMgr.getSelectedImages().size() >= 9) {
                    Toast.makeText(AlbumPlugin.gContext, getString(R.string.err_hint_select_count), Toast.LENGTH_SHORT).show();
                    mCheckBoxSelect.setChecked(false);
                    return;
                } else if (isChecked && sPreviewMgr.optionalIsLimitExceeded(mCurrentPosition)) {
                    Toast.makeText(AlbumPlugin.gContext, getString(R.string.err_hint_video_size), Toast.LENGTH_SHORT).show();
                    mCheckBoxSelect.setChecked(false);
                    return;
                } else if (isChecked && sPreviewMgr.optionalIsBigVideo(mCurrentPosition)) {
                    Toast.makeText(AlbumPlugin.gContext, getString(R.string.err_hint_video_length), Toast.LENGTH_SHORT).show();
                    mCheckBoxSelect.setChecked(false);
                    return;
                } else {
                    mCheckBoxSelect.setChecked(isChecked);
                }

                int selectIndex = sPreviewMgr.setSelectItem(mCurrentPosition, isChecked);
                mRecyclerAdapter.refreshData();
                if (selectIndex != mCurrentPosition) {
                    mRecyclerView.scrollToPosition(selectIndex);
                }
            }
        });

        mAreaBack = findViewById(R.id.area_back);
        mAreaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnSend = findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sPickerMgr.getSelectedImages().isEmpty()) {
                    if (sPreviewMgr.optionalIsLimitExceeded(mCurrentPosition)) {
                        Toast.makeText(AlbumPlugin.gContext, getString(R.string.err_hint_video_size), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (sPreviewMgr.optionalIsBigVideo(mCurrentPosition)) {
                        Toast.makeText(AlbumPlugin.gContext, getString(R.string.err_hint_video_length), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        sPreviewMgr.setSelectItem(mCurrentPosition, true);
                    }
                }
                ProgressDialog.showDialog(PreviewActivity.this);
                sPickerMgr.getMediaInfoList(new MediaInfoExtract.OnGetInfoListener() {
                    @Override
                    public void onGetInfoComplete(List<Map<String, Object>> dataList) {
                        sPickerMgr.setSendDataList(dataList);
                        ProgressDialog.dismissDialog();
                        setResult(Constants.RESULT_CODE_SEND);
                        finish();
                    }
                });
            }
        });

        mOriginalCheckBox = findViewById(R.id.cb_original);
        mOriginalCheckBox.setChecked(sPickerMgr.getOriginalMode());
        mOriginalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sPickerMgr.setOriginalMode(isChecked);
            }
        });
        onPagerSelect(mCurrentPosition);
    }

    private void onImageSingleTap(boolean bShowBar) {
        if (bShowBar) {
            mAreaAppbar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
        } else {
            mAreaAppbar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
        }
    }

    private void onListSelect(int position) {
        PreviewItem previewItem = sPreviewMgr.getSelectItem(position);
        if (previewItem == null) return;

        mCurrentPosition = previewItem.index;
        titleUpdate(mCurrentPosition);
        mViewPager.setCurrentItem(mCurrentPosition);
        mCheckBoxSelect.setChecked(previewItem.isSelect);
        mOriginalCheckBox.setVisibility(previewItem.mediaUir.getType() == MediaItem.TYPE_VIDEO ? View.GONE : View.VISIBLE);
    }

    private void onPagerSelect(int position) {
        mCurrentPosition = position;
        int selectIndex = sPreviewMgr.getFromSelectIndex(position);
        if (selectIndex != PreviewMgr.INVALID_INDEX) {
            mRecyclerView.scrollToPosition(selectIndex);
            mCheckBoxSelect.setChecked(sPreviewMgr.getSelectState(selectIndex));
        } else {
            mCheckBoxSelect.setChecked(false);
        }
        titleUpdate(mCurrentPosition);
        MediaItem item = sPreviewMgr.getOptionalItem(position);
        if (item != null) {
            mOriginalCheckBox.setVisibility(item.getType() == MediaItem.TYPE_VIDEO ? View.GONE : View.VISIBLE);
        }
    }

    @SuppressLint("StringFormatMatches")
    private void titleUpdate(int position) {
        mTitleCount.setText(getString(R.string.preview_image_count, position + 1, sPreviewMgr.getOptionalList().size()));
    }
}
