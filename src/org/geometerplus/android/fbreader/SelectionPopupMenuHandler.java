package org.geometerplus.android.fbreader;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.text.view.ZLTextView;

import com.onyx.android.sdk.ui.SelectionPopupMenu.ISelectionHandler;

public class SelectionPopupMenuHandler implements ISelectionHandler
{
    private FBReader mFBReader = null;

    public SelectionPopupMenuHandler(FBReader reader)
    {
        mFBReader = reader;
    }

    @Override
    public void copy()
    { 
        ZLApplication.Instance().runAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD);
    }

    @Override
    public void share()
    {
        ZLApplication.Instance().runAction(ActionCode.SELECTION_SHARE);
    }

    @Override
    public void translation()
    {
        ZLApplication.Instance().runAction(ActionCode.SELECTION_TRANSLATE);
    }

    @Override
    public void addBookmark()
    {
        ZLApplication.Instance().runAction(ActionCode.SELECTION_BOOKMARK);
    }

    @Override
    public void dismiss()
    {
        final FBReaderApp fbReader = (FBReaderApp)FBReaderApp.Instance();
        final ZLTextView view = fbReader.getTextView();
        view.clearSelection();
        mFBReader.getSelectionPopupMenu().hide();
    }

}
