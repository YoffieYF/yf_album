package plugin.album.view;

public abstract class ViewClickListener {
    public void onClickListener() {
    }

    public boolean onLongClick() {
        return false;
    }

    public void onCloseClick() {
    }

    public void onMoreClick() {
    }

    public void onClickPlay(boolean showBar) {
    }
}