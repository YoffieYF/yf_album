package plugin.album.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import plugin.album.AlbumPlugin;
import plugin.album.Constants;
import plugin.album.PickerMgr;
import plugin.album.PreviewMgr;
import plugin.album.R;
import plugin.album.adapter.AlbumListAdapter;
import plugin.album.adapter.PickerGridAdapter;
import plugin.album.data.AlbumItem;
import plugin.album.data.MediaItem;
import plugin.album.dialog.ProgressDialog;
import plugin.album.utils.FileStorage;
import plugin.album.utils.LimitClickUtils;
import plugin.album.utils.MediaCompression;
import plugin.album.utils.MediaFileGet;
import plugin.album.utils.MediaInfoExtract;
import plugin.album.utils.Utils;

public class PickerActivity extends FragmentActivity implements MediaFileGet.OnGetListener {
    private static final String TAG = "PreviewActivity";
    private RecyclerView mPickerGridView;
    private GridLayoutManager layoutManager;
    private View mAreaTitle;
    private TextView mTitleView;
    private TextView mCancelTv;
    private View mAppBar;
    private ImageView mMenuIcon;
    private Button mSendBtn;
    private Button mPreviewBtn;
    private View mBottomControlArea;
    private CheckBox mOriginalCheckBox;
    private ListPopupWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private AlbumListAdapter mAlbumListAdapter = new AlbumListAdapter();
    private PickerGridAdapter mPickerGridAdapter;
    private LimitClickUtils mLimitClick = new LimitClickUtils();
    private int mType = 0;
    private String mCropFilePath = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF4F4F4F"));
        setContentView(R.layout.activity_photo_picker);
        Intent intent = getIntent();
        if (intent != null) {
            mType = intent.getIntExtra("type", 0);
        }
        initView();
        initData();
    }

    private void initView() {
        mPickerGridAdapter = new PickerGridAdapter(mType);
        mAppBar = findViewById(R.id.area_appbar);
        mTitleView = findViewById(R.id.tv_title);
        mMenuIcon = findViewById(R.id.menu_icon);

        if (mType != 0) {
            mTitleView.setText("所有图片");
        }

        mAreaTitle = findViewById(R.id.area_title);
        mAreaTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }
                mAlbumListAdapter.refreshData(PickerMgr.getInstance().getAlbumList());  //刷新数据
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    Drawable arrowD = getResources().getDrawable(R.mipmap.icon_close_menu);
                    mMenuIcon.setImageDrawable(arrowD);
                    mFolderPopupWindow.show();
                    //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                    int index = mAlbumListAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

        layoutManager = new GridLayoutManager(this, 4);
        mPickerGridView = findViewById(R.id.recycler_picker_list);
        mPickerGridView.setLayoutManager(layoutManager);
        mPickerGridAdapter.setActionListener(new PickerGridAdapter.OnPhotoActionListener() {
            @Override
            public void onDeselect() {
                refreshThumb();
            }

            @Override
            public void onClick(MediaItem item) {
                if (mLimitClick.check(R.id.recycler_picker_list)) return;
                if (mType == 1) {
                    mCropFilePath = FileStorage.tempFilePath(System.currentTimeMillis() + ".jpg", true);
                    Utils.startCropActivity(item.getPath(), mCropFilePath, PickerActivity.this, Constants.REQUEST_CODE_CROP);
                } else if (mType == 2) {
                    ProgressDialog.showDialog(PickerActivity.this);
                    PickerMgr.getInstance().mediaItemCompress(item, new MediaCompression.OnCompressionListener() {
                        @Override
                        public void onCompressionComplete(Map<String, Object> dataList) {
                            ProgressDialog.dismissDialog();
                            Intent intent = new Intent();
                            intent.putExtra("path",(String) dataList.get("path"));
                            setResult(Constants.RESULT_CODE_SINGLE_IMAGE, intent);
                            finish();
                        }
                    });
                } else {
                    PreviewMgr.getInstance().initData(PreviewMgr.OPEN_ALBUM,
                            item, mPickerGridAdapter.getItemList(), PickerMgr.getInstance().getSelectedImages());
                    Intent intent = new Intent(PickerActivity.this, PreviewActivity.class);
                    PickerActivity.this.startActivityForResult(intent, Constants.REQUEST_CODE_PREVIEW);
                }
            }
        });
        mPickerGridView.setAdapter(mPickerGridAdapter);

        mCancelTv = findViewById(R.id.tv_cancel);
        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSendBtn = findViewById(R.id.btn_send);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PickerMgr.getInstance().getSelectedImages().isEmpty()) {
                    Toast.makeText(AlbumPlugin.gContext, getString(R.string.select_picture), Toast.LENGTH_LONG).show();
                } else {
                    ProgressDialog.showDialog(PickerActivity.this);
                    PickerMgr.getInstance().getMediaInfoList(new MediaInfoExtract.OnGetInfoListener() {
                        @Override
                        public void onGetInfoComplete(List<Map<String, Object>> dataList) {
                            PickerMgr.getInstance().setSendDataList(dataList);
                            ProgressDialog.dismissDialog();
                            setResult(Constants.RESULT_CODE_SEND);
                            finish();
                        }
                    });
                }
            }
        });

        mPreviewBtn = findViewById(R.id.btn_preview);
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLimitClick.check(v)) return;

                if (PickerMgr.getInstance().getSelectedImages().isEmpty()) {
                    Toast.makeText(AlbumPlugin.gContext, getString(R.string.select_picture), Toast.LENGTH_LONG).show();
                } else {
                    PreviewMgr.getInstance().initData(PreviewMgr.OPEN_SELECT, null,
                            PickerMgr.getInstance().getSelectedImages(), PickerMgr.getInstance().getSelectedImages());
                    Intent intent = new Intent(PickerActivity.this, PreviewActivity.class);
                    PickerActivity.this.startActivityForResult(intent, Constants.REQUEST_CODE_PREVIEW);
                }
            }
        });

        mOriginalCheckBox = findViewById(R.id.cb_original);
        mOriginalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PickerMgr.getInstance().setOriginalMode(isChecked);
            }
        });

        if (mType != 0) {
            mBottomControlArea = findViewById(R.id.bottom_control_area);
            mBottomControlArea.setVisibility(View.GONE);
            PickerMgr.getInstance().setOriginalMode(true);
        }
    }

    private void initData() {
        PickerMgr.getInstance().getAlbumList(mType);
        PickerMgr.getInstance().getImageList(0, mType, this);
        ProgressDialog.showDialog(this);
    }

    private void refreshThumb() {
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        for (int i = firstVisible; i <= lastVisible; i++) {
            View view = layoutManager.findViewByPosition(i);
            if (view == null) continue;
            TextView textView = view.findViewById(R.id.tv_select);
            MediaItem image = (MediaItem) view.getTag();
            if (image != null) {
                int index = PickerMgr.getInstance().getSelectedImages().indexOf(image);
                mPickerGridAdapter.setThumbCount(index, textView);
            }
        }
    }

    private void createPopupFolderList() {
        mFolderPopupWindow = new ListPopupWindow(this);
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mAlbumListAdapter);
        mFolderPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);  //如果不设置，就是 AnchorView 的宽度
        mFolderPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mFolderPopupWindow.setAnchorView(mAppBar);  //ListPopupWindow总会相对于这个View
        mFolderPopupWindow.setModal(true);  //是否为模态，影响返回键的处理
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mAlbumListAdapter.setSelectIndex(position);
                AlbumItem albumItem = mAlbumListAdapter.getItem(position);
                PickerMgr.getInstance().getImageList(albumItem.bucketId, mType, PickerActivity.this);
                mTitleView.setText(albumItem.AlbumName);
                mFolderPopupWindow.dismiss();
                ProgressDialog.showDialog(PickerActivity.this);
            }
        });
        mFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Drawable arrowD = getResources().getDrawable(R.mipmap.icon_open_menu);
                mMenuIcon.setImageDrawable(arrowD);
            }
        });
    }

    @Override
    public void onGetComplete(List<MediaItem> imageList) {
        mPickerGridAdapter.refreshData(imageList);
        ProgressDialog.dismissDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constants.REQUEST_CODE_PREVIEW == requestCode) {
            mOriginalCheckBox.setChecked(PickerMgr.getInstance().getOriginalMode());

            if (resultCode == Constants.RESULT_CODE_SEND) {
                setResult(resultCode);
                finish();
            } else {
                refreshThumb();
            }
        } else if (Constants.REQUEST_CODE_CROP == requestCode) {
            if (Utils.checkFileExists(mCropFilePath)) {
                Intent intent = new Intent();
                intent.putExtra("path", mCropFilePath);
                setResult(Constants.RESULT_CODE_SINGLE_IMAGE, intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLimitClick.destory();
    }
}
